package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.Menu;
import cc.siyo.iMenu.VCheck.model.MyCookieStore;
import cc.siyo.iMenu.VCheck.model.OrderInfo;
import cc.siyo.iMenu.VCheck.model.TotalPrice;
import cc.siyo.iMenu.VCheck.util.MD5;
import cc.siyo.iMenu.VCheck.util.MatcherUtil;
import cc.siyo.iMenu.VCheck.util.NumberFormatUtils;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;
/**
 * Created by Lemon on 2015/5/21.
 * Desc:填写订单
 */
public class OrderWriteActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "OrderWriteActivity";
    /** 手机号输入框*/
    @ViewInject(id = R.id.et_orderWrite_mobile)private EditText et_orderWrite_mobile;
    /** 验证码输入框*/
    @ViewInject(id = R.id.et_orderWrite_verifyCode)private EditText et_orderWrite_verifyCode;
    /** 获取验证码按钮*/
    @ViewInject(id = R.id.tv_orderWrite_getVerifyCode)private TextView tv_orderWrite_getVerifyCode;
    /** 底部产品合计显示*/
    @ViewInject(id = R.id.tv_price)private TextView tv_price;
    /** 提交订单按钮*/
    @ViewInject(id = R.id.tv_orderWrite_submitOrder)private TextView tv_orderWrite_submitOrder;
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 产品标题显示*/
    @ViewInject(id = R.id.tv_order_write_title)private TextView tv_order_write_title;
    /** 产品单价显示*/
    @ViewInject(id = R.id.tv_order_write_price)private TextView tv_order_write_price;
    /** 产品数量显示*/
    @ViewInject(id = R.id.tv_order_write_count)private TextView tv_order_write_count;
    /** 产品减按钮*/
    @ViewInject(id = R.id.tv_order_write_count_down)private TextView tv_order_write_count_down;
    /** 产品加按钮*/
    @ViewInject(id = R.id.tv_order_write_count_up)private TextView tv_order_write_count_up;
    /** 产品合计显示*/
    @ViewInject(id = R.id.tv_order_write_total_price)private TextView tv_order_write_total_price;
//    /** 立即登录按钮*/
//    @ViewInject(id = R.id.tv_order_write_login)private TextView tv_order_write_login;
    /** 未登录时显示的layout*/
    @ViewInject(id= R.id.ll_order_write_login)private LinearLayout ll_order_write_login;
    /** 登录后显示的手机号layout*/
    @ViewInject(id= R.id.rl_order_write_phone)private RelativeLayout rl_order_write_phone;
    /** 手机号显示*/
    @ViewInject(id= R.id.tv_order_write_phone)private TextView tv_order_write_phone;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 获取验证码成功标石*/
    private static final int GET_VERIFY_CODE_SUCCESS = 1000;
    /** 获取验证码失败标石*/
    private static final int GET_VERIFY_CODE_FALSE = 2000;
    /** 快速登录注册成功标石*/
    private static final int QUICK_LOGIN_SUCCESS = 100;
    /** 快速登录注册失败标石*/
    private static final int QUICK_LOGIN_FALSE = 200;
    /** 编辑购物车成功标石*/
    private static final int EDIT_CART_SUCCESS = 300;
    /** 提交订单成功标石*/
    private static final int ADD_ORDER_SUCCESS = 400;
    /** 存储验证码*/
    private String verifyCode;
    /** 存储与服务器通信CODE*/
    private String code;
    /** 产品实体*/
    private Article article;
    /** 蔡品品实体*/
    private Menu menu;
    /** 当前总价实体*/
    private TotalPrice totalPrice;
    /** 记录当前产品数量,默认为1*/
    private int productCount = 1;
    /** 记录当前菜品单价，方便计算*/
    private double unitPrice;

    @Override
    public int getContentView() {
        return R.layout.activity_order_write;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("填写订单");
        topbar.setHiddenButton(TopBar.RIGHT_BUTTON);
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //返回
                finish();
            }
        });
    }

    @Override
    public void initData() {
        article = (Article) getIntent().getExtras().getSerializable("article");
        finalHttp = new FinalHttp();
        tv_orderWrite_getVerifyCode.setOnClickListener(this);
        tv_orderWrite_submitOrder.setOnClickListener(this);
        tv_order_write_count_down.setOnClickListener(this);
        tv_order_write_count_up.setOnClickListener(this);
//        tv_order_write_login.setOnClickListener(this);
        rl_order_write_phone.setOnClickListener(this);
        if(!StringUtils.isBlank(token)){
            //已登录
            rl_order_write_phone.setVisibility(View.VISIBLE);
            ll_order_write_login.setVisibility(View.GONE);
            tv_order_write_phone.setText(hidMobile(PreferencesUtils.getString(this, Constant.KEY_MOBILE)));
            UploadAdapter_editCart();
        } else {
            //未登录
            rl_order_write_phone.setVisibility(View.GONE);
            ll_order_write_login.setVisibility(View.VISIBLE);

            tv_order_write_title.setText(article.menu_info.menu_name);
            unitPrice = Double.parseDouble(article.menu_info.price.special_price == "" ? article.menu_info.price.original_price : article.menu_info.price.special_price);
            tv_order_write_price.setText(unitPrice + article.menu_info.price.price_unit + "/" + article.menu_info.menu_unit.menu_unit);
            tv_order_write_total_price.setText(productCount * unitPrice + "" + article.menu_info.price.price_unit);
            tv_price.setText(productCount * unitPrice + "");
        }
        et_orderWrite_verifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    Log.e(TAG, "验证码输入完");
                    tv_orderWrite_submitOrder.performClick();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /** 更新数据*/
    private void updateData() {
        tv_order_write_count.setText(productCount + "");
        tv_order_write_title.setText(menu.menu_name);
        if(!StringUtils.isBlank(menu.price.special_price)) {
            unitPrice = Double.parseDouble(menu.price.special_price);
        } else {
            unitPrice = Double.parseDouble(menu.price.original_price);
        }
        tv_order_write_price.setText(unitPrice + menu.price.price_unit + "/" + menu.menu_unit.menu_unit);
        productCount = Integer.parseInt(menu.count);
        tv_order_write_total_price.setText(productCount * unitPrice + "" + menu.price.price_unit);
        if(!StringUtils.isBlank(totalPrice.special_price)) {
            tv_price.setText(Double.parseDouble(totalPrice.special_price) + "");
        } else {
            tv_price.setText(Double.parseDouble(totalPrice.original_price) + "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_order_write_count_down:
                //数量减,重新计算合计价格及底部合计
                if(productCount == 1) {
                    //不可为0
                    prompt("最少应订1份");
                }else {
                    productCount--;
                    if(!StringUtils.isBlank(token)) {
                        UploadAdapter_editCart();
                    } else {
                        tv_price.setText(unitPrice * productCount + "");
                        tv_order_write_count.setText(productCount + "");
                        tv_order_write_total_price.setText(productCount * unitPrice + "" + article.menu_info.price.price_unit);
                    }
                }
                break;
            case R.id.tv_order_write_count_up:
                //数量加,重新计算合计价格及底部合计
                productCount++;
                if(!StringUtils.isBlank(token)) {
                    UploadAdapter_editCart();
                } else {
                    tv_price.setText(unitPrice * productCount + "");
                    tv_order_write_count.setText(productCount + "");
                    tv_order_write_total_price.setText(productCount * unitPrice + "" + article.menu_info.price.price_unit);
                }
                break;
            case R.id.rl_order_write_phone:
                //修改手机号

                break;
            case R.id.tv_orderWrite_getVerifyCode:
                //获取验证码
                showProgressDialog(getResources().getString(R.string.loading));
                doGetVerifyCode();
                break;
            case R.id.tv_orderWrite_submitOrder:
                //提交订单
                if(!StringUtils.isBlank(PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN))){
                    //已登录
                    UploadAdapter_addOrder();
                }else{
                    //未登录
                    if(isMobile()){
                        if(isRightVerifyCode()){
                            //执行快速登录或注册
                            UploadAdapter_quickLogin();
                        }else{
                            prompt("验证码输入不正确");
                        }
                    }else{
                        prompt("请输入正确的手机号");
                    }
                }
                break;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_VERIFY_CODE_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(data != null){
                            verifyCode = data.optString("verify_code");
                            code = data.optString("code");
                            min = 60;
                            handler.post(timeRunnable);
                            prompt("验证码已发送");
                        }
                    }
                    break;
                case QUICK_LOGIN_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        String member_id = data.optString(Constant.KEY_MEMBER_ID);
                        String token = data.optString(Constant.KEY_TOKEN);
                        savePreferences(member_id, token);
                        tv_order_write_phone.setText(hidMobile(et_orderWrite_mobile.getText().toString()));
                        PreferencesUtils.putString(OrderWriteActivity.this, Constant.KEY_MOBILE, et_orderWrite_mobile.getText().toString());
                        //登录或注册成功，执行下单操作，后续补充
                        Log.e(TAG, "快速登录或注册成功");
                        rl_order_write_phone.setVisibility(View.VISIBLE);
                        ll_order_write_login.setVisibility(View.GONE);
                        UploadAdapter_editCart();
                    }
                    break;
                case EDIT_CART_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        menu = new Menu().parse(data.optJSONObject("menu_info"));
                        totalPrice = new TotalPrice().parse(data.optJSONObject("total_price"));
                        updateData();
                    }
                    break;
                case ADD_ORDER_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        OrderInfo orderInfo = new OrderInfo().parse(data.optJSONObject("order_info"));
                        Intent intent = new Intent(context, OrderConfirmActivity.class);
                        intent.putExtra("orderInfo", orderInfo);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case QUICK_LOGIN_FALSE:
                case GET_VERIFY_CODE_FALSE:
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

    /** 提交订单*/
    private void UploadAdapter_addOrder() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.ADD_ORDER);
        ajaxParams.put("token", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_addOrder());
        Log.e(TAG, Constant.REQUEST + API.ADD_ORDER + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.ADD_ORDER + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(ADD_ORDER_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(QUICK_LOGIN_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    private String makeJsonText_addOrder() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 编辑购物车*/
    private void UploadAdapter_editCart() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EDIT_CART);
        ajaxParams.put("token", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_editCart());
        Log.e(TAG, Constant.REQUEST + API.EDIT_CART + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.EDIT_CART + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(EDIT_CART_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(QUICK_LOGIN_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    private String makeJsonText_editCart() {
        JSONObject json = new JSONObject();
        try {
            JSONObject cartInfoJson = new JSONObject();
            JSONObject menuInfoJson = new JSONObject();
            JSONObject articleInfoJson = new JSONObject();
            articleInfoJson.put("article_id", article.article_id);
            menuInfoJson.put("menu_id", article.menu_info.menu_id);
            menuInfoJson.put("count", productCount + "");
            cartInfoJson.put("menu_info", menuInfoJson);
            cartInfoJson.put("article_info", articleInfoJson);
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("operator_type", Constant.OPERATOR_TYPE_EDIT);
            json.put("cart_info", cartInfoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 获取验证码请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_VERIFY_CODE);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.GET_VERIFY_CODE + "\n" + ajaxParams.toString());
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
                //从服务器获取CookieStore，保存到MyCookieStore
                DefaultHttpClient client=(DefaultHttpClient)finalHttp.getHttpClient();
                MyCookieStore.cookieStore = client.getCookieStore();

                if (!StringUtils.isBlank(t)) {
                    Log.e(TAG, Constant.RESULT + API.GET_VERIFY_CODE + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(GET_VERIFY_CODE_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(GET_VERIFY_CODE_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 快速登录注册请求*/
    private void UploadAdapter_quickLogin() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.QUICK_LOGIN);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_quickLogin());
        Log.e(TAG, Constant.REQUEST + API.QUICK_LOGIN + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.QUICK_LOGIN + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(QUICK_LOGIN_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(QUICK_LOGIN_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * mobile 手机号码|
     * code	MD5加密("code"+"salt_key")
     * @return json
     * */
    private String makeJsonText_quickLogin() {
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", et_orderWrite_mobile.getText().toString());
            json.put("code", MD5.MD5Encode(code + Constant.SALT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * mobile	手机号码
     * code	MD5加密(手机号+"Constant.SALT_KEY")
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", et_orderWrite_mobile.getText().toString());
            json.put("code", MD5.MD5Encode(et_orderWrite_mobile.getText().toString() + Constant.SALT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 执行获取验证码*/
    public void doGetVerifyCode(){
        if(isMobile()){
            //发送获取验证码请求
            UploadAdapter();
        }else{
            closeProgressDialog();
            prompt("请输入正确的手机号");
        }
    }

    /** 判断手机号是否正确*/
    private boolean isMobile(){
        if (!StringUtils.isBlank(et_orderWrite_mobile.getText().toString())){
            if(MatcherUtil.Matcher(Constant.MATCHER_MOBILE, et_orderWrite_mobile.getText().toString())){
                return true;
            }
        }
        return false;
    }

    /** 判断验证码是否正确*/
    private boolean isRightVerifyCode(){
        if (!StringUtils.isBlank(et_orderWrite_verifyCode.getText().toString())){
            if(verifyCode.equals(et_orderWrite_verifyCode.getText().toString())){
                return true;
            }
        }
        return false;
    }

    int min = 20;
    /** 倒计时*/
    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if(min > 0){
                min--;
                tv_orderWrite_getVerifyCode.setText(min + "s");
                tv_orderWrite_getVerifyCode.setEnabled(false);
                tv_orderWrite_getVerifyCode.setTextColor(getResources().getColor(R.color.gray_9c));
                handler.postDelayed(timeRunnable, 1000);
            }else{
                tv_orderWrite_getVerifyCode.setText("获取验证码");
                tv_orderWrite_getVerifyCode.setTextColor(getResources().getColor(R.color.gray_87));
                tv_orderWrite_getVerifyCode.setEnabled(true);
            }
        }
    };

    /** 隐藏电话*/
    private String hidMobile(String mobile){
        return  mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4, mobile.length());
    }
}
