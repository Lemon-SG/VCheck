package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 17:42.
 * Desc:产品详情内文章图片集合实体类
 */
public class ArticleImage extends BaseModel<ArticleImage> {

    private static final String TAG = "ArticleImageList";
    /** 图片ID*/
    public String article_image_id;
    /** 图片实体类*/
    public Image image;
    /** 排序号*/
    public String sort_order;
    /** 是否为封面图(1-封面/0-非封面)*/
    public String is_index;

    @Override
    public ArticleImage parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
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
