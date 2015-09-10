package cc.siyo.iMenu.VCheck.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import net.tsz.afinal.annotation.view.ViewInject;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.API;
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
    @ViewInject(id = R.id.iv)private ImageView iv;
    private ImageLoader imageLoader;

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

        imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher).build();
        imageLoader.displayImage(API.LOGO, iv, options);
    }

    @Override
    public void initData() {
        shareInvite = (ShareInvite) getIntent().getExtras().getSerializable(Constant.INTENT_INVITE);
        tvInviteCode.setText(shareInvite.invite_code);
        tvInviteCodeTips.setText(shareInvite.invite_people_tips);
        tvInvitePeopleTips.setText(shareInvite.invite_code_tips);
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
                share.title = "送你知味App独享30元礼券";
                share.imageUrl = imageLoader.getDiskCache().get(API.LOGO).getAbsolutePath();
                share.imagePath = imageLoader.getDiskCache().get(API.LOGO).getAbsolutePath();
                share.content = "使用邀请码:" + shareInvite.invite_code;
                share.link = shareInvite.share_url;
                ShareDialog shareDialog = new ShareDialog(InviteFriendsActivity.this, share);
                shareDialog.show();
            }
        });
    }
}
