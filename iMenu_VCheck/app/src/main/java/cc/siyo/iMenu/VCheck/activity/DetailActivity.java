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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
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
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Util;
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
    private TabPageIndicator indicator;
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
    /** A FINAL 框架的HTTP请求工具*/
    private FinalBitmap finalBitmap;
    /** 收藏标石：true->已收藏 | false->未收藏*/
    private boolean isCollect = false;
    /** 收藏数量*/
    private int collectCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);
        context = getApplicationContext();
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.test_menu_img);
        finalBitmap.configLoadfailImage(R.drawable.test_menu_img);
        initView();
        initData();
    }

    public void initView() {
        tv_detail_submit = (TextView) findViewById(R.id.tv_detail_submit);
        tv_detail_title = (TextView) findViewById(R.id.tv_detail_title);
        tv_detail_stockAndTime = (TextView) findViewById(R.id.tv_detail_stockAndTime);
        tv_detail_summary = (TextView) findViewById(R.id.tv_detail_summary);
        tv_detail_original_price = (TextView) findViewById(R.id.tv_detail_original_price);
        tv_detail_special_price = (TextView) findViewById(R.id.tv_detail_special_price);
        tv_detail_price_menu_unit = (TextView) findViewById(R.id.tv_detail_price_menu_unit);
        viewpager_detail_imgList = (ViewPager) findViewById(R.id.viewpager_detail_imgList);
        ll_viewpager_dian_detail_imgList = (LinearLayout) findViewById(R.id.ll_viewpager_dian_detail_imgList);
        tv_detail_collect = (TextView) findViewById(R.id.tv_detail_collect);
        tv_share_get_gift = (TextView) findViewById(R.id.tv_share_get_gift);

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
        mPager.setOnPageChangeListener(new FragmentPageChangeListener());
//        /** 只留一个分类*/
        indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
    }

    /** 加载FragmentViewPager*/
    private void initViewPager(){
        if(article.article_image_list != null && article.article_image_list.size() > 0) {
            List<View> mViewList = new ArrayList<>();
            articleImageList = article.article_image_list;
            for (int i = 0; i < articleImageList.size(); i++) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_detail_viewpager_item, null);
                ImageView iv_viewpager_img = (ImageView) view.findViewById(R.id.iv_viewpager_img);
                finalBitmap.display(iv_viewpager_img, articleImageList.get(i).image.source);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchBigViewPager(viewpager_detail_imgList.getCurrentItem());
                    }
                });
                mViewList.add(view);
            }
            setImageDian(mViewList);
            mViewPagerAdapter = new ViewPagerAdapter(context, mViewList);
            viewpager_detail_imgList.setAdapter(mViewPagerAdapter);
            viewpager_detail_imgList.getParent().requestDisallowInterceptTouchEvent(true);
            viewpager_detail_imgList.setOnPageChangeListener(new PageChangeListener());
        }
        fragmentsList.add(new DetailLightSpotFragment().newInstance(article.article_content_list));
        fragmentsList.add(new DetailMenuFragment().newInstance(article.article_menu_list, article.article_image_list));
        fragmentsList.add(new NoticeFragment().newInstance(article.store_info));
        mAdapter.notifyDataSetChanged();
        indicator.notifyDataSetChanged();

        /******************************  加载内容 ******************************/
        tv_detail_title.setText(article.title);
        tv_detail_summary.setText(article.summary);
        //TODO 根据不同订单状态来显示，稍后完善
        if(article.orderInfo != null) {
            tv_detail_submit.setText("立即支付");
        }else {
            tv_detail_submit.setText("CHECK NOW");
        }

        tv_detail_price_menu_unit.setText(article.menu_info.price.price_unit + "/" +article.menu_info.menu_unit.menu_unit);
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
            tv_detail_original_price.setVisibility(View.GONE);
        }
        collectCount = Integer.parseInt(article.collection_info.collection_count);
        tv_detail_collect.setText(collectCount + "");
        if(article.collection_info.is_collected.equals("0")) {
            //未收藏
            isCollect = false;
            Util.ChangeTextImage(context, tv_detail_collect, R.drawable.ic_collect_black);
        }else {
            //已收藏
            isCollect = true;
            Util.ChangeTextImage(context, tv_detail_collect, R.drawable.ic_collect_red);
        }

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
        UploadAdapter_Article();
        tv_detail_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交购买信息
                if(article.orderInfo != null) {
                    //跳转确认订单
                    Intent intent = new Intent(getApplicationContext(), OrderConfirmActivity.class);
                    intent.putExtra("orderInfo", article.orderInfo);
                    startActivity(intent);
                }else {
                    //跳转填写订单
                    Intent intent = new Intent(getApplicationContext(), OrderWriteActivity.class);
                    intent.putExtra("article", article);
                    startActivity(intent);
                }

            }
        });
        tv_detail_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收藏产品或取消收藏
                if(!isCollect) {
                    //收藏产品
                    UploadAdapter_EditCollect(Constant.COLLECT_TYPE_OPERATOR_ADD);
                }else {
                    //取消收藏
                    UploadAdapter_EditCollect(Constant.COLLECT_TYPE_OPERATOR_DELETE);
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
                case EDIT_COLLECTION_PRODUCT_SUCCESS:
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess){
                            if(!isCollect) {
                                //收藏成功
                                isCollect = true;
                                collectCount++;
                                tv_detail_collect.setText(collectCount + "");
                                Util.ChangeTextImage(context, tv_detail_collect, R.drawable.ic_collect_red);
                            }else {
                                //取消收藏成功
                                isCollect = false;
                                collectCount--;
                                tv_detail_collect.setText(collectCount + "");
                                Util.ChangeTextImage(context, tv_detail_collect, R.drawable.ic_collect_black);
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
                    Log.e(TAG, Constant.RESULT + API.EDIT_COLLECTION_PRODUCT + "\n" + t.toString());
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
                        .setBackgroundResource(R.drawable.page_indicator_focused);
                if (arg0 != i) {
                    imageViews[i]
                            .setBackgroundResource(R.drawable.page_indicator_unfocused);
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
        }
    }

    /** 设置图片下方的小圆点 */
    private void setImageDian(List<View> mViewsList) {
        ll_viewpager_dian_detail_imgList.removeAllViews();
        imageViews = new ImageView[mViewsList.size()];
        System.out.println("mViewsList:" + mViewsList.size());
        for (int i = 0; i < mViewsList.size(); i++) {
            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(20,20);
            // 设置每个小圆点距离左边的间距
            margin.setMargins(5, 0, 5, 0);
            ImageView imageView = new ImageView(this);
            imageViews[i] = imageView;
            if (i == 0) {
                // 默认选中第一张图片
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                // 其他图片都设置未选中状态
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
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
}
