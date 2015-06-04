package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 17:58.
 * Desc:文章亮点实体类
 */
public class ArticleContent extends BaseModel<ArticleContent> {

    private static final String TAG = "ArticleContent";
    /** 亮点ID*/
    public String article_content_id;
    /** 图片实体类*/
    public Image image;
    /** 亮点标题*/
    public String title;
    /** 亮点内容*/
    public String content;


    @Override
    public ArticleContent parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
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
