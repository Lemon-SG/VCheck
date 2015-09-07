package cc.siyo.iMenu.VCheck.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.BannerInfo;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.FirstLaunch;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.LinkPushParams;
import cc.siyo.iMenu.VCheck.model.Region;
import cc.siyo.iMenu.VCheck.util.CheckNetWorkUtil;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;

/**
 * Created by Lemon on 2015/5/26.
 * 闪屏
 */
public class Launch extends BaseActivity{

    private static final String TAG = "Launch";
    private FinalHttp finalHttp;
    private static final int GET_INDEX_IMAGE_SUCCESS = 300;
    /** 记住登录状态成功标石*/
    private static final int LOGIN_TOKEN_SUCCESS = 100;
    private Context mContext;
    private String versionCode;
    @ViewInject(id = R.id.tv_version)private TextView tv_version;
    @ViewInject(id = R.id.ivLaunch)private ImageView ivLaunch;
    Dialog dialog;
    /** because onResume have send http one and second,so...
     *  this is isHTTP holdup send http,
     *  if http(ing) -> not send http
     */
    private boolean isHttp = false;

    @Override
    public int getContentView() {
        // open LOGCAT output , provide debug ,release is close
        XGPushConfig.enableDebug(this, true);

        // 开启logcat输出，方便debug，发布时请关闭
        // XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
        Context context = getApplicationContext();
        XGPushManager.registerPush(context);
        return R.layout.activity_launch;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        finalHttp = new FinalHttp();
        dialog  = showDialog(Launch.this, getResources().getString(R.string.no_network),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }, true);
        dialog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        mContext = this;
        if(getIntent() != null && getIntent().getDataString() != null) {
            Intent intent = getIntent();
            String data = intent.getDataString();//  vcheck://product?id=7
            Log.e(TAG, data);
            try {
                GetWebMsg(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //第一次启动为-1，启动后为1
        int firstLaunch = PreferencesUtils.getInt(context, Constant.KEY_IS_LAUNCH);
        if(firstLaunch == -1) {
            //首次安装
            PreferencesUtils.putInt(context, Constant.KEY_IS_LAUNCH, 1);
            firstLaunch = 1;
            Intent intent = new Intent(mContext, VideoActivity.class);
            startActivity(intent);
        } else {
            finalHttp = new FinalHttp();
            if (checkNetwork()) {
                if (!isHttp) {
                    doUploadLoginWithToken();
                    UploadAdapter(Constant.LAUNACH_IMG_TYPE_H);
                }
            }
            GetPushMsg();
        }
    }

    private LinkPushParams linkPushParams;

    /** 获取推送命令*/
    private void GetPushMsg() {
        XGPushClickedResult result = XGPushManager.onActivityStarted(this);
        Log.d("Launch", "onResumeXGPushClickedResult:" + result);
        //[msgId=9, title=知味, customContent={"link_value":"article_id=2","link_route":"article"}, activityName=cc.siyo.iMenu.VCheck.activity.Launch, actionType=0, notificationActionType1]
        if (result != null) { // 判断是否来自信鸽的打开方式
            try {
                linkPushParams = new LinkPushParams().parse(new JSONObject(result.getCustomContent()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /** 执行网页进入跳转相应页面
     *  首页	route=home	vcheck://?route=home
     *  产品详情	route=article&article_id=[文章ID]	vcheck://?route=article&article_id=2
     * */
    private void GetWebMsg(String data) throws JSONException {
        String paramsStr = data.substring(9, data.length());
        String[] params = paramsStr.split("&");
        JSONObject jsonObject = new JSONObject();
//        String link_route = "";
//        if(params.length > 0) {
//            link_route = params[0].substring(params[0].indexOf("=") + 1, params[0].length());//article
//        }
        for (int i = 0; i < params.length; i++) {
            if(params[i].contains("route")) {
                jsonObject.put("link_route", params[i].substring(params[i].indexOf("=") + 1, params[i].length()));//路径保存
            }
            if(params[i].contains("id")) {
                jsonObject.put("link_value", params[i]);//跳转参数
            }
            if(params[i].contains("push_type")) {
                jsonObject.put("push_type", params[i]);//跳转方式
            }
        }
//        switch (params.length) {
//            case 1:
//                //只有路径 params[0] --> route=article
//                jsonObject.put("link_route", link_route);
//                break;
//            case 2:
//                //路径+值 params[0] --> route=article | params[1] --> article_id=2
//                String link_value = params[1];//article_id=2
//                jsonObject.put("link_route", link_route);
//                jsonObject.put("link_value", link_value);
//                break;
//        }
        Log.e(TAG, "封装跳转参数为json->" + jsonObject.toString());
        linkPushParams = new LinkPushParams().parse(jsonObject);
    }

    /** 跳转广告页面*/
    private void ToIndex(final BannerInfo bannerInfo) {
        Log.e(TAG, "run -> ToIndex");
        Intent intent = new Intent(mContext, LaunchIndexActivity.class);
        intent.putExtra("BannerInfo", bannerInfo);
        intent.putExtra("linkPushParams", linkPushParams);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_INDEX_IMAGE_SUCCESS:
                    //获取广告图片成功
                    closeProgressDialog();
                    if(msg.obj != null) {
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        BannerInfo banner_info = new BannerInfo().parse(data.optJSONObject("banner_info"));
                        ToIndex(banner_info);
                    }
                    break;
                case LOGIN_TOKEN_SUCCESS:
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        String member_id = data.optString("member_id");
                        String token = data.optString("token");
                        savePreferences(member_id, token);
                    }
                    break;
            }
        }
    };

    /** 获取广告图片请求*/
    private void UploadAdapter(int type) {
        isHttp = true;
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_INDEX_IMAGE);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText(type));
        Log.e(TAG, Constant.REQUEST + API.GET_INDEX_IMAGE + '\n' + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                showNetWorkDialog();
                isHttp = false;
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (!StringUtils.isBlank(t)) {
                    Log.e(TAG, Constant.RESULT + API.GET_INDEX_IMAGE + '\n' + t);
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(GET_INDEX_IMAGE_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(FAILURE, BaseJSONData(t)));
                    }
                    isHttp = false;
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                    isHttp = false;
                }
            }
        });
    }

    /***
     * image_type	图片尺寸类型：Android:1-hdpi,2-mdpi,3-xhdpi,4-xxhdpi
     * @return json
     */
    private String makeJsonText(int type) {
        JSONObject json = new JSONObject();
        try {
            json.put("image_type", type + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 检查网络*/
    private boolean checkNetwork() {
        if (!CheckNetWorkUtil.isNetwork(this)) {
            showNetWorkDialog();
            return false;
        }
        return true;
    }

    /** 获取版本信息*/
    private String getVersionCode() {
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            versionCode = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "versionCode:" + versionCode);
        return versionCode + "";
    }

    /** http false prompt dialog*/
    private void showNetWorkDialog(){
        if(!dialog.isShowing()){
            dialog.show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);// 必须要调用这句
    }

    /** 判断是否要去请求记住登录状态，否则清空登录信息*/
    private void doUploadLoginWithToken(){
        if (!StringUtils.isBlank(PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN))
                && !StringUtils.isBlank(PreferencesUtils.getString(getApplicationContext(), Constant.KEY_MEMBER_ID))) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    UploadAdapter();
                }
            });
        } else {
            PreferencesUtils.putString(context, Constant.KEY_TOKEN, "");
            PreferencesUtils.putString(context, Constant.KEY_MEMBER_ID, "");
            PreferencesUtils.putString(context, Constant.KEY_MOBILE, "");
        }
    }

    /** 记住登录状态请求*/
    private void UploadAdapter() {
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.LOGIN_WITH_TOKEN);
        ajaxParams.put("token", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.LOGIN_WITH_TOKEN + "\n:" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.request_time_out), Toast.LENGTH_SHORT).show();
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e(TAG, Constant.RESULT +API.LOGIN_WITH_TOKEN + "\n" + t.toString());
                if(!StringUtils.isBlank(t)){
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(LOGIN_TOKEN_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(FAILURE, BaseJSONData(t)));
                    }
                }
            }
        });
    }

    /***
     * 记住登录状态
     * member_id
     * @return
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_MEMBER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
