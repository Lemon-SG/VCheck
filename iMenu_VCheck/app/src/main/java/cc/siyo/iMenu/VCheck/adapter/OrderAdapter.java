package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.MemberOrder;
import cc.siyo.iMenu.VCheck.model.Region;

/**
 * Created by Lemon on 2015/7/6.
 * Desc:订单列表适配器
 */
public class OrderAdapter extends AbsAdapter<MemberOrder>{

    private static final String TAG = "OrderAdapter";
    /** A FINAL 框架的HTTP请求工具*/
    private FinalBitmap finalBitmap;

    public OrderAdapter(Activity context, int layout) {
        super(context, layout);
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.ic_member);
        finalBitmap.configLoadfailImage(R.drawable.ic_member);
    }

    @Override
    public ViewHolder<MemberOrder> getHolder() {
        return new MemberOrderViewHolder();
    }


    private class MemberOrderViewHolder implements ViewHolder<MemberOrder> {

        /** 订单图片*/
        private ImageView iv_order_menu;
        /** 订单状态*/
        private TextView tv_order_status;
        /** 订单名称*/
        private TextView tv_order_menu_name;
        /** 蔡品单价*/
        private TextView tv_order_menu_price;
        /** 菜品数量*/
        private TextView tv_order_menu_count;
        /** 支付*/
        private TextView tv_order_pay;

        @Override
        public void initViews(View v, int position) {
            tv_order_status = (TextView) v.findViewById(R.id.tv_order_status);
            tv_order_menu_name = (TextView) v.findViewById(R.id.tv_order_menu_name);
            tv_order_menu_price = (TextView) v.findViewById(R.id.tv_order_menu_price);
            tv_order_menu_count = (TextView) v.findViewById(R.id.tv_order_menu_count);
            tv_order_pay = (TextView) v.findViewById(R.id.tv_order_pay);
            iv_order_menu = (ImageView) v.findViewById(R.id.iv_order_menu);
        }

        @Override
        public void updateData(MemberOrder memberOrder, int position) {
            finalBitmap.display(iv_order_menu, memberOrder.article_info.article_image.source);
            tv_order_status.setText(memberOrder.order_info.order_type_description);
            tv_order_menu_name.setText(memberOrder.order_info.menu.menu_name);
            tv_order_menu_price.setText(memberOrder.order_info.menu.price.special_price + memberOrder.order_info.menu.price.price_unit);
            tv_order_menu_count.setText(memberOrder.order_info.menu.count);
        }

        @Override
        public void doOthers(MemberOrder memberOrder, int position) {

        }
    }
}
