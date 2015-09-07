package cc.siyo.iMenu.VCheck.http;

import android.content.Context;
import android.util.Log;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;

/**
 * Created by Lemon on 2015/8/7 15:26.
 * Desc:网络请求库
 */
public class LHttpLib {

    private static final String TAG = "LHttpLib";

    private static void HttpPost(Context context, AjaxParams ajaxParams, final String route, final LHttpResponseHandler responseHandler) {
        ajaxParams.put("route", route);
        ajaxParams.put("token", PreferencesUtils.getString(context, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);

        Log.e(TAG, Constant.REQUEST + route + "\n" + ajaxParams.toString());

        FinalHttp finalHttp = new FinalHttp();
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                if (responseHandler != null) {
                    responseHandler.onStart();
                }
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
                if (responseHandler != null) {
                    responseHandler.onLoading(count, current);
                }
            }

            @Override
            public void onSuccess(final String s) {
                super.onSuccess(s);
                if (responseHandler != null) {
                    if (!StringUtils.isBlank(s)) {
                        Log.e(TAG, Constant.RESULT + route + "\n" + s);
                        JSONStatus jsonStatus = BaseJSONData(s);
                        responseHandler.onSuccess(jsonStatus);
                    } else {
                        responseHandler.onFailure(new Throwable("无返回数据"), 404, "请求无返回数据，请重试");
                    }
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                if (responseHandler != null) {
                    responseHandler.onFailure(t, errorNo, strMsg);
                }
            }
        });
    }

    /***
     * 请求数据封装(单项)Map<String, String>... params
     * @return JSONObject
     */
    private static String makeJsonText(Map<String, String>... params) {
        JSONObject json = new JSONObject();
        if(params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                try {
                    Iterator iterator = params[i].entrySet().iterator();
                    while(iterator.hasNext()) {//只遍历一次,速度快
                        Map.Entry entry = (Map.Entry) iterator.next();
                        Log.e(TAG, "make-Key->>" + entry.getKey());
                        Log.e(TAG, "make-Value->>" + entry.getValue());
                        json.put(entry.getKey() + "", entry.getValue() + "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return json.toString();
    }

    /***
     * 封装返回数据
     * @param t 返回数据字符串
     * @return JSONStatus对象
     */
    private static JSONStatus BaseJSONData(String t){
        JSONStatus jsonStatus = new JSONStatus();
        try {
            JSONObject obj = new JSONObject(t);
            if(obj.length() > 0){
                jsonStatus = new JSONStatus().parse(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStatus;
    }


    /***
     * 获取消息列表
     * @param context 上下文
     * @param member_id 会员ID
     * @param message_type 消息类型(0-全部消息/1-已读消息/2-未读消息)	默认为0
     * @param pagination 页数封装信息
     * @param responseHandler 回调
     */
    public static void getMessageList (Context context, String member_id, String message_type, JSONObject pagination, final LHttpResponseHandler responseHandler) {
        AjaxParams ajaxParams = new AjaxParams();
        //根据参数的数量更改
        Map<String, String>[] maps = new Map[2];
        maps[0] = new HashMap<>();
        maps[1] = new HashMap<>();
        maps[0].put("member_id", member_id);
        maps[1].put("message_type", message_type);
        try {
            JSONObject jsonObject = new JSONObject(makeJsonText(maps));
            jsonObject.put("pagination", pagination);//需要传递页数
            ajaxParams.put("jsonText", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpPost(context, ajaxParams, API.GET_MESSAGE_LIST, responseHandler);
    }

    /***
     * 编辑推送设备
     * @param context 上下文
     * @param member_id 会员ID
     * @param device_token 设备推送号码
     * @param operator_type 1-添加/2-删除/3-清空(默认为1)
     * @param responseHandler 回调
     */
    public static void editPushDevice (Context context, String member_id, String device_token, String operator_type, final LHttpResponseHandler responseHandler) {
        AjaxParams ajaxParams = new AjaxParams();
        //根据参数的数量更改
        Map<String, String>[] maps = new Map[3];
        maps[0] = new HashMap<>();
        maps[1] = new HashMap<>();
        maps[2] = new HashMap<>();
        maps[0].put("member_id", member_id);
        maps[1].put("device_token", device_token);
        maps[2].put("operator_type", operator_type);
        try {
            JSONObject jsonObject = new JSONObject(makeJsonText(maps));
            ajaxParams.put("jsonText", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpPost(context, ajaxParams, API.EDIT_PUSH_DEVICE, responseHandler);
    }

    /***
     * 编辑微信信息请求
     * @param context 上下文
     * @param member_id 会员ID
     * @param operator_type 操作类型(1-绑定微信/2-解除绑定微信)
     * @param wx_info
     * @param responseHandler 回调
     */
    public static void editWithWx (Context context, String member_id, String operator_type, JSONObject wx_info, final LHttpResponseHandler responseHandler) {
        AjaxParams ajaxParams = new AjaxParams();
        //根据参数的数量更改
        Map<String, String>[] maps = new Map[2];
        maps[0] = new HashMap<>();
        maps[1] = new HashMap<>();
        maps[0].put("member_id", member_id);
        maps[1].put("operator_type", operator_type);
        try {
            JSONObject jsonObject = new JSONObject(makeJsonText(maps));
            jsonObject.put("wx_info", wx_info);
            ajaxParams.put("jsonText", jsonObject.toString());
            Log.e(TAG, ajaxParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpPost(context, ajaxParams, API.EDIT_WITH_WX, responseHandler);
    }

    /***
     * 获取城市列表请求
     * @param context 上下文
     * @param responseHandler 回调
     */
    public static void getRegionList (Context context, final LHttpResponseHandler responseHandler) {
        AjaxParams ajaxParams = new AjaxParams();

        HttpPost(context, ajaxParams, API.GET_REGION_LIST, responseHandler);
    }

    /***
     * 获取首页广告请求
     * @param context 上下文
     * @param region_id 城市ID
     * @param responseHandler 回调
     */
    public static void getAppBannerList (Context context, String region_id, final LHttpResponseHandler responseHandler) {
        AjaxParams ajaxParams = new AjaxParams();
        //根据参数的数量更改
        Map<String, String>[] maps = new Map[1];
        maps[0] = new HashMap<>();
        maps[0].put("region_id", region_id);
        try {
            JSONObject jsonObject = new JSONObject(makeJsonText(maps));
            ajaxParams.put("jsonText", jsonObject.toString());
            Log.e(TAG, ajaxParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpPost(context, ajaxParams, API.GET_APP_BANNER_LIST, responseHandler);
    }

}
