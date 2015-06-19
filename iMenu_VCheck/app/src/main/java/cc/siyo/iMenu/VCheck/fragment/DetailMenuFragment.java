package cc.siyo.iMenu.VCheck.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.DetailMenuAdapter;
import cc.siyo.iMenu.VCheck.model.ArticleContent;
import cc.siyo.iMenu.VCheck.model.ArticleMenu;
/**
 * Created by Lemon on 2015/5/6.
 * Desc:菜单界面
 */
public class DetailMenuFragment extends BaseFragment {

    private static final String TAG = "DetailMenuFragment";
    private Context context;
    /** 菜单列表*/
    private ListView list_menu;
    /** 数据源*/
    private List<ArticleMenu> articleMenuList;
    /** 列表适配器*/
    private DetailMenuAdapter detailMenuAdapter;

    public static final DetailMenuFragment newInstance(List<ArticleMenu> articleMenuList){
        DetailMenuFragment detailMenuFragment = new DetailMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("articleMenuList", (Serializable) articleMenuList);
        detailMenuFragment.setArguments(bundle);
        return detailMenuFragment;
    }

    @Override
    public int getContentView() {
        context = getActivity();
        return R.layout.fram_menu;
    }

    @Override
    public void initView(View v) {
        list_menu = (ListView) v.findViewById(R.id.list_menu);
    }

    @Override
    public void initData() {
        articleMenuList = new ArrayList<>();
        detailMenuAdapter = new DetailMenuAdapter(getActivity(), R.layout.list_item_detail_menu);
        list_menu.setAdapter(detailMenuAdapter);

        if(getArguments() != null){
            articleMenuList = (List<ArticleMenu>) getArguments().getSerializable("articleMenuList");
            if(articleMenuList != null && articleMenuList.size() > 0){
                detailMenuAdapter.getDataList().clear();
                detailMenuAdapter.getDataList().addAll(articleMenuList);
                detailMenuAdapter.notifyDataSetChanged();
            }
        }
    }
}
