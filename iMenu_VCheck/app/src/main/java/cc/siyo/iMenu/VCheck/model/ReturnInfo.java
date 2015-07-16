package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by Lemon on 2015/7/16 11:42.
 * Desc:退款原因实体
 */
public class ReturnInfo extends BaseModel<ReturnInfo> {

    private static final String TAG = "ReturnInfo";
    /** 退款ID*/
    public String return_id;
    /** 退款原因ID*/
    public String return_reason_id;
    public String return_reason_name;
    /** 状态为是否选中 */
    public boolean isTrue = false;
    /** 退款原因详情*/
    public String return_reason_description;
    /** 退款方式ID*/
    public String return_action_id;
    /** 退款方式详情*/
    public String return_action_description;
    /** 退款创建时间*/
    public String date_added;

    @Override
    public ReturnInfo parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0){
            Log.e(TAG, "开始解析");
            return_id = jsonObject.optString("return_id");
            return_reason_id = jsonObject.optString("return_reason_id");
            return_reason_description = jsonObject.optString("return_reason_description");
            return_action_id = jsonObject.optString("return_action_id");
            return_action_description = jsonObject.optString("return_action_description");
            date_added = jsonObject.optString("date_added");
            return_reason_name = jsonObject.optString("return_reason_name");
            return this;
        }
        return null;
    }
}
