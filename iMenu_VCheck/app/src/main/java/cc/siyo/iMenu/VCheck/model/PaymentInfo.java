package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/7/3 9:13.
 * Desc:支付方式
 */
public class PaymentInfo extends BaseModel<PaymentInfo>{

    private static final String TAG = "PaymentInfo";

    /** 支付方式code*/
    public String payment_code;
    /** 支付方式*/
    public String payment_name;

    @Override
    public PaymentInfo parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            payment_code = jsonObject.optString("payment_code");
            payment_name = jsonObject.optString("payment_name");
            return this;
        }
        return null;
    }
}
