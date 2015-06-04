package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 16:28.
 * Desc:�۸�ʵ����
 */
public class Price extends BaseModel<Price> {

    private static final String TAG = "Price";
    /** ԭ��*/
    public String original_price;
    /** �Żݼ۸�*/
    public String special_price;
    /** �۸�λ*/
    public String price_unit;

    @Override
    public Price parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
            original_price = jsonObject.optString("original_price");
            //TODO special_price�ĵ���ƴдΪspeical_price�����ӿ�������ɺ�Ա�
            special_price = jsonObject.optString("special_price");
            price_unit = jsonObject.optString("price_unit");
            return this;
        }
        return null;
    }
}
