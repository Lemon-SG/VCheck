package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.tsz.afinal.annotation.view.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.siyo.iMenu.VCheck.MainActivity;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.setting.MessageActivity;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.LinkPushParams;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/8/26 16:03.
 * Desc:
 */
public class WebViewActivity extends BaseActivity {

    @ViewInject(id = R.id.webView)private WebView webView;
    @ViewInject(id = R.id.topBar)private TopBar topBar;

    @Override
    public int getContentView() {
        return R.layout.activity_webview;
    }

    @Override
    public void initView() {

        String name = getIntent().getExtras().getString(Constant.INTENT_WEB_NAME);
        if(name.equals(Constant.INTENT_WEB_NAME_NOTICE)) {
            topBar.setText(TopBar.TITLE_VIEW, "用户协议");
        }
        if(name.equals(Constant.INTENT_WEB_NAME_WEB)) {
            topBar.setText(TopBar.TITLE_VIEW, "网页跳转");
        }
        topBar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //返回
                finish();
            }
        });

        webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.contains("vcheck://")) {
                        Log.e(TAG, "contains");
                        try {
                            view.loadUrl("file:///android_asset/skip.html");
                            GetWebMsg(url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        view.loadUrl(url);
                        Log.e(TAG, "no contains");
                    }
                    return true;
                }
            });
    }

    @Override
    public void initData() {
        final String url = getIntent().getExtras().getString(Constant.INTENT_WEB_URL);
        Log.e(TAG, "url->" + url);

        webView.loadUrl(url);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String myString = null;
//                StringBuffer sff = new StringBuffer();
//                try{
//                    Document doc = Jsoup.connect(url).get();
//                    Elements links = doc.select("a[href]");
//                    for(Element link : links){
//                        Log.e(TAG, "link->" + link);
//                        //link == <a href="vcheck://?route=home&amp;push_type=1">2.打开首页</a>
//                        //link.text() == 每个链接的展示文本
//                        sff.append(link.attr("href")).append("  ").append(link.text()).append(" ");
//
//                    }
//                    myString = sff.toString();
//                }catch (Exception e){
//                    myString = e.getMessage();
//                    e.printStackTrace();
//                }
//            }
//        }).start();
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
        switchPage(new LinkPushParams().parse(jsonObject));
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
