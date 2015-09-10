package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.util.StringUtils;

/**
 * Created by Lemon on 2015/7/23 11:31.
 * Desc:推送消息参数
 */
public class LinkPushParams extends BaseModel<LinkPushParams> {

    /** {"link_route":"article", "link_value":"article_id=2"}*/

    /** 通往路径*/
    public String link_route;
    /** 文章参数*/
    public String link_value;
    /** 如value带有参数的，解析出ID*/
    public String id;
    /** 跳转方式1->跳转打开；0,->正常打开*/
    public String push_type;

    @Override
    public LinkPushParams parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0) {
            link_route = jsonObject.optString("link_route");
            link_value = jsonObject.optString("link_value");
            if(!StringUtils.isBlank(link_value)) {
                if(link_value.contains("=")) {
                    id = link_value.split("=")[1];
                    Log.e("解析LinkPushParams", "参数" + link_value.split("=")[0] + "->" + link_value.split("=")[1]);
                } else {
                    Log.e("解析LinkPushParams", link_value);
                }
            }
            push_type = "1";
            String pushType = jsonObject.optString("push_type");
            if(!StringUtils.isBlank(pushType)) {
                if(pushType.contains("=")) {
                    push_type = pushType.split("=")[1];
                    Log.e("解析LinkPushParams", "类型" + pushType.split("=")[0] + "->" + pushType.split("=")[1]);
                } else {
                    Log.e("解析LinkPushParams", pushType);
                }
            }
            return this;
        }
        return null;
    }
}
