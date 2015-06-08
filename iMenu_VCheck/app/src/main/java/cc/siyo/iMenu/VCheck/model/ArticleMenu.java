package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 18:14.
 * Desc:文章菜单实体类
 */
public class ArticleMenu extends BaseModel<ArticleMenu> {

    private static final String TAG = "ArticleMenu";
    /** 菜单ID*/
    public String article_menu_id;
    /** 菜单标题*/
    public String title;
    /** 菜单内容*/
    public String content;

    @Override
    public ArticleMenu parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            article_menu_id = jsonObject.optString("article_menu_id");
            title = jsonObject.optString("title");
            content = jsonObject.optString("content");
            return this;
        }
        return null;
    }
}
