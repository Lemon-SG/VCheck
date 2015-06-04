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
import android.widget.TextView;
import com.tencent.android.tpush.XGPushConfig;
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
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.util.CheckNetWorkUtil;
import cc.siyo.iMenu.VCheck.util.PackageUtils;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Util;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Lemon on 2015/5/26.
 * Desc:闪屏
 */
public class Launch extends BaseActivity{

    private static final String TAG = "Launch";
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 记住登录状态成功标石*/
    private static final int GET_CLIENT_CONFIG_SUCCESS = 100;
    /** 记住登录状态失败标石*/
    private static final int GET_CLIENT_CONFIG_FALSE = 200;
    private Context mContext;
    /** 版本号*/
    private String versionCode;
    /** 配置文件集合*/
    private List<ClientConfig> clientConfigsList;
    /** 版本更新路径*/
    private String versionUrl;
    /** 版本号*/
    @ViewInject(id = R.id.tv_version)private TextView tv_version;
    Dialog dialog;
    /** 由于onResume方法有时会出发两次，标石进行拦截，如正在请求即不再重复请求*/
    private boolean isHttp = false;

    @Override
    public int getContentView() {
        // 开启LOGCAT输出，方便debug，发布时请关闭
        XGPushConfig.enableDebug(this, true);
        return R.layout.activity_launch;
    }

    @Override
    public void initView() {}

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
        mContext = this;
        tv_version.setText("version" + PackageUtils.getAppVersionName(mContext));
        finalHttp = new FinalHttp();
        if (checkNetwork()) {
            if (!isHttp) {
                System.out.println("UploadBaseClientConfig");
                UploadAdapter(getVersionCode());
            }
        }
//		XGPushClickedResult click = XGPushManager.onActivityStarted(this);
//		Log.d("TPush", "onResumeXGPushClickedResult:" + click);
//		if (click != null) { // 判断是否来自信鸽的打开方式
//			Toast.makeText(this, "通知被点击:" + click.toString(),
//					Toast.LENGTH_SHORT).show();
//		}
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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

    /** 获取配置信息请求*/
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

    /***
     * device_type	设备类型:10-iPhone/11-iPad/20-Android
     * version_android	Android版本
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

    /** 存储配置*/
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
        //是否弹出更新提示
        Boolean isTips = false;
        if(!StringUtils.isBlank(PreferencesUtils.getString(mContext, Constant.KEY_IS_TIPS))){
            if(PreferencesUtils.getString(mContext, Constant.KEY_IS_TIPS).equals(Constant.IS_TIPS_YES)){
                isTips = true;
            }
        }
        //是否强制更新
        Boolean needUpdate = false;
        if(!StringUtils.isBlank(PreferencesUtils.getString(mContext, Constant.KEY_NEED_UPDATE))){
            if(PreferencesUtils.getString(mContext, Constant.KEY_NEED_UPDATE).equals(Constant.NEED_UPDATE_YES)){
                //强制更新
                needUpdate = true;
            }
        }
        /** 判断是否需要更新*/
        if(!StringUtils.isBlank(PreferencesUtils.getString(mContext, Constant.KEY_VERSION_ANDROID))){
            System.out.println("version:" + getVersionCode());
            Double currentVersion = Double.parseDouble(getVersionCode());
            Double newVersion = Double.parseDouble(PreferencesUtils.getString(mContext, Constant.KEY_VERSION_ANDROID));
            if(currentVersion < newVersion){
                //需要更新
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

    /** 执行更新前判断*/
    private void doUpdate(Boolean isTips, Boolean needUpdate) {
        if(isTips){
            //需要弹出提示对话框
            if(needUpdate){
                //强制更新，弹出一个按钮的对话框
                showTokenDialog(Launch.this, getResources().getString(R.string.found_newVersion), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadApk(versionUrl);
                    }
                });
            }else{
                //不强制更新，弹出两个按钮的对话框
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
            //不弹出对话框，直接进行更新，如果不强制，即不更新
            if(needUpdate){
                //强制更新，弹出下载进度框
                downloadApk(versionUrl);
            }
            //不需要强制更新，即不执行更新
        }
    }

    /** 执行下载*/
    private void downloadApk(String url){
//		url = "http://www.imenu.so/android/iMenu.apk";//测试专用
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
                Log.e(TAG, "下载进度:" + progress + "%");
            }

            @Override
            public void onStart() {
                super.onStart();
                Log.e(TAG, "开始下载");
            }

            @Override
            public void onSuccess(File t) {
                super.onSuccess(t);
                Log.e(TAG, "下载成功");
                downloadDialog.dismiss();
                PackageUtils.installNormal(mContext, Constant.APK_TARGET);
            }
        });
    }

    /** 检查网络 */
    private boolean checkNetwork() {
        if (!CheckNetWorkUtil.isNetwork(this)) {
            showNetWorkDialog();
            return false;
        }
        return true;
    }

    /** 获取版本信息 */
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

    /** 延迟两秒进入主页 */
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

    /** 请求失败弹出对话框*/
    private void showNetWorkDialog(){
        if(!dialog.isShowing()){
            dialog.show();
        }
    }
}
