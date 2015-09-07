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
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/5/8.
 * Desc:注册2界面
 */
public class Reg2Activity extends BaseActivity{

    private static final String TAG = "Reg2Activity";
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 密码输入框*/
    @ViewInject(id = R.id.et_reg2_pass)private EditText et_reg2_pass;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 注册成功标石*/
    private static final int REG_SUCCESS = 100;
    /** 注册失败标石*/
    private static final int REG_FALSE = 200;
    /** 存储验证码*/
    private String verifyCode;
    /** 存储通信CODE*/
    private String code;
    /** 手机号*/
    private String mobile;
    /** 当前是否处于正常注册标石*/
    private boolean isNorReg = true;

    @Override
    public int getContentView() {
        return R.layout.activity_reg2;
    }

    @Override
    public void initView() {
        topbar = (TopBar)findViewById(R.id.topbar);
        topbar.settitleViewText("设置密码");
        topbar.setText(TopBar.RIGHT_BUTTON, "确定");
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //确定
                if(doVerifyPass()){
                    UploadAdapter();
                }else{
                    prompt("密码不能空且最少六位");
                }
            }
        });
        topbar.setHiddenButton(TopBar.LEFT_BUTTON);
    }

    @Override
    public void initData() {
        finalHttp = new FinalHttp();
        //配置已保存的CookieStore,保证处于同一session中请求
        finalHttp.configCookieStore(MyCookieStore.cookieStore);
        if(getIntent().getExtras() != null){
            Log.e(TAG, "接收-->|verifyCode = " + getIntent().getExtras().getString("verifyCode")
                    + "|code = " + getIntent().getExtras().getString("code"));
            verifyCode = getIntent().getExtras().getString("verifyCode");
            code = getIntent().getExtras().getString("code");
            mobile = getIntent().getExtras().getString("mobile");
            if(!StringUtils.isBlank(getIntent().getExtras().getString(Constant.INTENT_REG_WX_SINA))) {
                if(getIntent().getExtras().getString(Constant.INTENT_REG_WX_SINA).equals(Constant.INTENT_REG_WX_SINA)) {
                    isNorReg = false;
                }
            } else {
                isNorReg = true;
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REG_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        String member_id = data.optString(Constant.KEY_MEMBER_ID);
                        String token = data.optString(Constant.KEY_TOKEN);
                        savePreferences(member_id, token);
                        PreferencesUtils.putString(Reg2Activity.this, Constant.KEY_MOBILE, mobile);
                        finish();
                    }
                    break;
                case REG_FALSE:
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

    /** 注册请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.REGISTER);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.REGISTER + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.REGISTER + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(REG_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(REG_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * mobile 手机号码|
     * password	密码|
     * code	MD5加密("code"+"salt_key")
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("mobile", mobile);
            json.put("password", et_reg2_pass.getText().toString());
            json.put("code", MD5.MD5Encode(code + Constant.SALT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 验证密码输入是否为空*/
    private boolean doVerifyPass(){
        return !StringUtils.isBlank(et_reg2_pass.getText().toString()) && ((et_reg2_pass.getText().toString()).length() >= 6);
    }
}
