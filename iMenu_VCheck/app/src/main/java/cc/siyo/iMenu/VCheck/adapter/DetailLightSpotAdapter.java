package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.ArticleContent;

/**
 * Created by Lemon on 2015/6/8 15:15.
 * Desc:详情亮点列表适配器
 */
public class DetailLightSpotAdapter extends AbsAdapter<ArticleContent> {

    private static final String TAG = "LightSpotListAdapter";
    FinalBitmap finalBitmap;

    public DetailLightSpotAdapter(Activity context, int layout) {
        super(context, layout);
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.test_member_img);
        finalBitmap.configLoadfailImage(R.drawable.test_member_img);
    }

    @Override
    public ViewHolder<ArticleContent> getHolder() {
        return new LightSpotViewHolder();
    }

    private class LightSpotViewHolder implements ViewHolder<ArticleContent> {

        /** 亮点图片*/
        private ImageView iv_lightSpot_img;
        /** 亮点标题*/
        private TextView tv_lightSpot_title;
        /** 亮点内容*/
        private TextView tv_lightSpot_content;

        @Override
        public void initViews(View v, int position) {
            iv_lightSpot_img = (ImageView) v.findViewById(R.id.iv_lightSpot_img);
            tv_lightSpot_title = (TextView) v.findViewById(R.id.tv_lightSpot_title);
            tv_lightSpot_content = (TextView) v.findViewById(R.id.tv_lightSpot_content);
        }

        @Override
        public void updateData(ArticleContent articleContent, int position) {
            tv_lightSpot_title.setText(articleContent.title);
            tv_lightSpot_content.setText(articleContent.content);
            finalBitmap.display(iv_lightSpot_img, articleContent.image.source);
        }

        @Override
        public void doOthers(ArticleContent articleContent, int position) {

        }
    }
}
