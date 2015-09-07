package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
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
 * Created by Lemon on 2015/5/8.
 * Desc:忘记密码2界面
 */
public class ForgetPass2Activity extends BaseActivity{

    private static final String TAG = "ForgetPass2Activity";
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 手机号输入框*/
    @ViewInject(id = R.id.et_mobile)private EditText et_mobile;
    /** 验证码输入框*/
    @ViewInject(id = R.id.et_verifyCode)private EditText et_verifyCode;
    /** 获取验证码按钮*/
    @ViewInject(id = R.id.tv_getVerifyCode)private TextView tv_getVerifyCode;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 获取验证码成功标石*/
    private static final int GET_VERIFY_CODE_SUCCESS = 1000;
    /** 获取验证码失败标石*/
    private static final int GET_VERIFY_CODE_FALSE = 2000;
    /** 存储验证码*/
    private String verifyCode;
    /** 存储通信CODE*/
    private String code;

    @Override
    public int getContentView() {
        return R.layout.activity_forget2;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("验证手机");
        topbar.setText(TopBar.RIGHT_BUTTON, "下一步");
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //验证码输入正确，跳转至下一步
                if(isRightVerifyCode()){
                    if(getIntent().getExtras() != null){
                        Log.e(TAG, "接收-->loginName = " + getIntent().getExtras().getString("loginName") + "|loginType = " + getIntent().getExtras().getInt("loginType"));
                        Log.e(TAG, "传送-->verifyCode = " + verifyCode + "|code = " + code);
                        Intent intent = new Intent(ForgetPass2Activity.this, ForgetPass3Activity.class);
                        intent.putExtra("verifyCode", verifyCode);
                        intent.putExtra("code", code);
                        intent.putExtra("loginName", getIntent().getExtras().getString("loginName"));
                        intent.putExtra("loginType", getIntent().getExtras().getInt("loginType"));
                        startActivity(intent);
                        finish();
                    }
                }else{
                    prompt("验证码输入不正确");
                }
            }
        });
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
        et_mobile.setText(getIntent().getExtras().getString("loginName"));
        doGetVerifyCode();
        tv_getVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGetVerifyCode();
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

    /***
     * mobile	手机号码
     * code	MD5加密(手机号+"Constant.SALT_KEY")
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", et_mobile.getText().toString());
            json.put("code", MD5.MD5Encode(et_mobile.getText().toString() + Constant.SALT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 执行获取验证码*/
    public void doGetVerifyCode(){
        showProgressDialog(getResources().getString(R.string.loading));
        if(isMobile()){
            //发送获取验证码请求
            UploadAdapter();
        }else{
            closeProgressDialog();
            prompt("手机号输入不正确");
        }
    }

    /** 判断手机号是否正确*/
    private boolean isMobile(){
        if (!StringUtils.isBlank(et_mobile.getText().toString())){
            if(MatcherUtil.Matcher(Constant.MATCHER_MOBILE, et_mobile.getText().toString())){
                return true;
            }
        }
        return false;
    }

    /** 判断验证码是否正确*/
    private boolean isRightVerifyCode(){
        if (!StringUtils.isBlank(et_verifyCode.getText().toString())){
            if(verifyCode.equals(et_verifyCode.getText().toString())){
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
                tv_getVerifyCode.setText(min + "s");
                tv_getVerifyCode.setEnabled(false);
                tv_getVerifyCode.setTextColor(getResources().getColor(R.color.gray_9c));
                handler.postDelayed(timeRunnable, 1000);
            }else{
                tv_getVerifyCode.setText("获取验证码");
                tv_getVerifyCode.setTextColor(getResources().getColor(R.color.gray_87));
                tv_getVerifyCode.setEnabled(true);
            }
        }
    };
}
