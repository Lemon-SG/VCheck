package cc.siyo.iMenu.VCheck.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.siyo.iMenu.VCheck.R;

/**
 * Author：头部导航
 * 
 * @author Created by Lemon on 15-5-4 .
 */
public class TopBar extends RelativeLayout {

//	private RelativeLayout topar;
	/** 声明context*/
	private Context mContext;
	/** 声明右边的按钮*/
	public TextView mRightButton;
	/** 声明左边按钮*/
	public TextView mLeftButton;
	/** title*/
	private TextView titleView;
	/** 左侧图片view*/
	private ImageView mLeftImageView;
	/** 声明接口对象*/
//	private ButtonOnClick btnOnClick;
	/** 头部左侧文本*/
	public final static String LEFT_BUTTON = "left";
	/** 头部右侧文本*/
	public final static String RIGHT_BUTTON = "right";
	/** 头部左侧标题*/
	public final static String TITLE_VIEW = "title";
	/** 头部左侧图片*/
	public final static String LEFT_IMGVIEW = "left_img";
	/** 右侧图标*/
	private ImageView mRightImageView;

	/**
	 * 构�?函数
	 * 
	 * @param context
	 */
	public TopBar(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public TopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public TopBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	/**
	 * 初始化
	 */
	@SuppressLint("InflateParams") private void init() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v= inflater.inflate(R.layout.layout_topbar, null);
//		RelativeLayout top_bar = (RelativeLayout) v.findViewById(R.id.top_bar);
		mLeftButton = (TextView) v.findViewById(R.id.tv_left);
		mRightButton = (TextView) v.findViewById(R.id.tv_right);
		titleView = (TextView) v.findViewById(R.id.tv_title);
		mLeftImageView = (ImageView) v.findViewById(R.id.iv_left);
		mRightImageView = (ImageView) v.findViewById(R.id.iv_right);
		addView(v);
//		mRightButton = new TextView(mContext);
//		mLeftButton = new TextView(mContext);
//		titleView = new TextView(mContext);
//		mLeftButton.setId(0);
//		mRightButton.setId(1);
//		titleView.setId(3);
//		titleView.setTextColor(Color.WHITE);
//		topar = new RelativeLayout(mContext);
//		RelativeLayout.LayoutParams toBuu = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		toBuu.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//		toBuu.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//		toBuu.leftMargin = 10;
		// topar.addView(mLeftButton, toBuu);

//		RelativeLayout.LayoutParams to = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		to.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//		to.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//		to.rightMargin = 10;
		// topar.addView(mRightButton, to);

//		RelativeLayout.LayoutParams toi = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		toi.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		// topar.addView(titleView, toi);

//		addView(mLeftButton, toBuu);
//		addView(mRightButton, to);
//		addView(titleView, toi);
//		setBackgroundResource(R.drawable.topbar_bg_shape);
	}

	/**
	 * 显示button方法(默认全部显示)
	 * 
	 * @param hiddenBtn
	 *            left：表示隐藏左边的 right：表示隐藏右边的 LEFT_IMGVIEW：隐藏图片LEFT_IMGVIEW
	 */
	public void setHiddenButton(String hiddenBtn) {
		if (!"".equals(hiddenBtn) && hiddenBtn.equals(TopBar.LEFT_BUTTON)) {
			
			mLeftButton.setVisibility(View.GONE); // 隐藏左边的按钮
            mLeftImageView.setVisibility(View.GONE);
			
		} else if (!"".equals(hiddenBtn) && hiddenBtn.equals(TopBar.RIGHT_BUTTON)) {
			
			mRightButton.setVisibility(View.INVISIBLE); // 隐藏右边的按钮
			mRightImageView.setVisibility(View.INVISIBLE);
		} else if (!"".equals(hiddenBtn) && hiddenBtn.equals(TopBar.LEFT_IMGVIEW)) {
			
//			mLeftButton.setVisibility(View.GONE); // 隐藏左边跟右边按钮
//			mRightButton.setVisibility(View.GONE);
//			titleView.setGravity(Gravity.CENTER);
			mLeftImageView.setVisibility(View.INVISIBLE);
			mLeftButton.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置Button字体内容
	 * 
	 * @param text
	 *            设置的内�?
	 * @param btn
	 *            left：显示左边button字体内容 right：显示右边button字体内容 TITLE_VIEW：显示TITLE_VIEW字体内容
	 */
	public void setText(String btn, String text) {

		if (!"".equals(text) && btn.equals(TopBar.LEFT_BUTTON)) {

			mLeftButton.setText(text); // 设置左边的button内容
			mLeftImageView.setVisibility(View.INVISIBLE);
			mLeftButton.setVisibility(View.VISIBLE);
		} else if (!"".equals(btn) && btn.equals(TopBar.RIGHT_BUTTON)) {

			mRightButton.setText(text); // 设置右边button内容
			mRightButton.setVisibility(View.VISIBLE);
		} else if (!"".equals(btn) && btn.equals(TopBar.TITLE_VIEW)) {

			titleView.setText(text);
		}
	}

	/**
	 * 设置按钮的背景图�?
	 * 
	 * @param resourceID
	 *            图片id
	 * @param BtnImg
	 *            left：设置左边的按钮背景为resourceID right：设置右边的按钮背景为resourceID
	 *            all：设置左右的按钮背景为resourceID
	 */
	public void setButtonImage(String BtnImg, int resourceID) {

		if (!"".equals(BtnImg) && BtnImg.equals(TopBar.LEFT_BUTTON)) {
			mLeftButton.setVisibility(View.INVISIBLE);
			// 设置左边的button背景
			mLeftImageView.setImageResource(resourceID);
		} else if (!"".equals(BtnImg) && BtnImg.equals(TopBar.RIGHT_BUTTON)) {

			mRightImageView.setImageResource(resourceID); // 设置右边button背景
			mRightImageView.setVisibility(View.VISIBLE);
		} else if (!"".equals(BtnImg) && BtnImg.equals("all")) {

			mLeftButton.setBackgroundResource(resourceID); // 设置左边跟右边button背景
			mRightButton.setBackgroundResource(resourceID);
		} else {

		}
	}

	/**
	 * 设置文本的字体颜�?
	 * 
	 * @param setType
	 *            设置的类�? left：左边按�? left：右边按�? title：标�? all：所�?
	 * @param color
	 *            设置的字体颜�?
	 */
	public void setTextColor(String setType, int color) {

		int colors = Color.rgb(72, 72, 72);
		if (!"".equals(setType) && setType.equals("left")) {

			// mLeftButton.setTextColor(color); //设置左边的button字体颜色
		} else if (!"".equals(setType) && setType.equals("right")) {

			mRightButton.setTextColor(color); // 设置右边button字体颜色
		} else if (!"".equals(setType) && setType.equals("title")) {

			titleView.setTextColor(colors); // 设置textView字体颜色
		} else if (!"".equals(setType) && setType.equals("all")) {

			// mLeftButton.setTextColor(color); //设置左右两边跟textview的字体颜�?
			mRightButton.setTextColor(color);
			titleView.setTextColor(colors);
		} else {

		}
	}

	/**
	 * 设置文本的字体大�?
	 * 
	 * @param setType
	 *            设置的类�? left：左边按�? left：右边按�? title：标�? all：所�?
	 * @param size
	 *            设置的字体的大小
	 */
	public void setTextSize(String setType, int size) {

		int sizes = 20;
		if (!"".equals(setType) && setType.equals("left")) {

			// mLeftButton.setTextSize(size) ; //设置左边的button字体大小
		} else if (!"".equals(setType) && setType.equals("right")) {

			mRightButton.setTextSize(size); // 设置右边button字体大小
		} else if (!"".equals(setType) && setType.equals("title")) {

			titleView.setTextSize(sizes); // 设置textView字体大小
		} else if (!"".equals(setType) && setType.equals("all")) {

			// mLeftButton.setTextSize(size); //设置左右两边跟textview的字体大�?
			mRightButton.setTextSize(size);
			titleView.setTextSize(sizes);
		} else {

		}
	}

	/**
	 * 设置topBar 的title标题
	 * 
	 * @param title
	 */
	public void settitleViewText(String title) {

		titleView.setText(title);

	}

	/**
	 * 对右边的button进行事件监听 传�?ButtonOnClick接口为参�?
	 * 
	 * @param btnOnClick
	 */
	public void setRightButtonOnClickListener(final ButtonOnClick btnOnClick) {
		mRightButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (btnOnClick != null) {
					btnOnClick.onClick(view);
				}
			}
		});
		mRightImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (btnOnClick != null) {
					btnOnClick.onClick(view);
				}
			}
		});
	}

	/**
	 * 对左边的button进行事件监听 传�?ButtonOnClick接口为参�?
	 * 
	 * @param btnOnClick
	 */
	public void setLeftButtonOnClickListener(final ButtonOnClick btnOnClick) {
		mLeftButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (btnOnClick != null) {
					btnOnClick.onClick(view);
				}
			}
		});
		mLeftImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (btnOnClick != null) {
					btnOnClick.onClick(view);
				}
			}
		});
	}

	/**
	 * ButtonOnClick接口
	 * 
	 * @author Administrator
	 * 
	 */
	public static interface ButtonOnClick {
		public void onClick(View view);
	}

	public TextView getLeftButton() {

		return mLeftButton;
	}

	public TextView getRightButton() {

		return mRightButton;
	}
}
