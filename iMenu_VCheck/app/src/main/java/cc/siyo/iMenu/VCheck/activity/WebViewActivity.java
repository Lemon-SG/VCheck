package cc.siyo.iMenu.VCheck.activity;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import net.tsz.afinal.annotation.view.ViewInject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Constant;
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
    }

    @Override
    public void initData() {
        final String url = getIntent().getExtras().getString(Constant.INTENT_WEB_URL);
        webView.loadUrl(url);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String myString = null;
                StringBuffer sff = new StringBuffer();
                try{
                    Document doc = Jsoup.connect(url).get();
                    Elements links = doc.select("a[href]");
                    for(Element link : links){
                        Log.e(TAG, "link->" + link);
                        //link == <a href="vcheck://?route=home&amp;push_type=1">2.打开首页</a>
                        //link.text() == 每个链接的展示文本
                        sff.append(link.attr("href")).append("  ").append(link.text()).append(" ");

                    }
                    myString = sff.toString();
                }catch (Exception e){
                    myString = e.getMessage();
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /** 处理点击跳转*/
    private void doAppLinkSwitch(String link) {

    }
}
