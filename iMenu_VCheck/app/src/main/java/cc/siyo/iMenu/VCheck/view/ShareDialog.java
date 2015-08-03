package cc.siyo.iMenu.VCheck.view;

import java.util.HashMap;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Share;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
/**
 * 分享自定义对话框
 */
public class ShareDialog extends Dialog {

	private Context context;

	public ShareDialog(Context context, Share share) {
		super(context, R.style.LoadingDialogTheme);
		init(context, share);
	}

	private void init(final Context context, final Share share) {
		this.context = context;
        getWindow().setGravity(Gravity.BOTTOM);//设置dialog显示的位置
		setContentView(R.layout.dialog_share);
		getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		findViewById(R.id.tvShareCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		findViewById(R.id.llShareWeChat).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareSDK.initSDK(context);
				Platform weChat = ShareSDK.getPlatform(context, Wechat.NAME);
				Wechat.ShareParams weChatParams = new Wechat.ShareParams();
				weChatParams.setTitle(share.title);
				weChatParams.setText(share.content);
				weChatParams.setUrl(share.link);
				weChatParams.setSiteUrl(share.link);
				weChatParams.setImageUrl(share.imageUrl);
				weChatParams.setImagePath(share.imagePath);
				weChatParams.setShareType(Platform.SHARE_WEBPAGE);
				weChat.setPlatformActionListener(new SharePlatformActionListener());
				weChat.share(weChatParams);
			}
		});
		findViewById(R.id.llShareWeChatMoment).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareSDK.initSDK(context);
				Platform weChatMoment = ShareSDK.getPlatform(context, WechatMoments.NAME);
				WechatMoments.ShareParams weChatMomentParams = new WechatMoments.ShareParams();
				weChatMomentParams.setTitle(share.content);
				weChatMomentParams.setUrl(share.link);
				weChatMomentParams.setSiteUrl(share.link);
				weChatMomentParams.setImageUrl(share.imageUrl);
				weChatMomentParams.setImagePath(share.imagePath);
				System.out.println("weChatMoment->mShare.imagePath:" + share.imagePath);
				weChatMomentParams.setShareType(Platform.SHARE_WEBPAGE);
				weChatMoment.setPlatformActionListener(new SharePlatformActionListener());
				weChatMoment.share(weChatMomentParams);
			}
		});
		findViewById(R.id.llShareSina).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareSDK.initSDK(context);
				Platform sina = ShareSDK.getPlatform(context, SinaWeibo.NAME);
				SinaWeibo.ShareParams sinaParams = new SinaWeibo.ShareParams();
		        sinaParams.setTitle(share.title);
		        sinaParams.setText(share.content);
		        sinaParams.setSite(share.description);
		        sinaParams.setSiteUrl(share.link);
		        sinaParams.setImagePath(share.imagePath);
				sina.setPlatformActionListener(new SharePlatformActionListener());
				sina.share(sinaParams);
			}
		});
	}

	/** 回调监听*/
	private class SharePlatformActionListener implements PlatformActionListener {

		@Override
		public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
			if(platform.getName().equals(Wechat.NAME)) {
				Log.e("shareDialog", Wechat.NAME + "onComplete ->");
			}
			if(platform.getName().equals(WechatMoments.NAME)) {
				Log.e("shareDialog", WechatMoments.NAME + "onComplete ->");
			}
			if(platform.getName().equals(SinaWeibo.NAME)) {
				Log.e("shareDialog", SinaWeibo.NAME + "onComplete ->");
			}
			dismiss();
			Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(Platform platform, int i, Throwable throwable) {
			if(platform.getName().equals(Wechat.NAME)) {
				Log.e("shareDialog", Wechat.NAME + "onError ->");
			}
			if(platform.getName().equals(WechatMoments.NAME)) {
				Log.e("shareDialog", WechatMoments.NAME + "onError ->");
			}
			if(platform.getName().equals(SinaWeibo.NAME)) {
				Log.e("shareDialog", SinaWeibo.NAME + "onError ->");
			}
			Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(Platform platform, int i) {
			if(platform.getName().equals(Wechat.NAME)) {
				Log.e("shareDialog", Wechat.NAME + "onCancel ->");
			}
			if(platform.getName().equals(WechatMoments.NAME)) {
				Log.e("shareDialog", WechatMoments.NAME + "onCancel ->");
			}
			if(platform.getName().equals(SinaWeibo.NAME)) {
				Log.e("shareDialog", SinaWeibo.NAME + "onCancel ->");
			}
			Toast.makeText(context, "分享取消", Toast.LENGTH_SHORT).show();
		}
	}
}
