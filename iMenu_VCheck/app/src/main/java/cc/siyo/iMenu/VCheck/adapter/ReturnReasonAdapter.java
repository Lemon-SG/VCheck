package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.SubmitReturnActivity;
import cc.siyo.iMenu.VCheck.model.Region;
import cc.siyo.iMenu.VCheck.model.ReturnInfo;

/**
 * Created by Lemon on 2015/7/15.
 * Desc:退款原因列表适配器
 */
public class ReturnReasonAdapter extends AbsAdapter<ReturnInfo>{

    private static final String TAG = "ReturnReasonAdapter";

    public ReturnReasonAdapter(Activity context, int layout) {
        super(context, layout);
    }

    @Override
    public ViewHolder<ReturnInfo> getHolder() {
        return new ReturnInfoViewHolder();
    }


    private class ReturnInfoViewHolder implements ViewHolder<ReturnInfo> {

        private TextView return_reason_name;
        private ImageView check_box;
        private LinearLayout layout_return_reason_list_item;

        @Override
        public void initViews(View v, int position) {
            return_reason_name = (TextView) v.findViewById(R.id.return_reason_name);
            check_box = (ImageView) v.findViewById(R.id.check_box);
            layout_return_reason_list_item = (LinearLayout) v.findViewById(R.id.layout_return_reason_list_item);
        }

        @Override
        public void updateData(final ReturnInfo returnInfo, int position) {
            return_reason_name.setText(returnInfo.return_reason_name);
            if (returnInfo.isTrue) {
                check_box.setImageResource(R.drawable.ic_push_open_checked);
            } else {
                check_box.setImageResource(R.drawable.ic_push_open_check);
            }
            layout_return_reason_list_item.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // 取消选中
                            returnInfo.isTrue = true;
                            check_box.setImageResource(R.drawable.ic_push_open_checked);
                            ((SubmitReturnActivity) context).changeBox(returnInfo);
                        }
                    });
        }

        @Override
        public void doOthers(ReturnInfo returnInfo, int position) {

        }
    }
}
