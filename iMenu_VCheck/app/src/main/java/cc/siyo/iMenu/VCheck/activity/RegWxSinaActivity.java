package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.android.tpush.XGPushConfig;

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
import cc.siyo.iMenu.VCheck.model.ProFile;
import cc.siyo.iMenu.VCheck.util.MD5;
import cc.siyo.iMenu.VCheck.util.MatcherUtil;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/8/6.
 * Desc:第三方注册界面
 */
public class RegWxSinaActivity extends BaseActivity{

    private static final String TAG = "RegWxSinaActivity";
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
    /** 微信注册成功标石*/
    private static final int REG_WX_SUCCESS = 3000;
    /** 提交成功标石*/
    private static final int EDIT_PUSH_DEVICE_SUCCESS = 300;
    /** 第三方微信登录成功标石*/
    private static final int LOGIN_WX_SUCCESS = 600;
    /** 存储验证码*/
    private String verifyCode;
    /** 存储通信CODE*/
    private String code;

    private ProFile proFile;

    @Override
    public int getContentView() {
        return R.layout.activity_reg_wx_sina;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("完善资料");
        topbar.setText(TopBar.RIGHT_BUTTON, "提交");
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                if(isMobile()){
                    if(isRightVerifyCode()){
                        UploadAdapter_WxReg();
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
                finish();
            }
        });
    }

    @Override
    public void initData() {
        proFile = (ProFile) getIntent().getExtras().getSerializable("proFile");
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
                case LOGIN_WX_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        String member_id = data.optString("member_id");
                        String token = data.optString("token");
                        savePreferences(member_id, token);
                        UploadAdapter_Submit();
                    }
                    break;
                case EDIT_PUSH_DEVICE_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null) {
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess) {
                            setResult(Constant.RESULT_CODE_LOGIN);
                            finish();
                        }
                    }
                    break;
                case REG_WX_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        String member_id = data.optString(Constant.KEY_MEMBER_ID);
                        String token = data.optString(Constant.KEY_TOKEN);
                        savePreferences(member_id, token);
                        PreferencesUtils.putString(RegWxSinaActivity.this, Constant.KEY_MOBILE, et_reg_mobile.getText().toString());
                        UploadAdapter();
                    }
                    break;
                case GET_VERIFY_CODE_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(data != null){
                            verifyCode = data.optString("verify_code");
                            code = data.optString("code");
                            Log.e(TAG, "code->" + code);
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
                DefaultHttpClient client = (DefaultHttpClient) finalHttp.getHttpClient();
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

    /** 微信注册请求*/
    private void UploadAdapter_WxReg() {
        finalHttp = new FinalHttp();
        //配置已保存的CookieStore,保证处于同一session中请求
        finalHttp.configCookieStore(MyCookieStore.cookieStore);

        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.REGISTER_WITH_WX);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_WxReg());
        Log.e(TAG, Constant.REQUEST + API.REGISTER_WITH_WX + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.REGISTER_WITH_WX + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(REG_WX_SUCCESS, BaseJSONData(t)));
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
            //获取验证码
            UploadAdapter_getVerifyCode();
        }else{
            showOrHideProgressDialog(false);
            prompt("手机号输入不正确");
        }
    }

    /***
     * 第三方注册
     * mobile	手机号码
     * code	MD5加密(手机号+"Constant.SALT_KEY")
     * @return json
     */
    private String makeJsonText_WxReg() {
        JSONObject json = new JSONObject();
        try {
            json.put("wx_info", new ProFile().GetWxJson(proFile));
            json.put("mobile", et_reg_mobile.getText().toString());
            json.put("code", MD5.MD5Encode(code + Constant.SALT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
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

    /** 第三方登录请求*/
    private void UploadAdapter() {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.LOGIN_WITH_WX);
        ajaxParams.put("token", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.LOGIN_WITH_WX + "\n:" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                closeProgressDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.request_time_out), Toast.LENGTH_SHORT).show();
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e(TAG, Constant.RESULT +API.LOGIN_WITH_WX + "\n" + t.toString());
                if(!StringUtils.isBlank(t)){
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(LOGIN_WX_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(GET_VERIFY_CODE_FALSE, BaseJSONData(t)));
                    }
                }
            }
        });
    }

    /***
     * 第三方微信登录
     * member_id
     * @return
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("wx_info", new ProFile().GetWxJson(proFile));
//            json.put("member_id", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_MEMBER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * 获取验证码
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

    /** 提交推送设备信息*/
    private void UploadAdapter_Submit() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EDIT_PUSH_DEVICE);
        ajaxParams.put("token", PreferencesUtils.getString(context, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_Submit());
        Log.e(TAG, Constant.REQUEST + API.EDIT_PUSH_DEVICE + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.EDIT_PUSH_DEVICE + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(EDIT_PUSH_DEVICE_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(GET_VERIFY_CODE_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * member_id	会员ID
     * device_token	设备推送号码
     * operator_type	1-添加/2-删除/3-清空(默认为1)
     * @return json
     */
    private String makeJsonText_Submit() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("device_token", XGPushConfig.getToken(context));
            json.put("operator_type", Constant.OPERATOR_TYPE_ADD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
