package cc.siyo.iMenu.VCheck.activity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.tsz.afinal.annotation.view.ViewInject;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.Share;
import cc.siyo.iMenu.VCheck.model.ShareInvite;
import cc.siyo.iMenu.VCheck.view.ShareDialog;
import cc.siyo.iMenu.VCheck.view.TopBar;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Lemon on 2015/7/24 10:03.
 * Desc:
 */
public class InviteFriendsActivity extends BaseActivity {

    private static final String TAG = "InviteFriendsActivity";
    /** 头部*/
    @ViewInject(id = R.id.topBar)private TopBar topBar;
    /** 邀请码显示*/
    @ViewInject(id = R.id.tvInviteCode)private TextView tvInviteCode;
    /** 邀请人显示*/
    @ViewInject(id = R.id.tvInvitePeopleTips)private TextView tvInvitePeopleTips;
    /** 被邀请人显示*/
    @ViewInject(id = R.id.tvInviteCodeTips)private TextView tvInviteCodeTips;
    /** 分享按钮*/
    @ViewInject(id = R.id.tvShareFriends)private TextView tvShareFriends;
    /** 已经邀请人数显示*/
    @ViewInject(id = R.id.tvInviteTotalCount)private TextView tvInviteTotalCount;
    /** 上部邀请layout*/
    @ViewInject(id = R.id.rlInvite)private RelativeLayout rlInvite;
    /** 邀请实体*/
    private ShareInvite shareInvite;

    @Override
    public int getContentView() {
        return R.layout.activity_invite;
    }

    @Override
    public void initView() {
        topBar.settitleViewText("邀请好友");
        topBar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        shareInvite = (ShareInvite) getIntent().getExtras().getSerializable(Constant.INTENT_INVITE);
        tvInviteCode.setText(shareInvite.invite_code);
        tvInvitePeopleTips.setText(shareInvite.invite_people_tips);
        tvInviteCodeTips.setText(shareInvite.invite_code_tips);
        tvInviteTotalCount.setText(shareInvite.invite_total_count);
        rlInvite.setPadding(120, 120, 120, 120);
        rlInvite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e(TAG, " MotionEvent.ACTION_DOWN");
                        rlInvite.setPadding(100, 100, 100, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e(TAG, " MotionEvent.ACTION_UP");
                        rlInvite.setPadding(120, 120, 120, 120);
                        break;
                }
                return true;
            }
        });
        tvShareFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享
                Share share = new Share();
                share.title = "知味-限量精选美食";
                share.content = "发现一款很棒的美食软件，快来跟我一起体验吧！";
                share.link = "imenu.so";
                ShareDialog shareDialog = new ShareDialog(InviteFriendsActivity.this, share);
                shareDialog.show();
            }
        });
    }
}
