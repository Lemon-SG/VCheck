package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 17:58.
 * Desc:��������ʵ����
 */
public class ArticleContent extends BaseModel<ArticleContent> {

    private static final String TAG = "ArticleContent";
    /** ����ID*/
    public String article_content_id;
    /** ͼƬʵ����*/
    public Image image;
    /** �������*/
    public String title;
    /** ��������*/
    public String content;


    @Override
    public ArticleContent parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
            article_content_id = jsonObject.optString("article_content_id");
            if(jsonObject.optJSONObject("image") != null && jsonObject.optJSONObject("image").length() > 0){
                image = new Image().parse(jsonObject.optJSONObject("image"));
            }
            title = jsonObject.optString("title");
            content = jsonObject.optString("content");
            return  this;
        }
        return null;
    }
}
