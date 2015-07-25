package cc.siyo.iMenu.VCheck.activity.setting;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.BaseActivity;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.util.MatcherUtil;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/5/12.
 * Desc:账户设置公用类
 */
public class EditAccountActivity extends BaseActivity {

    private static final String TAG = "EditAccountActivity";
    /** 头部*/
    @ViewInject(id = R.id.topbar) TopBar topbar;
    /** 修改邮箱布局*/
    @ViewInject(id = R.id.ll_edit_email)LinearLayout ll_edit_email;
    /** 修改昵称布局*/
    @ViewInject(id = R.id.ll_edit_nickname)LinearLayout ll_edit_nickname;
    /** 修改密码布局*/
    @ViewInject(id = R.id.ll_edit_pass)LinearLayout ll_edit_pass;
    /** 邮箱输入框*/
    @ViewInject(id = R.id.et_email)EditText et_email;
    /** 确认邮箱输入框*/
    @ViewInject(id = R.id.et_affirm_eamil)EditText et_affirm_email;
    /** 昵称输入框*/
    @ViewInject(id = R.id.et_nickname)EditText et_nickname;
    /** 旧密码输入框*/
    @ViewInject(id = R.id.et_oldPass)EditText et_oldPass;
    /** 新密码输入框*/
    @ViewInject(id = R.id.et_newPass)EditText et_newPass;
    /** 确认新密码输入框*/
    @ViewInject(id = R.id.et_affirm_newPass)EditText et_affirm_newPass;
    /** 编辑的种类*/
    private int editType = Constant.EDIT_NICKNAME;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 校验邮箱或手机是否存在成功标石*/
    private static final int VERIFY_MEMBER_SUCCESS = 1000;
    /** 修改邮箱或手机是否存在失败标石*/
    private static final int VERIFY_MEMBER_FALSE = 2000;
    /** 修改个人信息成功标石*/
    private static final int EDIT_SUCCESS = 100;
    /** 修改个人信息失败标石*/
    private static final int EDIT_FALSE = 200;
    /** 校验会员信息类型*/
    private int verifyType = Constant.MATCHER_NICKNAME;

    @Override
    public int getContentView() {
        return R.layout.activity_edit_account;
    }

    @Override
    public void initView() {
        if(getIntent() != null && getIntent().getExtras() != null){
            editType = getIntent().getExtras().getInt(Constant.EDIT_ACCOUNT);
        }
        switch (editType){
            case Constant.EDIT_EMAIL:
                topbar.settitleViewText("设置邮箱");
                ll_edit_email.setVisibility(View.VISIBLE);
                ll_edit_nickname.setVisibility(View.GONE);
                ll_edit_pass.setVisibility(View.GONE);
                break;
            case Constant.EDIT_MOBILE:
                //暂不实现
                break;
            case Constant.EDIT_NICKNAME:
                topbar.settitleViewText("修改昵称");
                ll_edit_nickname.setVisibility(View.VISIBLE);
                ll_edit_email.setVisibility(View.GONE);
                ll_edit_pass.setVisibility(View.GONE);
                et_nickname.setText(getIntent().getExtras().getString("nickname"));
                break;
            case Constant.EDIT_PASS:
                topbar.settitleViewText("更改密码");
                ll_edit_pass.setVisibility(View.VISIBLE);
                ll_edit_email.setVisibility(View.GONE);
                ll_edit_nickname.setVisibility(View.GONE);
                break;
        }
        topbar.setText(TopBar.RIGHT_BUTTON, "保存");
        topbar.setRightButtonOnClickListener(SaveClickListener);
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
                case EDIT_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess){
                            prompt("保存成功");
                            Intent intent = new Intent();
                            switch (editType){
                                case Constant.EDIT_EMAIL:
                                    Log.e(TAG, "邮箱保存成功|" + et_affirm_email.getText().toString());
                                    intent.putExtra("email", et_affirm_email.getText().toString());
                                    setResult(Constant.EDIT_EMAIL, intent);
                                    break;
                                case Constant.EDIT_NICKNAME:
                                    Log.e(TAG, "昵称保存成功|" + et_nickname.getText().toString());
                                    intent.putExtra("nickname", et_nickname.getText().toString());
                                    setResult(Constant.EDIT_NICKNAME, intent);
                                    break;
                                case Constant.EDIT_PASS:
                                    Log.e(TAG, "密码修改成功|" + et_newPass.getText().toString());
                                    setResult(Constant.EDIT_PASS, intent);
                                    break;
                            }
                            finish();
                        }
                    }
                    break;
                case EDIT_FALSE:
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
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess){
                            UploadAdapter_Edit();
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
                                Log.e(TAG, "手机号或邮箱存在");
                                prompt("邮箱已存在");
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
                    Log.e(TAG, Constant.RESULT + API.VALIDATE_MEMBER_INFO + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(VERIFY_MEMBER_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(VERIFY_MEMBER_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 编辑个人信息请求*/
    private void UploadAdapter_Edit() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EDIT_MEMBER_INFO);
        ajaxParams.put("token", token);
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_Edit());
        Log.e(TAG, Constant.REQUEST + API.EDIT_MEMBER_INFO + "\n" + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.EDIT_MEMBER_INFO + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(EDIT_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(EDIT_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * 编辑个人信息
     * member_id	会员ID
     * member_name	会员名称
     * email	邮箱
     * sex	性别(1-男性/2-女性)
     * old_password	原密码
     * password	密码
     * @return json
     */
    private String makeJsonText_Edit() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(EditAccountActivity.this, Constant.KEY_MEMBER_ID));
            switch (editType){
                case Constant.EDIT_EMAIL:
                    json.put("email", et_affirm_email.getText().toString());
                    break;
                case Constant.EDIT_NICKNAME:
                    json.put("member_name", et_nickname.getText().toString());
                    break;
                case Constant.EDIT_PASS:
                    json.put("old_password", et_oldPass.getText().toString());
                    json.put("password", et_newPass.getText().toString());
                    break;
            }
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
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("validate_type", verifyType + "");
            json.put("validate_value", et_affirm_email.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    TopBar.ButtonOnClick SaveClickListener = new TopBar.ButtonOnClick(){

         @Override
         public void onClick(View v) {
             switch (editType){
                 case Constant.EDIT_EMAIL:
                     //校验邮箱是否存在
                    if(verifyEmail()){
                        UploadAdapter();
                    }
                     break;
                 case Constant.EDIT_MOBILE:
                     //暂不实现
                     break;
                 case Constant.EDIT_NICKNAME:
                    if(verifyNickname()){
                        //修改昵称请求
                        UploadAdapter_Edit();
                    }else{
                        prompt("昵称不能为空");
                    }
                     break;
                 case Constant.EDIT_PASS:
                    if(verifyPass()){
                        //修改密码请求
                        UploadAdapter_Edit();
                    }
                     break;
             }
         }
     };

    /** 校验邮箱*/
    private boolean verifyEmail(){
        Boolean isTrue = true;
        String email = et_email.getText().toString();
        String affirmEmail = et_affirm_email.getText().toString();
        if(!StringUtils.isBlank(email) && !StringUtils.isBlank(affirmEmail)){
            if(email.equals(affirmEmail)){
                if(!MatcherUtil.Matcher(Constant.MATCHER_EMAIL, email)){
                    prompt("邮箱格式不正确");
                    isTrue = false;
                }else{
                    //校验的类型为邮箱
                    verifyType = Constant.MATCHER_EMAIL;
                    isTrue = true;
                }
            }else{
                prompt("两次邮箱输入不一致");
                isTrue = false;
            }
        }else{
            prompt("邮箱不能为空");
            isTrue = false;
        }
        return isTrue;
    }

    /** 校验昵称*/
    private boolean verifyNickname(){
        return !StringUtils.isBlank(et_nickname.getText().toString());
    }

    /** 校验密码*/
    private boolean verifyPass(){
        Boolean isTrue = true;
        String oldPass = et_oldPass.getText().toString();
        String newPass = et_newPass.getText().toString();
        String affirmNewPass = et_affirm_newPass.getText().toString();
        if(!StringUtils.isBlank(oldPass) && !StringUtils.isBlank(newPass) && !StringUtils.isBlank(affirmNewPass)){
            if(newPass.equals(affirmNewPass)){
                isTrue = true;
            }else{
                prompt("两次密码输入不一致");
                isTrue = false;
            }
        }else{
            prompt("密码不能为空");
            isTrue = false;
        }
        return isTrue;
    }
}
