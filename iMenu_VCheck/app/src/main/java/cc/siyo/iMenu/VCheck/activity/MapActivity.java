package cc.siyo.iMenu.VCheck.activity;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import net.tsz.afinal.annotation.view.ViewInject;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/31 17:58.
 * Desc:地图显示
 */
public class MapActivity extends BaseActivity {

    @ViewInject(id = R.id.topBar)private TopBar topBar;
//    @ViewInject(id = R.id.bMapView)private MapView bMapView;
    @ViewInject(id = R.id.bMapWeb)private WebView bMapWeb;

    @Override
    public int getContentView() {
        return R.layout.activity_map;
    }

    @Override
    public void initView() {
        final double lng = Double.parseDouble(getIntent().getExtras().getString("lng"));
        final double lat = Double.parseDouble(getIntent().getExtras().getString("lat"));
        final String storeName = getIntent().getExtras().getString("storeName");
        topBar.settitleViewText("商家位置");
        topBar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        WebSettings webSettings = bMapWeb.getSettings();

        webSettings.setAllowFileAccess(true);
//        webSettings.setBlockNetworkImage(true);此属性设置会导致地图上一些图片资源不加载，比如图钉标注等
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadsImagesAutomatically(true);

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);

        bMapWeb.loadUrl("file:///android_asset/map.html");

        bMapWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //调用的js函数
                String title = storeName;
                bMapWeb.loadUrl("javascript:showMap(" + lng + "," + lat + ",'" + title + "')");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);

            }
        });
    }

    @Override
    public void initData() {
//        Intent intent = getIntent();
//        if (intent.hasExtra("x") && intent.hasExtra("y")) {
//            // 当用intent参数时，设置中心点为指定点
//            Bundle b = intent.getExtras();
//            LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
//            bMapView = new MapView(this,
//                    new BaiduMapOptions().mapStatus(new MapStatus.Builder()
//                            .target(p).build()));
//        } else {
//            bMapView = new MapView(this, new BaiduMapOptions());
//        }
//        mBaiduMap = bMapView.getMap();
//        //普通地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//        //开启交通图
//        mBaiduMap.setTrafficEnabled(true);
//        //开启交通图
//        mBaiduMap.setBaiduHeatMapEnabled(true);
//        //定义Maker坐标点
//        LatLng point = new LatLng(39.963175, 116.400244);
//        //构建Marker图标
//        BitmapDescriptor bitmap = BitmapDescriptorFactory
//                .fromResource(R.drawable.ic_collect_red);
//        //构建MarkerOption，用于在地图上添加Marker
//        OverlayOptions option = new MarkerOptions()
//                .position(point)
//                .icon(bitmap);
//        //在地图上添加Marker，并显示
//        mBaiduMap.addOverlay(option);
    }
}
