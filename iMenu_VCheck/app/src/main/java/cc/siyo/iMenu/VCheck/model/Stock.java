package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 16:34.
 * Desc:产品数量实体类
 */
public class Stock extends BaseModel<Stock> {

    private static final String TAG = "Stock";
    /** 产品数量*/
    public String menu_count;
    /** 数量单位*/
    public String menu_unit;
    /** 在没有产品时显示的文字*/
    public String out_of_stock_info;

    @Override
    public Stock parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            menu_count = jsonObject.optString("menu_count");
            menu_unit = jsonObject.optString("menu_unit");
            out_of_stock_info = jsonObject.optString("out_of_stock_info");
            return this;
        }
        return null;
    }
}
