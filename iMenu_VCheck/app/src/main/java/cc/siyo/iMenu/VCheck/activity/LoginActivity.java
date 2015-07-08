package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.util.MD5;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * Created by Lemon on 2015/5/5.
 * Desc:登陆界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    /** 头部*/
    @ViewInject(id = R.id.topbar) TopBar topbar;
    /** 去注册按钮*/
    @ViewInject(id = R.id.tv_reg) TextView tv_reg;
    /** 去忘记密码按钮*/
    @ViewInject(id = R.id.iv_forget_pass) ImageView iv_forget_pass;
    /** 登录名*/
    @ViewInject(id = R.id.et_login_name)EditText et_login_name;
    /** 密码*/
    @ViewInject(id = R.id.et_password)EditText et_password;
    /** 微博登录*/
    @ViewInject(id = R.id.iv_login_sina)ImageView iv_login_sina;
    /** 微信登陆*/
    @ViewInject(id = R.id.iv_login_weChat)ImageView iv_login_weChat;
    /** A FINAL 框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 登录成功标石*/
    private static final int LOGIN_SUCCESS = 100;
    /** 登录失败标石*/
    private static final int LOGIN_FALSE = 200;
    /** 输入的用户名是什么类型->1-手机号码/2-用户名*/
    private String loginType;

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        tv_reg.setOnClickListener(this);
        iv_forget_pass.setOnClickListener(this);
        iv_login_sina.setOnClickListener(this);
        iv_login_weChat.setOnClickListener(this);
        topbar.settitleViewText("登录");
        topbar.setText(TopBar.RIGHT_BUTTON, "确定");
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //确定登陆
                if(doVerifyLoginIsNull()){
                    doVerifyLoginName();
                    UploadAdapter();
                }else{
                    prompt("用户名或密码不能为空");
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
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        String member_id = data.optString(Constant.KEY_MEMBER_ID);
                        String token = data.optString(Constant.KEY_TOKEN);
                        savePreferences(member_id, token);
                        if(loginType.equals("1")) {
                            PreferencesUtils.putString(LoginActivity.this, Constant.KEY_MOBILE, et_login_name.getText().toString());
                        }
                        setResult(Constant.RESULT_CODE_LOGIN);
                        finish();
//                        UploadGetSellerDetail(seller_id);
                    }
                    break;
                case LOGIN_FALSE:
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

    /** 登录请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.LOGIN);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.LOGIN + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.LOGIN + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(LOGIN_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(LOGIN_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * login_name	登陆名
     * password	会员密码
     * type	登录类型(1-手机号码/2-用户名)
     * code	MD5加密(登录名+"Constant.SALT_KEY")
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("login_name", et_login_name.getText().toString());//0EB2961DE
            json.put("password", et_password.getText().toString());
            json.put("login_type", loginType);
            json.put("code", MD5.MD5Encode(et_login_name.getText().toString() + Constant.SALT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_forget_pass:
                //跳转至忘记密码
                Intent intent_forget = new Intent(LoginActivity.this, ForgetPassActivity.class);
                startActivity(intent_forget);
                break;
            case R.id.tv_reg:
                //跳转至注册
                Intent intent_reg = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(intent_reg);
                break;
            case R.id.iv_login_sina:
                //微博登陆
                showProgressDialog(getResources().getString(R.string.loading));
                Platform sina = ShareSDK.getPlatform(context, SinaWeibo.NAME);
                sina.setPlatformActionListener(new SinaPlatformActionListener());
                sina.showUser(null);
                break;
            case R.id.iv_login_weChat:
                //微信登录
//                showProgressDialog(getResources().getString(R.string.loading));

                break;
        }
    }

    /** 执行校验登录方式*/
    private void doVerifyLoginName() {
        String loginName = et_login_name.getText().toString();
        String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8、7中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if(loginName.matches(telRegex)){
            loginType = "1";
        }else{
            loginType ="2";
        }
    }

    /** 执行校验登录用户名或密码是否为空*/
    private Boolean doVerifyLoginIsNull() {
        return (!StringUtils.isBlank(et_login_name.getText().toString()) && !StringUtils.isBlank(et_password.getText().toString()));
    }

    /** 新浪回调监听*/
    private class SinaPlatformActionListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            closeProgressDialog();
            Log.e(TAG, "新浪登录回调onComplete ->");
            if(hashMap != null && !hashMap.isEmpty()){
                Log.e(TAG, "hashMap ->" + hashMap.toString());

            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            closeProgressDialog();
            Log.e(TAG, "新浪登录回调onError ->");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            closeProgressDialog();
            Log.e(TAG, "新浪登录回调onCancel ->");
        }
    }
}
