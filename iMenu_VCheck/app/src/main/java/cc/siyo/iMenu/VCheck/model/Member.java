package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/5/22.
 * Desc:用户实体类tips_info
 */
public class Member extends BaseModel<Member> {

    private static final String TAG = "Member";
    /**会员ID */
    public String member_id;
    /** 会员名称*/
    public String member_name;
    /** 性别(1-男性/2-女性)*/
    public String sex;
    /** Email*/
    public String email;
    /** 会员手机号*/
    public String mobile;
    /** 图片资源*/
    public Image icon_image;

    @Override
    public Member parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            member_id = jsonObject.optString("member_id");
            member_name = jsonObject.optString("member_name");
            icon_image = new Image().parse(jsonObject.optJSONObject("icon_image"));
            sex = jsonObject.optString("sex");
            email = jsonObject.optString("email");
            mobile = jsonObject.optString("mobile");
        }
        return this;
    }
}
