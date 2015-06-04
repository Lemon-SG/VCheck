package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 17:50.
 * Desc:收藏实体类
 */
public class Collection extends BaseModel<Collection> {

    private static final String TAG = "Collection";
    /** 收藏总数*/
    public String collection_count;
    /** 是否已收藏(1-已收藏/0-未收藏)*/
    public String is_collected;


    @Override
    public Collection parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            collection_count = jsonObject.optString("collection_count");
            is_collected = jsonObject.optString("is_collected");
            return  this;
        }
        return null;
    }
}
