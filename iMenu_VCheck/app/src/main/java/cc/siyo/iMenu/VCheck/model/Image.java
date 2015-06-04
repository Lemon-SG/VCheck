package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 17:27.
 * Desc:图片实体类
 */
public class Image extends BaseModel<Image> {

    private static final String TAG = "Image";
    /** 缩略图*/
    public String thumb;
    /** 原图*/
    public String source;

    @Override
    public Image parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            thumb = jsonObject.optString("thumb");
            source = jsonObject.optString("source");
            return this;
        }
        return null;
    }
}
