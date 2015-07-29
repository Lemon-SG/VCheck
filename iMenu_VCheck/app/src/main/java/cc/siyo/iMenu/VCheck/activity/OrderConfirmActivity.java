package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.pay.PayResult;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.OrderInfo;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/3 9:31.
 * Desc:确认订单页面
 */
public class OrderConfirmActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "OrderConfirmActivity";
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 产品标题显示*/
    @ViewInject(id = R.id.tv_order_confirm_title)private TextView tv_order_confirm_title;
    /** 产品单价显示*/
    @ViewInject(id = R.id.tv_order_confirm_price)private TextView tv_order_confirm_price;
    /** 产品数量显示*/
    @ViewInject(id = R.id.tv_order_confirm_count)private TextView tv_order_confirm_count;
    /** 产品总价显示*/
    @ViewInject(id = R.id.tv_order_confirm_total_price)private TextView tv_order_confirm_total_price;
    /** 产品优惠券可用数量显示*/
    @ViewInject(id = R.id.tv_order_confirm_gift_count)private TextView tv_order_confirm_gift_count;
    /** 还需支付金额显示*/
    @ViewInject(id = R.id.tv_order_confirm_need_pay)private TextView tv_order_confirm_need_pay;
    /** 微信支付方式选择layout*/
    @ViewInject(id= R.id.rl_payment_weChat)private RelativeLayout rl_payment_weChat;
    /** 微信复选框显示*/
    @ViewInject(id= R.id.iv_payment_weChat_choose)private ImageView iv_payment_weChat_choose;
    /** 支付宝支付方式选择layout*/
    @ViewInject(id= R.id.rl_payment_Alipay)private RelativeLayout rl_payment_Alipay;
    /** 支付宝复选框显示*/
    @ViewInject(id= R.id.iv_payment_Alipay_choose)private ImageView iv_payment_Alipay_choose;
    /** 底部还需支付金额显示*/
    @ViewInject(id= R.id.tv_order_confirm_needPay_price)private TextView tv_order_confirm_needPay_price;
    /** 立即支付按钮*/
    @ViewInject(id= R.id.tv_orderConfirm_nowPay)private TextView tv_orderConfirm_nowPay;
    /** 选择礼券layout*/
    @ViewInject(id= R.id.rl_order_confirm_gift)private RelativeLayout rl_order_confirm_gift;
    /** 礼券使用后显示layout*/
    @ViewInject(id= R.id.rl_order_confirm_gift_spend)private RelativeLayout rl_order_confirm_gift_spend;
    /** 使用礼券名称显示*/
    @ViewInject(id = R.id.tv_order_confirm_voucher_name)private TextView tv_order_confirm_voucher_name;
    /** 使用礼券金额*/
    @ViewInject(id = R.id.tv_order_confirm_voucher_discount)private TextView tv_order_confirm_voucher_discount;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 订单实体*/
    private OrderInfo orderInfo;
    /** 支付方式*/
    private String paymentCode;
    /** 编辑结算信息成功标石*/
    private static final int CHECKOUT_SUCCESS = 1000;
    /** 生成支付数据成功标石*/
    private static final int GENERATE_DATA_SUCCESS = 2000;
    /** 提交支付订单成功标石*/
    private static final int SUBMIT_PAY_ORDER_SUCCESS = 3000;
    /** 获取礼券列表成功标石*/
    private static final int GET_VOUCHER_LIST_SUCCESS = 5000;
    /** 失败标石*/
    private static final int ALL_FALSE = 4000;
    /** 支付宝返回结果标石*/
    private static final int SDK_PAY_FLAG = 1;
    /** 微信支付 */
    private IWXAPI api;

    @Override
    public int getContentView() {
        return R.layout.activity_order_confirm;
    }

    @Override
    public void initView() {
        finalHttp = new FinalHttp();
        orderInfo = (OrderInfo) getIntent().getExtras().getSerializable("orderInfo");
        rl_order_confirm_gift.setOnClickListener(this);
        rl_payment_Alipay.setOnClickListener(this);
        rl_payment_weChat.setOnClickListener(this);
        tv_orderConfirm_nowPay.setOnClickListener(this);
        topbar.settitleViewText("确认订单");
        topbar.setHiddenButton(TopBar.RIGHT_BUTTON);
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        paymentCode = Constant.PAMENT_CODE_ALIPAY;
        Upload(BaseAjaxParams(API.CHECKOUT, makeJsonTextCheckOut()), CHECKOUT_SUCCESS);
        Upload(BaseAjaxParams(API.GET_VOUCHER_LIST, makeJsonText()), GET_VOUCHER_LIST_SUCCESS);
    }

    @Override
    public void initData() {
        tv_order_confirm_title.setText(orderInfo.menu.menu_name);
        tv_order_confirm_price.setText(orderInfo.menu.price.special_price == "" ? orderInfo.menu.price.original_price : orderInfo.menu.price.special_price + orderInfo.totalPrice.price_unit);
        tv_order_confirm_count.setText(orderInfo.menu.count);
        tv_order_confirm_total_price.setText(orderInfo.totalPrice.special_price == "" ? orderInfo.totalPrice.original_price : orderInfo.totalPrice.special_price + orderInfo.totalPrice.price_unit);
        //TODO 还需支付金额应为合计-优惠券金额，暂无优惠券，后续完善
        tv_order_confirm_need_pay.setText(tv_order_confirm_total_price.getText().toString());
        tv_order_confirm_needPay_price.setText(orderInfo.totalPrice.special_price == "" ? orderInfo.totalPrice.original_price : orderInfo.totalPrice.special_price);

        if(!StringUtils.isBlank(orderInfo.paymentInfo.payment_code)) {
            //有选中的支付方式
            if(orderInfo.paymentInfo.payment_code.equals(Constant.PAMENT_CODE_ALIPAY)) {
                //选中支付宝
                iv_payment_Alipay_choose.setImageResource(R.drawable.ic_collect_red);
                iv_payment_weChat_choose.setImageResource(R.drawable.ic_collect_black);
            } else {
                //选中微信支付
                iv_payment_Alipay_choose.setImageResource(R.drawable.ic_collect_black);
                iv_payment_weChat_choose.setImageResource(R.drawable.ic_collect_red);
            }
        } else {
            //无选中的支付方式
            iv_payment_Alipay_choose.setImageResource(R.drawable.ic_collect_red);
            iv_payment_weChat_choose.setImageResource(R.drawable.ic_collect_black);
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECKOUT_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        orderInfo = new OrderInfo().parse(data.optJSONObject("order_info"));
                        initData();
                    }
                    break;
                case GET_VOUCHER_LIST_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        JSONArray voucherList = data.optJSONArray("voucher_list");
                        tv_order_confirm_gift_count.setText(voucherList.length() + "可使用");
                    }
                    break;
                case GENERATE_DATA_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        final String payInfo = data.optString("payment_order_param");
                        if(paymentCode.equals(Constant.PAMENT_CODE_ALIPAY)) {
                            Runnable payRunnable = new Runnable() {

                                @Override
                                public void run() {
                                    // 构造PayTask 对象
                                    PayTask alipay = new PayTask(OrderConfirmActivity.this);
                                    // 调用支付接口，获取支付结果
                                    String result = alipay.pay(payInfo);
                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    handler.sendMessage(msg);
                                }
                            };
                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        }
                        if(paymentCode.equals(Constant.PAMENT_CODE_WECHAT)) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(payInfo);
                                sendPayReq(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case SUBMIT_PAY_ORDER_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(jsonStatus.isSuccess && data != null && data.length() > 0) {
                            if(data.optJSONObject("order_info") != null && data.optJSONObject("order_info").length() > 0) {
                                OrderInfo orderInfo = new OrderInfo().parse(data.optJSONObject("order_info"));
                                Intent intent = new Intent(OrderConfirmActivity.this, PayResultActivity.class);
                                intent.putExtra("orderInfo", orderInfo);
                                startActivity(intent);
                                finish();
                            }
                        }else {
                            Intent intent = new Intent(OrderConfirmActivity.this, PayResultActivity.class);
                            intent.putExtra("orderInfo", orderInfo);
                            startActivity(intent);
                            finish();
                        }
                    }
                    break;
                case ALL_FALSE:
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
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(OrderConfirmActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Upload(BaseAjaxParams(API.SUBMIT_PAY_ORDER, makeJsonTextSubmitOrder(resultInfo)), SUBMIT_PAY_ORDER_SUCCESS);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(OrderConfirmActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                            Upload(BaseAjaxParams(API.SUBMIT_PAY_ORDER, makeJsonTextSubmitOrder(resultInfo)), SUBMIT_PAY_ORDER_SUCCESS);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(OrderConfirmActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_order_confirm_gift:
                //选择礼券
                Intent intent = new Intent(context, VoucherListActivity.class);
                intent.putExtra(Constant.INTENT_VOUCHER_TYPE, Constant.INTENT_VOUCHER_CHOOSE);
                startActivityForResult(intent, RESULT_OK);
                break;
            case R.id.rl_payment_Alipay:
                //支付宝选择
                paymentCode = Constant.PAMENT_CODE_ALIPAY;
                Upload(BaseAjaxParams(API.CHECKOUT, makeJsonTextCheckOut()), CHECKOUT_SUCCESS);
                break;
            case R.id.rl_payment_weChat:
                //微信支付选择
                paymentCode = Constant.PAMENT_CODE_WECHAT;
                Upload(BaseAjaxParams(API.CHECKOUT, makeJsonTextCheckOut()), CHECKOUT_SUCCESS);
                break;
            case R.id.tv_orderConfirm_nowPay:
                //立即支付
                Upload(BaseAjaxParams(API.GENERATE_PAY_DATA, makeJsonTextGenerateData()), GENERATE_DATA_SUCCESS);
                break;
        }
    }

    /** 公用请求*/
    private void Upload(AjaxParams ajaxParams, final int SuccessCode) {
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
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
                if (!StringUtils.isBlank(t)) {
                    switch (SuccessCode) {
                        case CHECKOUT_SUCCESS:
                            Log.e(TAG, Constant.RESULT + API.CHECKOUT + "\n" + t.toString());
                            break;
                        case GENERATE_DATA_SUCCESS:
                            Log.e(TAG, Constant.RESULT + API.GENERATE_PAY_DATA + "\n" + t.toString());
                            break;
                        case SUBMIT_PAY_ORDER_SUCCESS:
                            Log.e(TAG, Constant.RESULT + API.SUBMIT_PAY_ORDER + "\n" + t.toString());
                            break;
                    }
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(SuccessCode, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(ALL_FALSE, BaseJSONData(t)));
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
    private String makeJsonTextCheckOut() {
        JSONObject json = new JSONObject();
        JSONObject checkoutInfoJson = new JSONObject();
        try {
            checkoutInfoJson.put("payment_code", paymentCode);
            //TODO 优惠券
//            checkoutInfoJson.put("coupon_id", "");
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("order_id", orderInfo.order_id);
            json.put("checkout_info", checkoutInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * member_id	会员ID
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("pagination", makeJsonPageText(1, Integer.MAX_VALUE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 生成支付数据接口封装
     * member_id	会员ID
     * order_id	订单ID
     * @return
     */
    private String makeJsonTextGenerateData() {
        JSONObject json = new JSONObject();
        try {
            //TODO 优惠券
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("order_id", orderInfo.order_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 提交支付订单接口封装
     * member_id	会员ID
     * order_id	订单ID
     * payment_result	支付平台返回参数
     * @return
     */
    private String makeJsonTextSubmitOrder(String payment_result) {
        JSONObject json = new JSONObject();
        try {
            //TODO 优惠券
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("order_id", orderInfo.order_id);
            json.put("payment_result", payment_result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 返回公共AjaxParams*/
    private AjaxParams BaseAjaxParams(String API, String jsonText) {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("route", API);
        ajaxParams.put("token", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", jsonText);
        Log.e(TAG, Constant.REQUEST + API + "\n" + ajaxParams.toString());
        return ajaxParams;
    }

    private void sendPayReq(JSONObject obj) {
        api = WXAPIFactory.createWXAPI(context, obj.optString("appid"));
        if(!isWXAppInstalledAndSupported(api)){
            closeProgressDialog();
            prompt("微信客户端未安装，请确认");
        }else{
            PayReq req = new PayReq();
            req.appId = obj.optString("appid");
            req.partnerId = obj.optString("partnerid");
            req.prepayId = obj.optString("prepayid");
            req.packageValue = "Sign=WXPay";
            req.nonceStr = obj.optString("noncestr");
            req.timeStamp = obj.optString("timestamp");
            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", req.appId));
            signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
            signParams.add(new BasicNameValuePair("package", req.packageValue));
            signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
            signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
            signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
            req.sign = obj.optString("paySign");
            Log.e(TAG, "orion->" + signParams.toString());
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.registerApp(req.appId);
            api.sendReq(req);
        }
    }

    /**判断手机是否安装微信客户端*/
    private boolean isWXAppInstalledAndSupported(IWXAPI api) {
        boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled()
                && api.isWXAppSupportAPI();
        if (!sIsWXAppInstalledAndSupported) {
            Log.w("TAG", "~~~~~~~~~~~~~~微信客户端未安装，请确认");
        }
        return sIsWXAppInstalledAndSupported;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Constant.isWeChetPay) {
            Constant.isWeChetPay = false;
            //微信支付返回
            Upload(BaseAjaxParams(API.SUBMIT_PAY_ORDER, makeJsonTextSubmitOrder("")), SUBMIT_PAY_ORDER_SUCCESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_OK && resultCode == Constant.INTENT_VOUCHER_CHOOSE) {
            if(data != null) {

            }
        }
    }
}
