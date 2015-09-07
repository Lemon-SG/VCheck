package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Region;

/**
 * Created by Lemon on 2015/5/25.
 * Desc:城市列表适配器
 */
public class RegionAdapter extends AbsAdapter<Region>{

    public RegionAdapter(Activity context, int layout) {
        super(context, layout);
    }

    @Override
    public ViewHolder<Region> getHolder() {
        return new RegionViewHolder();
    }


    private class RegionViewHolder implements ViewHolder<Region> {

        private TextView tv_city;
        private TextView tv_city_open;

        @Override
        public void initViews(View v, int position) {
            tv_city = (TextView) v.findViewById(R.id.tv_city);
            tv_city_open = (TextView) v.findViewById(R.id.tv_city_open);
        }

        @Override
        public void updateData(Region region, int position) {
            tv_city.setText(region.region_name);
            if(region.is_open.equals("1")) {
                tv_city.setTextColor(context.getResources().getColor(R.color.white));
                tv_city_open.setVisibility(View.INVISIBLE);
            }
            if(region.is_open.equals("2")) {
                tv_city.setTextColor(context.getResources().getColor(R.color.gray_87));
                tv_city_open.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void doOthers(Region region, int position) {

        }
    }
}
