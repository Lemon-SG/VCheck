package cc.siyo.iMenu.VCheck.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.Message;
import cc.siyo.iMenu.VCheck.model.Region;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.TimeUtil;

/**
 * Created by Lemon on 2015/5/25.
 * Desc:消息列表适配器
 */
public class MessageAdapter extends AbsAdapter<Message>{

    private static final String TAG = "MessageAdapter";

    public MessageAdapter(Activity context, int layout) {
        super(context, layout);
    }

    @Override
    public ViewHolder<Message> getHolder() {
        return new MessageViewHolder();
    }


    private class MessageViewHolder implements ViewHolder<Message> {

        private ImageView ivMessageType;
        private TextView tvMessage;
        private TextView tvMessageTime;

        @Override
        public void initViews(View v, int position) {
            ivMessageType = (ImageView) v.findViewById(R.id.ivMessageType);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            tvMessageTime = (TextView) v.findViewById(R.id.tvMessageTime);
        }

        @Override
        public void updateData(Message message, int position) {
            tvMessage.setText(message.title);
            tvMessageTime.setText(TimeUtil.formatTime(message.message_date, "yyyy-MM-dd HH:mm"));
            switch (Integer.parseInt(message.message_type_id)) {
                case Constant.MESSAGE_ACTIVITY://1->活动消息;
                    ivMessageType.setImageResource(R.mipmap.ic_style_black_18dp);
                    break;
                case Constant.MESSAGE_PAY://2->消费确认;
                    ivMessageType.setImageResource(R.mipmap.ic_local_dining_black_18dp);
                    break;
                case Constant.MESSAGE_RETURN://3->退款提醒;
                    ivMessageType.setImageResource(R.mipmap.ic_insert_link_black_18dp);
                    break;
                case Constant.MESSAGE_VOUCHER://4->获得礼券;
                    ivMessageType.setImageResource(R.mipmap.ic_card_giftcard_black_18dp);
                    break;
                case Constant.MESSAGE_SALE://5->开售提醒
                    ivMessageType.setImageResource(R.mipmap.ic_favorite_red_18dp);
                    break;
            }
        }

        @Override
        public void doOthers(Message message, int position) {

        }
    }
}
