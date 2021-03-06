package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.VoucherInfo;
import cc.siyo.iMenu.VCheck.util.TimeUtil;

/**
 * Created by Lemon on 2015/7/25.
 * Desc:礼券列表适配器
 */
public class VoucherAdapter extends AbsAdapter<VoucherInfo>{

    private static final String TAG = "VoucherAdapter";

    public VoucherAdapter(Activity context, int layout) {
        super(context, layout);
    }

    @Override
    public ViewHolder<VoucherInfo> getHolder() {
        return new VoucherInfoViewHolder();
    }


    private class VoucherInfoViewHolder implements ViewHolder<VoucherInfo> {

        /** 可使用优惠券数量*/
        private TextView tvVoucherDiscount;
        /** 可使用优惠券数量单位*/
        private TextView tvVoucherDiscountUnit;
        /** 优惠详细*/
        private TextView tvVoucherDescription;
        /** 优惠限额详细*/
        private TextView tvVoucherLimitDescription;
        /** 有效期*/
        private TextView tvVoucherEndDate;
        private ImageView ivVoucherTag;
        private LinearLayout llVoucher;

        @Override
        public void initViews(View v, int position) {
            tvVoucherDiscount = (TextView) v.findViewById(R.id.tvVoucherDiscount);
            tvVoucherDiscountUnit = (TextView) v.findViewById(R.id.tvVoucherDiscountUnit);
            tvVoucherDescription = (TextView) v.findViewById(R.id.tvVoucherDescription);
            tvVoucherLimitDescription = (TextView) v.findViewById(R.id.tvVoucherLimitDescription);
            tvVoucherEndDate = (TextView) v.findViewById(R.id.tvVoucherEndDate);
            ivVoucherTag = (ImageView) v.findViewById(R.id.ivVoucherTag);
            llVoucher = (LinearLayout) v.findViewById(R.id.llVoucher);
        }

        @Override
        public void updateData(VoucherInfo voucherInfo, int position) {
            tvVoucherDiscount.setText(voucherInfo.discount);
            tvVoucherDiscountUnit.setText("元");
            tvVoucherDescription.setText(voucherInfo.description);
            tvVoucherLimitDescription.setText(voucherInfo.limit_description);
            tvVoucherEndDate.setText("有效期" + TimeUtil.FormatTime(voucherInfo.begin_date, "yyyy-MM-dd") + "至" + TimeUtil.FormatTime(voucherInfo.end_date, "yyyy-MM-dd"));
            switch (Integer.parseInt(voucherInfo.voucher_status)) {
                case Constant.VOUCHER_STATUS_NO_SPEND:
                    //未使用
//                    tvVoucherDiscount.setTextColor(context.getResources().getColor(R.color.voucher_deep_org));
//                    tvVoucherDiscountUnit.setTextColor(context.getResources().getColor(R.color.voucher_deep_org));
//                    tvVoucherDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_org));
//                    tvVoucherLimitDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_org));
//                    tvVoucherEndDate.setTextColor(context.getResources().getColor(R.color.voucher_light_org));
                    llVoucher.setBackgroundResource(R.drawable.bg_voucher_black);
                    ivVoucherTag.setVisibility(View.INVISIBLE);
                    break;
                case Constant.VOUCHER_STATUS_SPENDED://已使用
//                    tvVoucherDiscount.setTextColor(context.getResources().getColor(R.color.voucher_deep_gray));
//                    tvVoucherDiscountUnit.setTextColor(context.getResources().getColor(R.color.voucher_deep_gray));
//                    tvVoucherDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherLimitDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherEndDate.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherDescription.setText(voucherInfo.description + "(已使用)");
                    llVoucher.setBackgroundResource(R.drawable.bg_voucher_grey);
                    ivVoucherTag.setVisibility(View.VISIBLE);
                    ivVoucherTag.setImageResource(R.drawable.tag_voucher_used);
                    break;
                case Constant.VOUCHER_STATUS_NO://无效
//                    tvVoucherDiscount.setTextColor(context.getResources().getColor(R.color.voucher_deep_gray));
//                    tvVoucherDiscountUnit.setTextColor(context.getResources().getColor(R.color.voucher_deep_gray));
//                    tvVoucherDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherLimitDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherEndDate.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherDescription.setText(voucherInfo.description + "(无效)");
                    llVoucher.setBackgroundResource(R.drawable.bg_voucher_grey);
                    ivVoucherTag.setVisibility(View.VISIBLE);
                    ivVoucherTag.setImageResource(R.drawable.tag_voucher_expired);
                    break;
                case Constant.VOUCHER_STATUS_NO_START://未使用未开始
//                    tvVoucherDiscount.setTextColor(context.getResources().getColor(R.color.voucher_deep_gray));
//                    tvVoucherDiscountUnit.setTextColor(context.getResources().getColor(R.color.voucher_deep_gray));
//                    tvVoucherDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherLimitDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherEndDate.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherDescription.setText(voucherInfo.description + "(未开始)");
                    llVoucher.setBackgroundResource(R.drawable.bg_voucher_grey);
                    ivVoucherTag.setVisibility(View.INVISIBLE);
                    break;
                case Constant.VOUCHER_STATUS_NO_GUO://未使用过期
//                    tvVoucherDiscount.setTextColor(context.getResources().getColor(R.color.voucher_deep_gray));
//                    tvVoucherDiscountUnit.setTextColor(context.getResources().getColor(R.color.voucher_deep_gray));
//                    tvVoucherDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherLimitDescription.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
//                    tvVoucherEndDate.setTextColor(context.getResources().getColor(R.color.voucher_light_gray));
                    llVoucher.setBackgroundResource(R.drawable.bg_voucher_grey);
                    ivVoucherTag.setVisibility(View.VISIBLE);
                    ivVoucherTag.setImageResource(R.drawable.tag_voucher_expired);
                    break;
            }
        }

        @Override
        public void doOthers(VoucherInfo voucherInfo, int position) {

        }
    }
}
