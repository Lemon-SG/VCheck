package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/8 12:30.
 * Desc:商家实体类
 */
public class Store extends BaseModel<Store> {

    private static final String TAG = "Store";
    /** 商家ID*/
    public String store_id;
    /** 商家名称*/
    public String store_name;
    /** 地址*/
    public String address;
    /** 经度*/
    public String longitude_num;
    /** 纬度*/
    public String latitude_num;
    /** 电话*/
    public String tel_1;
    /** 电话备用*/
    public String tel_2;
    /** 图片资源*/
    public Image icon_image;
    /** 营业时间*/
    public String hours;
    /** 人均消费*/
    public String per;

    @Override
    public Store parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            store_id = jsonObject.optString("store_id");
            store_name = jsonObject.optString("store_name");
            address = jsonObject.optString("address");
            longitude_num = jsonObject.optString("longitude_num");
            latitude_num = jsonObject.optString("latitude_num");
            tel_1 = jsonObject.optString("tel_1");
            tel_2 = jsonObject.optString("tel_2");
            icon_image = new Image().parse(jsonObject.optJSONObject("icon_image"));
            hours = jsonObject.optString("hours");
            per = jsonObject.optString("per");
            return this;
        }
        return null;
    }
}
