package cc.siyo.iMenu.VCheck.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import cc.siyo.iMenu.VCheck.MainActivity;
import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.ClientConfig;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.Image;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.WebLinkParams;
import cc.siyo.iMenu.VCheck.util.CheckNetWorkUtil;
import cc.siyo.iMenu.VCheck.util.PackageUtils;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Util;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Lemon on 2015/5/26.
 * 闪屏
 */
public class Launch extends BaseActivity{

    private static final String TAG = "Launch";
    private FinalHttp finalHttp;
    private static final int GET_INDEX_IMAGE_SUCCESS = 300;
    private static final int GET_CLIENT_CONFIG_SUCCESS = 100;
    private static final int GET_CLIENT_CONFIG_FALSE = 200;
    private Context mContext;
    private String versionCode;
    private List<ClientConfig> clientConfigsList;
    private String versionUrl;
    @ViewInject(id = R.id.tv_version)private TextView tv_version;
    @ViewInject(id = R.id.ivLaunch)private ImageView ivLaunch;
    Dialog dialog;
    private FinalBitmap finalBitmap;
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
    public void initView() {}

    @Override
    public void initData() {
        finalHttp = new FinalHttp();
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.ic_member);
        finalBitmap.configLoadfailImage(R.drawable.ic_member);
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
        mContext = this;

        if(getIntent() != null && getIntent().getDataString() != null) {
            Intent intent = getIntent();
            String data = intent.getDataString();//vcheck://product?id=7
            Log.e(TAG, data);
            doSwitchPage(data);
        }
        tv_version.setText("version" + PackageUtils.getAppVersionName(mContext));
        finalHttp = new FinalHttp();
        if (checkNetwork()) {
            if (!isHttp) {
                UploadAdapter(Constant.LAUNACH_IMG_TYPE_H);
            }
        }

		XGPushClickedResult click = XGPushManager.onActivityStarted(this);
		Log.d("TPush", "onResumeXGPushClickedResult:" + click);
		if (click != null) { // 判断是否来自信鸽的打开方式
			Toast.makeText(this, "通知被点击:" + click.toString(), Toast.LENGTH_SHORT).show();
		}
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
                        JSONObject banner_info = data.optJSONObject("banner_info");
                        Image image = new Image().parse(banner_info.optJSONObject("image"));
                        finalBitmap.display(ivLaunch, image.source);
                        System.out.println("UploadBaseClientConfig");
                        UploadAdapter(getVersionCode());
                    }
                    break;
                case GET_CLIENT_CONFIG_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        JSONArray config_list = data.optJSONArray("config_list");
                        if(config_list != null && config_list.length() > 0){
                            clientConfigsList = new ArrayList<>();
                            for (int i = 0; i < config_list.length(); i++) {
                                if(config_list.optJSONObject(i) != null){
                                    ClientConfig clientConfig = new ClientConfig().parse(config_list.optJSONObject(i));
                                    clientConfigsList.add(clientConfig);
                                }
                            }
                            saveClientConfig();
                        }
                    }
                    break;
                case GET_CLIENT_CONFIG_FALSE:
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(!StringUtils.isBlank(jsonStatus.error_desc)){
                            Util.println(TAG, jsonStatus.error_desc);
                        }else{
                            if(!StringUtils.isBlank(jsonStatus.error_code)){
                                Util.println(TAG, getResources().getString(R.string.request_erro) + MyApplication.findErroDesc(jsonStatus.error_code));
                            }else{
                                Util.println(TAG, getResources().getString(R.string.request_erro));
                            }
                        }
                    }
                    ToMain();
                    break;
            }
        }
    };

    private void UploadAdapter(String versionAndroid) {
        isHttp = true;
        AjaxParams ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_CLIENT_CONFIG);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText(versionAndroid));
        Log.e(TAG, Constant.REQUEST + API.GET_CLIENT_CONFIG + '\n' + ajaxParams.toString());
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
                    Log.e(TAG, Constant.RESULT + API.GET_CLIENT_CONFIG + '\n' + t);
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(GET_CLIENT_CONFIG_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(GET_CLIENT_CONFIG_FALSE, BaseJSONData(t)));
                    }
                    isHttp = false;
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                    isHttp = false;
                }
            }
        });
    }

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
                        handler.sendMessage(handler.obtainMessage(GET_CLIENT_CONFIG_FALSE, BaseJSONData(t)));
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

    /***
     * device_type	10-iPhone/11-iPad/20-Android
     * version_android
     * @return json
     */
    private String makeJsonText(String versionAndroid) {
        JSONObject json = new JSONObject();
        try {
            json.put("device_type", Constant.DEVICE_TYPE);//0EB2961DE
            json.put("version_android", versionAndroid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private void saveClientConfig() {
        if(clientConfigsList != null && clientConfigsList.size() > 0){
            for (int i = 0; i < clientConfigsList.size(); i++) {
                if(clientConfigsList.get(i).key.equals(Constant.KEY_VERSION_ANDROID)){
                    PreferencesUtils.putString(mContext, Constant.KEY_VERSION_ANDROID, clientConfigsList.get(i).value);
                    versionUrl = clientConfigsList.get(i).data;
                    Log.e(TAG, "version_value:" + clientConfigsList.get(i).value);
                    Log.e(TAG, "versionUrl:" + clientConfigsList.get(i).data);
                }
                if(clientConfigsList.get(i).key.equals(Constant.KEY_NEED_UPDATE)){
                    Log.e(TAG, Constant.KEY_NEED_UPDATE + clientConfigsList.get(i).value);
                    PreferencesUtils.putString(mContext, Constant.KEY_NEED_UPDATE, clientConfigsList.get(i).value);
                }
                if(clientConfigsList.get(i).key.equals(Constant.KEY_IS_TIPS)){
                    Log.e(TAG, Constant.KEY_IS_TIPS + clientConfigsList.get(i).value);
                    PreferencesUtils.putString(mContext, Constant.KEY_IS_TIPS, clientConfigsList.get(i).value);
                }
            }
        }
        //Is popup update hint
        Boolean isTips = false;
        if(!StringUtils.isBlank(PreferencesUtils.getString(mContext, Constant.KEY_IS_TIPS))){
            if(PreferencesUtils.getString(mContext, Constant.KEY_IS_TIPS).equals(Constant.IS_TIPS_YES)){
                isTips = true;
            }
        }
        //Is do constraint update
        Boolean needUpdate = false;
        if(!StringUtils.isBlank(PreferencesUtils.getString(mContext, Constant.KEY_NEED_UPDATE))){
            if(PreferencesUtils.getString(mContext, Constant.KEY_NEED_UPDATE).equals(Constant.NEED_UPDATE_YES)){
                //constraint update
                needUpdate = true;
            }
        }
        /** do constraint update*/
        if(!StringUtils.isBlank(PreferencesUtils.getString(mContext, Constant.KEY_VERSION_ANDROID))){
            System.out.println("version:" + getVersionCode());
            Double currentVersion = Double.parseDouble(getVersionCode());
            Double newVersion = Double.parseDouble(PreferencesUtils.getString(mContext, Constant.KEY_VERSION_ANDROID));
            if(currentVersion < newVersion){
                //need update
                if(!StringUtils.isBlank(versionUrl)){
                    doUpdate(isTips, needUpdate);
                }else{
                    ToMain();
                }
            }else{
                ToMain();
            }
        }else{
            ToMain();
        }
    }

    /** update before verify*/
    private void doUpdate(Boolean isTips, Boolean needUpdate) {
        if(isTips){
            //need prompt dialog
            if(needUpdate){
                //constraint update,prompt one button dialog
                showTokenDialog(Launch.this, getResources().getString(R.string.found_newVersion), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadApk(versionUrl);
                    }
                });
            }else{
                //not constraint update , prompt two dialog
                showDialog(Launch.this, getResources().getString(R.string.found_newVersion), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadApk(versionUrl);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToMain();
                    }
                }, false).show();
            }
        }else{
            //not prompt dialog , background do update , if not constraint update..so not update
            if(needUpdate){
                //constraint update , prompt download dialog
                downloadApk(versionUrl);
            }
            //not need constraint update and not do update
        }
    }

    private void downloadApk(String url){
//		url = "http://www.imenu.so/android/iMenu.apk";//TEST
        final ProgressDialog downloadDialog = new ProgressDialog(mContext, R.style.LightDialog);
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setTitle(getResources().getString(R.string.down_downing));
        downloadDialog.setIndeterminate(false);
        downloadDialog.setCancelable(false);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();
        finalHttp.download(url, Constant.APK_TARGET, new AjaxCallBack<File>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
                prompt(getResources().getString(R.string.request_erro) + strMsg);
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
                int progress;
                if (current != count && current != 0) {
                    progress = (int) (current / (float) count * 100);
                } else {
                    progress = 100;
                }
                downloadDialog.setProgress(progress);
                Log.e(TAG, "download:" + progress + "%");
            }

            @Override
            public void onStart() {
                super.onStart();
                Log.e(TAG, "start download");
            }

            @Override
            public void onSuccess(File t) {
                super.onSuccess(t);
                Log.e(TAG, "download success");
                downloadDialog.dismiss();
                PackageUtils.installNormal(mContext, Constant.APK_TARGET);
            }
        });
    }

    private boolean checkNetwork() {
        if (!CheckNetWorkUtil.isNetwork(this)) {
            showNetWorkDialog();
            return false;
        }
        return true;
    }

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

    private void ToMain() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run -> ToMain");
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }
        }, 2500);
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

    /** 执行网页进入跳转相应页面
     *  首页	route=home	vcheck://?route=home
     *  产品详情	route=article&article_id=[文章ID]	vcheck://?route=article&article_id=2
     * */
    private void doSwitchPage(String data) {
        String paramsStr = data.substring(9, data.length());
        String[] params = paramsStr.split("&");
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < params.length; i++) {
            try {
                jsonObject.put(params[i].substring(0, params[i].indexOf("=")),
                        params[i].substring(params[i].indexOf("=") + 1, params[i].length()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        WebLinkParams webLinkParams = new WebLinkParams().parse(jsonObject);
        if(webLinkParams != null) {
            if(webLinkParams.route.equals(Constant.INTENT_HOME)) {
                //跳至首页

            }
            if(webLinkParams.route.equals(Constant.INTENT_ARTICLE)) {
                //跳至文章详情
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Constant.INTENT_ARTICLE_ID, webLinkParams.article_id);
                startActivity(intent);
            }

        }
    }
}
