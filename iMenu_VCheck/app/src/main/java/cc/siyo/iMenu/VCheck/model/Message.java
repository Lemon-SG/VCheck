package cc.siyo.iMenu.VCheck.model;

import org.json.JSONObject;

/**
 * Created by Lemon on 2015/8/10 10:59.
 * Desc:
 */
public class Message extends BaseModel<Message> {

    /** 消息ID*/
    public String message_id;
    /** 标题*/
    public String title;
    /** 摘要*/
    public String summary;
    /** 消息时间*/
    public String message_date;
    /** 消息类型:1->活动消息;2->消费确认;3->退款提醒;4->获得礼券;5->开售提醒*/
    public String message_type_id;
    /** 链接跳转*/
    public LinkPushParams link_info;
    /** 已读未读状态*/
    public String is_open;

    @Override
    public Message parse(JSONObject jsonObject) {
        if(jsonObject != null && jsonObject.length() > 0) {
            message_id = jsonObject.optString("message_id");
            title = jsonObject.optString("title");
            summary = jsonObject.optString("summary");
            message_date = jsonObject.optString("message_date");
            message_type_id = jsonObject.optString("message_type_id");
            is_open = jsonObject.optString("is_open");
            link_info = new LinkPushParams().parse(jsonObject.optJSONObject("link_info"));
            return this;
        }
        return null;
    }
}
