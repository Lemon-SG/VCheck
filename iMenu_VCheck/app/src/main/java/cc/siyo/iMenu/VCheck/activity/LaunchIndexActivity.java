package cc.siyo.iMenu.VCheck.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;
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
import cc.siyo.iMenu.VCheck.activity.setting.MessageActivity;
import cc.siyo.iMenu.VCheck.http.LHttpLib;
import cc.siyo.iMenu.VCheck.http.LHttpResponseHandler;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.BannerInfo;
import cc.siyo.iMenu.VCheck.model.ClientConfig;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.LinkPushParams;
import cc.siyo.iMenu.VCheck.model.Region;
import cc.siyo.iMenu.VCheck.util.PackageUtils;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Util;

/**
 * Created by Lemon on 2015/8/5.
 * 闪屏广告页面
 */
public class LaunchIndexActivity extends BaseActivity{

    private FinalHttp finalHttp;
    private static final int GET_CLIENT_CONFIG_SUCCESS = 100;
    private static final int GET_CLIENT_CONFIG_FALSE = 200;
    private Context mContext;
    private String versionCode;
    private List<ClientConfig> clientConfigsList;
    private String versionUrl;
    @ViewInject(id = R.id.ivIndex)private ImageView ivIndex;
    @ViewInject(id = R.id.tvIndexSkip)private TextView tvIndexSkip;
    private FinalBitmap finalBitmap;
    private FinalDb db;

    @Override
    public int getContentView() {
        return R.layout.activity_index;
    }

    @Override
    public void initView() {
        mContext = this;
        db = FinalDb.create(LaunchIndexActivity.this, true);
        finalHttp = new FinalHttp();
        Upload();//获取城市列表
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.bg_mine_top_big);
        finalBitmap.configLoadfailImage(R.drawable.bg_mine_top_big);
        tvIndexSkip.setVisibility(View.VISIBLE);
        tvIndexSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳过
                if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getSerializable("linkPushParams") != null) {
                    LinkPushParams linkPushParams = (LinkPushParams) getIntent().getExtras().getSerializable("linkPushParams");
                    ToMain(linkPushParams, true);
                } else {
                    ToMain(null, true);
                }
            }
        });
    }

    @Override
    public void initData() {
        if(getIntent() != null && getIntent().getExtras() != null) {
            final BannerInfo bannerInfo = (BannerInfo) getIntent().getExtras().getSerializable("BannerInfo");
            finalBitmap.display(ivIndex, bannerInfo.image.source);
            ivIndex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //广告跳转
                    ToMain(bannerInfo.linkPushParams, true);
                }
            });
        }
        ToMain(null, false);
    }

    private void ToMain(final LinkPushParams linkPushParams, boolean isSkip) {
        final Intent intent = new Intent(mContext, MainActivity.class);
        if (linkPushParams != null) {
            intent.putExtra("linkPushParams", linkPushParams);
        }
        if(isSkip) {
            startActivity(intent);
            finish();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "2000run -> ToMain");
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }
    }

    /** 获取城市列表请求*/
    private void Upload(){
        LHttpLib.getRegionList(getApplicationContext(), new LHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onLoading(long count, long current) {

            }

            @Override
            public void onSuccess(JSONStatus jsonStatus) {
                if (jsonStatus.isSuccess) {
                    JSONObject data = jsonStatus.data;
                    if (data != null) {
                        JSONArray jsonArray = data.optJSONArray("region_list");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Region region = new Region().parse(jsonArray.optJSONObject(i));
                                if (db.findById(region.region_id, Region.class) != null) {
                                    db.update(region);
                                } else {
                                    db.save(region);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {

            }
        });
    }

    /** 根据参数进行跳转*/
    private void switchPage(LinkPushParams linkPushParams) {
        if(linkPushParams.link_route.equals(Constant.LINK_WEB)) {
            //打开网页链接
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(Constant.INTENT_WEB_URL, linkPushParams.link_value);
            intent.putExtra(Constant.INTENT_WEB_NAME, Constant.INTENT_WEB_NAME_WEB);
            startActivity(intent);
        }
        if(linkPushParams.link_route.equals(Constant.LINK_HOME)) {
            //打开首页，不做操作
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if(linkPushParams.link_route.equals(Constant.LINK_ARTICLE)) {
            //打开文章详情,传递ID
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(Constant.INTENT_ARTICLE_ID, linkPushParams.id);
            startActivity(intent);
        }
        if(linkPushParams.link_route.equals(Constant.LINK_MEMBER)) {//打开用户中心
            startActivity(new Intent(context, MineActivity.class));
        }
        if(linkPushParams.link_route.equals(Constant.LINK_MESSAGE)) {//打开消息列表
            startActivity(new Intent(context, MessageActivity.class));
        }
        if(linkPushParams.link_route.equals(Constant.LINK_COLLECTION)) {//打开收藏列表
            startActivity(new Intent(context, CollectListActivity.class));
        }
        if(linkPushParams.link_route.equals(Constant.LINK_ORDER_LIST)) {//打开订单列表
            startActivity(new Intent(context, OrderListActivity.class));
        }
        if(linkPushParams.link_route.equals(Constant.LINK_ORDER_DETAIL)) {//打开订单详情,传递ID
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", linkPushParams.id);
            startActivity(intent);
        }
        if(linkPushParams.link_route.equals(Constant.LINK_VOUCHER)) {//打开礼券列表
            startActivity(new Intent(context, VoucherListActivity.class));
        }
        finish();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            switch (msg.what) {
//                case GET_CLIENT_CONFIG_SUCCESS:
//                    closeProgressDialog();
//                    if(msg.obj != null){
//                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
//                        JSONObject data = jsonStatus.data;
//                        JSONArray config_list = data.optJSONArray("config_list");
//                        if(config_list != null && config_list.length() > 0){
//                            clientConfigsList = new ArrayList<>();
//                            for (int i = 0; i < config_list.length(); i++) {
//                                if(config_list.optJSONObject(i) != null){
//                                    ClientConfig clientConfig = new ClientConfig().parse(config_list.optJSONObject(i));
//                                    clientConfigsList.add(clientConfig);
//                                }
//                            }
//                            saveClientConfig();
//                        }
//                        tvIndexSkip.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case GET_CLIENT_CONFIG_FALSE:
//                    if(msg.obj != null){
//                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
//                        if(!StringUtils.isBlank(jsonStatus.error_desc)){
//                            Util.println(TAG, jsonStatus.error_desc);
//                        }else{
//                            if(!StringUtils.isBlank(jsonStatus.error_code)){
//                                Util.println(TAG, getResources().getString(R.string.request_erro) + MyApplication.findErroDesc(jsonStatus.error_code));
//                            }else{
//                                Util.println(TAG, getResources().getString(R.string.request_erro));
//                            }
//                        }
//                    }
//                    ToMain();
//                    break;
//            }
        }
    };

//    /** 获取配置信息，版本更新请求*/
//    private void UploadAdapter(String versionAndroid) {
//        AjaxParams ajaxParams = new AjaxParams();
//        ajaxParams.put("route", API.GET_CLIENT_CONFIG);
//        ajaxParams.put("token", "");
//        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
//        ajaxParams.put("jsonText", makeJsonText(versionAndroid));
//        Log.e(TAG, Constant.REQUEST + API.GET_CLIENT_CONFIG + '\n' + ajaxParams.toString());
//        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
//            @Override
//            public void onFailure(Throwable t, int errorNo, String strMsg) {
//                super.onFailure(t, errorNo, strMsg);
//                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
//            }
//
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onLoading(long count, long current) {
//                super.onLoading(count, current);
//            }
//
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//                if (!StringUtils.isBlank(t)) {
//                    Log.e(TAG, Constant.RESULT + API.GET_CLIENT_CONFIG + '\n' + t);
//                    JSONStatus jsonStatus = BaseJSONData(t);
//                    if (jsonStatus.isSuccess) {
//                        handler.sendMessage(handler.obtainMessage(GET_CLIENT_CONFIG_SUCCESS, BaseJSONData(t)));
//                    } else {
//                        handler.sendMessage(handler.obtainMessage(GET_CLIENT_CONFIG_FALSE, BaseJSONData(t)));
//                    }
//                } else {
//                    prompt(getResources().getString(R.string.request_no_data));
//                }
//            }
//        });
//    }
//
//    /***
//     * device_type	10-iPhone/11-iPad/20-Android
//     * version_android
//     * @return json
//     */
//    private String makeJsonText(String versionAndroid) {
//        JSONObject json = new JSONObject();
//        try {
//            json.put("device_type", Constant.DEVICE_TYPE);//0EB2961DE
//            json.put("version_android", versionAndroid);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return json.toString();
//    }
//
//    private void saveClientConfig() {
//        if(clientConfigsList != null && clientConfigsList.size() > 0){
//            for (int i = 0; i < clientConfigsList.size(); i++) {
//                if(clientConfigsList.get(i).key.equals(Constant.KEY_VERSION_ANDROID)){
//                    PreferencesUtils.putString(mContext, Constant.KEY_VERSION_ANDROID, clientConfigsList.get(i).value);
//                    versionUrl = clientConfigsList.get(i).data;
//                    Log.e(TAG, "version_value:" + clientConfigsList.get(i).value);
//                    Log.e(TAG, "versionUrl:" + clientConfigsList.get(i).data);
//                }
//                if(clientConfigsList.get(i).key.equals(Constant.KEY_NEED_UPDATE)){
//                    Log.e(TAG, Constant.KEY_NEED_UPDATE + clientConfigsList.get(i).value);
//                    PreferencesUtils.putString(mContext, Constant.KEY_NEED_UPDATE, clientConfigsList.get(i).value);
//                }
//                if(clientConfigsList.get(i).key.equals(Constant.KEY_IS_TIPS)){
//                    Log.e(TAG, Constant.KEY_IS_TIPS + clientConfigsList.get(i).value);
//                    PreferencesUtils.putString(mContext, Constant.KEY_IS_TIPS, clientConfigsList.get(i).value);
//                }
//            }
//        }
//        //Is popup update hint
//        Boolean isTips = false;
//        if(!StringUtils.isBlank(PreferencesUtils.getString(mContext, Constant.KEY_IS_TIPS))){
//            if(PreferencesUtils.getString(mContext, Constant.KEY_IS_TIPS).equals(Constant.IS_TIPS_YES)){
//                isTips = true;
//            }
//        }
//        //Is do constraint update
//        Boolean needUpdate = false;
//        if(!StringUtils.isBlank(PreferencesUtils.getString(mContext, Constant.KEY_NEED_UPDATE))){
//            if(PreferencesUtils.getString(mContext, Constant.KEY_NEED_UPDATE).equals(Constant.NEED_UPDATE_YES)){
//                //constraint update
//                needUpdate = true;
//            }
//        }
//        /** do constraint update*/
//        if(!StringUtils.isBlank(PreferencesUtils.getString(mContext, Constant.KEY_VERSION_ANDROID))){
//            System.out.println("version:" + getVersionCode());
//            Double currentVersion = Double.parseDouble(getVersionCode());
//            Double newVersion = Double.parseDouble(PreferencesUtils.getString(mContext, Constant.KEY_VERSION_ANDROID));
//            if(currentVersion < newVersion){
//                //need update
//                if(!StringUtils.isBlank(versionUrl)){
//                    doUpdate(isTips, needUpdate);
//                }else{
//                    ToMain();
//                }
//            }else{
//                ToMain();
//            }
//        }else{
//            ToMain();
//        }
//    }
//
//    /** update before verify*/
//    private void doUpdate(Boolean isTips, Boolean needUpdate) {
//        if(isTips){
//            //need prompt dialog
//            if(needUpdate){
//                //constraint update,prompt one button dialog
//                showTokenDialog(LaunchIndexActivity.this, getResources().getString(R.string.found_newVersion), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        downloadApk(versionUrl);
//                    }
//                });
//            }else{
//                //not constraint update , prompt two dialog
//                showDialog(LaunchIndexActivity.this, getResources().getString(R.string.found_newVersion), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        downloadApk(versionUrl);
//                    }
//                }, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ToMain();
//                    }
//                }, false).show();
//            }
//        }else{
//            //not prompt dialog , background do update , if not constraint update..so not update
//            if(needUpdate){
//                //constraint update , prompt download dialog
//                downloadApk(versionUrl);
//            }
//            //not need constraint update and not do update
//        }
//    }
//
//    private void downloadApk(String url){
////		url = "http://www.imenu.so/android/iMenu.apk";//TEST
//        final ProgressDialog downloadDialog = new ProgressDialog(mContext, R.style.LightDialog);
//        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        downloadDialog.setTitle(getResources().getString(R.string.down_downing));
//        downloadDialog.setIndeterminate(false);
//        downloadDialog.setCancelable(false);
//        downloadDialog.setCanceledOnTouchOutside(false);
//        downloadDialog.show();
//        finalHttp.download(url, Constant.APK_TARGET, new AjaxCallBack<File>() {
//            @Override
//            public void onFailure(Throwable t, int errorNo, String strMsg) {
//                super.onFailure(t, errorNo, strMsg);
//                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
//                prompt(getResources().getString(R.string.request_erro) + strMsg);
//            }
//
//            @Override
//            public void onLoading(long count, long current) {
//                super.onLoading(count, current);
//                int progress;
//                if (current != count && current != 0) {
//                    progress = (int) (current / (float) count * 100);
//                } else {
//                    progress = 100;
//                }
//                downloadDialog.setProgress(progress);
//                Log.e(TAG, "download:" + progress + "%");
//            }
//
//            @Override
//            public void onStart() {
//                super.onStart();
//                Log.e(TAG, "start download");
//            }
//
//            @Override
//            public void onSuccess(File t) {
//                super.onSuccess(t);
//                Log.e(TAG, "download success");
//                downloadDialog.dismiss();
//                PackageUtils.installNormal(mContext, Constant.APK_TARGET);
//            }
//        });
//    }

//    private String getVersionCode() {
//        PackageManager pm = getPackageManager();
//        PackageInfo packageInfo;
//        try {
//            packageInfo = pm.getPackageInfo(getPackageName(),
//                    PackageManager.GET_CONFIGURATIONS);
//            versionCode = packageInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        Log.e(TAG, "versionCode:" + versionCode);
//        return versionCode + "";
//    }
}
