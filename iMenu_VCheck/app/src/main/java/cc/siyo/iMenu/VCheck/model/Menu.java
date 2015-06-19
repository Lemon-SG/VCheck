package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/4/30.
 * Desc:菜品实体类menu_info
 */
public class Menu extends BaseModel<Menu>{

    private static final String TAG = "Menu";
    /** 菜品ID*/
    public String menu_id;
    /** 菜品名称*/
    public String menu_name;
    /** 价格实体*/
    public Price price;
    /** 产品数量实体*/
    public Stock stock;
    /** 菜品单位*/
    public MenuUnit menu_unit;
    /** 菜品状态*/
    public MenuStatus menu_status;
    /** 结束时间*/
    public String end_date;

    @Override
    public Menu parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            menu_id = jsonObject.optString("menu_id");
            menu_name = jsonObject.optString("menu_name");
            if(jsonObject.optJSONObject("price") != null && jsonObject.optJSONObject("price").length() > 0){
                price = new Price().parse(jsonObject.optJSONObject("price"));
            }
            if(jsonObject.optJSONObject("stock") != null && jsonObject.optJSONObject("stock").length() > 0){
                stock = new Stock().parse(jsonObject.optJSONObject("stock"));
            }
            if(jsonObject.optJSONObject("menu_unit") != null && jsonObject.optJSONObject("menu_unit").length() > 0){
                menu_unit = new MenuUnit().parse(jsonObject.optJSONObject("menu_unit"));
            }
            if(jsonObject.optJSONObject("menu_status") != null && jsonObject.optJSONObject("menu_status").length() > 0){
                menu_status = new MenuStatus().parse(jsonObject.optJSONObject("menu_status"));
            }
            end_date = jsonObject.optString("end_date");
            return this;
        }
        return null;
    }
}
