package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.ArticleContent;

/**
 * Created by Lemon on 2015/6/8 15:15.
 * Desc:���������б�������
 */
public class LightSpotListAdapter extends AbsAdapter<ArticleContent> {

    private static final String TAG = "LightSpotListAdapter";
    private Context mContext;

    public LightSpotListAdapter(Activity context, int layout) {
        super(context, layout);
    }

    @Override
    public ViewHolder<ArticleContent> getHolder() {
        return new LightSpotViewHolder();
    }

    private class LightSpotViewHolder implements ViewHolder<ArticleContent> {

        /** ����ͼƬ*/
        private ImageView iv_lightSpot_img;
        /** �������*/
        private TextView tv_lightSpot_title;
        /** ��������*/
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
            //TODO ͼƬչʾ������
        }

        @Override
        public void doOthers(ArticleContent articleContent, int position) {

        }
    }
}
