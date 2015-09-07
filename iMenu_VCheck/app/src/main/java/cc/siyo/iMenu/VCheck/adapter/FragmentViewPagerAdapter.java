package cc.siyo.iMenu.VCheck.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Html;

/**
 * 碎片viewPager适配器
 * 
 * @author ShangGuan
 */
public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

    //{"我的", "商家", "动态", "搜索"}
	private static final String[] TITLE = new String[] {"商家"};
	/** 碎片集合 */
	private List<Fragment> mFragmentsList;

	public FragmentViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> mFragmentsList) {
		super(fragmentManager);
		this.mFragmentsList = mFragmentsList;
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragmentsList.get(arg0);
	}

	@Override
	public CharSequence getPageTitle(int position) {
//		java.awt.Font()
//		new ForegroundColorSpan(Color.parseColor("#000000"))
//		Html.fromHtml("<font color=black>"+ TITLE[position % TITLE.length]+ "</font>")
		return Html.fromHtml("<font color=gray>"+ TITLE[position % TITLE.length]+ "</font>");
	}

	@Override
	public int getCount() {
		return TITLE.length;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}
