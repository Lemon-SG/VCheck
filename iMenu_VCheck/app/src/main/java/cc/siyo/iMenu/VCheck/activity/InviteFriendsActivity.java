package cc.siyo.iMenu.VCheck.activity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.tsz.afinal.annotation.view.ViewInject;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/24 10:03.
 * Desc:
 */
public class InviteFriendsActivity extends BaseActivity {

    private static final String TAG = "InviteFriendsActivity";/** 头部*/
    @ViewInject(id = R.id.topBar)private TopBar topBar;
    /** 邀请码显示*/
    @ViewInject(id = R.id.tvInviteCode)private TextView tvInviteCode;
    /** 上部邀请layout*/
    @ViewInject(id = R.id.rlInvite)private RelativeLayout rlInvite;
    /** 邀请码*/
    private String inviteCode = "";

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
        inviteCode = getIntent().getExtras().getString(Constant.INTENT_INVITE_CODE);
        tvInviteCode.setText(inviteCode);
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
    }
}
