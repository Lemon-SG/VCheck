package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Mine;

/**
 * Created by Lemon on 2015/4/30.
 * Desc:
 */
public class StoreAdapter extends AbsAdapter<Mine> {
    private static final String TAG = "MineAdapter";
    FinalBitmap finalBitmap;

    public StoreAdapter(Activity context, int layout) {
        super(context, layout);
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.img);
        finalBitmap.configLoadfailImage(R.drawable.img);
    }

    @Override
    public ViewHolder<Mine> getHolder() {
        return new MineViewHolder();
    }

    private class MineViewHolder implements ViewHolder<Mine> {

        private TextView text;
        /** 分割线*/
        private LinearLayout driver;
        private ImageView iv_menu_img_item;

        @Override
        public void initViews(View v, int position) {
            text = (TextView) v.findViewById(R.id.tv_title);
            driver = (LinearLayout) v.findViewById(R.id.driver);
            iv_menu_img_item = (ImageView) v.findViewById(R.id.iv_menu_img_item);
        }

        @Override
        public void updateDatas(Mine mine, int position) {
            finalBitmap.display(iv_menu_img_item, "https://dn-img-seriousapps.qbox.me/business/14071541323372?imageView2/1/w/639/h/472/interlace/1/format/webp");
            text.setText(mine.getText());
            driver.setVisibility(View.VISIBLE);
            System.out.println("position = " + position + "|size = " + getDataList().size());
            if(position + 1 == getDataList().size()){
                //需要处理末尾的分割线
                System.out.println("KILL分割线");
                driver.setVisibility(View.GONE);
            }
        }

        @Override
        public void doOthers(Mine mine, int position) {

        }
    }
}
