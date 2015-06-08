package cc.siyo.iMenu.VCheck.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.io.Serializable;
import java.util.List;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.LightSpotListAdapter;
import cc.siyo.iMenu.VCheck.model.ArticleContent;

/**
 * Created by Lemon on 2015/5/6.
 * Desc:亮点界面
 */
public class LightSpotFragment extends BaseFragment{

    private static final String TAG = "LightSpotFragment";
    /** 亮点ListView*/
    private ListView list_lightSpot;
    /** 列表适配器*/
    private LightSpotListAdapter lightSpotListAdapter;
    /** 数据源*/
    private List<ArticleContent> articleContentList;

    public static final LightSpotFragment newInstance(List<ArticleContent> articleContentList){
        LightSpotFragment lightSpotFragment = new LightSpotFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("articleContentList", (Serializable) articleContentList);
        lightSpotFragment.setArguments(bundle);
        return lightSpotFragment;
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
        lightSpotListAdapter = new LightSpotListAdapter(getActivity(), R.layout.list_item_lightspot);
        list_lightSpot.setAdapter(lightSpotListAdapter);
        articleContentList = (List<ArticleContent>) getArguments().getSerializable("articleContentList");

        lightSpotListAdapter.getDataList().clear();
        lightSpotListAdapter.getDataList().addAll(articleContentList);
        lightSpotListAdapter.notifyDataSetChanged();
    }
}
