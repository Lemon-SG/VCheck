package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.Image;
import cc.siyo.iMenu.VCheck.model.Menu;
import cc.siyo.iMenu.VCheck.util.NumberFormatUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Util;

/**
 * Created by Lemon on 2015/4/30.
 * Desc:主页列表适配器
 */
public class MainAdapter extends AbsAdapter<Article> {

    private static final String TAG = "MineAdapter";
    FinalBitmap finalBitmap;

    public MainAdapter(Activity context, int layout) {
        super(context, layout);
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.test_menu_img);
        finalBitmap.configLoadfailImage(R.drawable.test_menu_img);
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

        @Override
        public void initViews(View v, int position) {
            iv_menu_img_item = (ImageView) v.findViewById(R.id.iv_menu_img_item);
            driver = (LinearLayout) v.findViewById(R.id.driver);
            tv_title = (TextView) v.findViewById(R.id.tv_title);
            tv_sub_title = (TextView) v.findViewById(R.id.tv_sub_title);
            tv_special_price = (TextView) v.findViewById(R.id.tv_special_price);
            tv_price_menu_unit = (TextView) v.findViewById(R.id.tv_price_menu_unit);
            tv_original_price = (TextView) v.findViewById(R.id.tv_original_price);
            tv_menu_stock = (TextView) v.findViewById(R.id.tv_menu_stock);
            iv_member_img_item = (ImageView) v.findViewById(R.id.iv_member_img_item);
        }

        @Override
        public void updateData(Article article, int position) {
            finalBitmap.display(iv_menu_img_item, article.article_image.source);
            finalBitmap.display(iv_member_img_item, article.member_info.icon_image.thumb);
            driver.setVisibility(View.VISIBLE);
            Log.e(TAG, "position = " + position + "|size = " + getDataList().size());
            if(position + 1 == getDataList().size()){
                //不是末尾->分割线隐藏
                driver.setVisibility(View.GONE);
            }
            tv_title.setText(article.title);
            tv_sub_title.setText(article.sub_title);
            tv_price_menu_unit.setText(article.menu_info.price.price_unit + "/" +article.menu_info.menu_unit.menu_unit);
            //判断是否有优惠价格：如果有优惠价格，显示优惠价格并显示原价添加删除线；如果没有优惠价格，显示原价，隐藏原价view
            if(!StringUtils.isBlank(article.menu_info.price.special_price)){
                //有优惠价格
                Log.e(TAG, "有优惠价格");
                tv_special_price.setText(article.menu_info.price.special_price);
                tv_original_price.setText(article.menu_info.price.original_price + article.menu_info.price.price_unit);
                Util.PaintTvAddStrike(tv_original_price);
                tv_original_price.setVisibility(View.VISIBLE);
            }else{
                //无优惠价格
                Log.e(TAG, "无优惠价格");
                tv_special_price.setText(article.menu_info.price.original_price);
                tv_original_price.setVisibility(View.GONE);
            }
            if(article.menu_info.stock.menu_count.equals(0)) {
                //无库存
                tv_menu_stock.setText(article.menu_info.stock.out_of_stock_info);
            } else {
                //有库存
                tv_menu_stock.setText("仅剩" + article.menu_info.stock.menu_count + article.menu_info.stock.menu_unit);
            }
        }

        @Override
        public void doOthers(Article article, int position) {

        }
    }
}
