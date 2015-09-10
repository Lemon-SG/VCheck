package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.MemberOrder;
import cc.siyo.iMenu.VCheck.model.OrderInfo;
import cc.siyo.iMenu.VCheck.model.ReturnInfo;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.TimeUtil;
import cc.siyo.iMenu.VCheck.view.PromptDialog;
import cc.siyo.iMenu.VCheck.view.TopBar;
/**
 * Created by Lemon on 2015/7/8 16:16.
 * Desc:订单详情页面
 */
public class OrderDetailActivity extends BaseActivity {

    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 菜品layout*/
    @ViewInject(id = R.id.rl_orderDetail_menu)private RelativeLayout rl_orderDetail_menu;
    /** 底部整体layout*/
    @ViewInject(id = R.id.rl_bottom)private RelativeLayout rl_bottom;
    /** 退款底部layout*/
    @ViewInject(id = R.id.rl_orderDetail_return_bottom)private RelativeLayout rl_orderDetail_return_bottom;
    /** 菜品图片显示*/
    @ViewInject(id = R.id.iv_orderDetail_menu)private ImageView iv_orderDetail_menu;
    /** 菜品名称显示*/
    @ViewInject(id = R.id.tv_orderDetail_name)private TextView tv_orderDetail_name;
    /** 菜品价格显示*/
    @ViewInject(id = R.id.tv_orderDetail_price)private TextView tv_orderDetail_price;
    /** 商家名称显示*/
    @ViewInject(id = R.id.tv_orderDetail_store_name)private TextView tv_orderDetail_store_name;
    /** 商家地址显示*/
    @ViewInject(id = R.id.tv_orderDetail_address)private TextView tv_orderDetail_address;
    /** 商家电话显示*/
    @ViewInject(id = R.id.tv_orderDetail_store_phone)private TextView tv_orderDetail_store_phone;
    /** 商家营业时间显示*/
    @ViewInject(id = R.id.tv_orderDetail_time)private TextView tv_orderDetail_time;
    /** 订单编号显示*/
    @ViewInject(id = R.id.tv_orderDetail_order_no)private TextView tv_orderDetail_order_no;
    /** 下单手机显示*/
    @ViewInject(id = R.id.tv_orderDetail_order_phone)private TextView tv_orderDetail_order_phone;
    /** 下单时间显示*/
    @ViewInject(id = R.id.tv_orderDetail_order_time)private TextView tv_orderDetail_order_time;
    /** 订单单价显示*/
    @ViewInject(id = R.id.tv_orderDetail_order_price)private TextView tv_orderDetail_order_price;
    /** 订单数量显示*/
    @ViewInject(id = R.id.tv_orderDetail_count)private TextView tv_orderDetail_count;
    /** 订单合计价格显示*/
    @ViewInject(id = R.id.tv_orderDetail_order_totprice)private TextView tv_orderDetail_order_totprice;
    /** 订单使用优惠券显示*/
    @ViewInject(id = R.id.tv_orderDetail_order_voucher)private TextView tv_orderDetail_order_voucher;
    /** 订单合计还需支付价格显示*/
    @ViewInject(id = R.id.tv_orderDetail_order_totPrice)private TextView tv_orderDetail_order_totPrice;
    /** 取消订单按钮*/
    @ViewInject(id = R.id.tv_orderDetail_cancel)private TextView tv_orderDetail_cancel;
    /** 底部分割线显示*/
    @ViewInject(id = R.id.tv_orderDetail_bottom_driver)private TextView tv_orderDetail_bottom_driver;
    /** 底部立即支付layout*/
    @ViewInject(id = R.id.ll_orderDetail_buy_now)private LinearLayout ll_orderDetail_buy_now;
    /** 底部合计价格显示*/
    @ViewInject(id = R.id.tv_orderDetail_bottom_price)private TextView tv_orderDetail_bottom_price;
    /** 立即支付按钮*/
    @ViewInject(id = R.id.tv_orderDetail_submitOrder)private TextView tv_orderDetail_submitOrder;
    /** 退款状态显示*/
    @ViewInject(id = R.id.tv_order_type_description)private TextView tv_order_type_description;
    /** 申请退款的时间显示*/
    @ViewInject(id = R.id.tv_date_added)private TextView tv_date_added;
    /** 订单详情使用须知显示*/
    @ViewInject(id = R.id.tv_order_detail_notice)private TextView tv_order_detail_notice;
    /** 退款方式显示*/
    @ViewInject(id = R.id.tv_return_action_description)private TextView tv_return_action_description;
    /** 消费码显示*/
    @ViewInject(id = R.id.tvOrderDetailPayCode)private TextView tvOrderDetailPayCode;
    /** 消费码有效期显示*/
    @ViewInject(id = R.id.tvOrderDetailConsumeData)private TextView tvOrderDetailConsumeData;
    /** 消费码layout*/
    @ViewInject(id = R.id.llOrderDetailConsume)private LinearLayout llOrderDetailConsume;
    /** 退款进度描述显示*/
    @ViewInject(id = R.id.tv_description)private TextView tv_description;
    private static final String TAG = "OrderDetailActivity";
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    private static final int GET_ORDER_DETAIL_SUCCESS = 100;
    private static final int GET_ORDER_DETAIL_FALSE = 200;
    private static final int EDIT_ORDER_SUCCESS = 300;
    private static final int GET_RETURN_ORDER_DETAIL_SUCCESS = 500;
    /** 订单ID*/
    private String orderId;
    private FinalBitmap finalBitmap;
    private PromptDialog promptDialog;
    private MemberOrder memberOrder;
    /** 当前订单类型*/
    private String orderType;
    /** 当页面关闭的时候标石订单状态是否被改变，取决于申请退款，用于返回到列表去重新加载列表*/
    private boolean changeOrderStatus= false;

    @Override
    public int getContentView() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("订单详情");
        topbar.setHiddenButton(TopBar.RIGHT_BUTTON);
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                if (changeOrderStatus) {
                    //订单状态更改，返回需重新加载
                    setResult(Constant.RESULT_CODE_ORDER_DETAIL);
                }
                finish();
            }
        });
    }

    @Override
    public void initData() {
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.mipmap.default_menu);
        finalBitmap.configLoadfailImage(R.mipmap.default_menu);
        finalHttp = new FinalHttp();
        orderId = getIntent().getExtras().getString("orderId");
        if(StringUtils.isBlank(PreferencesUtils.getString(context, Constant.KEY_TOKEN))
                && StringUtils.isBlank(PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID))) {
            //未登录状态
            startActivityForResult(new Intent(context, LoginActivity.class), Constant.RESQUEST_CODE);
        } else {
            UploadAdapter();
        }
        if(!StringUtils.isBlank(getIntent().getExtras().getString("orderType"))) {
            orderType = getIntent().getExtras().getString("orderType");
            if(orderType.equals(Constant.ORDER_TYPE_RETURN_OVER) || orderType.equals(Constant.ORDER_TYPE_RETURN_IN)) {
                //已退款 | 退款中
                UploadAdapter_ReturnDetail();
            }
        }
    }

    /** 显示数据*/
    private void showData(final MemberOrder memberOrder){
        if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_NO_PAY)) {
            //未付款订单,可取消，可删除，可支付订单，列表显示：等待付款
            showBottom(true, false, "取消订单");
            tv_orderDetail_cancel.setOnClickListener(new OnCancelOrderClickListener());
        }
        if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_PAY_NO_SPEND)) {
            //已支付未消费订单，只能申请退款，且不可删除，列表实现：等待消费
            if(memberOrder.order_info.consume_info != null && !StringUtils.isBlank(memberOrder.order_info.consume_info.consume_code)) {
                tvOrderDetailPayCode.setText(memberOrder.order_info.consume_info.consume_code);
                tvOrderDetailConsumeData.setText("有效期至:" + TimeUtil.FormatTime(memberOrder.order_info.consume_info.exprie_date, "yyyy-MM-dd"));
                llOrderDetailConsume.setVisibility(View.VISIBLE);
            }
            if(memberOrder.order_info.is_return.equals("1")) {
                //可退款
                showBottom(false, false, "申请退款");
                tv_orderDetail_cancel.setOnClickListener(new OnSubmitReturnClickListener());
            } else {
                //不可退款
                showBottom(false, false, "不可退款");
            }
        }
        if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_PAY_SPEND)) {
            //已支付已消费订单，只能删除，列表显示：已消费
            showBottom(false, false, "");
            if(memberOrder.order_info.consume_info != null && !StringUtils.isBlank(memberOrder.order_info.consume_info.consume_code)) {
                tvOrderDetailPayCode.setText(memberOrder.order_info.consume_info.consume_code);
                tvOrderDetailConsumeData.setText("有效期至:" + TimeUtil.FormatTime(memberOrder.order_info.consume_info.exprie_date, "yyyy-MM-dd"));
                llOrderDetailConsume.setVisibility(View.VISIBLE);
            }
        }
        if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_RETURN_IN)) {
            //退款中订单，无操作，且不可删除，列表显示：退款中
            showBottom(false, true, "");
            tv_order_type_description.setTextColor(getResources().getColor(R.color.orange_red));
            tv_order_type_description.setText(memberOrder.order_info.order_type_description);
        }
        if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_RETURN_OVER)) {
            //退款完成订单，只能删除，列表显示：已退款
            showBottom(false, true, "");
            tv_order_type_description.setTextColor(getResources().getColor(R.color.green));
            tv_order_type_description.setText(memberOrder.order_info.order_type_description);
        }
        if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_NO_PAY_TIMEOUT)) {
            //待付款过期订单，只能删除，列表显示：交易关闭
            showBottom(false, false, "");
        }
        if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_PAY_TIMEOUT)) {
            //已付款过期订单，只能申请退款，且不能删除，列表显示：订单已过期，请申请退款
            if(memberOrder.order_info.is_return.equals("1")) {
                //可退款
                showBottom(false, false, "申请退款");
                tv_orderDetail_cancel.setOnClickListener(new OnSubmitReturnClickListener());
            } else {
                //不可退款
                showBottom(false, false, "不可退款");
            }
        }
        rl_orderDetail_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到文章详情
                Intent intent = new Intent(OrderDetailActivity.this, DetailActivity.class);
                intent.putExtra("article_id", memberOrder.article_info.article_id);
                startActivity(intent);
            }
        });
        tv_orderDetail_submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //立即支付
                Intent intent = new Intent(OrderDetailActivity.this, OrderConfirmActivity.class);
                intent.putExtra("orderInfo", memberOrder.order_info);
                startActivity(intent);
            }
        });
        finalBitmap.display(iv_orderDetail_menu, memberOrder.article_info.article_image.source);
        tv_orderDetail_name.setText(memberOrder.order_info.menu.menu_name);
        tv_orderDetail_store_name.setText(memberOrder.article_info.store_info.store_name);
        tv_orderDetail_address.setText(memberOrder.article_info.store_info.address);
        tv_orderDetail_store_phone.setText(memberOrder.article_info.store_info.tel_1);
        tv_orderDetail_time.setText(memberOrder.article_info.store_info.hours);
        tv_orderDetail_order_no.setText(memberOrder.order_info.order_no);
        tv_orderDetail_order_phone.setText(memberOrder.order_info.mobile);
        tv_orderDetail_order_time.setText(memberOrder.order_info.create_date);
        tv_orderDetail_count.setText(memberOrder.order_info.menu.count);

        /** 菜品单价显示*/
        if(!StringUtils.isBlank(memberOrder.order_info.menu.price.special_price)
                && !memberOrder.order_info.menu.price.special_price.equals("0.0")) {
            //有优惠价格
            tv_orderDetail_order_price.setText(memberOrder.order_info.menu.price.special_price + memberOrder.order_info.menu.price.price_unit);
            tv_orderDetail_price.setText("单价:" + memberOrder.order_info.menu.price.special_price + memberOrder.order_info.menu.price.price_unit);
        } else {
            //无优惠价格
            tv_orderDetail_order_price.setText(memberOrder.order_info.menu.price.original_price + memberOrder.order_info.menu.price.price_unit);
            tv_orderDetail_price.setText("单价:" + memberOrder.order_info.menu.price.original_price + memberOrder.order_info.menu.price.price_unit);
        }
        /** 支付相关价格显示*/
        if(!StringUtils.isBlank(memberOrder.order_info.totalPrice.special_price) && !memberOrder.order_info.totalPrice.special_price.equals("0.0")){
            //优惠总价
            tv_orderDetail_order_totprice.setText(memberOrder.order_info.totalPrice.special_price + memberOrder.order_info.totalPrice.price_unit);
            tv_orderDetail_bottom_price.setText(memberOrder.order_info.totalPrice.special_price);
            //底部还需支付价格显示
            tv_orderDetail_order_totPrice.setText(memberOrder.order_info.totalPrice.special_price + memberOrder.order_info.menu.price.price_unit);
        }else {
            tv_orderDetail_order_totprice.setText(memberOrder.order_info.totalPrice.original_price + memberOrder.order_info.totalPrice.price_unit);
            tv_orderDetail_bottom_price.setText(memberOrder.order_info.totalPrice.original_price);
            //底部还需支付价格显示
            tv_orderDetail_order_totPrice.setText(memberOrder.order_info.totalPrice.original_price + memberOrder.order_info.menu.price.price_unit);
        }
        if(memberOrder.order_info.voucherInfo != null && !StringUtils.isBlank(memberOrder.order_info.voucherInfo.voucher_member_id)) {
            tv_orderDetail_order_voucher.setText("-" + memberOrder.order_info.voucherInfo.discount + "元");
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < memberOrder.article_info.tips_info.content.length; i++) {
            if(i + 1 == memberOrder.article_info.tips_info.content.length) {
                stringBuffer.append(" · " + memberOrder.article_info.tips_info.content[i]);
            } else {
                stringBuffer.append(" · " + memberOrder.article_info.tips_info.content[i] + "\n");
            }
        }
        tv_order_detail_notice.setText(stringBuffer);
    }

    /** 显示退款底部*/
    private void showReturnBottom(OrderInfo orderInfo, ReturnInfo returnInfo) {
        showBottom(false, true, "");
        tv_date_added.setText("申请:" + returnInfo.date_added);
        tv_return_action_description.setText(returnInfo.return_action_description.trim());
        if(orderInfo.order_type.equals(Constant.ORDER_TYPE_RETURN_OVER)) {
            tv_description.setText("您的款项已退回");
        }
        if(orderInfo.order_type.equals(Constant.ORDER_TYPE_RETURN_IN)) {
            tv_description.setText("预计两个工作日内完成");
        }
    }

    /***
     * 底部显示
     * @param isShowPayBottom 是否显示支付底部
     * @param isShowReturnBottom 是否显示退款底部
     * @param bottomText 最底部的操作按钮的文本内容，如为空，则不显示
     */
    private void showBottom(boolean isShowPayBottom, boolean isShowReturnBottom, String bottomText) {
        if(isShowPayBottom) {
            //显示支付底部
            rl_bottom.setVisibility(View.VISIBLE);
            ll_orderDetail_buy_now.setVisibility(View.VISIBLE);//显示支付底部
            tv_orderDetail_bottom_driver.setVisibility(View.VISIBLE);//显示支付底部分割线
            rl_orderDetail_return_bottom.setVisibility(View.GONE);//隐藏退款底部
        }
        if(isShowReturnBottom) {
            //显示退款底部
            rl_bottom.setVisibility(View.VISIBLE);
            ll_orderDetail_buy_now.setVisibility(View.GONE);//隐藏支付底部
            tv_orderDetail_bottom_driver.setVisibility(View.VISIBLE);//隐藏支付底部分割线
            rl_orderDetail_return_bottom.setVisibility(View.VISIBLE);//显示退款底部
        }
        if(!isShowPayBottom && !isShowReturnBottom) {
            //去掉整体底部
            rl_bottom.setVisibility(View.GONE);
            tv_orderDetail_bottom_driver.setVisibility(View.GONE);//隐藏支付底部分割线
        }
        if(!StringUtils.isBlank(bottomText)) {
            //显示底部按钮文本
            tv_orderDetail_cancel.setText(bottomText);
            tv_orderDetail_cancel.setVisibility(View.VISIBLE);
        } else {
            //去掉底部按钮
            tv_orderDetail_cancel.setVisibility(View.GONE);
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_RETURN_ORDER_DETAIL_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(jsonStatus.isSuccess && data != null){
                            ReturnInfo returnInfo = new ReturnInfo().parse(data.optJSONObject("return_info"));
                            OrderInfo orderInfo = new OrderInfo().parse(data.optJSONObject("order_info"));
                            showReturnBottom(orderInfo, returnInfo);
                        }
                    }
                    break;
                case GET_ORDER_DETAIL_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(jsonStatus.isSuccess && data != null){
                            memberOrder = new MemberOrder().parse(data.optJSONObject("member_order_info"));
                            if(StringUtils.isBlank(orderType)) {
                                //没有传递进来订单类型，则属于从推送进来的，因此需要判断订单类型是否为退款等
                                if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_RETURN_OVER)
                                        || memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_RETURN_IN)) {
                                    //需要请求获取退款详情来显示底部  已退款 | 退款中
                                    UploadAdapter_ReturnDetail();
                                }
                            }
                            showData(memberOrder);
                        }
                    }
                    break;
                case EDIT_ORDER_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess) {
                            setResult(Constant.RESULT_CODE_ORDER_DETAIL);
                            finish();
                        }
                    }
                    break;
                case GET_ORDER_DETAIL_FALSE:
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

    /** 获取订单详情请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_ORDER_DETAIL);
        ajaxParams.put("token", token);
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.GET_ORDER_DETAIL + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.GET_ORDER_DETAIL + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(GET_ORDER_DETAIL_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(GET_ORDER_DETAIL_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 编辑订单请求*/
    public void UploadAdapter_Edit() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EDIT_ORDER);
        ajaxParams.put("token", PreferencesUtils.getString(OrderDetailActivity.this, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText(orderId));
        Log.e(TAG, Constant.REQUEST + API.EDIT_ORDER + "\n" + ajaxParams.toString());
        finalHttp.post(API.server,  ajaxParams, new AjaxCallBack<String>() {
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
                if(!StringUtils.isBlank(t)){
                    Log.e(TAG, Constant.RESULT + API.EDIT_ORDER + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(EDIT_ORDER_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(GET_ORDER_DETAIL_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 退款订单详细请求*/
    public void UploadAdapter_ReturnDetail() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_RETURN_DETAIL);
        ajaxParams.put("token", PreferencesUtils.getString(OrderDetailActivity.this, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.GET_RETURN_DETAIL + "\n" + ajaxParams.toString());
        finalHttp.post(API.server,  ajaxParams, new AjaxCallBack<String>() {
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
                if(!StringUtils.isBlank(t)){
                    Log.e(TAG, Constant.RESULT + API.GET_RETURN_DETAIL + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(GET_RETURN_ORDER_DETAIL_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(GET_ORDER_DETAIL_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * member_id	会员ID
     * order_id	订单ID
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(OrderDetailActivity.this, Constant.KEY_MEMBER_ID));
            json.put("order_id", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * member_id	会员ID
     * order_id	订单ID
     * operator_type	操作类型(1-删除订单)
     * @return json
     */
    private String makeJsonText(String orderId) {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(OrderDetailActivity.this, Constant.KEY_MEMBER_ID));
            json.put("operator_type", "1");
            json.put("order_id", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 取消订单的监听事件*/
    class OnCancelOrderClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.e(TAG, "click onCancel");
            promptDialog = new PromptDialog(OrderDetailActivity.this, "提示", "确定要取消订单？", "确定", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //删除订单
                            UploadAdapter_Edit();
                            promptDialog.dismiss();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            promptDialog.dismiss();
                        }
                    });
                    promptDialog.show();

        }
    }

    /** 申请退款的监听事件*/
    class OnSubmitReturnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.e(TAG, "click onSubmit");
            Intent intent = new Intent(OrderDetailActivity.this, SubmitReturnActivity.class);
            intent.putExtra("memberOrder", memberOrder);
            startActivityForResult(intent, Constant.RESQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.RESQUEST_CODE) {
            switch (resultCode) {
                case Constant.RESULT_CODE_RETURN:
                    //申请退款返回
                    changeOrderStatus = true;
                    UploadAdapter_ReturnDetail();
                    break;
                case Constant.RESULT_CODE_LOGIN:
                    Log.e(TAG, "resultCode ->" + resultCode);
                    initData();
                    break;
                case Constant.RESULT_CODE_CANCEL_LOGIN:
                    Log.e(TAG, "resultCode(取消登录) ->" + resultCode);
                    finish();
                    break;
            }
        }
    }
}
