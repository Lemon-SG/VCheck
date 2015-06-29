package cc.siyo.iMenu.VCheck.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.DetailLightSpotAdapter;
import cc.siyo.iMenu.VCheck.model.ArticleContent;
import cc.siyo.iMenu.VCheck.util.Utility;

/**
 * Created by Lemon on 2015/5/6.
 * Desc:亮点界面
 */
public class DetailLightSpotFragment extends BaseFragment{

    private static final String TAG = "DetailLightSpotFragment";
    /** 亮点ListView*/
    private ListView list_lightSpot;
    /** 列表适配器*/
    private DetailLightSpotAdapter detailLightSpotAdapter;
    /** 数据源*/
    private List<ArticleContent> articleContentList;

//    private WebView web_lightSpot;
//    private String article_content;

//    public static final DetailLightSpotFragment newInstance(String article_content){
//        DetailLightSpotFragment detailLightSpotFragment = new DetailLightSpotFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("article_content", article_content);
//        detailLightSpotFragment.setArguments(bundle);
//        return detailLightSpotFragment;
//    }

    public static final DetailLightSpotFragment newInstance(List<ArticleContent> articleContentList){
        DetailLightSpotFragment detailLightSpotFragment = new DetailLightSpotFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("articleContentList", (Serializable) articleContentList);
        detailLightSpotFragment.setArguments(bundle);
        return detailLightSpotFragment;
    }

    @Override
    public int getContentView() {
        return R.layout.fram_lightspot;
    }

    @Override
    public void initView(View v) {
        list_lightSpot = (ListView) v.findViewById(R.id.list_lightSpot);
//        web_lightSpot = (WebView) v.findViewById(R.id.web_lightSpot);
    }

    @Override
    public void initData() {
        articleContentList = new ArrayList<>();
        detailLightSpotAdapter = new DetailLightSpotAdapter(getActivity(), R.layout.list_item_detail_lightspot);
        list_lightSpot.setAdapter(detailLightSpotAdapter);
        if(getArguments() != null){
            articleContentList = (List<ArticleContent>) getArguments().getSerializable("articleContentList");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    detailLightSpotAdapter.getDataList().clear();
                    detailLightSpotAdapter.getDataList().addAll(articleContentList);
                    Utility.setListViewHeightBasedOnChildren(list_lightSpot);
                    detailLightSpotAdapter.notifyDataSetChanged();
                }
            });
        }
    }

//    /** 预留WebView展示*/
//    private void loadHtml() {
//        WebSettings webSettings = web_lightSpot.getSettings();
//        webSettings.setSavePassword(false);
//        webSettings.setSaveFormData(false);
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(false);
//        /**
//         * NORMAL：正常显示，没有渲染变化。
//         * SINGLE_COLUMN：把所有内容放到WebView组件等宽的一列中。
//         * NARROW_COLUMNS：可能的话，使所有列的宽度不超过屏幕宽度。
//         */
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webSettings.setLoadWithOverviewMode(true);
////        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染的优先级
////        webSettings.setBlockNetworkImage(true);//把图片放置最后渲染
//        webSettings.setBuiltInZoomControls(false);//设置WebView可触摸放大缩小
//        webSettings.setUseWideViewPort(true);//WebView双击变大，再双击后变小，当手动放大后，双击可以恢复到原始大小
////        webSettings.setTextSize(FONT_SIZES[TextSize.NORMAL]);
//
//        web_lightSpot.setInitialScale(160);//适应全屏  39适应竖屏    57适应横屏
//        web_lightSpot.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条风格，为0指滚动条不占用空间，直接覆盖在网页
//        if(getArguments() != null){
//            article_content = getArguments().getString("article_content");
////            web_lightSpot.setText(Html.fromHtml(article_content));
//            Log.e(TAG, article_content);
//            web_lightSpot.loadDataWithBaseURL(null, article_content, "text/html", "UTF-8", null);
//
////            Html.fromHtml(article_content, null, new Html.TagHandler() {
////                @Override
////                public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
////                    Log.e(TAG, "opening->" + opening);
////                    Log.e(TAG, "tag->" + tag);
////                    Log.e(TAG, "output->" + output);
////                    Log.e(TAG, "xmlReader->" + xmlReader.toString());
////                }
////            });
//        }
//    }
}
