package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/8/21 15:38.
 * Desc:消费吗实体类
 */
public class ConsumeInfo extends BaseModel<ConsumeInfo> {

    /** 过期时间*/
    public String exprie_date;
    /** 消费吗*/
    public String consume_code;
    /** 消费码使用时间*/
    public String consume_date;

    @Override
    public ConsumeInfo parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0) {
            exprie_date = jsonObject.optString("exprie_date");
            consume_code = jsonObject.optString("consume_code");
            consume_date = jsonObject.optString("consume_date");
            return this;
        }
        return null;
    }
}
