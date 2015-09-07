package cc.siyo.iMenu.VCheck.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.DetailActivity;
import cc.siyo.iMenu.VCheck.activity.MenuBigImgViewPagerActivity;
import cc.siyo.iMenu.VCheck.adapter.DetailMenuAdapter;
import cc.siyo.iMenu.VCheck.adapter.ViewPagerAdapter;
import cc.siyo.iMenu.VCheck.model.ArticleContent;
import cc.siyo.iMenu.VCheck.model.ArticleImage;
import cc.siyo.iMenu.VCheck.model.ArticleMenu;
import cc.siyo.iMenu.VCheck.util.ScreenUtils;
import cc.siyo.iMenu.VCheck.util.Utility;

/**
 * Created by Lemon on 2015/5/6.
 * Desc:菜单界面
 */
public class DetailMenuFragment extends BaseFragment {

    private Context context;
    /** 菜单列表*/
    private ListView list_menu;
    /** 数据源*/
    private List<ArticleMenu> articleMenuList;
    /** 列表适配器*/
    private DetailMenuAdapter detailMenuAdapter;
    /** 菜品图片第一张显示*/
    private ImageView iv_menu_img;
    /** 文章内图片集合*/
    private List<ArticleImage> articleImageList;
    /** A FINAL 框架的HTTP请求工具*/
    private FinalBitmap finalBitmap;
    /** 菜品图片数量显示*/
    private TextView tv_menu_image_count;

    private ScreenUtils.ScreenResolution screenResolution;

    public static final DetailMenuFragment newInstance(List<ArticleMenu> articleMenuList, List<ArticleImage> articleImageList){
        DetailMenuFragment detailMenuFragment = new DetailMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("articleMenuList", (Serializable) articleMenuList);
        bundle.putSerializable("articleImageList", (Serializable) articleImageList);
        detailMenuFragment.setArguments(bundle);
        return detailMenuFragment;
    }

    @Override
    public int getContentView() {
        context = getActivity();
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.mipmap.default_menu);
        finalBitmap.configLoadfailImage(R.mipmap.default_menu);
        return R.layout.fram_menu;
    }

    @Override
    public void initView(View v) {

        iv_menu_img = (ImageView) v.findViewById(R.id.iv_menu_img);
        screenResolution = ScreenUtils.getScreenResolution(context);
        //获取16:9尺寸的图片宽度，距离屏幕两侧各10dp
        int width = (screenResolution.getWidth() - ScreenUtils.dpToPxInt(context, 20));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, (int) (width * 0.5625));
        iv_menu_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv_menu_img.setLayoutParams(layoutParams);

        list_menu = (ListView) v.findViewById(R.id.list_menu);
        tv_menu_image_count = (TextView) v.findViewById(R.id.tv_menu_image_count);
        list_menu.addFooterView(LayoutInflater.from(context).inflate(R.layout.list_item_footview_driver, null));
    }

    @Override
    public void initData() {
        articleMenuList = new ArrayList<>();
        detailMenuAdapter = new DetailMenuAdapter(getActivity(), R.layout.list_item_detail_menu);
        list_menu.setAdapter(detailMenuAdapter);

        if(getArguments() != null){
            articleMenuList = (List<ArticleMenu>) getArguments().getSerializable("articleMenuList");
            articleImageList = (List<ArticleImage>) getArguments().getSerializable("articleImageList");
            if(articleImageList != null && articleImageList.size() > 0){
                finalBitmap.display(iv_menu_img, articleImageList.get(0).image.source);
                tv_menu_image_count.setText(articleImageList.size() + "");
            }
            if(articleMenuList != null && articleMenuList.size() > 0){
                detailMenuAdapter.getDataList().clear();
                detailMenuAdapter.getDataList().addAll(articleMenuList);
                detailMenuAdapter.notifyDataSetChanged();
            }
        }

        iv_menu_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchBigViewPager(0);
            }
        });
    }

    /** 跳转大图模式*/
    private void switchBigViewPager(int index) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("articleImageList", (Serializable) articleImageList);
        intent.putExtra("bundle", bundle);
        bundle.putInt("index", index);
        intent.setClass(context, MenuBigImgViewPagerActivity.class);
        startActivity(intent);
    }
}
