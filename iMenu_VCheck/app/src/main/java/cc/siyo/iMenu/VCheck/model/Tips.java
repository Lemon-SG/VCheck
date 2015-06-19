package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 17:37.
 * Desc:提示实体类
 */
public class Tips extends BaseModel<Tips> {

    private static final String TAG = "Tips";
    /** 提示内容*/
    public String content;

    @Override
    public Tips parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            content = jsonObject.optString("content");
            return this;
        }
        return null;
    }
}
