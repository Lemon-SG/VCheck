package cc.siyo.iMenu.VCheck.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Html;

import java.util.List;

/**
 * Created by Lemon on 2015/5/6.
 * Desc:详情viewpager adapter
 */
public class DetailFragmentViewPagerAdapter extends FragmentPagerAdapter{

    private static final String TAG = "DetailFragmentViewPagerAdapter";
    private static final String[] TITLE = new String[] {"亮点", "菜单", "须知"};
    /** 碎片集合*/
    private List<Fragment> fragmentList;
    private Context context;

    public DetailFragmentViewPagerAdapter(Context context, FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.context = context;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentList.get(arg0);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Html.fromHtml("<font color = '#878787'>" + TITLE[position % TITLE.length] + "</font>");
    }

    //TITLE.length
    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
