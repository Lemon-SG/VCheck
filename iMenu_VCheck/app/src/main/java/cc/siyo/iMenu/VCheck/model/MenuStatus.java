package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 16:40.
 * Desc:��Ʒ״̬ʵ����
 */
public class MenuStatus extends BaseModel<MenuStatus>{

    private static final String TAG = "MenuStatus";
    /** ��Ʒ״̬ID*/
    public String menu_status_id;
    /** ��Ʒ״̬*/
    public String menu_status;

    @Override
    public MenuStatus parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
            menu_status_id = jsonObject.optString("menu_status_id");
            menu_status = jsonObject.optString("menu_status");
            return this;
        }
        return null;
    }
}
