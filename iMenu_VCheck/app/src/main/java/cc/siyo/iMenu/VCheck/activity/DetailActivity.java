package cc.siyo.iMenu.VCheck.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.DetailFragmentViewPagerAdapter;
import cc.siyo.iMenu.VCheck.fragment.DetailLightSpotFragment;
import cc.siyo.iMenu.VCheck.fragment.DetailMenuFragment;
import cc.siyo.iMenu.VCheck.fragment.NoticeFragment;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.Share;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.LoadingDialog;
import cc.siyo.iMenu.VCheck.view.TopBar;
import cc.siyo.iMenu.VCheck.view.viewpager_indicator.TabPageIndicator;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * Created by Lemon on 2015/5/6.
 * Desc:详情页面
 */
public class DetailActivity extends FragmentActivity{

    private static final String TAG = "DetailActivity";
    /** 头部*/
    private TopBar topbar;
    /** ViewPager控件*/
    private ViewPager mPager;
    /** 碎片viewPager适配器*/
    private DetailFragmentViewPagerAdapter mAdapter;
    /** 碎片集合数据源*/
    private List<Fragment> fragmentsList;
    /** 提交购买按钮*/
    private TextView tv_detail_submit;
    /** A FINAL 框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 获取产品详情成功标石*/
    private static final int GET_DETAIL_SUCCESS = 100;
    /** 获取产品详情失败标石*/
    private static final int GET_DETAIL_FALSE = 200;
    /** 加载圈*/
    private LoadingDialog loadingDialog;
    private Context context;
    /** 文章ID*/
    private String article_id;
    /** 数据源*/
    private Article article;
    /** 文章详情标题显示*/
    private TextView tv_detail_title;
    /** 剩余库存及时间显示*/
    private TextView tv_detail_stockAndTime;
    /** 文章详情摘要显示*/
    private TextView tv_detail_summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);
        context = getApplicationContext();
        initView();
        initData();
    }

    public void initView() {
        tv_detail_submit = (TextView) findViewById(R.id.tv_detail_submit);
        tv_detail_title = (TextView) findViewById(R.id.tv_detail_title);
        tv_detail_stockAndTime = (TextView) findViewById(R.id.tv_detail_stockAndTime);
        tv_detail_summary = (TextView) findViewById(R.id.tv_detail_summary);

        topbar = (TopBar) findViewById(R.id.topbar);
        topbar.settitleViewText("礼遇详情");
        topbar.setButtonImage(TopBar.RIGHT_BUTTON, R.drawable.abc_ic_menu_share_mtrl_alpha);
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //返回
                finish();
            }
        });
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //分享
                Share share = new Share();
                share.content = "测试分享";
                ShareSDK.initSDK(DetailActivity.this);
                Platform sina = ShareSDK.getPlatform(DetailActivity.this, SinaWeibo.NAME);
                SinaWeibo.ShareParams sinaParams = new SinaWeibo.ShareParams();
                sinaParams.setTitle(share.title);
                sinaParams.setText(share.content);
                sinaParams.setSite(share.description);
                sinaParams.setSiteUrl(share.link);
                sinaParams.setImagePath(share.imagePath);
                sina.setPlatformActionListener(new SinaPlatformActionListener());
                sina.share(sinaParams);
            }
        });

        if(fragmentsList == null)
            fragmentsList = new ArrayList<Fragment>();
        /** 只留一个分类,如需多个，需重新增加新的Fragment，FragmentViewPagerAdapter进行加入标题数组即可*/
        mAdapter = new DetailFragmentViewPagerAdapter(getApplicationContext(), this.getSupportFragmentManager(), fragmentsList);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
//        /** 只留一个分类*/
        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
    }

    /** 加载FragmentViewPager*/
    private void initViewPager(){
        fragmentsList.add(new DetailLightSpotFragment().newInstance(article.article_content_list));
        fragmentsList.add(new DetailMenuFragment().newInstance(article.article_menu_list));
        fragmentsList.add(new NoticeFragment().newInstance(article.store_info));
        mAdapter.notifyDataSetChanged();

        tv_detail_title.setText(article.title);
        tv_detail_summary.setText(article.summary);
        //TODO 分别用ID去调用菜品详情，商家详情，会员详情
//        if(article.menu_info.stock != null){
//            tv_detail_stockAndTime.setText("剩余" + article.menu_info.stock.menu_count + "   剩余" + article.article_date);
//        }
    }

    private void initData(){
        if(getIntent().getExtras() != null){
            article_id = getIntent().getExtras().getString("article_id");
        }
        finalHttp = new FinalHttp();
        UploadAdapter();
        tv_detail_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交购买信息
                Intent intent = new Intent(getApplicationContext(), OrderWriteActivity.class);
                startActivity(intent);
            }
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DETAIL_SUCCESS:
//                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(data.optJSONObject("article_info") != null){
                            article = new Article().parse(data.optJSONObject("article_info"));
                            initViewPager();
                        }
                    }
                    break;
                case GET_DETAIL_FALSE:
//                    closeProgressDialog();
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

    /** 获取产品详情请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_PRODUCT_DETAIL);
        ajaxParams.put("token", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.GET_PRODUCT_DETAIL + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
//                closeProgressDialog();
                prompt(getResources().getString(R.string.request_time_out));
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
//                showProgressDialog(getResources().getString(R.string.loading));
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (!StringUtils.isBlank(t)) {
                    Log.e(TAG, Constant.RESULT + API.GET_PRODUCT_DETAIL + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(GET_DETAIL_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(GET_DETAIL_FALSE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * 请求数据封装
     * member_id	会员ID(可选)
     * article_id	文章ID(必须)
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("article_id", "1");//TODO article_id 暂写死为1
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 新浪回调监听*/
    private class SinaPlatformActionListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Log.e(TAG, "新浪回调onComplete ->");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.e(TAG, "新浪回调onError ->");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Log.e(TAG, "新浪回调onCancel ->");
        }
    }

    /******************************************  公共方法 ******************************************/
    /**
     * 非阻塞提示方式 *
     */
    public void prompt(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    //TODO 此页面进度圈显示稍后调整
//    /**
//     * 进行耗时阻塞操作时,需要调用改方法,显示等待效果
//     *
//     * @author Sylar *
//     */
//    public void showProgressDialog(String content) {
//        if (loadingDialog == null) {
//            loadingDialog = new LoadingDialog(context, content);
//        }
//
//        if (!isFinishing() && !loadingDialog.isShowing()) {
//            loadingDialog.show();
//        }
//    }
//
//    /**
//     * 耗时阻塞操作结束时,需要调用改方法,关闭等待效果
//     *
//     * @author Sylar *
//     */
//    public void closeProgressDialog() {
//        if (loadingDialog != null && !isFinishing()) {
//            loadingDialog.dismiss();
//            loadingDialog = null;
//        }
//    }

    public JSONStatus BaseJSONData(String t){
        JSONStatus jsonStatus = new JSONStatus();
        try {
            JSONObject obj = new JSONObject(t);
            if(obj != null && obj.length() > 0){
                jsonStatus = new JSONStatus().parse(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStatus;
    }
}
