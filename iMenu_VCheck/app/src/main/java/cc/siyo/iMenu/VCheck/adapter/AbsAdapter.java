package cc.siyo.iMenu.VCheck.adapter;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
 /**
 * @author robin
 * 所有适配器的抽象处理.
 * **/
public abstract class AbsAdapter<T> extends BaseAdapter {
	
	private List<T> dataList = new ArrayList<T>();
	
	public List<T> getDataList()
	{
		return dataList;
	}
	
	public void release()
	{
		dataList.clear();
		dataList = null;
		context = null;
		mInflater = null;
	}
	
	public LayoutInflater mInflater =null;
	public static Activity context = null;
	private int layout=0;
	
	public AbsAdapter(Activity context,int layout)
	{
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.layout = layout;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public T getItem(int i) {
		if(getCount()<=0 || i>=getCount()) return null;
		return dataList.get(i);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}
	
	public abstract ViewHolder<T> getHolder();

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View v, ViewGroup vg) {
		ViewHolder<T> vh = null;
		if(null==v)
		{
			v = mInflater.inflate(layout, null);
			vh = getHolder();
			vh.initViews(v, position);
			v.setTag(vh);
		}
		else
		{
			vh=(ViewHolder<T>) v.getTag();
		}
		
		if(getItem(position)!=null)
		{
			vh.updateDatas(getItem(position), position);
			vh.doOthers(getItem(position), position);
		}
		
		return v;
	}
	
	public static interface ViewHolder<T>{
		public void initViews(View v, int position);
		public void updateDatas(T t, int position);
		public void doOthers(T t, int position);
	}

}
