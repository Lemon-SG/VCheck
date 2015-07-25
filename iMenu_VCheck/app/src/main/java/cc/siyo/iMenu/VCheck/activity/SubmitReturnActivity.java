package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.ReturnReasonAdapter;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.MemberOrder;
import cc.siyo.iMenu.VCheck.model.ReturnInfo;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Utility;
import cc.siyo.iMenu.VCheck.view.PromptDialog;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/16 11:23.
 * Desc:申请退款页面
 */
public class SubmitReturnActivity extends BaseActivity {

    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 退款套餐名称*/
    @ViewInject(id = R.id.refund_menu_name)private TextView tv_refund_menu_name;
    /** 退款金额*/
    @ViewInject(id = R.id.refund_money)private TextView tv_refund_money;
    /** 退款原因列表*/
    @ViewInject(id = R.id.return_reason_list)private ListView return_reason_list;
    /** 提交退款按钮*/
    @ViewInject(id = R.id.submit)private TextView tv_submit;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 获取退款原因成功标石*/
    private static final int GET_RETURN_REASON_SUCCESS = 100;
    /** 提交退款成功标石*/
    private static final int SUBMIT_RETURN_SUCCESS = 300;
    /** 获取退款原因失败标石*/
    private static final int GET_RETURN_REASON_FALSE = 200;
    private PromptDialog promptDialog;
    private MemberOrder memberOrder;
    private ReturnReasonAdapter returnReasonAdapter;
    private List<ReturnInfo> returnInfoList;

    private static final String TAG = "SubmitReturnActivity";

    @Override
    public int getContentView() {
        return R.layout.activity_order_refund;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("申请退款");
        topbar.setHiddenButton(TopBar.RIGHT_BUTTON);
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        returnReasonAdapter = new ReturnReasonAdapter(SubmitReturnActivity.this, R.layout.list_item_return_reason);
        return_reason_list.setAdapter(returnReasonAdapter);
    }

    @Override
    public void initData() {
        finalHttp = new FinalHttp();
        memberOrder = (MemberOrder) getIntent().getExtras().getSerializable("memberOrder");
        tv_refund_menu_name.setText(memberOrder.order_info.menu.menu_name);
        tv_refund_money.setText(memberOrder.order_info.totalPrice.special_price);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交退款
                String return_reason_id = "";
                boolean isChoose = false;
                for (int i = 0; i < returnInfoList.size(); i++) {
                    if (returnInfoList.get(i).isTrue) {
                        isChoose = true;
                        return_reason_id = returnInfoList.get(i).return_reason_id;
                        UploadAdapter_Submit(memberOrder.order_info.order_id, return_reason_id);
                        return;
                    }
                }
                if (!isChoose) {
                    prompt("请选择退款原因");
                }
            }
        });
        UploadAdapter();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_RETURN_REASON_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONArray jsonArray = jsonStatus.data.optJSONArray("return_reason_list");
                        if(jsonArray != null && jsonArray.length() >0) {
                            returnInfoList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ReturnInfo returnInfo = new ReturnInfo().parse(jsonArray.optJSONObject(i));
                                returnInfoList.add(returnInfo);
                            }
                            returnReasonAdapter.getDataList().clear();
                            returnReasonAdapter.getDataList().addAll(returnInfoList);
                            returnReasonAdapter.notifyDataSetChanged();
                            Utility.setListViewHeightBasedOnChildren(return_reason_list);
                        }
                    }
                    break;
                case SUBMIT_RETURN_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess) {
                            setResult(Constant.RESULT_CODE_RETURN);
                            finish();
                        }
                    }
                    break;
                case GET_RETURN_REASON_FALSE:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(!StringUtils.isBlank(jsonStatus.error_desc)){
                            prompt(jsonStatus.error_desc);
                        }else{
                            if(!StringUtils.isBlank(jsonStatus.error_code)){
                                prompt(getResources().getString(R.string.request_erro) + MyApplication.findErroDesc(jsonStatus.error_code));
                            }else{
                                prompt(getResources().getString(R.string.request_erro));
                            }
                        }
                    }
                    break;
            }
        }
    };

    /** 提交退款申请请求
     * */
    public void UploadAdapter_Submit(String orderId, String returnReasonId) {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.SUBMIT_RETURN);
        ajaxParams.put("token", PreferencesUtils.getString(SubmitReturnActivity.this, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText(orderId, returnReasonId));
        Log.e(TAG, Constant.REQUEST + API.SUBMIT_RETURN + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                prompt("无网络");
                closeProgressDialog();
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog(getResources().getString(R.string.loading));
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (!StringUtils.isBlank(t)) {
                    Log.e(TAG, Constant.RESULT + API.SUBMIT_RETURN + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(SUBMIT_RETURN_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(GET_RETURN_REASON_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 获取退款原因请求
     * */
    public void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_RETURN_REASON_LIST);
        ajaxParams.put("token", PreferencesUtils.getString(SubmitReturnActivity.this, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", "");
        Log.e(TAG, Constant.REQUEST + API.GET_RETURN_REASON_LIST + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                prompt("无网络");
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog(getResources().getString(R.string.loading));
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (!StringUtils.isBlank(t)) {
                    Log.e(TAG, Constant.RESULT + API.GET_RETURN_REASON_LIST + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(GET_RETURN_REASON_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(GET_RETURN_REASON_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * member_id	会员ID
     * order_id	订单ID
     * return_reason_id	退款原因ID
     * return_reason_description	退款原因详情
     * @return json
     */
    private String makeJsonText(String orderId, String returnReasonId) {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(SubmitReturnActivity.this, Constant.KEY_MEMBER_ID));
            json.put("order_id", orderId);
            json.put("return_reason_id", returnReasonId);
            json.put("return_reason_description", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public void changeBox(ReturnInfo reason) {
        for (int i = 0; i < returnInfoList.size(); i++) {
            if (returnInfoList.get(i).return_reason_id
                    .equals(reason.return_reason_id)) {
                returnInfoList.get(i).isTrue = true;
            } else {
                returnInfoList.get(i).isTrue = false;
            }
        }
        returnReasonAdapter.getDataList().clear();
        returnReasonAdapter.getDataList().addAll(returnInfoList);
        returnReasonAdapter.notifyDataSetChanged();
    }
}
