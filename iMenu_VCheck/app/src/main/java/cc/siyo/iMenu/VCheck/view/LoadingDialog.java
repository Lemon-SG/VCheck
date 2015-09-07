package cc.siyo.iMenu.VCheck.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.util.AnimationController;
import cc.siyo.iMenu.VCheck.util.StringUtils;

/**
 * 
 * @author Created by ShangGuan on 15-03-04.
 */
public class LoadingDialog extends Dialog {
	private AnimationDrawable animationDrawable;
	private String message;

	public LoadingDialog(Context context, String message) {
		super(context, R.style.LoadingDialogTheme);
		this.message = message;
		init(context);
	}

	private void init(Context context) {
		setContentView(R.layout.layout_loading_dialog);
//		ImageView loadingImageView = (ImageView) findViewById(R.id.loadingImageView);
		TextView tv = (TextView) findViewById(R.id.tv);
		if(!StringUtils.isBlank(message)){
			tv.setText("正在加载...");
		}else{
			tv.setText(message);
		}
//		loadingImageView.setImageResource(R.drawable.anim_loading_dialog);

//		animationDrawable = (AnimationDrawable) loadingImageView.getDrawable();
//		animationDrawable.setOneShot(false);
//		animationDrawable.start();
		setCanceledOnTouchOutside(false);
		setCancelable(false);
	}
}
