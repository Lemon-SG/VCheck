package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.tsz.afinal.FinalBitmap;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.util.ScreenUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Util;

/**
 * Created by Lemon on 2015/4/30.
 * Desc:主页列表适配器
 */
public class MainAdapter extends AbsAdapter<Article> {

    private static final String TAG = "MineAdapter";
    FinalBitmap finalBitmap;
    private ImageLoader imageLoader;
    private DisplayImageOptions option;
    private ScreenUtils.ScreenResolution screenResolution;

    public MainAdapter(Activity context, int layout) {
        super(context, layout);
        imageLoader = ImageLoader.getInstance();
        option = MyApplication.getDisplayImageOptions(context, 25, R.drawable.default_member);
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.mipmap.default_menu);
        finalBitmap.configLoadfailImage(R.mipmap.default_menu);
        screenResolution = ScreenUtils.getScreenResolution(context);
    }

    @Override
    public ViewHolder<Article> getHolder() {
        return new ArticleViewHolder();
    }

    private class ArticleViewHolder implements ViewHolder<Article> {

        /** 分割线*/
        private LinearLayout driver;
        /** 文章图片显示*/
        private ImageView iv_menu_img_item;
        /** 商家图标*/
        private ImageView iv_member_img_item;
        /** 文章标题显示*/
        private TextView tv_title;
        /** 文章子标题显示*/
        private TextView tv_sub_title;
        /** 优惠价格显示*/
        private TextView tv_special_price;
        /** 价格单位及菜品单位显示*/
        private TextView tv_price_menu_unit;
        /** 原价显示:需要加删除线*/
        private TextView tv_original_price;
        /** 菜品库存数量显示*/
        private TextView tv_menu_stock;
        /** 会员名称显示*/
        private TextView tv_main_member_name;
        /** 时间标签显示*/
        private TextView tv_detail_time;
        private RelativeLayout rl_main_img;
//        /** 商家地址*/
//        private TextView tv_store_address;

        @Override
        public void initViews(View v, int position) {
            tv_detail_time = (TextView) v.findViewById(R.id.tv_detail_time);
            iv_menu_img_item = (ImageView) v.findViewById(R.id.iv_menu_img_item);
            driver = (LinearLayout) v.findViewById(R.id.driver);
            tv_title = (TextView) v.findViewById(R.id.tv_title);
//            tv_store_address = (TextView) v.findViewById(R.id.tv_store_address);
            tv_sub_title = (TextView) v.findViewById(R.id.tv_sub_title);
            tv_special_price = (TextView) v.findViewById(R.id.tv_special_price);
            tv_price_menu_unit = (TextView) v.findViewById(R.id.tv_price_menu_unit);
            tv_original_price = (TextView) v.findViewById(R.id.tv_original_price);
            tv_menu_stock = (TextView) v.findViewById(R.id.tv_menu_stock);
            iv_member_img_item = (ImageView) v.findViewById(R.id.iv_member_img_item);
            tv_main_member_name = (TextView) v.findViewById(R.id.tv_main_member_name);
            rl_main_img = (RelativeLayout) v.findViewById(R.id.rl_main_img);
        }

        @Override
        public void updateData(Article article, int position) {
            //获取16:9尺寸的图片宽度，距离屏幕两侧各10dp
            int width = (screenResolution.getWidth() - ScreenUtils.dpToPxInt(context, 20));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, (int) (width * 0.5625));
            iv_menu_img_item.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv_menu_img_item.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams layoutParam = (LinearLayout.LayoutParams) ScreenUtils.getImageParam(context, 20);
            layoutParam.setMargins(0, ScreenUtils.dpToPxInt(context, 10), 0, -(ScreenUtils.dpToPxInt(context, 40)));
            rl_main_img.setLayoutParams(layoutParam);

            finalBitmap.display(iv_menu_img_item, article.article_image.source);
            imageLoader.displayImage(article.member_info.icon_image.thumb, iv_member_img_item, option);
            driver.setVisibility(View.VISIBLE);
            tv_main_member_name.setText(article.member_info.member_name);
            tv_title.setText(article.title);
            tv_detail_time.setText(article.article_date);
            //            tv_store_address.setText(article.store_info.address.substring(0, 4));
            tv_sub_title.setText(article.sub_title);
            tv_price_menu_unit.setText(article.menu_info.price.price_unit + "/" +article.menu_info.menu_unit.menu_unit);
            //判断是否有优惠价格：如果有优惠价格，显示优惠价格并显示原价添加删除线；如果没有优惠价格，显示原价，隐藏原价view
            if(!StringUtils.isBlank(article.menu_info.price.special_price)){
                //有优惠价格
                tv_special_price.setText(article.menu_info.price.special_price);
                tv_original_price.setText(article.menu_info.price.original_price + article.menu_info.price.price_unit);
                Util.PaintTvAddStrike(tv_original_price);
                tv_original_price.setVisibility(View.VISIBLE);
            }else{
                //无优惠价格
                tv_special_price.setText(article.menu_info.price.original_price);
                tv_original_price.setVisibility(View.GONE);
            }
            switch (Integer.parseInt(article.menu_info.menu_status.menu_status_id)) {
                case Constant.MENU_STATUS_OUT://已售罄
                    tv_menu_stock.setText(article.menu_info.menu_status.menu_status);
                    tv_menu_stock.setTextColor(context.getResources().getColor(R.color.main_gray_address));
                    break;
                case Constant.MENU_STATUS_OVER://已结束
                    tv_menu_stock.setText(article.menu_info.menu_status.menu_status);
                    tv_menu_stock.setTextColor(context.getResources().getColor(R.color.main_gray_address));
                    break;
                case Constant.MENU_STATUS_SALE://销售中
                    tv_menu_stock.setText("剩余" + article.menu_info.stock.menu_count + article.menu_info.stock.menu_unit);
                    tv_menu_stock.setTextColor(context.getResources().getColor(R.color.main_org_price));
                    break;
            }
        }

        @Override
        public void doOthers(Article article, int position) {

        }
    }
}
