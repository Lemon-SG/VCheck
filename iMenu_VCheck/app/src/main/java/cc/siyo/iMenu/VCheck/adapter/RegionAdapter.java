package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Region;

/**
 * Created by Lemon on 2015/5/25.
 * Desc:
 */
public class RegionAdapter extends AbsAdapter<Region>{

    private static final String TAG = "RegionAdapter";

    public RegionAdapter(Activity context, int layout) {
        super(context, layout);
    }

    @Override
    public ViewHolder<Region> getHolder() {
        return new RegionViewHolder();
    }


    private class RegionViewHolder implements ViewHolder<Region> {

        private TextView tv_city;

        @Override
        public void initViews(View v, int position) {
            tv_city = (TextView) v.findViewById(R.id.tv_city);
        }

        @Override
        public void updateDatas(Region region, int position) {
            tv_city.setText(region.region_name);
        }

        @Override
        public void doOthers(Region region, int position) {

        }
    }
}
