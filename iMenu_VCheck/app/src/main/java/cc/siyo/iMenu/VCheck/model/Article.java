package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lemon on 2015/6/4 16:53.
 * Desc:文章实体类 article_info|article_list
 */
public class Article extends BaseModel<Article>{

    private static final String TAG = "Article";
    /** 文章ID*/
    public String article_id;
    /** 标题*/
    public String title;
    /** 子标题*/
    public String sub_title;
    /** 摘要*/
    public String summary;
    /** 菜单实体*/
    public Menu menu_info;
    /** 商家实体*/
    public Store store_info;
    /** 会员实体*/
    public Member member_info;
    /** 图片实体类*/
    public Image article_image;
    /** 提示实体类*/
    public Tips tips_info;
    /** 文章内图片集合*/
    public List<ArticleImage> article_image_list;
    /** 收藏实体类*/
    public Collection collection_info;
    /** 分享实体类*/
    public ShareInvite share_info;
    /** 文章亮点实体类*/
    public List<ArticleContent> article_content_list;
    /** 文章菜单列表*/
    public List<ArticleMenu> article_menu_list;
    /** 文章日期*/
    public String article_date;
    /** 文章亮点*/
    public String article_content;
    /** 文章菜单*/
    public String article_menu;
    /** 文章提示*/
    public String article_tips;
    /** 订单实体*/
    public OrderInfo orderInfo;
    /** 消费提示列表*/
    public String consume_tips_list;

    @Override
    public Article parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            article_id = jsonObject.optString("article_id");
            title = jsonObject.optString("title");
            sub_title = jsonObject.optString("sub_title");
            summary = jsonObject.optString("summary");
            menu_info = new Menu().parse(jsonObject.optJSONObject("menu_info"));
            store_info = new Store().parse(jsonObject.optJSONObject("store_info"));
            member_info = new Member().parse(jsonObject.optJSONObject("member_info"));
            if(jsonObject.optJSONObject("article_image") != null && jsonObject.optJSONObject("article_image").length() > 0){
                article_image = new Image().parse(jsonObject.optJSONObject("article_image"));
            }
            if(jsonObject.optJSONObject("article_tips_info") != null && jsonObject.optJSONObject("article_tips_info").length() > 0){
                tips_info = new Tips().parse(jsonObject.optJSONObject("article_tips_info"));
            }
            if(jsonObject.optJSONArray("article_image_list") != null && jsonObject.optJSONArray("article_image_list").length() > 0){
                article_image_list = new ArrayList<>();
                for (int i = 0; i < jsonObject.optJSONArray("article_image_list").length(); i++) {
                    ArticleImage articleImage = new ArticleImage().parse(jsonObject.optJSONArray("article_image_list").optJSONObject(i));
                    article_image_list.add(articleImage);
                }
            }
            if(jsonObject.optJSONObject("collection_info") != null && jsonObject.optJSONObject("collection_info").length() > 0){
                collection_info = new Collection().parse(jsonObject.optJSONObject("collection_info"));
            }
            if(jsonObject.optJSONObject("order_info") != null && jsonObject.optJSONObject("order_info").length() > 0){
                orderInfo = new OrderInfo().parse(jsonObject.optJSONObject("order_info"));
            }
            if(jsonObject.optJSONObject("share_info") != null && jsonObject.optJSONObject("share_info").length() > 0){
                share_info = new ShareInvite().parse(jsonObject.optJSONObject("share_info"));
            }
            if(jsonObject.optJSONArray("article_content_list") != null && jsonObject.optJSONArray("article_content_list").length() > 0){
                article_content_list = new ArrayList<>();
                for (int i = 0; i < jsonObject.optJSONArray("article_content_list").length(); i++) {
                    ArticleContent articleContent = new ArticleContent().parse(jsonObject.optJSONArray("article_content_list").optJSONObject(i));
                    article_content_list.add(articleContent);
                }
            }
            if(jsonObject.optJSONArray("article_menu_list") != null && jsonObject.optJSONArray("article_menu_list").length() > 0){
                article_menu_list = new ArrayList<>();
                for (int i = 0; i < jsonObject.optJSONArray("article_menu_list").length(); i++) {
                    ArticleMenu articleMenu = new ArticleMenu().parse(jsonObject.optJSONArray("article_menu_list").optJSONObject(i));
                    article_menu_list.add(articleMenu);
                }
            }
            article_date = jsonObject.optString("article_date");
            article_content = jsonObject.optString("article_content");
            article_menu = jsonObject.optString("article_menu");
            article_tips = jsonObject.optString("article_tips");
            if(jsonObject.optJSONArray("consume_tips_list") != null && jsonObject.optJSONArray("consume_tips_list").length() > 0) {
                StringBuffer stringBuffer = new StringBuffer("");
                for (int i = 0; i < jsonObject.optJSONArray("consume_tips_list").length(); i++) {
                    stringBuffer.append(jsonObject.optJSONArray("consume_tips_list").opt(i));
                    stringBuffer.append("  ");
                }
                consume_tips_list = stringBuffer.toString();
            }
            return this;
        }
        return null;
    }
}
