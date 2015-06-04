package cc.siyo.iMenu.VCheck.model;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/4/30.
 * Desc:��Ʒʵ����menu_info
 */
public class Menu extends BaseModel<Menu>{

    private static final String TAG = "Menu";
    /** ��ƷID*/
    public String menu_id;
    /** ��Ʒ����*/
    public String menu_name;
    /** �۸�ʵ��*/
    public Price price;
    /** ��Ʒ����ʵ��*/
    public Stock stock;
    /** ��Ʒ��λ*/
    public MenuUnit menu_unit;
    /** ��Ʒ״̬*/
    public MenuStatus menu_status;
    /** ����ʱ��*/
    public String end_date;

    @Override
    public Menu parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "��ʼ����");
            menu_id = jsonObject.optString("menu_id");
            menu_name = jsonObject.optString("menu_name");
            if(jsonObject.optJSONObject("price") != null && jsonObject.optJSONObject("price").length() > 0){
                price = new Price().parse(jsonObject.optJSONObject("price"));
            }
            if(jsonObject.optJSONObject("stock") != null && jsonObject.optJSONObject("stock").length() > 0){
                stock = new Stock().parse(jsonObject.optJSONObject("stock"));
            }
            if(jsonObject.optJSONObject("menu_unit") != null && jsonObject.optJSONObject("menu_unit").length() > 0){
                menu_unit = new MenuUnit().parse(jsonObject.optJSONObject("menu_unit"));
            }
            if(jsonObject.optJSONObject("menu_status") != null && jsonObject.optJSONObject("menu_status").length() > 0){
                menu_status = new MenuStatus().parse(jsonObject.optJSONObject("menu_status"));
            }
            end_date = jsonObject.optString("end_date");
            return this;
        }
        return null;
    }
}
