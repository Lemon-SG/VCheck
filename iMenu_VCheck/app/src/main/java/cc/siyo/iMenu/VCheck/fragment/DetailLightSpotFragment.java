package cc.siyo.iMenu.VCheck.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.DetailLightSpotAdapter;
import cc.siyo.iMenu.VCheck.model.ArticleContent;

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
    }

    @Override
    public void initData() {
        articleContentList = new ArrayList<>();
        detailLightSpotAdapter = new DetailLightSpotAdapter(getActivity(), R.layout.list_item_detail_lightspot);
        list_lightSpot.setAdapter(detailLightSpotAdapter);
        if(getArguments() != null){
            articleContentList = (List<ArticleContent>) getArguments().getSerializable("articleContentList");
            if(articleContentList != null && articleContentList.size() > 0){
                detailLightSpotAdapter.getDataList().clear();
                detailLightSpotAdapter.getDataList().addAll(articleContentList);
                detailLightSpotAdapter.notifyDataSetChanged();
            }
        }
    }
}
