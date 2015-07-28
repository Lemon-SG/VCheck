package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.OrderConfirmActivity;
import cc.siyo.iMenu.VCheck.activity.OrderDetailActivity;
import cc.siyo.iMenu.VCheck.activity.OrderListActivity;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.MemberOrder;
import cc.siyo.iMenu.VCheck.model.Region;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.PromptDialog;

/**
 * Created by Lemon on 2015/7/6.
 * Desc:订单列表适配器
 */
public class OrderAdapter extends AbsAdapter<MemberOrder>{

    private static final String TAG = "OrderAdapter";
    /** A FINAL 框架的HTTP请求工具*/
    private FinalBitmap finalBitmap;

    String orderId = "";
    PromptDialog promptDialog;

    public OrderAdapter(Activity context, int layout) {
        super(context, layout);
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.test_member_img);
        finalBitmap.configLoadfailImage(R.drawable.test_member_img);
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
        /** 全局layout*/
        private LinearLayout llOrderItem;

        @Override
        public void initViews(View v, int position) {
            llOrderItem = (LinearLayout) v.findViewById(R.id.llOrderItem);
            tv_order_status = (TextView) v.findViewById(R.id.tv_order_status);
            tv_order_menu_name = (TextView) v.findViewById(R.id.tv_order_menu_name);
            tv_order_menu_price = (TextView) v.findViewById(R.id.tv_order_menu_price);
            tv_order_menu_count = (TextView) v.findViewById(R.id.tv_order_menu_count);
            tv_order_pay = (TextView) v.findViewById(R.id.tv_order_pay);
            iv_order_menu = (ImageView) v.findViewById(R.id.iv_order_menu);
        }

        @Override
        public void updateData(final MemberOrder memberOrder, int position) {
            finalBitmap.display(iv_order_menu, memberOrder.article_info.article_image.source);
            tv_order_status.setText(memberOrder.order_info.order_type_description);
            tv_order_menu_name.setText(memberOrder.order_info.menu.menu_name);
            tv_order_menu_price.setText(memberOrder.order_info.menu.price.special_price + memberOrder.order_info.menu.price.price_unit);
            tv_order_menu_count.setText(memberOrder.order_info.menu.count);
            if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_NO_PAY)) {
                tv_order_pay.setVisibility(View.VISIBLE);
            } else {
                tv_order_pay.setVisibility(View.GONE);
            }
            tv_order_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderConfirmActivity.class);
                    intent.putExtra("orderInfo", memberOrder.order_info);
                    context.startActivity(intent);
                }
            });
            llOrderItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_NO_PAY)) {
                        orderId = memberOrder.order_info.order_id;
                    }
                    if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_RETURN_OVER)) {
                        orderId = memberOrder.order_info.order_id;
                    }
                    if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_NO_PAY_TIMEOUT)) {
                        orderId = memberOrder.order_info.order_id;
                    }
                    if(memberOrder.order_info.order_type.equals(Constant.ORDER_TYPE_PAY_SPEND)) {
                        orderId = memberOrder.order_info.order_id;
                    }
                    if(!StringUtils.isBlank(orderId)) {
                        //长安删除订单
                        if(promptDialog == null) {
                            promptDialog = new PromptDialog(context, "提示", "确定要删除此订单？", "确定", "取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //删除订单
                                    ((OrderListActivity)context).UploadAdapter(orderId);
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
                        orderId = "";
                    }
                    return false;
                }
            });
            llOrderItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("orderId", memberOrder.order_info.order_id);
                    intent.putExtra("orderType", memberOrder.order_info.order_type);
                    context.startActivityForResult(intent, Constant.RESQUEST_CODE);
                }
            });
        }

        @Override
        public void doOthers(MemberOrder memberOrder, int position) {

        }
    }
}
