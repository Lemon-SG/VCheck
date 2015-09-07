package cc.siyo.iMenu.VCheck.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Lemon on 2015/8/6 16:21.
 * Desc:第三方获取到的用户信息实体类
 */
public class ProFile extends BaseModel<ProFile> {

    /** OPENID*/
    public String openid;
    /** 昵称*/
    public String nickname;
    /** 性别*/
    public String sex;
    /** 省份*/
    public String province;
    /** 城市*/
    public String city;
    /** 国家*/
    public String country;
    /** 头像URL*/
    public String headimgurl;
    /** UNIONID*/
    public String unionid;

    /**
     * 微信用户信息json(实体转换json)
     * @return
     */
    public static JSONObject GetWxJson(ProFile proFile) {
        JSONObject json = new JSONObject();
        try {
            json.put("sex", proFile.sex);
            json.put("nickname", proFile.nickname);
            json.put("unionid", proFile.unionid);
            json.put("province", proFile.province);
            json.put("openid", proFile.openid);
            json.put("city", proFile.city);
            json.put("country", proFile.country);
            json.put("headimgurl", proFile.headimgurl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 微信用户信息Json(map转换json)
     * @param hashMap
     * @return
     */
    public static JSONObject GetWxJson(HashMap<String, Object> hashMap) {
        JSONObject json = new JSONObject();
//        JSONObject wx_info = new JSONObject();
        try {
            json.put("sex", hashMap.get("sex").toString());
            json.put("nickname", hashMap.get("nickname").toString());
            json.put("unionid", hashMap.get("unionid").toString());
            json.put("province", hashMap.get("province").toString());
            json.put("openid", hashMap.get("openid").toString());
            json.put("city", hashMap.get("city").toString());
            json.put("country", hashMap.get("country").toString());
            json.put("headimgurl", hashMap.get("headimgurl").toString());
//            wx_info.put("wx_info", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public ProFile parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0) {
            sex = jsonObject.optString("sex");
            nickname = jsonObject.optString("nickname");
            unionid = jsonObject.optString("unionid");
            province = jsonObject.optString("province");
            openid = jsonObject.optString("openid");
            city = jsonObject.optString("city");
            country = jsonObject.optString("country");
            headimgurl = jsonObject.optString("headimgurl");
            return this;
        }
        return null;
    }
}
