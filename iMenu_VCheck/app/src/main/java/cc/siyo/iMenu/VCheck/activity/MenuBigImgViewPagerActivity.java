package cc.siyo.iMenu.VCheck.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.ViewPagerAdapter;
import cc.siyo.iMenu.VCheck.model.ArticleImage;
import cc.siyo.iMenu.VCheck.util.ScreenUtils;

/**
 * Created by Lemon on 2015/6/29 14:13.
 * Desc:菜品大图显示
 */
public class MenuBigImgViewPagerActivity extends BaseActivity {

    private static final String TAG = "MenuBigImgViewPager";
    /** ViewPager*/
    private ViewPager viewpager_bigImg;
    /** 当前图片下标*/
    private TextView tv_bigImg_index;

    /** viewPage适配器 */
    private ViewPagerAdapter mViewPagerAdapter;
    /** 文章内图片集合*/
    private List<ArticleImage> articleImageList;
    /** A FINAL 框架的HTTP请求工具*/
    private FinalBitmap finalBitmap;

    @Override
    public int getContentView() {
        finalBitmap = FinalBitmap.create(this);
        finalBitmap.configLoadingImage(R.mipmap.default_menu);
        finalBitmap.configLoadfailImage(R.mipmap.default_menu);
        return R.layout.activity_menu_bigimg;
    }

    @Override
    public void initView() {
        viewpager_bigImg = (ViewPager) findViewById(R.id.viewpager_bigImg);
        tv_bigImg_index = (TextView) findViewById(R.id.tv_bigImg_index);
    }

    @Override
    public void initData() {
        if(getIntent().getExtras() != null) {
            if(getIntent().getBundleExtra("bundle") != null) {
                Bundle bundle = getIntent().getBundleExtra("bundle");
                if(bundle.getSerializable("articleImageList") != null) {
                    articleImageList = (ArrayList<ArticleImage>) bundle.getSerializable("articleImageList");
                    if(articleImageList != null && articleImageList.size() > 0) {
                        List<View> mViewList = new ArrayList<>();
                        for (int i = 0; i < articleImageList.size(); i++) {
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.layout_viewpager_item, null);
                            ImageView iv_viewpager_img = (ImageView) view.findViewById(R.id.iv_viewpager_img);

                            //获取16:9尺寸的图片宽度，距离屏幕两侧各10dp
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ScreenUtils.getImageParam(context, 0);
                            iv_viewpager_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            iv_viewpager_img.setLayoutParams(layoutParams);


                            finalBitmap.display(iv_viewpager_img, articleImageList.get(i).image.source);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                            mViewList.add(view);
                        }
                        mViewPagerAdapter = new ViewPagerAdapter(this, mViewList);
                        viewpager_bigImg.setAdapter(mViewPagerAdapter);
                        viewpager_bigImg.getParent().requestDisallowInterceptTouchEvent(true);
                        viewpager_bigImg.setOnPageChangeListener(new PageChangeListener());
                        viewpager_bigImg.setCurrentItem(bundle.getInt("index"));
                        tv_bigImg_index.setText(bundle.getInt("index") + 1 + "/" + articleImageList.size());
                    }
                }
            }
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
            tv_bigImg_index.setText(arg0 + 1 + "/" + articleImageList.size());
            // 遍历数组让当前选中图片下的小圆点设置颜色
//            for (int i = 0; i < imageViews.length; i++) {
//                imageViews[arg0]
//                        .setBackgroundResource(R.drawable.page_indicator_focused);
//                if (arg0 != i) {
//                    imageViews[i]
//                            .setBackgroundResource(R.drawable.page_indicator_unfocused);
//                }
//            }
        }
    }
}
