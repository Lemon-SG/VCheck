package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 16:40.
 * Desc:菜品状态实体类
 */
public class MenuStatus extends BaseModel<MenuStatus>{

    private static final String TAG = "MenuStatus";
    /** 菜品状态ID*/
    public String menu_status_id;
    /** 菜品状态*/
    public String menu_status;

    @Override
    public MenuStatus parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            menu_status_id = jsonObject.optString("menu_status_id");
            menu_status = jsonObject.optString("menu_status");
            return this;
        }
        return null;
    }
}
