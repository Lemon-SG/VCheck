package cc.siyo.iMenu.VCheck.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.DetailActivity;
import cc.siyo.iMenu.VCheck.adapter.MainAdapter;
import cc.siyo.iMenu.VCheck.model.Menu;

/**
 * Created by Lemon on 2015/4/29.
 * Desc:主页界面
 */
public class MainFragment extends BaseFragment{
    private static final String TAG = "StoreFragment";

    /** LIST VIEW*/
    private ListView store_list;
    /** Adapter*/
    private MainAdapter mineAdapter;
    /** Context*/
    private Context mContext;
    /** 数据源*/
    private List<Menu> menuList;

    @Override
    public int getContentView() {
        mContext = getActivity();
        return R.layout.fram_store;
    }

    @Override
    public void initView(View v) {
        store_list = (ListView) v.findViewById(R.id.store_list);
        store_list.addFooterView((LayoutInflater.from(mContext)).inflate(R.layout.list_item_footview, null));
        store_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        makeData();
        mineAdapter = new MainAdapter(getActivity(), R.layout.list_item_store);
        store_list.setAdapter(mineAdapter);
        mineAdapter.getDataList().clear();;
        mineAdapter.getDataList().addAll(menuList);
        mineAdapter.notifyDataSetChanged();
    }

    /** 封装数据*/
    private void makeData(){
        menuList = new ArrayList<>();
        for (int i = 0; i < 10; i ++){
            Menu menu = new Menu();
            menu.setText("13款烤鸟 刺身打造东瀛风情宵夜 千和日料双人菜单" + i);
            menuList.add(menu);
        }
    }
}
