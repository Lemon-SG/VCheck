package cc.siyo.iMenu.VCheck.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cc.siyo.iMenu.VCheck.R;

/**
 * Created by Lemon on 2015/7/5.
 * Desc:自定义提示对话框
 */
public class PromptDialog extends Dialog {

    private TextView tvDialogTitle;
    private TextView tvDialogMsg;
    private TextView tvDialogConfirm;
    private TextView tvDialogCancel;
    private TextView tvDialogDriver;
    private String title;
    private String msg;
    private String confirmText;
    private String cancelText;
    private View.OnClickListener OnConfirmListener;
    private View.OnClickListener OnCancelListener;

    public PromptDialog(Context context) {
        super(context, R.style.LoadingDialogTheme);
        init(context);
    }

    public PromptDialog(Context context, String title, String msg, String confirmText, View.OnClickListener OnConfirmListener) {
        super(context, R.style.LoadingDialogTheme);
        this.title = title;
        this.msg = msg;
        this.confirmText = confirmText;
        this.OnConfirmListener = OnConfirmListener;
        init(context);
    }

    public PromptDialog(Context context, String title, String msg, String confirmText, String cancelText, View.OnClickListener OnConfirmListener, View.OnClickListener OnCancelListener) {
        super(context, R.style.LoadingDialogTheme);
        this.title = title;
        this.msg = msg;
        this.confirmText = confirmText;
        this.cancelText = cancelText;
        this.OnConfirmListener = OnConfirmListener;
        this.OnCancelListener = OnCancelListener;
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.dialog_prompt);

        tvDialogDriver = (TextView) findViewById(R.id.tvDialogDriver);
        tvDialogTitle = (TextView) findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText(title);
        tvDialogMsg = (TextView) findViewById(R.id.tvDialogMsg);
        tvDialogMsg.setText(msg);
        tvDialogConfirm = (TextView) findViewById(R.id.tvDialogConfirm);
        tvDialogConfirm.setText(confirmText);
        tvDialogConfirm.setOnClickListener(OnConfirmListener);
        tvDialogCancel = (TextView) findViewById(R.id.tvDialogCancel);
        tvDialogCancel.setText(cancelText);
        tvDialogCancel.setOnClickListener(OnCancelListener);
        if(OnCancelListener == null) {
            //只显示一个按钮
            tvDialogConfirm.setBackgroundResource(R.drawable.bg_dialog_all_btn_selector);
            tvDialogCancel.setVisibility(View.GONE);
            tvDialogDriver.setVisibility(View.GONE);
        }
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public void setTitleSize(int size) {
        tvDialogTitle.setTextSize(size);
    }

}
