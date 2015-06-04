package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 16:38.
 * Desc:��Ʒ��λʵ����
 */
public class MenuUnit extends BaseModel<MenuUnit> {

    private static final String TAG = "MenuUnit";
    /** ��Ʒ��λID*/
    public String menu_unit_id;
    /** ��Ʒ��λ*/
    public String menu_unit;

    @Override
    public MenuUnit parse(JSONObject jsonObject) {
        Log.e(TAG, "��ʼ����");
        if(jsonObject != null && jsonObject.length() > 0){
            menu_unit_id = jsonObject.optString("menu_unit_id");
            menu_unit = jsonObject.optString("menu_unit");
            return this;
        }
        return null;
    }
}
