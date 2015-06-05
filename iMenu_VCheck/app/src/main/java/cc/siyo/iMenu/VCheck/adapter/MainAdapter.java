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
import cc.siyo.iMenu.VCheck.model.Menu;
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
        finalBitmap.configLoadingImage(R.drawable.img);
        finalBitmap.configLoadfailImage(R.drawable.img);
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
        }

        @Override
        public void updateData(Article article, int position) {
            finalBitmap.display(iv_menu_img_item, "https://dn-img-seriousapps.qbox.me/business/14071541323372?imageView2/1/w/639/h/472/interlace/1/format/webp");
            driver.setVisibility(View.VISIBLE);
            Log.e(TAG, "position = " + position + "|size = " + getDataList().size());
            if(position + 1 == getDataList().size()){
                //需要处理末尾的分割线
                Log.e(TAG, "KILL分割线");
                driver.setVisibility(View.GONE);
            }
            tv_title.setText(article.title);
            tv_sub_title.setText(article.sub_title);
        }

        @Override
        public void doOthers(Article article, int position) {

        }
    }
}