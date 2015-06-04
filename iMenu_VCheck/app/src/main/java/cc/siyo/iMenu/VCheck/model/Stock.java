package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/6/4 16:34.
 * Desc:��Ʒ����ʵ����
 */
public class Stock extends BaseModel<Stock> {

    private static final String TAG = "Stock";
    /** ��Ʒ����*/
    public String menu_count;
    /** ������λ*/
    public String menu_unit;
    /** ��û�в�Ʒʱ��ʾ������*/
    public String out_of_stock_info;
            
    @Override
    public Stock parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
            menu_count = jsonObject.optString("menu_count");
            menu_unit = jsonObject.optString("menu_unit");
            out_of_stock_info = jsonObject.optString("out_of_stock_info");
            return this;
        }
        return null;
    }
}
