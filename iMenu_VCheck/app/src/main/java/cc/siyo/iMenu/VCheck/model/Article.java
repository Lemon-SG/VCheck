package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lemon on 2015/6/4 16:53.
 * Desc:����ʵ���� article_info|article_list
 */
public class Article extends BaseModel<Article>{

    private static final String TAG = "Article";
    /** ����ID*/
    public String article_id;
    /** ����*/
    public String title;
    /** �ӱ���*/
    public String sub_title;
    /** ժҪ*/
    public String summary;
    /** ��������*/
    public String article_date;
//    /** ����ʱֱ��ȡ��ID���˵�ID*/
//    public String menu_id;
//    /** ����ʱֱ��ȡ��ID���̼�ID*/
//    public String store_id;
//    /** ����ʱֱ��ȡ��ID����ԱID*/
//    public String member_id;
    /** �˵�ʵ��*/
    public Menu menu_info;
    /** �̼�ʵ��*/
    public Store store_info;
    /** ��Աʵ��*/
    public Member member_info;
    /** ͼƬʵ����*/
    public Image article_image;
    /** ��ʾʵ����*/
    public Tips tips_info;
    /** ������ͼƬ����*/
    public List<ArticleImage> article_image_list;
    /** �ղ�ʵ����*/
    public Collection collection_info;
    /** ����ʵ����*/
    public Share share_info;
    /** ��������ʵ����*/
    public List<ArticleContent> article_content_list;
    /** ���²˵��б�*/
    public List<ArticleMenu> article_menu_list;


    @Override
    public Article parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
            article_id = jsonObject.optString("article_id");
            sub_title = jsonObject.optString("sub_title");
            summary = jsonObject.optString("summary");
            article_date = jsonObject.optString("article_date");
//            if(jsonObject.optJSONObject("menu_info") != null && jsonObject.optJSONObject("menu_info").length() > 0){
//                menu_id = jsonObject.optJSONObject("menu_info").optString("menu_id");
//            }
//            if(jsonObject.optJSONObject("store_info") != null && jsonObject.optJSONObject("store_info").length() > 0){
//                store_id = jsonObject.optJSONObject("store_info").optString("store_id");
//            }
//            if(jsonObject.optJSONObject("member_info") != null && jsonObject.optJSONObject("member_info").length() > 0){
//                member_id = jsonObject.optJSONObject("member_info").optString("member_id");
//            }
            menu_info = new Menu().parse(jsonObject.optJSONObject("menu_info"));
            store_info = new Store().parse(jsonObject.optJSONObject("store_info"));
            member_info = new Member().parse(jsonObject.optJSONObject("member_info"));
            if(jsonObject.optJSONObject("article_image") != null && jsonObject.optJSONObject("article_image").length() > 0){
                article_image = new Image().parse(jsonObject.optJSONObject("article_image"));
            }
            if(jsonObject.optJSONObject("tips_info") != null && jsonObject.optJSONObject("tips_info").length() > 0){
                tips_info = new Tips().parse(jsonObject.optJSONObject("tips_info"));
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
            if(jsonObject.optJSONObject("share_info") != null && jsonObject.optJSONObject("share_info").length() > 0){
                share_info = new Share().parse(jsonObject.optJSONObject("share_info"));
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
            return this;
        }
        return null;
    }
}
