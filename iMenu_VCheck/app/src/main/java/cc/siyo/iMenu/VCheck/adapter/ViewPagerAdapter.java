package cc.siyo.iMenu.VCheck.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import cc.siyo.iMenu.VCheck.activity.DetailActivity;

/**
 * 店长推荐viewPager适配器
 * 
 * @author shang_guan
 * 
 */
public class ViewPagerAdapter extends PagerAdapter {

	private Context mContext;
	private List<View> mListViews;

	public ViewPagerAdapter(Context mContext, List<View> mListViews) {
		this.mListViews = mListViews;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(mListViews.get(position));
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(mListViews.get(position), 0);
		return mListViews.get(position);
	}
}
