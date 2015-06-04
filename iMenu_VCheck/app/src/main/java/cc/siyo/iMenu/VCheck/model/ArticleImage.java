package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 17:42.
 * Desc:��Ʒ����������ͼƬ����ʵ����
 */
public class ArticleImage extends BaseModel<ArticleImage> {

    private static final String TAG = "ArticleImageList";
    /** ͼƬID*/
    public String article_image_id;
    /** ͼƬʵ����*/
    public Image image;
    /** �����*/
    public String sort_order;
    /** �Ƿ�Ϊ����ͼ(1-����/0-�Ƿ���)*/
    public String is_index;

    @Override
    public ArticleImage parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
            article_image_id = jsonObject.optString("article_image_id");
            if(jsonObject.optJSONObject("image") != null && jsonObject.optJSONObject("image").length() > 0){
                image = new Image().parse(jsonObject.optJSONObject("image"));
            }
            sort_order = jsonObject.optString("sort_order");
            is_index = jsonObject.optString("is_index");
            return this;
        }
        return null;
    }
}
