package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/7/23 11:31.
 * Desc:
 */
public class WebLinkParams extends BaseModel<WebLinkParams> {

    /** vcheck://?route=article&article_id=2 | vcheck://?route=home*/

    /** 通往路径*/
    public String route;
    /** 文章参数*/
    public String article_id;

    @Override
    public WebLinkParams parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0) {
            route = jsonObject.optString("route");
            article_id = jsonObject.optString("article_id");
            return  this;
        }
        return null;
    }
}
