package cc.siyo.iMenu.VCheck.activity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
import cc.siyo.iMenu.VCheck.model.MyCookieStore;
import cc.siyo.iMenu.VCheck.util.MD5;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/5/8.
 * Desc:忘记密码3界面
 */
public class ForgetPass3Activity extends BaseActivity{

    private static final String TAG = "ForgetPass3Activity";
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 新密码输入框*/
    @ViewInject(id = R.id.et_newPass)private EditText et_newPass;
    /** 确认新密码输入框*/
    @ViewInject(id = R.id.et_affirm_newPass)private EditText et_affirm_newPass;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 校验邮箱或手机是否存在成功标石*/
    private static final int RESET_SUCCESS = 1000;
    /** 修改邮箱或手机是否存在失败标石*/
    private static final int RESET_FALSE = 2000;
    /** 存储登录名*/
    private String loginName;
    /** 存储登录方式*/
    private int loginType;
    /** 存储验证码*/
    private String verifyCode;
    /** 存储通信CODE*/
    private String code;

    @Override
    public int getContentView() {
        return R.layout.activity_forget3;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("重置密码");
        topbar.setText(TopBar.RIGHT_BUTTON, "保存");
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //保存
                if(doVerifyPass()){
                    //请求重置密码
                    UploadAdapter();
                }else{
                    prompt("密码不能为空或两次密码输入不一致");
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
        //配置已保存的CookieStore,保证处于同一session中请求
        finalHttp.configCookieStore(MyCookieStore.cookieStore);
        if(getIntent().getExtras() != null){
            Log.e(TAG, "接收-->loginName = " + getIntent().getExtras().getString("loginName")
                    + "|loginType = " + getIntent().getExtras().getInt("loginType")
                    + "|verifyCode = " + getIntent().getExtras().getString("verifyCode")
                    + "|code = " + getIntent().getExtras().getString("code"));
            loginName = getIntent().getExtras().getString("loginName");
            loginType = getIntent().getExtras().getInt("loginType");
            verifyCode = getIntent().getExtras().getString("verifyCode");
            code = getIntent().getExtras().getString("code");
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESET_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        String member_id = data.optString(Constant.KEY_MEMBER_ID);
                        String token = data.optString(Constant.KEY_TOKEN);
                        savePreferences(member_id, token);
                        finish();
                    }
                    break;
                case RESET_FALSE:
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

    /** 重置密码请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.RESET_PASSWORD);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.RESET_PASSWORD + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.RESET_PASSWORD + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(RESET_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(RESET_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * login_name	登录名
     * new_password	新密码
     * login_type	登录方式	1-手机/2-邮箱
     * code	MD5加密(登录名+"Constant.SALT_KEY")
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("login_name", loginName);
            json.put("new_password", et_affirm_newPass.getText().toString());
            json.put("login_type", loginType + "");
            json.put("code", MD5.MD5Encode(code + Constant.SALT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 验证密码输入是否为空*/
    private boolean doVerifyPass(){
        return !StringUtils.isBlank(et_newPass.getText().toString())
                && !StringUtils.isBlank(et_affirm_newPass.getText().toString())
                && et_newPass.getText().toString().equals(et_affirm_newPass.getText().toString());
    }
}
