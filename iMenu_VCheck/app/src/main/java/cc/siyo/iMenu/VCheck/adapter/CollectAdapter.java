package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.CollectListActivity;
import cc.siyo.iMenu.VCheck.activity.DetailActivity;
import cc.siyo.iMenu.VCheck.activity.OrderConfirmActivity;
import cc.siyo.iMenu.VCheck.activity.OrderDetailActivity;
import cc.siyo.iMenu.VCheck.activity.OrderListActivity;
import cc.siyo.iMenu.VCheck.activity.OrderWriteActivity;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.MemberOrder;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.PromptDialog;

/**
 * Created by Lemon on 2015/7/6.
 * Desc:收藏列表适配器
 */
public class CollectAdapter extends AbsAdapter<Article>{

    private static final String TAG = "CollectAdapter";
    /** A FINAL 框架的HTTP请求工具*/
    private FinalBitmap finalBitmap;

    String orderId = "";
    PromptDialog promptDialog;

    public CollectAdapter(Activity context, int layout) {
        super(context, layout);
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.ic_member);
        finalBitmap.configLoadfailImage(R.drawable.ic_member);
    }

    @Override
    public ViewHolder<Article> getHolder() {
        return new ArticleViewHolder();
    }


    private class ArticleViewHolder implements ViewHolder<Article> {

        /** 订单图片*/
        private ImageView iv_collect_menu;
        /** 订单状态*/
        private TextView tv_collect_status;
        /** 订单名称*/
        private TextView tv_collect_menu_name;
        /** 蔡品单价*/
        private TextView tv_collect_menu_price;
        /** 支付*/
        private TextView tv_collect_pay;
        /** 全局layout*/
        private LinearLayout llCollectItem;

        @Override
        public void initViews(View v, int position) {
            llCollectItem = (LinearLayout) v.findViewById(R.id.llCollectItem);
            tv_collect_status = (TextView) v.findViewById(R.id.tv_collect_status);
            tv_collect_menu_name = (TextView) v.findViewById(R.id.tv_collect_menu_name);
            tv_collect_menu_price = (TextView) v.findViewById(R.id.tv_collect_menu_price);
            tv_collect_pay = (TextView) v.findViewById(R.id.tv_collect_pay);
            iv_collect_menu = (ImageView) v.findViewById(R.id.iv_collect_menu);
        }

        @Override
        public void updateData(final Article article, int position) {
            finalBitmap.display(iv_collect_menu, article.article_image.source);
            switch (Integer.parseInt(article.menu_info.menu_status.menu_status_id)) {
                case Constant.MENU_STATUS_OUT://已售罄
                    tv_collect_status.setText(article.menu_info.menu_status.menu_status);
                    tv_collect_pay.setVisibility(View.GONE);
                    break;
                case Constant.MENU_STATUS_OVER://已结束
                    tv_collect_status.setText(article.menu_info.menu_status.menu_status);
                    tv_collect_pay.setVisibility(View.GONE);
                    break;
                case Constant.MENU_STATUS_SALE://销售中
                    tv_collect_status.setText("售卖中");
                    tv_collect_pay.setVisibility(View.VISIBLE);
                    break;
            }
            tv_collect_menu_name.setText(article.menu_info.menu_name);
            if(!StringUtils.isBlank(article.menu_info.price.special_price)) {
                //有促销价格
                tv_collect_menu_price.setText(article.menu_info.price.special_price + article.menu_info.price.price_unit);
            } else {
                //无促销价格
                tv_collect_menu_price.setText(article.menu_info.price.original_price + article.menu_info.price.price_unit);
            }
            tv_collect_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderWriteActivity.class);
                    intent.putExtra("article", article);
                    context.startActivity(intent);
                }
            });
            llCollectItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //长安删除订单
                    if(promptDialog == null) {
                        promptDialog = new PromptDialog(context, "提示", "确定要删除此收藏？", "确定", "取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //删除订单
                                ((CollectListActivity) context).UploadAdapter(article.article_id);
                                promptDialog.dismiss();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                promptDialog.dismiss();
                            }
                        });
                    }
                    promptDialog.show();
                    return false;
                }
            });
            llCollectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("article_id", article.article_id);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void doOthers(Article article, int position) {

        }
    }
}
