package cc.siyo.iMenu.VCheck.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.CollectListActivity;
import cc.siyo.iMenu.VCheck.activity.DetailActivity;
import cc.siyo.iMenu.VCheck.activity.MineActivity;
import cc.siyo.iMenu.VCheck.activity.OrderDetailActivity;
import cc.siyo.iMenu.VCheck.activity.OrderListActivity;
import cc.siyo.iMenu.VCheck.activity.VoucherListActivity;
import cc.siyo.iMenu.VCheck.activity.WebViewActivity;
import cc.siyo.iMenu.VCheck.activity.setting.MessageActivity;
import cc.siyo.iMenu.VCheck.adapter.MainAdapter;
import cc.siyo.iMenu.VCheck.adapter.ViewPagerAdapter;
import cc.siyo.iMenu.VCheck.http.LHttpLib;
import cc.siyo.iMenu.VCheck.http.LHttpResponseHandler;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.BannerInfo;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.LinkPushParams;
import cc.siyo.iMenu.VCheck.model.Region;
import cc.siyo.iMenu.VCheck.util.ScreenUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Utility;
import cc.siyo.iMenu.VCheck.view.RefreshListView;

/**
 * Created by Lemon on 2015/4/29.
 * Desc:主页界面
 */
public class MainFragment extends BaseFragment{

    private static final String TAG = "MainFragment";
    /** LIST VIEW*/
    private RefreshListView store_list;
    private ViewPager viewPager_main;
    /** Adapter*/
    private MainAdapter mainAdapter;
    /** Context*/
    private Context mContext;
    /** 数据源*/
    private List<Article> articleList;
    /** A FINAL 框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;

    /** 是否到最后一页*/
    boolean doNotOver = true;
    /** 是否已经提醒过一遍*/
    boolean isTip = false;
    /** 是否是下拉刷新，清空数据*/
    private boolean isPull = false;
    private int page = Constant.PAGE;
    private int pageSize = Constant.PAGE_SIZE;
    private Region region;
    /** 广告内容单页view*/
    private View bannerView;
    FinalBitmap finalBitmap;
    /** viewPage适配器 */
    private ViewPagerAdapter mViewPagerAdapter;
    /** 小圆点的ImageView */
    private ImageView[] imageViews;
    List<View> mViewList = new ArrayList<>();
    /** 包裹广告的layout*/
    private RelativeLayout rl_main_imgList;
    /** 小圆点view*/
    private LinearLayout ll_viewpager_dian_main_imgList;
    /** 广告栏下方分割线*/
    private LinearLayout driver;
    /** 列表的headView*/
    private View headView;
    /** 计时器 */
    private Timer timer;
    /** 当前展示的页面 */
    private int arg0;

    public static MainFragment newInstance(Region region) {
        Bundle args = new Bundle();
        args.putSerializable("Region", region);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentView() {
        mContext = getActivity();
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.mipmap.default_menu);
        finalBitmap.configLoadfailImage(R.mipmap.default_menu);
        return R.layout.fram_store;
    }

    @Override
    public void initView(View v) {
        //headView控件
        headView = LayoutInflater.from(context).inflate(R.layout.layout_main_list_headview, null);
        driver = (LinearLayout) headView.findViewById(R.id.driver);
        ll_viewpager_dian_main_imgList = (LinearLayout) headView.findViewById(R.id.ll_viewpager_dian_main_imgList);
        rl_main_imgList = (RelativeLayout) headView.findViewById(R.id.rl_main_imgList);
        viewPager_main = (ViewPager) headView.findViewById(R.id.viewPager_main);

        store_list = (RefreshListView) v.findViewById(R.id.store_list);
        store_list.addFooterView((LayoutInflater.from(mContext)).inflate(R.layout.list_item_footview, null));
        store_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = 1;
                if(rl_main_imgList.isShown()) {//有广告，索引+1
                    pos ++;
                } else {//无广告
                    pos = 1;
                }
                if (mainAdapter.getDataList().size() > (position - pos)) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("article_id", mainAdapter.getDataList().get(position - pos).article_id);
                    getActivity().startActivity(intent);
                }
            }
        });

        mViewPagerAdapter = new ViewPagerAdapter(context, mViewList);
        viewPager_main.setAdapter(mViewPagerAdapter);
        viewPager_main.getParent().requestDisallowInterceptTouchEvent(true);
        viewPager_main.setOnPageChangeListener(new PageChangeListener());
    }

    @Override
    public void initData() {
        if(getArguments() != null) {
            region = (Region) getArguments().getSerializable("Region");
        }
        finalHttp = new FinalHttp();
        isPull = true;
        Upload();
        UploadAdapter();
        mainAdapter = new MainAdapter(getActivity(), R.layout.list_item_main);
        store_list.setAdapter(mainAdapter);

        store_list.setOnLoadMoreListenter(new RefreshListView.OnLoadMoreListener() {

            public void onLoadMore() {
                Log.e(TAG, "setOnLoadMoreListenter");
                isPull = false;
                if (doNotOver) {
                    page++;
                    UploadAdapter();
                } else {
                    store_list.onLoadMoreComplete();
                    if (!isTip) {
                        isTip = true;
                        prompt("已经到底了");
                    }
                }
            }
        });
        store_list.setOnRefreshListener(new RefreshListView.OnRefreshListener() {

            public void onRefresh() {
                Log.e(TAG, "setOnRefreshListener");
                page = Constant.PAGE;
                isPull = true;
//                Upload();
                UploadAdapter();
            }
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    closeProgressDialog();
                    store_list.onRefreshComplete();
                    store_list.onLoadMoreComplete();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(data.optJSONArray("article_list") != null && data.optJSONArray("article_list").length() > 0){
                            articleList = new ArrayList<>();
                            for (int i = 0; i < data.optJSONArray("article_list").length(); i++) {
                                Article article = new Article().parse(data.optJSONArray("article_list").optJSONObject(i));
                                Log.e(TAG, "TITLE ->" + article.title);
                                articleList.add(article);
                            }
                            if(isPull) {
                                mainAdapter.getDataList().clear();
                            }
                            mainAdapter.getDataList().addAll(articleList);
                            mainAdapter.notifyDataSetChanged();
                            if(jsonStatus.pageInfo != null) {
                                String more = jsonStatus.pageInfo.more;
                                if(more.equals("1")) {
                                    //有下一页
                                    doNotOver = true;
                                } else {
                                    //最后一页
                                    doNotOver = false;
                                }
                            }
                        }else{
                            prompt(getResources().getString(R.string.request_no_data));
                        }
                    }
                    break;
                case FAILURE:
                    closeProgressDialog();
                    store_list.onRefreshComplete();
                    store_list.onLoadMoreComplete();
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
                case 1:
                    if (arg0 > 2) {
                        arg0 = 0;
                        viewPager_main.setCurrentItem(arg0);
                    } else {
                        viewPager_main.setCurrentItem(arg0);
                    }
                    arg0++;
                    break;
            }
        }
    };

    /** 获取产品列表请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_PRODUCT_LIST);
        ajaxParams.put("token", token);
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.GET_PRODUCT_LIST + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                closeProgressDialog();
                store_list.onRefreshComplete();
                store_list.onLoadMoreComplete();
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
                    Log.e(TAG, Constant.RESULT + API.GET_PRODUCT_LIST + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(FAILURE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 请求数据封装*/
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("filter_info", makeJsonText_filterInfo());
            json.put("pagination", makeJsonText_pagination());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * filter_info:{filter_value:搜索关键字+region_id:地区ID} |
     * @return json
     */
    private JSONObject makeJsonText_filterInfo() {
        JSONObject json = new JSONObject();
        try {
            json.put("filter_value", "");
            json.put("region_id", region.region_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /***
     * pagination:{page；页+count：数量}
     * @return json
     */
    private JSONObject makeJsonText_pagination() {
        JSONObject json = new JSONObject();
        try {
            json.put("page", page + "");
            json.put("count", pageSize + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /** 获取广告列表*/
    private void Upload() {
        //TODO 城市ID暂时写死，百度地图无法实现定位功能
        LHttpLib.getAppBannerList(context, "29", new LHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onLoading(long count, long current) {

            }

            @Override
            public void onSuccess(JSONStatus jsonStatus) {
                if(jsonStatus.isSuccess) {
                    if(jsonStatus.data.optJSONArray("banner_list") != null
                            && jsonStatus.data.optJSONArray("banner_list").length() > 0) {
                        //有广告，显示
                        store_list.addHeaderView(headView);
                        rl_main_imgList.setVisibility(View.VISIBLE);
                        driver.setVisibility(View.VISIBLE);
                        mViewList.clear();
                        for (int i = 0; i < jsonStatus.data.optJSONArray("banner_list").length(); i++) {
                            final BannerInfo bannerInfo = new BannerInfo().parse(jsonStatus.data.optJSONArray("banner_list").optJSONObject(i));
                            View bannerView = LayoutInflater.from(context).inflate(R.layout.layout_viewpager_item, null);
                            ImageView imageView = (ImageView) bannerView.findViewById(R.id.iv_viewpager_img);
                            //获取16:9尺寸的图片宽度，距离屏幕两侧各10dp
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ScreenUtils.getImageParam(context, 20);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageView.setLayoutParams(layoutParams);

                            finalBitmap.display(imageView, bannerInfo.image.source);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    switchPage(bannerInfo.linkPushParams);
                                }
                            });
                            mViewList.add(bannerView);
                        }
                        mViewPagerAdapter.notifyDataSetChanged();
                        setImageDian(mViewList);
                        initTimer();
                    } else {
                        //无广告
                        store_list.removeHeaderView(headView);
                        rl_main_imgList.setVisibility(View.GONE);
                        driver.setVisibility(View.GONE);
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
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra(Constant.INTENT_WEB_URL, linkPushParams.link_value);
            intent.putExtra(Constant.INTENT_WEB_NAME, Constant.INTENT_WEB_NAME_WEB);
            startActivity(intent);
        }
        if(linkPushParams.link_route.equals(Constant.LINK_HOME)) {
            //打开首页，不做操作
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
    }

    /** 指引页面更改事件监听器 */
    class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            MainFragment.this.arg0 = arg0;
            // 遍历数组让当前选中图片下的小圆点设置颜色
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[arg0]
                        .setBackgroundResource(R.mipmap.page_indicator_focused);
                if (arg0 != i) {
                    imageViews[i]
                            .setBackgroundResource(R.mipmap.page_indicator_unfocused);
                }
            }
        }
    }

    /** 设置图片下方的小圆点 */
    private void setImageDian(List<View> mViewsList) {
        ll_viewpager_dian_main_imgList.removeAllViews();
        imageViews = new ImageView[mViewsList.size()];
        if(imageViews.length == 1) {
            ll_viewpager_dian_main_imgList.setVisibility(View.INVISIBLE);
        } else {
            ll_viewpager_dian_main_imgList.setVisibility(View.VISIBLE);
        }
        System.out.println("mViewsList:" + mViewsList.size());
        for (int i = 0; i < mViewsList.size(); i++) {
            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(15, 15);
            // 设置每个小圆点距离左边的间距
            margin.setMargins(5, 0, 5, 0);
            ImageView imageView = new ImageView(context);
            imageViews[i] = imageView;
            if (i == 0) {
                // 默认选中第一张图片
                imageViews[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                // 其他图片都设置未选中状态
                imageViews[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
            ll_viewpager_dian_main_imgList.setGravity(Gravity.CENTER_VERTICAL
                    | Gravity.CENTER_HORIZONTAL);
            ll_viewpager_dian_main_imgList.addView(imageViews[i], margin);
        }
    }

    /** view pager轮番计时器 */
    private void initTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 8000, 8000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /** 关闭计时器 */
        timer.cancel();
    }
}
