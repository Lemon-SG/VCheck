package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 17:50.
 * Desc:�ղ�ʵ����
 */
public class Collection extends BaseModel<Collection> {

    private static final String TAG = "Collection";
    /** �ղ�����*/
    public String collection_count;
    /** �Ƿ����ղ�(1-���ղ�/0-δ�ղ�)*/
    public String is_collected;


    @Override
    public Collection parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
            collection_count = jsonObject.optString("collection_count");
            is_collected = jsonObject.optString("is_collected");
            return  this;
        }
        return null;
    }
}
