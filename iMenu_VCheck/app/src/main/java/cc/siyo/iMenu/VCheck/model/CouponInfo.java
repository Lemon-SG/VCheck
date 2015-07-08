package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by Lemon on 2015/7/3 9:21.
 * Desc:
 */
public class CouponInfo extends BaseModel<CouponInfo> {

    private static final String TAG = "CouponInfo";
    /** 优惠ID*/
    public String coupon_id;
    /** 优惠名称*/
    public String coupon_name;

    @Override
    public CouponInfo parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            coupon_id = jsonObject.optString("coupon_id");
            coupon_name = jsonObject.optString("coupon_name");
            return this;
        }
        return null;
    }
}
