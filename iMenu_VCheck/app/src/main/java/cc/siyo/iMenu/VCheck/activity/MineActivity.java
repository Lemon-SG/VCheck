package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.setting.AboutUsActivity;
import cc.siyo.iMenu.VCheck.activity.setting.AccountSettingActivity;
import cc.siyo.iMenu.VCheck.activity.setting.AppSetActivity;
import cc.siyo.iMenu.VCheck.activity.setting.FeedbackActivity;
import cc.siyo.iMenu.VCheck.activity.setting.MessageActivity;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.Member;
import cc.siyo.iMenu.VCheck.model.OrderInfo;
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
    /** 反馈按钮*/
    @ViewInject(id = R.id.ll_feedback)private LinearLayout ll_feedback;
    /** 我的订单按钮*/
    @ViewInject(id = R.id.ll_order_list)private LinearLayout ll_order_list;
    /** 我收藏的按钮*/
    @ViewInject(id = R.id.ll_collect_list)private LinearLayout ll_collect_list;
    /** 联系客服按钮*/
    @ViewInject(id = R.id.ll_contention_server)private LinearLayout ll_contention_server;
    /** 关于按钮*/
    @ViewInject(id = R.id.ll_about)private LinearLayout ll_about;
    /** 应用设置按钮*/
    @ViewInject(id = R.id.ll_app_set)private LinearLayout ll_app_set;
    /** 启动画面进入*/
    @ViewInject(id = R.id.tv_open_launch)private TextView tv_open_launch;
    /** 昵称显示*/
    @ViewInject(id = R.id.tv_mine_nickName)private TextView tv_mine_nickName;
    /** 头像*/
    @ViewInject(id = R.id.iv_user_headImg)private ImageView iv_user_headImg;
    /** 订单数量显示*/
    @ViewInject(id = R.id.tv_order_count)private TextView tv_order_count;
    /** 礼券数量显示*/
    @ViewInject(id = R.id.tv_coupon_count)private TextView tv_coupon_count;
    /** 收藏数量显示*/
    @ViewInject(id = R.id.tv_collect_count)private TextView tv_collect_count;
    /** 消息中心*/
    @ViewInject(id = R.id.tv_message_center)private TextView tv_message_center;
    /** 邀请好友*/
    @ViewInject(id = R.id.tv_share)private TextView tv_share;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 登出成功标石*/
    private static final int GET_MEMBER_DETAIL_SUCCESS = 100;
    /** 登出失败标石*/
    private static final int GET_MEMBER_DETAIL_FALSE = 200;
    private Member member;
    /** A FINAL 框架的HTTP请求工具*/
    private FinalBitmap finalBitmap;
    /** 邀请码*/
    private String invite_code;

    @Override
    public int getContentView() {
        finalBitmap = FinalBitmap.create(this);
        finalBitmap.configLoadingImage(R.drawable.ic_member);
        finalBitmap.configLoadfailImage(R.drawable.ic_member);
        return R.layout.activity_mine;
    }

    @Override
    public void initView() {
        iv_user_headImg.setOnClickListener(this);
        tv_mine_nickName.setOnClickListener(this);
        ll_feedback.setOnClickListener(this);
        ll_order_list.setOnClickListener(this);
        ll_collect_list.setOnClickListener(this);
        ll_contention_server.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        ll_app_set.setOnClickListener(this);
        tv_open_launch.setOnClickListener(this);
        tv_message_center.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        topbar.settitleViewText("我的账户");
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
                @Override
                public void onClick(View view) {
                    finish();
                }
        });
    }

    @Override
    public void initData() {
        finalHttp = new FinalHttp();
        if(StringUtils.isBlank(PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN))
            || PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN).equals("null")){
            //未登录状态
            tv_mine_nickName.setText("立即登录赢礼券");
        }else{
            //已登录状态
            UploadAdapter();
        }
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
                                Log.e(TAG, member.icon_image.thumb);
                                finalBitmap.display(iv_user_headImg, member.icon_image.thumb);
                            }
                            if(data.optJSONObject("order_info") != null){//订单
                                String pendingOrderCount = data.optJSONObject("order_info").optString("order_pending_count");
                                if(!pendingOrderCount.equals("0")) {
                                    tv_order_count.setText(pendingOrderCount);
                                    tv_order_count.setVisibility(View.VISIBLE);
                                }
                            }
                            if(data.optJSONObject("collection_info") != null){//收藏//TODO 接口有误
                                String couponTotalCount = data.optJSONObject("collection_info").optString("coupon_total_count");
                                if(!couponTotalCount.equals("0")) {
                                    tv_collect_count.setText(couponTotalCount);
                                    tv_collect_count.setVisibility(View.VISIBLE);
                                }
                            }
                            if(data.optJSONObject("coupon_info") != null){//礼券详情
                            }
                            if(data.optJSONObject("share_info") != null){//分享详情：邀请码
                                invite_code = data.optJSONObject("share_info").optString("invite_code");
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
                closeProgressDialog();
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
            case R.id.iv_user_headImg:
            case R.id.tv_mine_nickName:
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
            case R.id.ll_feedback://跳转意见反馈
                if(!StringUtils.isBlank(PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN))){
                    startActivity(new Intent(MineActivity.this, FeedbackActivity.class));
                }else{prompt("请先登录");}
                break;
            case R.id.ll_order_list://我的订单
                if(!StringUtils.isBlank(PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN))){
                    startActivity(new Intent(MineActivity.this, OrderListActivity.class));
                }else{prompt("请先登录");}
                break;
            case R.id.ll_collect_list://收藏列表
                if(!StringUtils.isBlank(PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN))){
                    startActivity(new Intent(MineActivity.this, CollectListActivity.class));
                }else{prompt("请先登录");}
                break;
            case R.id.ll_contention_server://联系客服

                break;
            case R.id.ll_about://关于我们
                startActivity(new Intent(MineActivity.this, AboutUsActivity.class));
                break;
            case R.id.tv_open_launch://启动画面进入

                break;
            case R.id.ll_app_set://应用设置
                startActivity(new Intent(MineActivity.this, AppSetActivity.class));
                break;
            case R.id.tv_message_center://消息中心
                if(!StringUtils.isBlank(PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN))){
                    startActivity(new Intent(MineActivity.this, MessageActivity.class));
                }else{prompt("请先登录");}

                break;
            case R.id.tv_share://邀请好友
                if(!StringUtils.isBlank(PreferencesUtils.getString(MineActivity.this, Constant.KEY_TOKEN))){
                    Intent intent = new Intent(MineActivity.this, InviteFriendsActivity.class);
                    intent.putExtra(Constant.INTENT_INVITE_CODE, invite_code);
                    startActivity(intent);
                }else{prompt("请先登录");}
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
                    UploadAdapter();
                    break;
                case Constant.RESULT_CODE_EDIT_ACCOUNT:
                    //登陆成功
                    Log.e(TAG, "修改账户信息返回，在本地更新昵称等");
                    UploadAdapter();
                    break;
                case Constant.RESULT_CODE_LOGOUT:
                    //退出登录
                    tv_mine_nickName.setText("立即登录赢礼券");
                    iv_user_headImg.setImageResource(R.drawable.ic_member);
                    break;
            }
        }
    }
}
