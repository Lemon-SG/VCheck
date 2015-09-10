package cc.siyo.iMenu.VCheck.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.DetailFragmentViewPagerAdapter;
import cc.siyo.iMenu.VCheck.adapter.ViewPagerAdapter;
import cc.siyo.iMenu.VCheck.fragment.DetailLightSpotFragment;
import cc.siyo.iMenu.VCheck.fragment.DetailMenuFragment;
import cc.siyo.iMenu.VCheck.fragment.NoticeFragment;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.ArticleImage;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.Share;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.ScreenUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.TimeUtil;
import cc.siyo.iMenu.VCheck.util.Util;
import cc.siyo.iMenu.VCheck.view.ShareDialog;
import cc.siyo.iMenu.VCheck.view.TopBar;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
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
//    private TabPageIndicator indicator;
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
    /** 获取产品详情成功标石*/
    private static final int EDIT_COLLECTION_PRODUCT_SUCCESS = 300;
    /** 获取产品详情失败标石*/
    private static final int EDIT_COLLECTION_PRODUCT_FALSE = 400;
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
    /** 产品原价格显示*/
    private TextView tv_detail_original_price;
    /** 产品优惠价格显示*/
    private TextView tv_detail_special_price;
    /** 产品价格单位及人数单位显示*/
    private TextView tv_detail_price_menu_unit;
    /** 分享获取现金礼券按钮*/
    private TextView tv_share_get_gift;
    /** 收藏按钮*/
    private TextView tv_detail_collect;
    /** 消费提示列表*/
    private TextView tv_detail_consume_tips;

    /** viewPager*/
    private ViewPager viewpager_detail_imgList;
    /** viewPager小圆点*/
    private LinearLayout ll_viewpager_dian_detail_imgList;
    /** viewPage适配器 */
    private ViewPagerAdapter mViewPagerAdapter;
    /** 小圆点的ImageView */
    private ImageView[] imageViews;
    /** 文章内图片集合*/
    private List<ArticleImage> articleImageList;
//    /** A FINAL 框架的HTTP请求工具*/
//    private FinalBitmap finalBitmap;
    /** 收藏标石：true->已收藏 | false->未收藏*/
    private boolean isCollect = false;
    /** 收藏数量*/
    private int collectCount;
    private ImageLoader imageLoader;

    private LinearLayout llDetailLight;
    private LinearLayout llDetailMenu;
    private LinearLayout llDetailNotice;

    private TextView tvLightDriverTop;
    private TextView tvLightDriverBottom;
    private TextView tvMenuDriverTop;
    private TextView tvMenuDriverBottom;
    private TextView tvNoticeDriverTop;
    private TextView tvNoticeDriverBottom;

    /** 书签时间显示*/
    private TextView tv_detail_time;
    List<View> mViewList = new ArrayList<>();
    private ScreenUtils.ScreenResolution screenResolution;
    private DisplayImageOptions menuOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);
        context = getApplicationContext();
//        finalBitmap = FinalBitmap.create(context);
        imageLoader = ImageLoader.getInstance();
        menuOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.mipmap.default_menu)
                .showImageForEmptyUri(R.mipmap.default_menu)
                .showImageOnFail(R.mipmap.default_menu).build();
//        finalBitmap.configLoadingImage(R.mipmap.default_menu);
//        finalBitmap.configLoadfailImage(R.mipmap.default_menu);
        initView();
        initData();
    }

    public void initView() {
        tv_detail_time = (TextView) findViewById(R.id.tv_detail_time);
        llDetailLight = (LinearLayout) findViewById(R.id.llDetailLight);
        llDetailMenu = (LinearLayout) findViewById(R.id.llDetailMenu);
        llDetailNotice = (LinearLayout) findViewById(R.id.llDetailNotice);
        tvLightDriverTop = (TextView) findViewById(R.id.tvLightDriverTop);
        tvLightDriverBottom = (TextView) findViewById(R.id.tvLightDriverBottom);
        tvMenuDriverTop = (TextView) findViewById(R.id.tvMenuDriverTop);
        tvMenuDriverBottom = (TextView) findViewById(R.id.tvMenuDriverBottom);
        tvNoticeDriverTop = (TextView) findViewById(R.id.tvNoticeDriverTop);
        tvNoticeDriverBottom = (TextView) findViewById(R.id.tvNoticeDriverBottom);
        tv_detail_consume_tips = (TextView) findViewById(R.id.tv_detail_consume_tips);
        tv_detail_submit = (TextView) findViewById(R.id.tv_detail_submit);
        tv_detail_title = (TextView) findViewById(R.id.tv_detail_title);
        tv_detail_stockAndTime = (TextView) findViewById(R.id.tv_detail_stockAndTime);
        tv_detail_summary = (TextView) findViewById(R.id.tv_detail_summary);
        tv_detail_original_price = (TextView) findViewById(R.id.tv_detail_original_price);
        tv_detail_special_price = (TextView) findViewById(R.id.tv_detail_special_price);
        tv_detail_price_menu_unit = (TextView) findViewById(R.id.tv_detail_price_menu_unit);
        viewpager_detail_imgList = (ViewPager) findViewById(R.id.viewpager_detail_imgList);

        //获取16:9尺寸的图片宽度，距离屏幕两侧各10dp
        screenResolution = ScreenUtils.getScreenResolution(context);
        int width = (screenResolution.getWidth() - ScreenUtils.dpToPxInt(context, 0));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, (int) (width * 0.5625));
        viewpager_detail_imgList.setLayoutParams(layoutParams);

        ll_viewpager_dian_detail_imgList = (LinearLayout) findViewById(R.id.ll_viewpager_dian_detail_imgList);
        tv_detail_collect = (TextView) findViewById(R.id.tv_detail_collect);
        tv_share_get_gift = (TextView) findViewById(R.id.tv_share_get_gift);

        topbar = (TopBar) findViewById(R.id.topbar);
        topbar.settitleViewText("知味详情");
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
                ShareDialog shareDialog = new ShareDialog(DetailActivity.this, returnShare());
                shareDialog.show();
            }
        });
        tv_share_get_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog shareDialog = new ShareDialog(DetailActivity.this, returnShare());
                shareDialog.show();
            }
        });

        if(fragmentsList == null)
            fragmentsList = new ArrayList<>();

        /** 只留一个分类,如需多个，需重新增加新的Fragment，FragmentViewPagerAdapter进行加入标题数组即可*/
        mAdapter = new DetailFragmentViewPagerAdapter(getApplicationContext(), this.getSupportFragmentManager(), fragmentsList);
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(new FragmentPageChangeListener());

        /** 顶部viewpager组件配置*/
        mViewPagerAdapter = new ViewPagerAdapter(context, mViewList);
        viewpager_detail_imgList.setAdapter(mViewPagerAdapter);
        viewpager_detail_imgList.getParent().requestDisallowInterceptTouchEvent(true);
        viewpager_detail_imgList.setOnPageChangeListener(new PageChangeListener());

        llDetailLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(0, false);
            }
        });
        llDetailMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(1, false);
            }
        });
        llDetailNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(2, false);
            }
        });
    }

    /** 加载FragmentViewPager*/
    private void initViewPager(){
        mViewList.clear();
        if(article.article_image_list != null && article.article_image_list.size() > 0) {
            articleImageList = article.article_image_list;
            for (int i = 0; i < articleImageList.size(); i++) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_viewpager_item, null);
                ImageView iv_viewpager_img = (ImageView) view.findViewById(R.id.iv_viewpager_img);

                //获取16:9尺寸的图片宽度，距离屏幕两侧各10dp
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ScreenUtils.getImageParam(context, 0);
                iv_viewpager_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv_viewpager_img.setLayoutParams(layoutParams);

//                finalBitmap.display(iv_viewpager_img, articleImageList.get(i).image.source);
                imageLoader.displayImage(articleImageList.get(i).image.source, iv_viewpager_img, menuOptions);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchBigViewPager(viewpager_detail_imgList.getCurrentItem());
                    }
                });
                mViewList.add(view);
            }
            mViewPagerAdapter.notifyDataSetChanged();
            setImageDian(mViewList);
        }
        fragmentsList.clear();
        fragmentsList.add(new DetailLightSpotFragment().newInstance(article.article_content_list));
        fragmentsList.add(new DetailMenuFragment().newInstance(article.article_menu_list, article.article_image_list));
        fragmentsList.add(new NoticeFragment().newInstance(article.store_info, article.tips_info));
        mAdapter.notifyDataSetChanged();

        /******************************  加载内容 ******************************/
        tv_detail_title.setText(article.title);
        tv_detail_summary.setText(article.summary);
        tv_detail_consume_tips.setText(article.consume_tips_list);
        tv_detail_time.setText(article.article_date);

        int padding = ScreenUtils.dpToPxInt(context, 5);
        if(article.orderInfo != null) {
            tv_detail_submit.setText(StringUtils.getString(context, R.string.pay));
            tv_detail_submit.setBackgroundResource(R.drawable.bg_btn_detail_submit_green);
            tv_detail_submit.setPadding(0, padding, 0, padding);
        }else {
            switch (Integer.parseInt(article.menu_info.menu_status.menu_status_id)) {
                case Constant.MENU_STATUS_OUT://已售罄
                    tv_detail_submit.setText(article.menu_info.menu_status.menu_status);
                    tv_detail_submit.setBackgroundResource(R.drawable.bg_btn_detail_submit_gray);
                    tv_detail_submit.setPadding(0, padding, 0, padding);
                    break;
                case Constant.MENU_STATUS_OVER://已结束
                    tv_detail_submit.setText(article.menu_info.menu_status.menu_status);
                    tv_detail_submit.setBackgroundResource(R.drawable.bg_btn_detail_submit_gray);
                    tv_detail_submit.setPadding(0, padding, 0, padding);
                    break;
                case Constant.MENU_STATUS_SALE://销售中
                    tv_detail_submit.setText(StringUtils.getString(context, R.string.check));
                    tv_detail_submit.setBackgroundResource(R.drawable.bg_btn_detail_submit_org);
                    tv_detail_submit.setPadding(0, padding, 0, padding);
                    break;
            }
        }
        if(!StringUtils.isBlank(article.menu_info.remainder_time) && Integer.parseInt(article.menu_info.remainder_time) != 0){
            String timeMsg = TimeUtil.getTimeDiff(Long.parseLong(article.menu_info.remainder_time));
            if(Integer.parseInt(article.menu_info.stock.menu_count) <= 0) {
                tv_detail_stockAndTime.setText("剩余0" + article.menu_info.stock.menu_unit + "   " + timeMsg);
            }else {
                tv_detail_stockAndTime.setText("剩余" + article.menu_info.stock.menu_count + article.menu_info.stock.menu_unit + "   " + timeMsg);
            }
        }else {
            tv_detail_stockAndTime.setText(StringUtils.getString(context, R.string.over));
        }

        tv_detail_price_menu_unit.setText(article.menu_info.price.price_unit + "/" + article.menu_info.menu_unit.menu_unit);
        if(!StringUtils.isBlank(article.menu_info.price.special_price)){
            //有优惠价格
            Log.e(TAG, "有优惠价格");
            tv_detail_special_price.setText(article.menu_info.price.special_price);
            tv_detail_original_price.setText(article.menu_info.price.original_price + article.menu_info.price.price_unit);
            Util.PaintTvAddStrike(tv_detail_original_price);
            tv_detail_original_price.setVisibility(View.VISIBLE);
        }else{
            //无优惠价格
            Log.e(TAG, "无优惠价格");
            tv_detail_special_price.setText(article.menu_info.price.original_price);
            tv_detail_original_price.setVisibility(View.INVISIBLE);
        }
        collectCount = Integer.parseInt(article.collection_info.collection_count);
        tv_detail_collect.setText(collectCount + "");
        if(article.collection_info.is_collected.equals("0")) {
            //未收藏
            isCollect = false;
            Util.ChangeTextImage(context, tv_detail_collect, R.mipmap.ic_favorite_black_18dp);
        }else {
            //已收藏
            isCollect = true;
            Util.ChangeTextImage(context, tv_detail_collect, R.mipmap.ic_favorite_red_18dp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isGoOrder) {
            isGoOrder = false;
            if(getIntent().getExtras() != null){
                article_id = getIntent().getExtras().getString(Constant.INTENT_ARTICLE_ID);
            }
            finalHttp = new FinalHttp();
            UploadAdapter_Article();
        }
    }

    /** 去支付或下单跳转出去，当界面回复时重新请求数据标石*/
    private boolean isGoOrder = true;

    private void initData(){
        tv_detail_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交购买信息
                if(article != null) {
                    if(article.orderInfo != null) {
                        //跳转确认订单
                        isGoOrder = true;
                        Intent intent = new Intent(getApplicationContext(), OrderConfirmActivity.class);
                        intent.putExtra("orderInfo", article.orderInfo);
                        startActivity(intent);
                    }else {
                        if(Integer.parseInt(article.menu_info.stock.menu_count) > 0) {
                            //跳转填写订单
                            isGoOrder = true;
                            Intent intent = new Intent(getApplicationContext(), OrderWriteActivity.class);
                            intent.putExtra("article", article);
                            startActivity(intent);
                        }else {
                            //已售罄了
                        }
                    }
                }
            }
        });
        tv_detail_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收藏产品或取消收藏
                if (!StringUtils.isBlank(PreferencesUtils.getString(context, Constant.KEY_TOKEN))) {
                    if (!isCollect) {
                        //收藏产品
                        UploadAdapter_EditCollect(Constant.COLLECT_TYPE_OPERATOR_ADD);
                    } else {
                        //取消收藏
                        UploadAdapter_EditCollect(Constant.COLLECT_TYPE_OPERATOR_DELETE);
                    }
                } else {
                    prompt("请先登录");
                }
            }
        });
    }

    /************************************ 请求数据 ********************************************/
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DETAIL_SUCCESS:
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
                case EDIT_COLLECTION_PRODUCT_SUCCESS:
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess){
                            if(!isCollect) {
                                //收藏成功
                                isCollect = true;
                                collectCount++;
                                tv_detail_collect.setText(collectCount + "");
                                Util.ChangeTextImage(context, tv_detail_collect, R.mipmap.ic_favorite_red_18dp);
                            }else {
                                //取消收藏成功
                                isCollect = false;
                                collectCount--;
                                tv_detail_collect.setText(collectCount + "");
                                Util.ChangeTextImage(context, tv_detail_collect, R.mipmap.ic_favorite_black_18dp);
                            }
                        }
                    }
                    break;
                case EDIT_COLLECTION_PRODUCT_FALSE:
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
    private void UploadAdapter_Article() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_PRODUCT_DETAIL);
        ajaxParams.put("token", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_Article());
        Log.e(TAG, Constant.REQUEST + API.GET_PRODUCT_DETAIL + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                prompt(getResources().getString(R.string.request_time_out));
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

    /** 编辑收藏请求*/
    private void UploadAdapter_EditCollect(int operatorType) {
       // finalHttp = new FinalHttp();
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EDIT_COLLECTION_PRODUCT);
        ajaxParams.put("token", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText_EditCollect(operatorType));
        Log.e(TAG, Constant.REQUEST + API.EDIT_COLLECTION_PRODUCT + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                prompt(getResources().getString(R.string.request_time_out));
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
                    Log.e(TAG, Constant.RESULT + API.EDIT_COLLECTION_PRODUCT + "\n" + t);
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(EDIT_COLLECTION_PRODUCT_SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(EDIT_COLLECTION_PRODUCT_FALSE, BaseJSONData(t)));
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
     * operator_type	操作类型(1-添加/2-删除/3-清空)
     */
    private String makeJsonText_EditCollect(int operatorType) {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("operator_type", operatorType);
            json.put("article_list", getArticleList());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * 请求数据封装
     * article_id	文章ID
     */
    private JSONObject getArticleList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("article_id", article_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /***
     * 请求数据封装
     * member_id	会员ID(可选)
     * article_id	文章ID(必须)
     */
    private String makeJsonText_Article() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("article_id", article_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
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

    /** 指引页面更改事件监听器 */
    class FragmentPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
//            mPager.setCurrentItem(arg0, false);
            switch (arg0) {
                case 0:
                    tvLightDriverTop.setBackgroundColor(getResources().getColor(R.color.top_black));
                    tvLightDriverBottom.setBackgroundColor(getResources().getColor(R.color.top_black));

                    tvMenuDriverTop.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    tvMenuDriverBottom.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    tvNoticeDriverTop.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    tvNoticeDriverBottom.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    break;
                case 1:
                    tvMenuDriverTop.setBackgroundColor(getResources().getColor(R.color.top_black));
                    tvMenuDriverBottom.setBackgroundColor(getResources().getColor(R.color.top_black));

                    tvLightDriverTop.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    tvLightDriverBottom.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    tvNoticeDriverTop.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    tvNoticeDriverBottom.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    break;
                case 2:
                    tvNoticeDriverTop.setBackgroundColor(getResources().getColor(R.color.top_black));
                    tvNoticeDriverBottom.setBackgroundColor(getResources().getColor(R.color.top_black));

                    tvMenuDriverTop.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    tvMenuDriverBottom.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    tvLightDriverTop.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    tvLightDriverBottom.setBackgroundColor(getResources().getColor(R.color.gray_ddd));
                    break;
            }
        }
    }

    /** 设置图片下方的小圆点 */
    private void setImageDian(List<View> mViewsList) {
        ll_viewpager_dian_detail_imgList.removeAllViews();
        imageViews = new ImageView[mViewsList.size()];
        System.out.println("mViewsList:" + mViewsList.size());
        for (int i = 0; i < mViewsList.size(); i++) {
            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(15, 15);
            // 设置每个小圆点距离左边的间距
            margin.setMargins(5, 0, 5, 0);
            ImageView imageView = new ImageView(this);
            imageViews[i] = imageView;
            if (i == 0) {
                // 默认选中第一张图片
                imageViews[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                // 其他图片都设置未选中状态
                imageViews[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
            ll_viewpager_dian_detail_imgList.setGravity(Gravity.CENTER_VERTICAL
                    | Gravity.CENTER_HORIZONTAL);
            ll_viewpager_dian_detail_imgList.addView(imageViews[i], margin);
        }
    }

    /** 跳转大图模式*/
    private void switchBigViewPager(int index) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("articleImageList", (Serializable) articleImageList);
        bundle.putInt("index", index);
        intent.putExtra("bundle", bundle);
        intent.setClass(context, MenuBigImgViewPagerActivity.class);
        startActivity(intent);
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

    /** 返回分享实体*/
    private Share returnShare () {
        Share share = new Share();
        share.title = article.title;
        share.description = article.title;
        if(!StringUtils.isBlank(PreferencesUtils.getString(context, Constant.KEY_TOKEN))) {
            share.content = "使用邀请码:" + PreferencesUtils.getString(context, Constant.KEY_INVITE_CODE) + "注册既获30元礼券";
        } else {
            share.content = "最精致的高端定制餐饮体验";
        }
        share.imagePath = imageLoader.getDiskCache().get(article.article_image_list.get(0).image.source).getAbsolutePath();
        share.imageUrl = article.article_image_list.get(0).image.source;
        share.link = article.share_info.share_url;
        return share;
    }
}
