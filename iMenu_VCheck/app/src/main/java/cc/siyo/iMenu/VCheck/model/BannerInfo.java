package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/8/5 18:50.
 * Desc:广告信息
 */
public class BannerInfo extends BaseModel<BannerInfo> {

    public Image image;
    public LinkPushParams linkPushParams;

    @Override
    public BannerInfo parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0) {
            image = new Image().parse(jsonObject.optJSONObject("image"));
            linkPushParams = new LinkPushParams().parse(jsonObject.optJSONObject("link_info"));
            return  this;
        }
        return null;
    }
}
