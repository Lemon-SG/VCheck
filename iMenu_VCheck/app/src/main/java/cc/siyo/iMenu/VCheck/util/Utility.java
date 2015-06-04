package cc.siyo.iMenu.VCheck.util;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utility {
	/** 获取ListView高度 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/** 获取ExpandableListView高度 */
	public static void setListViewHeightBasedOnChildren(
			ExpandableListView expandableListView) {
		ListAdapter listAdapter = expandableListView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, expandableListView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
		params.height = totalHeight
				+ (expandableListView.getDividerHeight() * (listAdapter
						.getCount() - 1));
		expandableListView.setLayoutParams(params);
	}

	// 获取ListView对应的Adapter ListAdapter listAdapter = listView.getAdapter(); if
	// (listAdapter == null) { // pre-condition return; } int totalHeight = 0;
	// for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
	// //listAdapter.getCount()返回数据项的数目 View listItem = listAdapter.getView(i,
	// null, listView); listItem.measure(0, 0); //计算子项View 的宽高 totalHeight +=
	// listItem.getMeasuredHeight(); //统计所有子项的总高度 } ViewGroup.LayoutParams
	// params = listView.getLayoutParams(); params.height = totalHeight +
	// (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	// //listView.getDividerHeight()获取子项间分隔符占用的高度
	// //params.height最后得到整个ListView完整显示需要的高度 listView.setLayoutParams(params);
	// }

	/** 获取GridView高度 */
	@SuppressLint("NewApi")
	public static void setListViewHeightBasedOnChildren(GridView gridView) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int totalHeight1 = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, gridView);
			listItem.measure(0, 0);
			// totalHeight += listItem.getMeasuredHeight();
			totalHeight1 = listItem.getMeasuredHeight();
		}
		int totalCount = listAdapter.getCount();
		int numColumns = gridView.getNumColumns();
		int column = totalCount / numColumns;
		int yushu = totalCount % numColumns == 0 ? 0 : totalCount % numColumns;
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = (column + yushu) * totalHeight1
				+ (gridView.getVerticalSpacing() * 2 * (column + yushu - 1));
		gridView.setLayoutParams(params);
	}

}
// public class Utility {
// public static void setListViewHeightBasedOnChildren(ListView listView) {
// ListAdapter listAdapter = listView.getAdapter();
// if (listAdapter == null) {
// // pre-condition
// return;
// }
//
// int totalHeight = 0;
// for (int i = 0; i < listAdapter.getCount(); i++) {
// View listItem = listAdapter.getView(i, null, listView);
// listItem.measure(0, 0);
// totalHeight += listItem.getMeasuredHeight();
// }
//
// ViewGroup.LayoutParams params = listView.getLayoutParams();
// params.height = totalHeight + (listView.getDividerHeight() *
// (listAdapter.getCount() - 1));
// listView.setLayoutParams(params);
// }
// }