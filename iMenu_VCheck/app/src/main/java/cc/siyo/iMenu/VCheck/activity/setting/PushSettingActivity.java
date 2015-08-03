package cc.siyo.iMenu.VCheck.activity.setting;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.BaseActivity;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.PushInfo;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/29 17:03.
 * Desc:推送设置
 */
public class PushSettingActivity extends BaseActivity implements View.OnClickListener {

    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 总推送开关*/
    @ViewInject(id = R.id.rlPushAll)private RelativeLayout rlPushAll;
    /** 消费确认开关*/
    @ViewInject(id = R.id.rlPushPay)private RelativeLayout rlPushPay;
    /** 退款提醒开关*/
    @ViewInject(id = R.id.rlPushReturn)private RelativeLayout rlPushReturn;
    /** 获得礼券开关*/
    @ViewInject(id = R.id.rlPushVoucher)private RelativeLayout rlPushVoucher;
    @ViewInject(id = R.id.ivPushAll)private ImageView ivPushAll;
    @ViewInject(id = R.id.ivPushPay)private ImageView ivPushPay;
    @ViewInject(id = R.id.ivPushReturn)private ImageView ivPushReturn;
    @ViewInject(id = R.id.ivPushVoucher)private ImageView ivPushVoucher;
    /** 总开关标石*/
    private int AllStatus;
    /** 消费确认开关标石*/
    private int PayStatus;
    /** 退款提醒开关标石*/
    private int ReturnStatus;
    /** 礼券开关标石*/
    private int VoucherStatus;
    /** 推送实体*/
    private PushInfo pushInfo;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;

    @Override
    public int getContentView() {
        return R.layout.activity_push_set;
    }

    @Override
    public void initView() {
        AllStatus = PreferencesUtils.getInt(context, Constant.KEY_PUSH_ALL, 1);
        PayStatus = PreferencesUtils.getInt(context, Constant.KEY_PUSH_PAY, 1);
        ReturnStatus = PreferencesUtils.getInt(context, Constant.KEY_PUSH_RETURN, 1);
        VoucherStatus = PreferencesUtils.getInt(context, Constant.KEY_PUSH_VOUCHER, 1);
        topbar.settitleViewText("推送设置");
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rlPushAll.setOnClickListener(this);
        rlPushPay.setOnClickListener(this);
        rlPushReturn.setOnClickListener(this);
        rlPushVoucher.setOnClickListener(this);
    }

    @Override
    public void initData() {
        finalHttp = new FinalHttp();
//        pushInfo = (PushInfo) getIntent().getExtras().getSerializable("pushInfo");
//        if(Integer.parseInt(pushInfo.push_switch) == Constant.PUSH_ON) {
//            isPushAll = true;
//        }
//        if(Integer.parseInt(pushInfo.consume_msg) == Constant.PUSH_ON) {
//            isPushPay = true;
//        }
//        if(Integer.parseInt(pushInfo.refund_msg) == Constant.PUSH_ON) {
//            isPushReturn = true;
//        }
//        if(Integer.parseInt(pushInfo.voucher_msg) == Constant.PUSH_ON) {
//            isPushVoucher = true;
//        }
        if(AllStatus == Constant.PUSH_ON) {
            //总开关开
            ivPushAll.setImageResource(R.drawable.ic_push_open_checked);
            if(PayStatus == Constant.PUSH_ON) {
                ivPushPay.setImageResource(R.drawable.ic_push_open_checked);
            } else {
                ivPushPay.setImageResource(R.drawable.ic_push_open_check);
            }
            if(ReturnStatus == Constant.PUSH_ON) {
                ivPushReturn.setImageResource(R.drawable.ic_push_open_checked);
            } else {
                ivPushReturn.setImageResource(R.drawable.ic_push_open_check);
            }
            if(VoucherStatus == Constant.PUSH_ON) {
                ivPushVoucher.setImageResource(R.drawable.ic_push_open_checked);
            } else {
                ivPushVoucher.setImageResource(R.drawable.ic_push_open_check);
            }
        } else {
            //总开关关
            ivPushAll.setImageResource(R.drawable.ic_push_close_check);
            if(PayStatus == Constant.PUSH_ON) {
                ivPushPay.setImageResource(R.drawable.ic_push_close_checked);
            } else {
                ivPushPay.setImageResource(R.drawable.ic_push_close_check);
            }
            if(ReturnStatus == Constant.PUSH_ON) {
                ivPushReturn.setImageResource(R.drawable.ic_push_close_checked);
            } else {
                ivPushReturn.setImageResource(R.drawable.ic_push_close_check);
            }
            if(VoucherStatus == Constant.PUSH_ON) {
                ivPushVoucher.setImageResource(R.drawable.ic_push_close_checked);
            } else {
                ivPushVoucher.setImageResource(R.drawable.ic_push_close_check);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlPushAll:
                //推送总开关
                if(AllStatus == Constant.PUSH_ON) {
                    AllStatus = Constant.PUSH_OFF;
                } else {
                    AllStatus = Constant.PUSH_ON;
                }
                savePreferences(Constant.KEY_PUSH_ALL, AllStatus);
                UploadAdapter_Edit(AllStatus + "", PayStatus + "", ReturnStatus + "", VoucherStatus + "");
                break;
            case R.id.rlPushPay:
                //消费确认
                if(AllStatus == Constant.PUSH_ON) {
                    if(PayStatus == Constant.PUSH_ON) {
                        PayStatus = Constant.PUSH_OFF;
                    } else {
                        PayStatus = Constant.PUSH_ON;
                    }
                    savePreferences(Constant.KEY_PUSH_PAY, PayStatus);
                    UploadAdapter_Edit(AllStatus + "", PayStatus + "", ReturnStatus + "", VoucherStatus + "");
                }
                break;
            case R.id.rlPushReturn:
                //退款提醒
                if(AllStatus == Constant.PUSH_ON) {
                    if(ReturnStatus == Constant.PUSH_ON) {
                        ReturnStatus = Constant.PUSH_OFF;
                    } else {
                        ReturnStatus = Constant.PUSH_ON;
                    }
                    savePreferences(Constant.KEY_PUSH_RETURN, ReturnStatus);
                    UploadAdapter_Edit(AllStatus + "", PayStatus + "", ReturnStatus + "", VoucherStatus + "");
                }
                break;
            case R.id.rlPushVoucher:
                //获得礼券
                if(AllStatus == Constant.PUSH_ON) {
                    if(VoucherStatus == Constant.PUSH_ON) {
                        VoucherStatus = Constant.PUSH_OFF;
                    } else {
                        VoucherStatus = Constant.PUSH_ON;
                    }
                    savePreferences(Constant.KEY_PUSH_VOUCHER, VoucherStatus);
                    UploadAdapter_Edit(AllStatus + "", PayStatus + "", ReturnStatus + "", VoucherStatus + "");
                }
                break;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess){
                            initData();
                        }
                    }
                    break;
                case FAILURE:
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

    /** 编辑个人信息请求*/
    private void UploadAdapter_Edit(String allStatus, String payStatus, String returnStatus, String voucherStatus) {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EDIT_MEMBER_INFO);
        ajaxParams.put("token", token);
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_Edit(allStatus, payStatus, returnStatus, voucherStatus));
        Log.e(TAG, Constant.REQUEST + API.EDIT_MEMBER_INFO + "\n" + ajaxParams.toString());
        finalHttp.post(API.server,  ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                closeProgressDialog();
                prompt(getResources().getString(R.string.request_time_out));
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
                if(!StringUtils.isBlank(t)){
                    Log.e(TAG, Constant.RESULT + API.EDIT_MEMBER_INFO + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(FAILURE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * 编辑个人信息
     * member_id	会员ID
     * @return json
     */
    private String makeJsonText_Edit(String allStatus, String payStatus, String returnStatus, String voucherStatus) {
        JSONObject json = new JSONObject();
        JSONObject jsonPushInfo = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            jsonPushInfo.put("push_switch", allStatus);
            jsonPushInfo.put("consume_msg", payStatus);
            jsonPushInfo.put("refund_msg", returnStatus);
            jsonPushInfo.put("voucher_msg", voucherStatus);
            json.put("push_info", jsonPushInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 本类存储至本地属性*/
    public void savePreferences(String key, int value){
        PreferencesUtils.putInt(context, key, value);
    }
}
