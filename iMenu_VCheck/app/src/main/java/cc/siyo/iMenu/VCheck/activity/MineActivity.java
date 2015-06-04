package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import cc.siyo.iMenu.VCheck.model.Member;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/4/29.
 * Desc:我的界面
 */
public class MineActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "MineActivity";
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 去登陆按钮*/
    @ViewInject(id = R.id.ll_login)private LinearLayout ll_login;
    /** 反馈按钮*/
    @ViewInject(id = R.id.ll_feedback)private LinearLayout ll_feedback;
    /** 昵称显示*/
    @ViewInject(id = R.id.tv_mine_nickName)private TextView tv_mine_nickName;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 登出成功标石*/
    private static final int GET_MEMBER_DETAIL_SUCCESS = 100;
    /** 登出失败标石*/
    private static final int GET_MEMBER_DETAIL_FALSE = 200;
    private Member member;

    @Override
    public int getContentView() {
        return R.layout.activity_mine;
    }

    @Override
    public void initView() {
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        ll_login.setOnClickListener(this);
        ll_feedback.setOnClickListener(this);
        topbar = (TopBar)findViewById(R.id.topbar);
        topbar.settitleViewText("我的账户");
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
                case GET_MEMBER_DETAIL_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(data != null){
                            if(data.optJSONObject("member_info") != null){
                                //个人详情
                                member = new Member().parse(data.optJSONObject("member_info"));
                                tv_mine_nickName.setText(member.member_name);
                            }
                            if(data.optJSONObject("order_info") != null){
                                //订单详情
//                                member = new Member().parse(data.optJSONObject("member_info"));
                            }
                            if(data.optJSONObject("collection_info") != null){
                                //收藏详情
//                                member = new Member().parse(data.optJSONObject("member_info"));
                            }
                            if(data.optJSONObject("coupon_info") != null){
                                //礼券详情
//                                member = new Member().parse(data.optJSONObject("member_info"));
                            }
                            if(data.optJSONObject("share_info") != null){
                                //分享详情：邀请码
//                                member = new Member().parse(data.optJSONObject("member_info"));
                            }
                        }
                    }
                    break;
                case GET_MEMBER_DETAIL_FALSE:
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

    /** 获取个人详情请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_MEMBER_DETAIL);
        ajaxParams.put("token", PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.GET_MEMBER_DETAIL + "\n" + ajaxParams.toString());
        finalHttp.post(API.server,  ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
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
                    Log.e(TAG, Constant.RESULT + API.GET_MEMBER_DETAIL + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(GET_MEMBER_DETAIL_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(GET_MEMBER_DETAIL_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * member_id	会员ID
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(MineActivity.this, Constant.KEY_MEMBER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_login:
                //跳转登录界面,if 登录成功点击跳转至账户设置 AccountSettingActivity
                if(StringUtils.isBlank(PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN))
                        || PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN).equals("null")){
                    Log.e(TAG, "跳转登录");
                    Intent intent_login = new Intent(MineActivity.this, LoginActivity.class);
                    startActivityForResult(intent_login, Constant.RESQUEST_CODE);
                }else{
                    Log.e(TAG, "跳转个人设置");
                    Intent intent_accountSetting = new Intent(MineActivity.this, AccountSettingActivity.class);
                    intent_accountSetting.putExtra("MEMBER", member);
                    startActivityForResult(intent_accountSetting, Constant.RESQUEST_CODE);
                }
                break;
            case R.id.ll_feedback:
                //跳转意见反馈
                if(!StringUtils.isBlank(PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN))){
                    Intent intent_feedback = new Intent(MineActivity.this, FeedbackActivity.class);
                    startActivity(intent_feedback);
                }else{
                    prompt("请先登录");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.RESQUEST_CODE){
            switch (resultCode){
                case Constant.RESULT_CODE_LOGIN:
                    //登陆成功
                    Log.e(TAG, "登陆成功，稍后去请求个人信息接口");
                    break;
                case Constant.RESULT_CODE_EDIT_ACCOUNT:
                    //登陆成功
                    Log.e(TAG, "修改账户信息返回，在本地更新昵称等");
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(StringUtils.isBlank(PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN))
                || PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN).equals("null")){
            //未登录状态
            tv_mine_nickName.setText("立即登录赢礼券");
        }else{
            //已登录状态
            UploadAdapter();
        }
    }
}
