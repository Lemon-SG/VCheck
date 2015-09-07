package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/8/10 17:05.
 * Desc:
 */
public class ThirdPartInfo extends BaseModel<ThirdPartInfo> {

    /** 是否绑定微信1->绑定;0->未绑定*/
    public String weixin_bind;
    /** 是否绑定微博1->绑定;0->未绑定*/
    public String weibo_bind;
    public Member member;

    @Override
    public ThirdPartInfo parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() >0) {
            weixin_bind = jsonObject.optString("weixin_bind");
            weibo_bind = jsonObject.optString("weibo_bind");
            return this;
        }
        return null;
    }
}
