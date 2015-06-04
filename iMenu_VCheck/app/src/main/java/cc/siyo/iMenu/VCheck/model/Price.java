package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 16:28.
 * Desc:价格实体类
 */
public class Price extends BaseModel<Price> {

    private static final String TAG = "Price";
    /** 原价*/
    public String original_price;
    /** 优惠价格*/
    public String special_price;
    /** 价格单位*/
    public String price_unit;

    @Override
    public Price parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            original_price = jsonObject.optString("original_price");
            //TODO special_price文档上拼写为speical_price，待接口数据完成后对比
            special_price = jsonObject.optString("special_price");
            price_unit = jsonObject.optString("price_unit");
            return this;
        }
        return null;
    }
}
