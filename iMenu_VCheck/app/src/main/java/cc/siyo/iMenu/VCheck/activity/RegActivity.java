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
 * Created by Lemon on 2015/5/7.
 * Desc:注册1界面
 */
public class RegActivity extends BaseActivity{

    private static final String TAG = "RegActivity";
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 手机号输入框*/
    @ViewInject(id = R.id.et_reg_mobile)private EditText et_reg_mobile;
    /** 验证码输入框*/
    @ViewInject(id = R.id.et_reg_verifyCode)private EditText et_reg_verifyCode;
    /** 邀请码输入框*/
    @ViewInject(id = R.id.et_reg_inviteCode)private EditText et_reg_inviteCode;
    /** 获取验证码按钮*/
    @ViewInject(id = R.id.tv_reg_getVerifyCode)private TextView tv_reg_getVerifyCode;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 获取验证码成功标石*/
    private static final int GET_VERIFY_CODE_SUCCESS = 100;
    /** 获取验证码失败标石*/
    private static final int GET_VERIFY_CODE_FALSE = 200;
    /** 校验邮箱或手机是否存在成功标石*/
    private static final int VERIFY_MEMBER_SUCCESS = 1000;
    /** 修改邮箱或手机是否存在失败标石*/
    private static final int VERIFY_MEMBER_FALSE = 2000;

    /** 存储验证码*/
    private String verifyCode;
    /** 存储通信CODE*/
    private String code;

    @Override
    public int getContentView() {
        return R.layout.activity_reg;
    }

    @Override
    public void initView() {
        topbar = (TopBar)findViewById(R.id.topbar);
        topbar.settitleViewText("注册");
        topbar.setText(TopBar.RIGHT_BUTTON, "下一步");
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //跳转至下一步亦设置密码
                if(isMobile()){
                    if(isRightVerifyCode()){
                        Log.e(TAG, "传送-->verifyCode = " + verifyCode + "|code = " + code);
                        Intent intent = new Intent(RegActivity.this, Reg2Activity.class);
                        intent.putExtra("mobile", et_reg_mobile.getText().toString());
                        intent.putExtra("verifyCode", verifyCode);
                        intent.putExtra("code", code);
                        startActivity(intent);
                        finish();
                    }else{
                        prompt("验证码输入不正确");
                    }
                }else{
                    prompt("请输入正确的手机号");
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
        tv_reg_getVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取验证码
                showOrHideProgressDialog(true);
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
                case VERIFY_MEMBER_SUCCESS:
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess){
                            //发送获取验证码请求
                            UploadAdapter_getVerifyCode();
                        }
                    }
                    break;
                case VERIFY_MEMBER_FALSE:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(!StringUtils.isBlank(jsonStatus.error_code)){
                            if(jsonStatus.error_code.equals(Constant.ERROR_CODE_EMAIL_HERE + "") ||
                                    jsonStatus.error_code.equals(Constant.ERROR_CODE_MOBILE_HERE + "")){
                                Log.e(TAG, "手机号存在");
                                prompt("手机号已存在");
                            }
                        }else{
                            prompt(getResources().getString(R.string.request_erro));
                        }
                    }
                    break;
            }
        }
    };

    /** 校验用户信息是否存在请求*/
    private void UploadAdapter_VerifyMember() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.VALIDATE_MEMBER_INFO);
        ajaxParams.put("token", token);
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_VerifyMember());
        Log.e(TAG, Constant.REQUEST + API.VALIDATE_MEMBER_INFO + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.VALIDATE_MEMBER_INFO + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(VERIFY_MEMBER_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(VERIFY_MEMBER_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 获取验证码请求*/
    private void UploadAdapter_getVerifyCode() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_VERIFY_CODE);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_getVerifyCode());
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

    /** 执行获取验证码*/
    public void doGetVerifyCode(){
        if(isMobile()){
            //验证是否已注册
            UploadAdapter_VerifyMember();
        }else{
            showOrHideProgressDialog(false);
            prompt("手机号输入不正确");
        }
    }

    /** 判断手机号是否正确*/
    private boolean isMobile(){
        if (!StringUtils.isBlank(et_reg_mobile.getText().toString())){
            if(MatcherUtil.Matcher(Constant.MATCHER_MOBILE, et_reg_mobile.getText().toString())){
                return true;
            }
        }
        return false;
    }

    /** 判断验证码是否正确*/
    private boolean isRightVerifyCode(){
        if (!StringUtils.isBlank(et_reg_verifyCode.getText().toString())){
            if(verifyCode.equals(et_reg_verifyCode.getText().toString())){
                return true;
            }
        }
        return false;
    }

    /***
     * mobile	手机号码
     * code	MD5加密(手机号+"Constant.SALT_KEY")
     * @return json
     */
    private String makeJsonText_getVerifyCode() {
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", et_reg_mobile.getText().toString());
            json.put("code", MD5.MD5Encode(et_reg_mobile.getText().toString() + Constant.SALT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * 校验会员信息
     * validate_type	校验类型
     * validate_value	需要校验是否存在的值
     * @return json
     */
    private String makeJsonText_VerifyMember() {
        JSONObject json = new JSONObject();
        try {
            json.put("validate_type", Constant.MATCHER_MOBILE);
            json.put("validate_value", et_reg_mobile.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 用于进度圈的管理*/
    private void showOrHideProgressDialog(boolean isShow){
        if(isShow){
            showProgressDialog(getResources().getString(R.string.loading));
        }else{
            closeProgressDialog();
        }
    }

    int min = 60;
    /** 倒计时*/
    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if(min > 0){
                min--;
                tv_reg_getVerifyCode.setText(min + "s");
                tv_reg_getVerifyCode.setEnabled(false);
                tv_reg_getVerifyCode.setTextColor(getResources().getColor(R.color.gray_9c));
                handler.postDelayed(timeRunnable, 1000);
            }else{
                tv_reg_getVerifyCode.setText("获取验证码");
                tv_reg_getVerifyCode.setTextColor(getResources().getColor(R.color.gray_87));
                tv_reg_getVerifyCode.setEnabled(true);
            }
        }
    };
}
