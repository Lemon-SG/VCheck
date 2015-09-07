package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by Lemon on 2015/7/3 9:10.
 * Desc:订单实体类
 */
public class OrderInfo extends BaseModel<OrderInfo> {

    private static final String TAG = "OrderInfo";

    /** 订单ID*/
    public String order_id;
    /** 订单编号*/
    public String order_no;
    /** 创建时间*/
    public String create_date;
    /** 购买时手机号码*/
    public String mobile;
    /** 订单类型*/
    public String order_type;
    /** 订单类型描述*/
    public String order_type_description;
    /** 支付方式实体*/
    public PaymentInfo paymentInfo;
    /** 优惠实体*/
    public CouponInfo couponInfo;
    /** 礼券实体*/
    public VoucherInfo voucherInfo;
    /** 总价格实体*/
    public TotalPrice totalPrice;
    /** 菜品实体*/
    public Menu menu;
    /** 消费实体*/
    public ConsumeInfo consume_info;
    /** 是否可退款(1-可退款/0-不可退款)*/
    public String is_return;

    @Override
    public OrderInfo parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            order_id = jsonObject.optString("order_id");
            order_no = jsonObject.optString("order_no");
            create_date = jsonObject.optString("create_date");
            mobile = jsonObject.optString("mobile");
            order_type = jsonObject.optString("order_type");
            order_type_description = jsonObject.optString("order_type_description");
            is_return = jsonObject.optString("is_return");
            paymentInfo = new PaymentInfo().parse(jsonObject.optJSONObject("payment_info"));
            couponInfo = new CouponInfo().parse(jsonObject.optJSONObject("coupon_info"));
            voucherInfo = new VoucherInfo().parse(jsonObject.optJSONObject("voucher_info"));
            totalPrice = new TotalPrice().parse(jsonObject.optJSONObject("total_price"));
            menu = new Menu().parse(jsonObject.optJSONObject("menu_info"));
            consume_info = new ConsumeInfo().parse(jsonObject.optJSONObject("consume_info"));
            return this;
        }
        return null;
    }
}
