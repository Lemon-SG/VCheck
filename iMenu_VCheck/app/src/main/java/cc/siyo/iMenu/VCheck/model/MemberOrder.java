package cc.siyo.iMenu.VCheck.model;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by Lemon on 2015/7/6 16:00.
 * Desc:会员订单实体类
 */
public class MemberOrder extends BaseModel<MemberOrder> {

    private static final String TAG = "MemberOrder";
    /** 文章实体*/
    public Article article_info;
    /** 订单实体*/
    public OrderInfo order_info;

    @Override
    public MemberOrder parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0) {
            Log.e(TAG, "开始解析");
            article_info = new Article().parse(jsonObject.optJSONObject("article_info"));
            order_info = new OrderInfo().parse(jsonObject.optJSONObject("order_info"));
            return this;
        }
        return null;
    }
}
