package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.util.NumberFormatUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;

/**
 * Created by Lemon on 2015/7/27 14:35.
 * Desc:礼券实体
 */
public class VoucherInfo extends BaseModel<VoucherInfo> {

    /** 会员优惠券ID*/
    public String voucher_member_id;
    /** 优惠券名称*/
    public String voucher_name;
    /** 描述信息*/
    public String description;
    /** 开始时间*/
    public String begin_date;
    /** 结束时间*/
    public String end_date;
    /** 优惠券状态((1-未使用/2-已使用/0-无效/12-未使用未开始/13-未使用过期))*/
    public String voucher_status;
    /** 金额*/
    public String discount;
    /** 订单限额*/
    public String total;
    /** 限制描述*/
    public String limit_description;
    /** 会员礼券总数量*/
    public String voucher_total_count;
    /** 会员可使用的礼券数量*/
    public String voucher_use_count;

    @Override
    public VoucherInfo parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0) {
            voucher_member_id = jsonObject.optString("voucher_member_id");
            voucher_name = jsonObject.optString("voucher_name");
            description = jsonObject.optString("description");
            begin_date = jsonObject.optString("begin_date");
            end_date = jsonObject.optString("end_date");
            voucher_total_count = jsonObject.optString("voucher_total_count");
            voucher_use_count = jsonObject.optString("voucher_use_count");
            voucher_status = jsonObject.optString("voucher_status");
            if(!StringUtils.isBlank(jsonObject.optString("discount"))) {
                discount = NumberFormatUtils.format(Double.parseDouble(jsonObject.optString("discount")));
            }
            if(!StringUtils.isBlank(jsonObject.optString("total"))) {
                total = NumberFormatUtils.format(Double.parseDouble(jsonObject.optString("total")));
            }
            limit_description = jsonObject.optString("limit_description");
            return this;
        }
        return null;
    }
}
