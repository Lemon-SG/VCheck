package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 16:38.
 * Desc:菜品单位实体类
 */
public class MenuUnit extends BaseModel<MenuUnit> {

    private static final String TAG = "MenuUnit";
    /** 菜品单位ID*/
    public String menu_unit_id;
    /** 菜品单位*/
    public String menu_unit;

    @Override
    public MenuUnit parse(JSONObject jsonObject) {
        Log.e(TAG, "开始解析");
        if(jsonObject != null && jsonObject.length() > 0){
            menu_unit_id = jsonObject.optString("menu_unit_id");
            menu_unit = jsonObject.optString("menu_unit");
            return this;
        }
        return null;
    }
}
