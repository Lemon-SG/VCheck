package cc.siyo.iMenu.VCheck.activity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.MyCookieStore;
import cc.siyo.iMenu.VCheck.util.MD5;
import cc.siyo.iMenu.VCheck.util.MatcherUtil;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/5/21.
 * Desc:填写订单
 */
public class OrderWriteActivity extends BaseActivity{

    private static final String TAG = "OrderWriteActivity";
    /** 手机号输入框*/
    @ViewInject(id = R.id.et_orderWrite_mobile)private EditText et_orderWrite_mobile;
    /** 验证码输入框*/
    @ViewInject(id = R.id.et_orderWrite_verifyCode)private EditText et_orderWrite_verifyCode;
    /** 获取验证码按钮*/
    @ViewInject(id = R.id.tv_orderWrite_getVerifyCode)private TextView tv_orderWrite_getVerifyCode;
    /** 提交订单按钮*/
    @ViewInject(id = R.id.tv_orderWrite_submitOrder)private TextView tv_orderWrite_submitOrder;
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
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
    /** 存储验证码*/
    private String verifyCode;
    /** 存储通信CODE*/
    private String code;

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
        finalHttp = new FinalHttp();
        tv_orderWrite_getVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(getResources().getString(R.string.loading));
                doGetVerifyCode();
            }
        });
        tv_orderWrite_submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交订单
                if(!StringUtils.isBlank(token)){
                    //已登录

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
            }
        });
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
                        //登录或注册成功，执行下单操作，后续补充
                        prompt("快速登录或注册成功，稍后实现下单，member_id = " + member_id);
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
     */
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
}
