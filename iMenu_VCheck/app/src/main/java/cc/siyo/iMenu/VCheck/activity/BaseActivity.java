package cc.siyo.iMenu.VCheck.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import net.tsz.afinal.FinalActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.Share;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.LoadingDialog;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public abstract class BaseActivity extends FinalActivity {

    private static final String TAG = "BaseActivity";
    public Context context;
    protected final static int SUCCESS = 100;
    protected final static int FAILURE = 101;
    protected String token;
    private NetworkChangedReceiver receiver;

    private LoadingDialog loadingDialog;
    /**每页显示几条数据*/
    protected final static int PAGESIZE = 10;
    /**显示第几页*/
    protected final static int PAGE = 1;
    /** 全局微信分享成功标石*/
    public final static int SHARE_SUCCESS = 10000;
    /** 全局微信分享失败标石*/
    public final static int SHARE_FAILED = 10001;
    /** 全局微信分享取消标石*/
    public final static int SHARE_CANCEL = 10002;
    /** 全局微信分享状态,默认为失败，如在回调中呗重置，即更改状态*/
    public static int SHARE_STATUS = SHARE_FAILED;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityStackManager.getActivityManager().push(this);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentView());
        token = PreferencesUtils.getString(context, Constant.KEY_TOKEN);
        Log.e(TAG, "token=" + token);
//        colorValue = PreferencesUtils.getString(context, "ColorValue");
//        receiver = new NetworkChangedReceiver();
        initView();
        initData();
        ShareSDK.initSDK(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        token = PreferencesUtils.getString(context, "token");
//        colorValue = PreferencesUtils.getString(context, "ColorValue");
//        registerReceiver(receiver, new IntentFilter(
//                ConnectivityManager.CONNECTIVITY_ACTION));
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(receiver);
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ActivityStackManager.getActivityManager().remove(this);
    }

    /**
     * 返回当前所需要的布局文件 *
     */
    public abstract int getContentView();

    /**
     * 统一初始化view的方法
     *
     * @author Sylar *
     */
    public abstract void initView();

    /**
     * 统一初始化数据的方法
     *
     * @author Sylar *
     */
    public abstract void initData();

    /**
     * 非阻塞提示方式 *
     */
    public void prompt(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 进行耗时阻塞操作时,需要调用改方法,显示等待效果
     *
     * @author Sylar *
     */
    public void showProgressDialog(String content) {
        // if (dialog == null) {
        // dialog = new ProgressDialog(this);
        // dialog.setCancelable(false);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // }
        // dialog.setMessage(content);
        // if (!isFinishing() && !dialog.isShowing()) {
        // dialog.show();
        // }
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this, content);
        }
        if (!isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

	public void showProgressDialog() {
		if (loadingDialog == null) {
			loadingDialog = new LoadingDialog(this,"加载中...");
		}
		if (!isFinishing() && !loadingDialog.isShowing()) {
			loadingDialog.show();
		}
	}

    /**
     * 耗时阻塞操作结束时,需要调用改方法,关闭等待效果
     *
     * @author Sylar *
     */
    public void closeProgressDialog() {
        if (loadingDialog != null && !isFinishing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
//        if (loadingDialog != null && !isFinishing()) {
//            loadingDialog.dismiss();
//        }
    }

    /***
     * 显示对户框，只有一个确定按钮
     * @param context
     * @param msg
     * @param lister 确定按钮监听
     * @param isConfirm true:只有一个确定按钮 false: 带取消按钮
     * @return
     */
    public AlertDialog showDialog(Activity context, String msg,
                                  OnClickListener lister, boolean isConfirm) {
        Builder builder = new Builder(context);
        builder.setTitle("提示");
        builder.setMessage(msg);
        if(isConfirm){
            builder.setPositiveButton("确定", lister);
        }else{
            builder.setPositiveButton("确定", lister);
            builder.setNegativeButton("取消", null);
        }
        AlertDialog dialog2 = builder.create();
        dialog2.setCancelable(false);
        return dialog2;
    }

    /***
     * 显示对户框，只有一个确定按钮
     * @param context
     * @param msg
     * @param lister1 确定按钮监听
     * @param lister2 取消按钮监听
     * @param isConfirm true:只有一个确定按钮 false: 带取消按钮
     * @return
     */
    public AlertDialog showDialog(Activity context, String msg,
                                  OnClickListener lister1, OnClickListener lister2, boolean isConfirm) {
        Builder builder = new Builder(context);
        builder.setTitle("提示");
        builder.setMessage(msg);
        if(isConfirm){
            builder.setPositiveButton("确定", lister1);
        }else{
            builder.setPositiveButton("确定", lister1);
            builder.setNegativeButton("取消", lister2);
        }
        AlertDialog dialog2 = builder.create();
        dialog2.setCancelable(false);
        return dialog2;
    }

    /**
     * token过期显示对话框，一个强制操作按钮
     *
     * @param context
     * @param msg
     * @param lister
     * @return
     */
    public void showTokenDialog(Activity context, String msg, OnClickListener lister) {
    	if(context != null){
//    		PreferencesUtils.putString(this, "member_id", "");
//        	PreferencesUtils.putString(this, "token", "");
            Builder builder = new Builder(this);
//            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", lister);
            builder.setMessage(msg);
            builder.setTitle("提示");
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            if(dialog != null && !dialog.isShowing()){
            	dialog.show();
            }
    	}
    	closeProgressDialog();
    }

    private class NetworkChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(
//                    ConnectivityManager.CONNECTIVITY_ACTION)) {
//                // 仅在Wi-Fi下加载图片
//                if (PreferencesUtils.getBoolean(context, "LoadImageOnWifiOnly",
//                        true)) {
//                    if (NetStateUtil.isWifiConnected(context)) {
//                        ImageLoader.getInstance().denyNetworkDownloads(false);
//                    } else {
//                        ImageLoader.getInstance().denyNetworkDownloads(true);
//                    }
//                } else {
//                    if (NetStateUtil.isNetConnected(context)) {
//                        ImageLoader.getInstance().denyNetworkDownloads(false);
//                    } else {
//                        ImageLoader.getInstance().denyNetworkDownloads(true);
//                    }
//                }
//            }
        }
    }
    
    public static void onBackFinishTransition(Activity activity){
    	activity.finish();
//		((Activity)activity).overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
    }
    
    /** 返回默认为淡入淡出动画*/
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	finish();
    	overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /** 公共解析返回数据成功与否
     t:{
     "status": {
     "succeed": "0",
     "error_code": "2001",
     "error_desc": "用户名或密码错误"
     }
     }
     t:{
     "status": {
     "succeed": "1"
     },
     "data": {
     "seller_id": "1",
     "token": "8f0226b243cb63280a069412c121c66d"
     }
     }
     * */
    public JSONStatus BaseJSONData(String t){
        JSONStatus jsonStaus = new JSONStatus();
        try {
            JSONObject obj = new JSONObject(t);
            if(obj != null && obj.length() > 0){
                jsonStaus = new JSONStatus().parse(obj);
                if(!StringUtils.isBlank(jsonStaus.error_desc)){
                    if(jsonStaus.error_code.equals(Constant.TOKEN_ERROR_CODE)){
                        prompt("登录过期，请重新登录");
                        System.out.println("TOKEN_ERROR_CODE");
                        PreferencesUtils.clear(context);
//                        Intent intent = new Intent(context, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
                        return null;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStaus;
    }

    /** 传送页数封装*/
    public JSONObject makeJsonPageText(int page, int count){
        JSONObject json = new JSONObject();
        try {
            json.put("page", page + "");
            json.put("count", count + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /** 本类存储至本地属性*/
    public void savePreferences(String memberId, String token){
        PreferencesUtils.putString(this, Constant.KEY_TOKEN, token);
        PreferencesUtils.putString(this, Constant.KEY_MEMBER_ID, memberId);
    }

    /** 启动分享*/
    public void shareTo(Share mShare, int shareType) {
        if (mShare == null)
            return;
        ShareSDK.initSDK(context);
        showProgressDialog(context.getString(R.string.loading));
        switch (shareType) {
            case Constant.SHARE_TYPE_SINA:
                Platform sina = ShareSDK.getPlatform(context, SinaWeibo.NAME);
                SinaWeibo.ShareParams sinaParams = new SinaWeibo.ShareParams();
                sinaParams.setTitle(mShare.title);
                sinaParams.setText(mShare.content);
                sinaParams.setSite(mShare.description);
                sinaParams.setSiteUrl(mShare.link);
                sinaParams.setImagePath(mShare.imagePath);
                sina.setPlatformActionListener(new SinaPlatformActionListener());
                sina.share(sinaParams);
                break;
            case Constant.SHARE_TYPE_RENREN:
//                Platform renren = ShareSDK.getPlatform(context, Renren.NAME);
//                Renren.ShareParams renrenParams = new Renren.ShareParams();
//                renrenParams.setTitle(mShare.title);
//                renrenParams.setTitleUrl(mShare.link);
//                renrenParams.setText(mShare.description);
//                renrenParams.setComment(mShare.content);
//                renrenParams.setImageUrl(mShare.imageUrl);
//                renrenParams.setImagePath(mShare.imagePath);
//                renrenParams.setImageData(bitmap);
//                renren.setPlatformActionListener(this);
//                renren.share(renrenParams);
                break;
            case Constant.SHARE_TYPE_WECHAT:
                Platform weChat = ShareSDK.getPlatform(context, Wechat.NAME);
                Wechat.ShareParams weChatParams = new Wechat.ShareParams();
                weChatParams.setTitle(mShare.title);
                weChatParams.setText(mShare.content);
                weChatParams.setUrl(mShare.link);
                weChatParams.setSiteUrl(mShare.link);
//                if(mShare.imagePath.equals("")){
//                	weChatParams.setImageData(mShare.imageBitmap);
//                }else{
//                	weChatParams.setImageUrl(mShare.imageUrl);
//                	weChatParams.setImagePath(mShare.imagePath);
//                }
                weChatParams.setImageUrl(mShare.imageUrl);
                weChatParams.setImagePath(mShare.imagePath);
                weChatParams.setShareType(Platform.SHARE_WEBPAGE);
                weChat.setPlatformActionListener(new WeChatPlatformActionListener());
                weChat.share(weChatParams);
                break;
            case Constant.SHARE_TYPE_WECHAT_MOMENT:
                Platform weChatMoment = ShareSDK.getPlatform(context, WechatMoments.NAME);
                WechatMoments.ShareParams weChatMomentParams = new WechatMoments.ShareParams();
                weChatMomentParams.setTitle(mShare.content);
//                weChatMomentParams.setText(mShare.content);
                weChatMomentParams.setUrl(mShare.link);
                weChatMomentParams.setSiteUrl(mShare.link);
//                if(mShare.imagePath.equals("")){
//                	weChatMomentParams.setImageData(mShare.imageBitmap);
//                }else{
//                	weChatMomentParams.setImageUrl(mShare.imageUrl);
//                	weChatMomentParams.setImagePath(mShare.imagePath);
//                }
                weChatMomentParams.setImageUrl(mShare.imageUrl);
                weChatMomentParams.setImagePath(mShare.imagePath);
                System.out.println("mShare.imagePath:"+mShare.imagePath);
                weChatMomentParams.setShareType(Platform.SHARE_WEBPAGE);
                weChatMoment.setPlatformActionListener(new WeChatMomentPlatformActionListener());
                weChatMoment.share(weChatMomentParams);
                break;
        }
    }

    /** 新浪回调监听*/
    private class SinaPlatformActionListener implements PlatformActionListener{

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(Platform platform, int i) {

        }
    }

    /** 微信分享回调监听*/
    private class WeChatPlatformActionListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(Platform platform, int i) {

        }
    }

    /** 微信朋友圈回调监听*/
    private class WeChatMomentPlatformActionListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(Platform platform, int i) {

        }
    }


}
