package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/7/30 17:55.
 * Desc:推送实体
 */
public class PushInfo extends BaseModel<PushInfo> {

    /** 推送总开关 1->开启 | 0-> 关闭*/
    public String push_switch;
    /** 消费确认开关 1->开启 | 0-> 关闭*/
    public String consume_msg;
    /** 退款确认开关 1->开启 | 0-> 关闭*/
    public String refund_msg;
    /** 获得礼券开关 1->开启 | 0-> 关闭*/
    public String voucher_msg;

    @Override
    public PushInfo parse(JSONObject jsonObject) {
        if(jsonObject != null) {
            push_switch = jsonObject.optString("push_switch");
            consume_msg = jsonObject.optString("consume_msg");
            refund_msg = jsonObject.optString("refund_msg");
            voucher_msg = jsonObject.optString("voucher_msg");
            return this;
        }
        return null;
    }
}
