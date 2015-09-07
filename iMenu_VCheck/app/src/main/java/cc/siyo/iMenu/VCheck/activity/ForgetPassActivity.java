package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
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

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.util.MatcherUtil;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/5/8.
 * Desc:忘记密码1界面
 */
public class ForgetPassActivity extends BaseActivity{

    private static final String TAG = "ForgetPassActivity";
    /** 头部*/
    @ViewInject(id = R.id.topbar) TopBar topbar;
    /** 邮箱或手机号输入框*/
    @ViewInject(id = R.id.et_email_mobile)EditText et_email_mobile;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 校验邮箱或手机是否存在成功标石*/
    private static final int VERIFY_MEMBER_SUCCESS = 1000;
    /** 修改邮箱或手机是否存在失败标石*/
    private static final int VERIFY_MEMBER_FALSE = 2000;
    /** 登录方式*/
    private int loginType;

    @Override
    public int getContentView() {
        return R.layout.activity_forget;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("找回密码");
        topbar.setText(TopBar.RIGHT_BUTTON, "下一步");
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //跳转至下一步亦设置密码
                if(verifyEmail()){
                    UploadAdapter();
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
                case VERIFY_MEMBER_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess){
                            if(loginType == Constant.MATCHER_EMAIL){
                                prompt("邮箱不存在");
                            }else{
                                prompt("手机号不存在");
                            }
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
                                    Log.e(TAG, "手机号或邮箱存在,可进行下一步");
                                    Log.e(TAG, "传送-->loginName = " + et_email_mobile.getText().toString() + "|loginType = " + loginType);
                                    Intent intent = new Intent(ForgetPassActivity.this, ForgetPass2Activity.class);
                                    intent.putExtra("loginName", et_email_mobile.getText().toString());
                                    intent.putExtra("loginType", loginType);
                                    startActivity(intent);
                                    finish();
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
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.VALIDATE_MEMBER_INFO);
        ajaxParams.put("token", token);
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
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

    /***
     * 校验会员信息
     * validate_type	校验类型
     * validate_value	需要校验是否存在的值
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("validate_type", loginType + "");
            json.put("validate_value", et_email_mobile.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 校验邮箱*/
    private boolean verifyEmail(){
        Boolean isTrue = true;
        String loginName = et_email_mobile.getText().toString();
        if(!StringUtils.isBlank(loginName)){
            doVerifyLoginName();
            switch (loginType){
                case Constant.MATCHER_MOBILE:
                    if(!MatcherUtil.Matcher(Constant.MATCHER_MOBILE, loginName)){
                        prompt("手机号格式不正确");
                        isTrue = false;
                    }else{
                        isTrue = true;
                    }
                    break;
                case Constant.MATCHER_EMAIL:
                    if(!MatcherUtil.Matcher(Constant.MATCHER_EMAIL, loginName)){
                        prompt("邮箱格式不正确");
                        isTrue = false;
                    }else{
                        isTrue = true;
                    }
                    break;
            }
        }else{
            prompt("邮箱或手机号不能为空");
            isTrue = false;
        }
        return isTrue;
    }

    /** 登录方式*/
    private void doVerifyLoginName() {
        loginType = Constant.MATCHER_MOBILE;
//        String loginName = et_email_mobile.getText().toString();
////        String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8、7中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        boolean emailRegex = MatcherUtil.Matcher(Constant.MATCHER_EMAIL, loginName);//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8、7中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if(!emailRegex){
//            loginType = Constant.MATCHER_MOBILE;
//        }else{
//            loginType = Constant.MATCHER_EMAIL;
//        }
    }
}
