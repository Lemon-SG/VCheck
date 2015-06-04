package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

import android.graphics.Bitmap;

/***
 * 分享实体类
 * @author Created by Lemon on 15-6-4.
 */
public class Share extends BaseModel<Share> {

	/**"title": "微点分享"*/
    public String title;
    /**"description": "微点分享,发现身边美食"*/
    public String description;
    /**"content": "\"微点\" 分享 {0} 这个商家。详情请访问：http://www.imenu.so/s/{1}/ "*/
    public String content;
    /**"link": "http://s.imenu.so/du8eF"*/
    public String link;
    public String imagePath;
    public String imageUrl;
    /**本地资源图片分享*/
    public Bitmap imageBitmap;

    @Override
    public Share parse(JSONObject jsonObject) {
        if (jsonObject != null) {
            title = jsonObject.optString("title");
            description = jsonObject.optString("description");
            content = jsonObject.optString("content");
            link = jsonObject.optString("link");
        }
        return this;
    }
}
