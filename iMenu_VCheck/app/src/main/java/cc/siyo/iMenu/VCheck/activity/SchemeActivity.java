package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.MainActivity;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.setting.MessageActivity;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.LinkPushParams;

/**
 * Created by Lemon on 2015/9/7 11:19.
 * Desc:
 */
public class SchemeActivity extends BaseActivity {

    private LinkPushParams linkPushParams;

    @Override
    public int getContentView() {
        return R.layout.activity_scheme;
    }

    @Override
    public void initView() {
        if(getIntent() != null && getIntent().getDataString() != null) {
            Intent intent = getIntent();
            String data = intent.getDataString();//  vcheck://product?id=7
            Log.e(TAG, data);
            try {
                GetWebMsg(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initData() {

    }

    /** 执行网页进入跳转相应页面
     *  首页	route=home	vcheck://?route=home
     *  产品详情	route=article&article_id=[文章ID]	vcheck://?route=article&article_id=2
     * */
    private void GetWebMsg(String data) throws JSONException {
        String paramsStr = data.substring(9, data.length());
        String[] params = paramsStr.split("&");
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < params.length; i++) {
            if(params[i].contains("route")) {
                jsonObject.put("link_route", params[i].substring(params[i].indexOf("=") + 1, params[i].length()));//路径保存
            }
            if(params[i].contains("id")) {
                jsonObject.put("link_value", params[i]);//跳转参数
            }
            if(params[i].contains("url")) {
                jsonObject.put("link_value", params[i].substring(params[i].indexOf("=") + 1, params[i].length()));//跳转参数链接
            }
            if(params[i].contains("push_type")) {
                jsonObject.put("push_type", params[i]);//跳转方式
            }
        }
        Log.e(TAG, "封装跳转参数为json->" + jsonObject.toString());
        linkPushParams = new LinkPushParams().parse(jsonObject);

        switchPage(linkPushParams);
    }

    /** 根据参数进行跳转*/
    private void switchPage(LinkPushParams linkPushParams) {
        if(linkPushParams.push_type.equals("1")) {
            //跳转打开
            if(linkPushParams.link_route.equals(Constant.LINK_WEB)) {
                //打开网页链接
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Constant.INTENT_WEB_URL, linkPushParams.link_value);
                intent.putExtra(Constant.INTENT_WEB_NAME, Constant.INTENT_WEB_NAME_WEB);
                startActivity(intent);
            }
            if(linkPushParams.link_route.equals(Constant.LINK_HOME)) {
                //打开首页，不做操作
                startActivity(new Intent(context, MainActivity.class));
            }
            if(linkPushParams.link_route.equals(Constant.LINK_ARTICLE)) {
                //打开文章详情,传递ID
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Constant.INTENT_ARTICLE_ID, linkPushParams.id);
                startActivity(intent);
            }
            if(linkPushParams.link_route.equals(Constant.LINK_MEMBER)) {//打开用户中心
                startActivity(new Intent(context, MineActivity.class));
            }
            if(linkPushParams.link_route.equals(Constant.LINK_MESSAGE)) {//打开消息列表
                startActivity(new Intent(context, MessageActivity.class));
            }
            if(linkPushParams.link_route.equals(Constant.LINK_COLLECTION)) {//打开收藏列表
                startActivity(new Intent(context, CollectListActivity.class));
            }
            if(linkPushParams.link_route.equals(Constant.LINK_ORDER_LIST)) {//打开订单列表
                startActivity(new Intent(context, OrderListActivity.class));
            }
            if(linkPushParams.link_route.equals(Constant.LINK_ORDER_DETAIL)) {//打开订单详情,传递ID
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderId", linkPushParams.id);
                startActivity(intent);
            }
            if(linkPushParams.link_route.equals(Constant.LINK_VOUCHER)) {//打开礼券列表
                startActivity(new Intent(context, VoucherListActivity.class));
            }
        } else {
            //正常打开，跳转至Launch
            Intent intent = new Intent(this, Launch.class);
            intent.putExtra("linkPushParams", linkPushParams);
            startActivity(intent);
        }
        finish();
    }
}
