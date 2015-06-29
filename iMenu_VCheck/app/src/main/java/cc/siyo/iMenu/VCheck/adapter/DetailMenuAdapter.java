package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.ArticleMenu;

/**
 * Created by Lemon on 2015/6/9 11:29.
 * Desc:详情菜单列表适配器
 */
public class DetailMenuAdapter extends AbsAdapter<ArticleMenu> {

    private static final String TAG = "DetailMenuAdapter";

    public DetailMenuAdapter(Activity context, int layout) {
        super(context, layout);
    }

    @Override
    public ViewHolder<ArticleMenu> getHolder() {
        return new ArticleMenuViewHolder();
    }

    private class ArticleMenuViewHolder implements ViewHolder<ArticleMenu> {

        /** 菜单标题*/
        private TextView tv_menu_title;
        /** 菜单内容*/
        private TextView tv_menu_content;

        @Override
        public void initViews(View v, int position) {
            tv_menu_title = (TextView) v.findViewById(R.id.tv_menu_title);
            tv_menu_content = (TextView) v.findViewById(R.id.tv_menu_content);
        }

        @Override
        public void updateData(ArticleMenu articleMenu, int position) {
            tv_menu_title.setText(articleMenu.title);
            if(articleMenu.content != null && articleMenu.content.length > 0) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < articleMenu.content.length; i++) {
                    buffer.append(articleMenu.content[i]);
                    buffer.append("\n");
                }
                tv_menu_content.setText(buffer);
            }

        }

        @Override
        public void doOthers(ArticleMenu articleMenu, int position) {

        }
    }
}
