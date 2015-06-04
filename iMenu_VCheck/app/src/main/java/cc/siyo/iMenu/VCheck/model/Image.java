package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 17:27.
 * Desc:ͼƬʵ����
 */
public class Image extends BaseModel<Image> {

    private static final String TAG = "Image";
    /** ����ͼ*/
    public String thumb;
    /** ԭͼ*/
    public String source;

    @Override
    public Image parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
            thumb = jsonObject.optString("thumb");
            source = jsonObject.optString("source");
            return this;
        }
        return null;
    }
}
