package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import cc.siyo.iMenu.VCheck.adapter.VoucherAdapter;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.Member;
import cc.siyo.iMenu.VCheck.model.OrderInfo;
import cc.siyo.iMenu.VCheck.model.ShareInvite;
import cc.siyo.iMenu.VCheck.model.VoucherInfo;
import cc.siyo.iMenu.VCheck.util.AnimationController;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.RefreshListView;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/27 15:50.
 * Desc:我的礼券界面
 */
public class VoucherListActivity extends BaseActivity {

    private static final String TAG = "VoucherListActivity";
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 当前可用礼券数量*/
    @ViewInject(id = R.id.tvVoucherCount)private TextView tvVoucherCount;
    /** 兑换礼券按钮*/
    @ViewInject(id = R.id.rlVoucherBottom)private RelativeLayout rlVoucherBottom;
    /** 列表*/
    @ViewInject(id = R.id.list_voucher)private ListView list_voucher;
    /** 输入礼券码*/
    @ViewInject(id = R.id.etVoucherCode)private EditText etVoucherCode;
    /** 兑换按钮*/
    @ViewInject(id = R.id.tvVoucherExchange)private TextView tvVoucherExchange;
    /** 兑换礼券layout*/
    @ViewInject(id = R.id.rlVoucherCode)private RelativeLayout rlVoucherCode;
    /** 可使用礼券显示layout*/
    @ViewInject(id = R.id.rlVoucherCount)private RelativeLayout rlVoucherCount;
    /** 不使用礼券按钮*/
    @ViewInject(id = R.id.tvVoucherNoChoose)private TextView tvVoucherNoChoose;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    private static final int GET_VOUCHER_LIST_SUCCESS = 100;
    private static final int EXCHANGE_VOUCHER_SUCCESS = 300;
    private static final int CHECKOUT_SUCCESS = 400;
    private static final int GET_VOUCHER_LIST_FALSE = 200;
    /** 适配器*/
    private VoucherAdapter voucherAdapter;
    /** 数据源*/
    private List<VoucherInfo> voucherInfoList;
//    private int page = Constant.PAGE;
//    private int pageSize = Constant.PAGE_SIZE;
//    /** 是否到最后一页*/
//    boolean doNotOver = true;
//    /** 是否已经提醒过一遍*/
//    boolean isTip = false;
//    /** 是否是下拉刷新，清空数据*/
//    private boolean isPull = false;
//    private AnimationController animationController;
    /** 此页面的操作类型*/
    private int VoucherDoType = Constant.INTENT_VOUCHER_SHOW;
    /** 当前请求多少张礼券*/
    private int voucherCount = 20;

    @Override
    public int getContentView() {
        return R.layout.activity_voucher_list;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("我的礼券");
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        list_voucher.addFooterView((LayoutInflater.from(context)).inflate(R.layout.list_item_footview, null));
//        animationController = new AnimationController();
    }

    @Override
    public void initData() {
        if(getIntent() != null && getIntent().getExtras() != null) {
            VoucherDoType = getIntent().getExtras().getInt(Constant.INTENT_VOUCHER_TYPE);
            switch (VoucherDoType) {
                case Constant.INTENT_VOUCHER_CHOOSE:
                    //选择礼券操作
                    tvVoucherNoChoose.setVisibility(View.VISIBLE);
                    final String orderId = getIntent().getExtras().getString("orderId");
                    final String paymentCode = getIntent().getExtras().getString("paymentCode");
                    list_voucher.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            UploadAdapter_checkout(orderId, paymentCode, voucherAdapter.getDataList().get(position - 1).voucher_member_id);
                        }
                    });
                    break;
                case Constant.INTENT_VOUCHER_SHOW:
                    //展示操作
                    tvVoucherNoChoose.setVisibility(View.GONE);
                    list_voucher.setOnItemClickListener(null);
                    break;
            }
            voucherCount = getIntent().getExtras().getInt("voucherCount");
        }
//        page = Constant.PAGE;
        finalHttp = new FinalHttp();
//        isPull = true;
        if(!StringUtils.isBlank(PreferencesUtils.getString(context, Constant.KEY_TOKEN))) {
            UploadAdapter(voucherCount);
        }
        voucherAdapter = new VoucherAdapter(VoucherListActivity.this, R.layout.list_item_voucher);
        list_voucher.setAdapter(voucherAdapter);

        tvVoucherExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //兑换
                if (!StringUtils.isBlank(etVoucherCode.getText().toString())) {
                    UploadAdapter_exchange();
                } else {
                    prompt("请输入兑换码");
                }
            }
        });
        rlVoucherBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //兑换按钮
                if(rlVoucherCode.isShown()) {
                    rlVoucherCode.setVisibility(View.INVISIBLE);
                    rlVoucherCount.setVisibility(View.VISIBLE);
                } else {
//                    animationController.slideFadeIn(rlVoucherCode, 100, 0);
                    rlVoucherCode.setVisibility(View.VISIBLE);
                    rlVoucherCount.setVisibility(View.INVISIBLE);
                }
            }
        });
        tvVoucherNoChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不使用礼券
                final String orderId = getIntent().getExtras().getString("orderId");
                final String paymentCode = getIntent().getExtras().getString("paymentCode");
                UploadAdapter_checkout(orderId, paymentCode, "");
            }
        });

//        list_voucher.setOnLoadMoreListenter(new RefreshListView.OnLoadMoreListener() {
//
//            public void onLoadMore() {
//                isPull = false;
//                if (doNotOver) {
//                    page++;
//                    UploadAdapter();
//                } else {
//                    list_voucher.onLoadMoreComplete();
//                    if (!isTip) {
//                        isTip = true;
//                        prompt("已经到底了");
//                    }
//                }
//            }
//        });
//        list_voucher.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
//
//            public void onRefresh() {
//                page = Constant.PAGE;
//                isPull = true;
//                UploadAdapter();
//            }
//        });
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_VOUCHER_LIST_SUCCESS:
                    closeProgressDialog();
//                    list_voucher.onRefreshComplete();
//                    list_voucher.onLoadMoreComplete();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        JSONArray voucherList = data.optJSONArray("voucher_list");
                        if(voucherList != null && voucherList.length() > 0){
                            voucherInfoList = new ArrayList<>();
                            List<VoucherInfo> voucherInfos = new ArrayList<>();//筛选出可使用的优惠券
                            for (int i = 0; i < voucherList.length(); i++) {
                                VoucherInfo voucherInfo = new VoucherInfo().parse(voucherList.optJSONObject(i));
                                voucherInfoList.add(voucherInfo);
                                if(Integer.parseInt(voucherInfo.voucher_status) == Constant.VOUCHER_STATUS_NO_SPEND) {
                                    voucherInfos.add(voucherInfo);
                                }
                            }
//                            if(isPull) {
                                voucherAdapter.getDataList().clear();
//                            }
                            voucherAdapter.getDataList().addAll(voucherInfoList);
                            voucherAdapter.notifyDataSetChanged();
                            tvVoucherCount.setText(voucherInfos.size() + "张可使用礼券");
//                            if(jsonStatus.pageInfo != null) {
//                                String more = jsonStatus.pageInfo.more;
//                                if(more.equals("1")) {
//                                    //有下一页
//                                    doNotOver = true;
//                                } else {
//                                    //最后一页
//                                    doNotOver = false;
//                                }
//                            }
                        }
                    }
                    break;
                case EXCHANGE_VOUCHER_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null) {
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess) {
                            //TODO 兑换礼券成功后的操作
                            UploadAdapter(voucherCount);
                        }
                    }
                    break;
                case CHECKOUT_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        OrderInfo orderInfo = new OrderInfo().parse(data.optJSONObject("order_info"));
                        Intent intent = new Intent(context, OrderConfirmActivity.class);
                        intent.putExtra("orderInfo", orderInfo);
                        if(!StringUtils.isBlank(orderInfo.voucherInfo.voucher_member_id)) {
                            //礼券使用成功
                            setResult(Constant.RESULT_VOUCHER_SPEND, intent);
                            finish();
                        } else {
                            //不使用
                            setResult(Constant.RESULT_VOUCHER_NO_SPEND, intent);
                            finish();
                        }
                    }
                    break;
                case GET_VOUCHER_LIST_FALSE:
                    closeProgressDialog();
//                    list_voucher.onRefreshComplete();
//                    list_voucher.onLoadMoreComplete();
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

    /** 获取礼券列表请求*/
    private void UploadAdapter(int voucherCount) {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_VOUCHER_LIST);
        ajaxParams.put("token", PreferencesUtils.getString(context, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText(voucherCount));
        Log.e(TAG, Constant.REQUEST + API.GET_VOUCHER_LIST + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
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
                    Log.e(TAG, Constant.RESULT + API.GET_VOUCHER_LIST + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(GET_VOUCHER_LIST_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(GET_VOUCHER_LIST_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 激活兑换码请求*/
    private void UploadAdapter_exchange() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EXCHANGE_VOUCHER);
        ajaxParams.put("token", PreferencesUtils.getString(context, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_exchange());
        Log.e(TAG, Constant.REQUEST + API.EXCHANGE_VOUCHER + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
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
                    Log.e(TAG, Constant.RESULT + API.EXCHANGE_VOUCHER + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(EXCHANGE_VOUCHER_SUCCESS, BaseJSONData(t)));
                    } else {
//                        handler.sendMessage(handler.obtainMessage(GET_VOUCHER_LIST_FALSE, BaseJSONData(t)));
                        closeProgressDialog();
                        prompt(jsonStatus.error_desc);
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 编辑结算信息请求*/
    private void UploadAdapter_checkout(String order_id, String paymentCode, String voucher_member_id) {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.CHECKOUT);
        ajaxParams.put("token", PreferencesUtils.getString(context, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonTextCheckOut(order_id, paymentCode, voucher_member_id));
        Log.e(TAG, Constant.REQUEST + API.CHECKOUT + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
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
                    Log.e(TAG, Constant.RESULT + API.CHECKOUT + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(CHECKOUT_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(GET_VOUCHER_LIST_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }
    /**
     * 编辑结算信息封装
     * member_id	会员ID
     * checkout_info	payment_code	支付方式code
     *                  coupon_id	优惠ID
     * order_id	订单ID
     * @return
     */
    private String makeJsonTextCheckOut(String order_id, String paymentCode, String voucher_member_id) {
        JSONObject json = new JSONObject();
        JSONObject checkoutInfoJson = new JSONObject();
        try {
            checkoutInfoJson.put("payment_code", paymentCode);
            checkoutInfoJson.put("voucher_member_id", voucher_member_id);
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("order_id", order_id);
            json.put("checkout_info", checkoutInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
    /***
     * member_id	会员ID
     * code_no  礼券吗
     * @return json
     */
    private String makeJsonText_exchange() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("code_no", etVoucherCode.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * member_id	会员ID
     * @return json
     */
    private String makeJsonText(int voucherCount) {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("pagination", makeJsonPageText(1, voucherCount));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.RESQUEST_CODE) {
            switch (resultCode) {
                case Constant.RESULT_CODE_LOGIN:
                    Log.e(TAG, "resultCode ->" + resultCode);
                    UploadAdapter(voucherCount);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(StringUtils.isBlank(PreferencesUtils.getString(context, Constant.KEY_TOKEN))
                && StringUtils.isBlank(PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID))) {
            //未登录状态
            startActivityForResult(new Intent(context, LoginActivity.class), Constant.RESQUEST_CODE);
        }
    }
}
