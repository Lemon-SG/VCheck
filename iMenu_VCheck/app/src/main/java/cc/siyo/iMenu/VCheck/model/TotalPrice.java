package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.util.NumberFormatUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;

/**
 * Created by Lemon on 2015/6/4 16:28.
 * Desc:总价格实体类
 */
public class TotalPrice extends BaseModel<TotalPrice> {

    private static final String TAG = "TotalPrice";
    /** 原价*/
    public String original_price;
    /** 优惠价格*/
    public String special_price;
    /** 价格单位*/
    public String price_unit;

    @Override
    public TotalPrice parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            original_price = NumberFormatUtils.format(Double.parseDouble(jsonObject.optString("original_price")));
            if(!StringUtils.isBlank(jsonObject.optString("special_price"))) {
                special_price = NumberFormatUtils.format(Double.parseDouble(jsonObject.optString("special_price")));
            } else {
                special_price = "0.0";
            }
            price_unit = jsonObject.optString("price_unit");
            return this;
        }
        return null;
    }
}
