package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/8 12:30.
 * Desc:�̼�ʵ����
 */
public class Store extends BaseModel<Store> {

    private static final String TAG = "Store";
    /** �̼�ID*/
    public String store_id;
    /** �̼�����*/
    public String store_name;
    /** ��ַ*/
    public String address;
    /** ����*/
    public String longitude_num;
    /** γ��*/
    public String latitude_num;
    /** �绰*/
    public String tel_1;
    /** �绰����*/
    public String tel_2;
    /** ͼƬ��Դ*/
    public Image icon_image;
    /** Ӫҵʱ��*/
    public String hours;
    /** �˾�����*/
    public String per;

    @Override
    public Store parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
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
