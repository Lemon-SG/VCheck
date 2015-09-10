package cc.siyo.iMenu.VCheck.activity.setting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.android.tpush.XGPushConfig;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.BaseActivity;
import cc.siyo.iMenu.VCheck.activity.MineActivity;
import cc.siyo.iMenu.VCheck.http.LHttpLib;
import cc.siyo.iMenu.VCheck.http.LHttpResponseHandler;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.Member;
import cc.siyo.iMenu.VCheck.model.ProFile;
import cc.siyo.iMenu.VCheck.model.ThirdPartInfo;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.TimeUtil;
import cc.siyo.iMenu.VCheck.util.Util;
import cc.siyo.iMenu.VCheck.view.PromptDialog;
import cc.siyo.iMenu.VCheck.view.TopBar;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by Lemon on 2015/5/12.
 * Desc:账户设置界面
 */
public class AccountSettingActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "AccountSettingActivity";
    /** 退出登录按钮*/
    @ViewInject(id = R.id.tv_login_out)private TextView tv_login_out;
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 邮箱布局*/
    @ViewInject(id = R.id.rl_email)private RelativeLayout rl_email;
    /** 邮箱状态显示*/
    @ViewInject(id = R.id.tv_setting_email)private TextView tv_setting_email;
    /** 昵称布局*/
    @ViewInject(id = R.id.rl_nickname)private RelativeLayout rl_nickname;
    /** 昵称显示*/
    @ViewInject(id = R.id.tv_setting_nickName)private TextView tv_setting_nickName;
    /** 头像布局*/
    @ViewInject(id = R.id.rl_member_icon)private RelativeLayout rl_member_icon;
    /** 头像显示*/
    @ViewInject(id = R.id.iv_member_icon)private ImageView iv_member_icon;
    /** 手机号布局*/
    @ViewInject(id = R.id.rl_mobile)private RelativeLayout rl_mobile;
    /** 手机号显示*/
    @ViewInject(id = R.id.tv_setting_mobile)private TextView tv_setting_mobile;
    /** 密码布局*/
    @ViewInject(id = R.id.rl_pass)private RelativeLayout rl_pass;
    /** 新浪微博启用状态显示*/
    @ViewInject(id = R.id.tv_setting_sina)private TextView tv_setting_sina;
    /** 微信启用状态显示*/
    @ViewInject(id = R.id.tv_setting_weChat)private TextView tv_setting_weChat;
    @ViewInject(id = R.id.rlSettingSina)private RelativeLayout rlSettingSina;
    @ViewInject(id = R.id.rlSettinWeChat)private RelativeLayout rlSettinWeChat;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 注销推送设备成功标石*/
    private static final int EDIT_PUSH_DEVICE_SUCCESS = 300;
    /** 登出成功标石*/
    private static final int LOGOUT_SUCCESS = 100;
    /** 登出失败标石*/
    private static final int LOGOUT_FALSE = 200;
    /** 邮箱已绑定标石*/
    private boolean isHadEmail = true;
    private static final int CAMERA = 1;
    /** 上传头像成功 */
    private static final int ICON_SUCCESS = 1000;
    /** 上传头像失败 */
    private static final int ICON_FAILED = 1001;
    /** 头像文件全路径 **/
    private String headFile;
    /** 上传的图片对象 **/
    private Bitmap bitmap;
    private PromptDialog promptDialog;
    private ImageLoader imageLoader;
    private DisplayImageOptions option;

    @Override
    public int getContentView() {
        imageLoader = ImageLoader.getInstance();
        option = MyApplication.getDisplayImageOptions(context, 30, R.drawable.default_member);
        return R.layout.activity_account_setting;
    }

    @Override
    public void initView() {
        tv_login_out.setOnClickListener(this);
        rl_email.setOnClickListener(this);
        rl_nickname.setOnClickListener(this);
        rl_mobile.setOnClickListener(this);
        rl_pass.setOnClickListener(this);
        rl_member_icon.setOnClickListener(this);
        topbar.settitleViewText("账户设置");
        topbar.setHiddenButton(TopBar.RIGHT_BUTTON);
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //确定登陆

            }
        });
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //返回
                setResult(Constant.RESULT_CODE_EDIT_ACCOUNT);
                finish();
            }
        });
    }

    @Override
    public void initData() {
        finalHttp = new FinalHttp();
        if(getIntent().getExtras() != null){
            ThirdPartInfo thirdpart_info = (ThirdPartInfo)getIntent().getExtras().getSerializable("thirdpart_info");
            Member member = (Member)getIntent().getExtras().getSerializable("MEMBER");
            tv_setting_mobile.setText(hidMobile(member.mobile));
            tv_setting_nickName.setText(member.member_name);

            imageLoader.displayImage(member.icon_image.thumb, iv_member_icon, option);
            if(thirdpart_info.weixin_bind.equals(Constant.BIND_WECHAT_SINA)) {
                //绑定微信
                tv_setting_weChat.setText("已启用");
                tv_setting_weChat.setTextColor(getResources().getColor(R.color.top_black));
                rlSettinWeChat.setOnClickListener(null);
            } else {
                tv_setting_weChat.setText("未启用");
                tv_setting_weChat.setTextColor(getResources().getColor(R.color.gray_87));
                rlSettinWeChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog(getResources().getString(R.string.loading));
                        Platform weChat = ShareSDK.getPlatform(context, Wechat.NAME);
                        weChat.setPlatformActionListener(new WeChatPlatformActionListener());
                        weChat.showUser(null);
                    }
                });
            }
            if(thirdpart_info.weibo_bind.equals(Constant.BIND_WECHAT_SINA)) {
                //绑定微博
                tv_setting_sina.setText("已启用");
                tv_setting_sina.setTextColor(getResources().getColor(R.color.top_black));
                rlSettingSina.setOnClickListener(null);
            } else {
                tv_setting_sina.setText("未启用");
                tv_setting_sina.setTextColor(getResources().getColor(R.color.gray_87));
                rlSettingSina.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog(getResources().getString(R.string.loading));
                        Platform sina = ShareSDK.getPlatform(context, SinaWeibo.NAME);
                        sina.setPlatformActionListener(new SinaPlatformActionListener());
                        sina.showUser(null);
                    }
                });
            }
            if(!StringUtils.isBlank(member.email)){
                tv_setting_email.setText(hidEmail(member.email));
                isHadEmail = true;
            }else{
                isHadEmail = false;
                tv_setting_email.setText("未绑定");
            }
        }
        promptDialog = new PromptDialog(context, "提示", "确定要退出吗？", "确定", "取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出
                UploadEditPushDev();
                promptDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptDialog.dismiss();
            }
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGOUT_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess){
                            PreferencesUtils.clear(AccountSettingActivity.this);
                            PreferencesUtils.putInt(context, Constant.KEY_IS_LAUNCH, 1);
                            setResult(Constant.RESULT_CODE_LOGOUT);
                            finish();
                        }
                    }
                    break;
                case ICON_FAILED:
                case LOGOUT_FALSE:
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
                case ICON_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess){
                            prompt("头像上传成功");
                            imageLoader.displayImage("file://" + headFile, iv_member_icon, option);
//                            iv_member_icon.setImageBitmap(bitmap);
                        }
                    }
                    break;
            }
        }
    };

    /** 注销推送设备请求*/
    private void UploadEditPushDev() {
        LHttpLib.editPushDevice(context, memberId, XGPushConfig.getToken(context), Constant.OPERATOR_TYPE_DEL + "",
                new LHttpResponseHandler() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }

                    @Override
                    public void onSuccess(JSONStatus jsonStatus) {
                        if(jsonStatus.isSuccess) {
                            UploadAdapter();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {

                    }
                });
    }

    /** 登出请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.LOGOUT);
        ajaxParams.put("token", token);
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.LOGOUT + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
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
                if (!StringUtils.isBlank(t)) {
                    Log.e(TAG, Constant.RESULT + API.LOGOUT + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(LOGOUT_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(LOGOUT_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 获取文件，上传 */
    public File getFile() {
        File ss = new File(headFile);
        if (!ss.isDirectory()) {
            ss.mkdirs();
        }
        Log.e(TAG, "submit headFile ->" + ss);
        return ss;
    }

    /** 上传头像请求 */
    private void uploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EDIT_MEMBER_ICON);
        ajaxParams.put("token", token);
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_EditIcon());
        try {
            InputStream inputStream = new FileInputStream(getFile());
            if (bitmap != null) {
                ajaxParams.put("image", inputStream, getFile().getName(), "image/jpeg");
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        Log.e(TAG, Constant.REQUEST + API.EDIT_MEMBER_ICON + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
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
                if (!StringUtils.isBlank(t)) {
                    Log.e(TAG, Constant.RESULT + API.EDIT_MEMBER_ICON + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(ICON_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(ICON_FAILED, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * member_id	会员ID
     * device_token	设备推送号码
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(AccountSettingActivity.this, Constant.KEY_MEMBER_ID));
            json.put("device_token", XGPushConfig.getToken(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * member_id	会员ID
     * @return json
     */
    private String makeJsonText_EditIcon() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(AccountSettingActivity.this, Constant.KEY_MEMBER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login_out:
                //退出登录
                promptDialog.show();
                break;
            case R.id.rl_email:
                //修改邮箱,需要判断是否已经绑定邮箱，如已绑定不可点击跳转修改
                if(isHadEmail || !(tv_setting_email.getText().toString()).equals("未绑定")){
                    prompt("邮箱不可修改");
                }else{
                    Intent intent_email = new Intent(AccountSettingActivity.this, EditAccountActivity.class);
                    intent_email.putExtra(Constant.EDIT_ACCOUNT, Constant.EDIT_EMAIL);
                    startActivityForResult(intent_email, Constant.RESQUEST_CODE);
                }
                break;
            case R.id.rl_nickname:
                //修改昵称
                Intent intent_nickname = new Intent(AccountSettingActivity.this, EditAccountActivity.class);
                intent_nickname.putExtra(Constant.EDIT_ACCOUNT, Constant.EDIT_NICKNAME);
                intent_nickname.putExtra("nickname", tv_setting_nickName.getText().toString());
                startActivityForResult(intent_nickname, Constant.RESQUEST_CODE);
                break;
            case R.id.rl_mobile:
                //修改手机号，暂不实现
                break;
            case R.id.rl_pass:
                //修改密码
                Intent intent_pass = new Intent(AccountSettingActivity.this, EditAccountActivity.class);
                intent_pass.putExtra(Constant.EDIT_ACCOUNT, Constant.EDIT_PASS);
                startActivity(intent_pass);
                break;
            case R.id.rl_member_icon:
                //修改头像
                headFile = Constant.PATH_HEADPHOTO_IMG + PreferencesUtils.getString(context, Constant.KEY_MOBILE) + System.currentTimeMillis() + ".jpg";
                Log.e(TAG, "init headFile->" + headFile);
                on_CreateDialog();
                break;
        }
    }

    /** 隐藏邮箱*/
    private String hidEmail(String email){
        int IndexAT = email.indexOf("@");
        StringBuffer hidCode = new StringBuffer();
        for (int i = 0; i < email.substring(2, IndexAT).length(); i++) {
            hidCode.append("*");
        }
        return email.replace(email.substring(2, IndexAT), hidCode);
    }

    /** 隐藏电话*/
    private String hidMobile(String mobile){
        return  mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4, mobile.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.RESQUEST_CODE){
            switch (resultCode){
                case Constant.EDIT_EMAIL:
                    if(data != null){
                        Log.e(TAG, "修改邮箱数据返回|" + data.getStringExtra("email"));
                        tv_setting_email.setText(hidEmail(data.getStringExtra("email")));
                    }
                    break;
                case Constant.EDIT_NICKNAME:
                    if(data != null){
                        Log.e(TAG, "修改昵称数据返回|" + data.getStringExtra("nickname"));
                        tv_setting_nickName.setText(data.getStringExtra("nickname"));
                    }
                    break;
                case Constant.EDIT_PASS:
                    Log.e(TAG, "修改密码返回");
                    break;
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                /** 相册选择照片结果 */
                case Constant.USERINFO_IAMGELIB_REQUEST:
                    Uri originalUri = data.getData();
                    Util.startPhotoZoom(originalUri, Constant.USERINFO_CLIP_REQUEST, this);
                    break;
                /** 相机拍照结果 */
                case Constant.USERINFO_CAMERA_REQUEST:
                    Util.startPhotoZoom(Uri.fromFile(Util.getFileHeadPoto()), Constant.USERINFO_CLIP_REQUEST, this);
                    break;
                /** 修剪后的图片结果 */
                case Constant.USERINFO_CLIP_REQUEST:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            bitmap = extras.getParcelable("data");
                            if (bitmap != null) {
                                try {
                                    System.out.println("图片路径:" + headFile);
                                    Util.saveMyBitmap(headFile, bitmap);
                                    uploadAdapter();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (!bitmap.isRecycled()) {
                                    bitmap.isRecycled();
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    /** 拍照选择 */
    private void on_CreateDialog() {
        new AlertDialog.Builder(this)
                .setTitle("照片")
                .setItems(new String[] { "相册", "拍照", },
                        new Dialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Boolean isSdcardExist = Environment
                                        .getExternalStorageState().equals(
                                                Environment.MEDIA_MOUNTED);
                                switch (which) {
                                    case 0:
                                        if (isSdcardExist) {
                                            Intent intent = new Intent(Intent.ACTION_PICK, null);
                                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                            startActivityForResult(intent, Constant.USERINFO_IAMGELIB_REQUEST);
                                        } else {
                                            dialog.cancel();
                                        }

                                        break;
                                    case 1:
                                        if (isSdcardExist) {
                                            Photo();
                                        } else {
                                            dialog.cancel();
                                        }
                                        break;
                                }
                            }
                        }).show();
    }

    /** 照片页面请求 */
    private void Photo() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(Util.getFileHeadPoto()));
        startActivityForResult(intent, Constant.USERINFO_CAMERA_REQUEST);
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

    /** 微信回调监听*/
    private class WeChatPlatformActionListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            closeProgressDialog();
            Log.e(TAG, "微信登录回调onComplete ->");
            if(hashMap != null && !hashMap.isEmpty()){//绑定微信
                LHttpLib.editWithWx(context, memberId, "1", new ProFile().GetWxJson(hashMap),
                        new LHttpResponseHandler() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onLoading(long count, long current) {

                            }

                            @Override
                            public void onSuccess(JSONStatus jsonStatus) {
                                closeProgressDialog();
                                if(jsonStatus.isSuccess) {
                                    prompt("绑定微信成功");
                                    tv_setting_weChat.setText("已启用");
                                    tv_setting_weChat.setTextColor(getResources().getColor(R.color.top_black));
                                    rlSettinWeChat.setOnClickListener(null);
                                } else {
                                    if(!StringUtils.isBlank(jsonStatus.error_desc)){
                                        if(jsonStatus.error_code.equals("2022")) {
                                            //此微信已绑定
                                            prompt("该微信已绑定其他账号");
                                        } else {
                                            prompt(jsonStatus.error_desc);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Throwable t, int errorNo, String strMsg) {
                                closeProgressDialog();
                            }
                        });
            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            closeProgressDialog();
            Log.e(TAG, "微信登录回调onError ->");
            throwable.printStackTrace();
        }

        @Override
        public void onCancel(Platform platform, int i) {
            closeProgressDialog();
            Log.e(TAG, "微信登录回调onCancel ->");
        }
    }
}
