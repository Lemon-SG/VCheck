package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/7/24 11:42.
 * Desc:邀请分享
 */
public class ShareInvite extends BaseModel<ShareInvite> {

    /** 邀请码*/
    public String invite_code;
    /** 已经邀请的人数*/
    public String invite_total_count;
    /** 邀请人的提示*/
    public String invite_people_tips;
    /** 被邀请的提示*/
    public String invite_code_tips;
    /** 分享链接*/
    public String share_url;

    @Override
    public ShareInvite parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            invite_code = jsonObject.optString("invite_code");
            invite_total_count = jsonObject.optString("invite_total_count");
            invite_people_tips = jsonObject.optString("invite_people_tips");
            invite_code_tips = jsonObject.optString("invite_code_tips");
            share_url = jsonObject.optString("share_url");
            return this;
        }
        return null;
    }
}
