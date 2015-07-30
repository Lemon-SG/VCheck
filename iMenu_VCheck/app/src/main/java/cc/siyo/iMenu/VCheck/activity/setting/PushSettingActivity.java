package cc.siyo.iMenu.VCheck.activity.setting;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import net.tsz.afinal.annotation.view.ViewInject;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.BaseActivity;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/29 17:03.
 * Desc:推送设置
 */
public class PushSettingActivity extends BaseActivity implements View.OnClickListener {

    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 总推送开关*/
    @ViewInject(id = R.id.rlPushAll)private RelativeLayout rlPushAll;
    /** 消费确认开关*/
    @ViewInject(id = R.id.rlPushPay)private RelativeLayout rlPushPay;
    /** 退款提醒开关*/
    @ViewInject(id = R.id.rlPushReturn)private RelativeLayout rlPushReturn;
    /** 活动信息开关*/
    @ViewInject(id = R.id.rlPushActivity)private RelativeLayout rlPushActivity;
    /** 获得礼券开关*/
    @ViewInject(id = R.id.rlPushVoucher)private RelativeLayout rlPushVoucher;
    @ViewInject(id = R.id.ivPushAll)private ImageView ivPushAll;
    @ViewInject(id = R.id.ivPushPay)private ImageView ivPushPay;
    @ViewInject(id = R.id.ivPushReturn)private ImageView ivPushReturn;
    @ViewInject(id = R.id.ivPushActivity)private ImageView ivPushActivity;
    @ViewInject(id = R.id.ivPushVoucher)private ImageView ivPushVoucher;
    /** 总开关标石*/
    private boolean isPushAll;
    /** 消费确认开关标石*/
    private boolean isPushPay;
    /** 退款提醒开关标石*/
    private boolean isPushReturn;
    /** 活动消息开关标石*/
    private boolean isPushActivity;
    /** 礼券开关标石*/
    private boolean isPushVoucher;

    @Override
    public int getContentView() {
        return R.layout.activity_push_set;
    }

    @Override
    public void initView() {
        isPushAll = PreferencesUtils.getBoolean(context, Constant.KEY_PUSH_ALL, true);
        isPushPay = PreferencesUtils.getBoolean(context, Constant.KEY_PUSH_PAY, true);
        isPushReturn = PreferencesUtils.getBoolean(context, Constant.KEY_PUSH_RETURN, true);
        isPushActivity = PreferencesUtils.getBoolean(context, Constant.KEY_PUSH_ACTIVITY, true);
        isPushVoucher = PreferencesUtils.getBoolean(context, Constant.KEY_PUSH_VOUCHER, true);
        topbar.settitleViewText("推送设置");
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rlPushAll.setOnClickListener(this);
        rlPushPay.setOnClickListener(this);
        rlPushReturn.setOnClickListener(this);
        rlPushActivity.setOnClickListener(this);
        rlPushVoucher.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if(isPushAll) {
            ivPushAll.setImageResource(R.drawable.ic_collect_red);
            if(isPushPay) {
                ivPushPay.setImageResource(R.drawable.ic_collect_red);
            } else {
                ivPushPay.setImageResource(R.drawable.ic_collect_black);
            }
            if(isPushReturn) {
                ivPushReturn.setImageResource(R.drawable.ic_collect_red);
            } else {
                ivPushReturn.setImageResource(R.drawable.ic_collect_black);
            }
            if(isPushActivity) {
                ivPushActivity.setImageResource(R.drawable.ic_collect_red);
            } else {
                ivPushActivity.setImageResource(R.drawable.ic_collect_black);
            }
            if(isPushVoucher) {
                ivPushVoucher.setImageResource(R.drawable.ic_collect_red);
            } else {
                ivPushVoucher.setImageResource(R.drawable.ic_collect_black);
            }
        } else {
            ivPushAll.setImageResource(R.drawable.ic_collect);
            ivPushAll.setImageResource(R.drawable.ic_collect);
            ivPushPay.setImageResource(R.drawable.ic_collect);
            ivPushReturn.setImageResource(R.drawable.ic_collect);
            ivPushActivity.setImageResource(R.drawable.ic_collect);
            ivPushVoucher.setImageResource(R.drawable.ic_collect);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlPushAll:
                //推送总开关
                if(isPushAll) {
                    isPushAll = false;
                    ivPushAll.setImageResource(R.drawable.ic_collect);
                    ivPushPay.setImageResource(R.drawable.ic_collect);
                    ivPushReturn.setImageResource(R.drawable.ic_collect);
                    ivPushActivity.setImageResource(R.drawable.ic_collect);
                    ivPushVoucher.setImageResource(R.drawable.ic_collect);
                    savePreferences(Constant.KEY_PUSH_ALL, false);
                } else {
                    isPushAll = true;
                    savePreferences(Constant.KEY_PUSH_ALL, true);
                    initData();
                }
                break;
            case R.id.rlPushPay:
                //消费确认
                if(isPushAll) {
                    if(isPushPay) {
                        isPushPay = false;
                        savePreferences(Constant.KEY_PUSH_PAY, false);
                    } else {
                        isPushPay = true;
                        savePreferences(Constant.KEY_PUSH_PAY, true);
                    }
                    initData();
                }
                break;
            case R.id.rlPushReturn:
                //退款提醒
                if(isPushAll) {
                    if(isPushReturn) {
                        isPushReturn = false;
                        savePreferences(Constant.KEY_PUSH_RETURN, false);
                    } else {
                        isPushReturn = true;
                        savePreferences(Constant.KEY_PUSH_RETURN, true);
                    }
                    initData();
                }
                break;
            case R.id.rlPushActivity:
                //活动消息
                if(isPushAll) {
                    if(isPushActivity) {
                        isPushActivity = false;
                        savePreferences(Constant.KEY_PUSH_ACTIVITY, false);
                    } else {
                        isPushActivity = true;
                        savePreferences(Constant.KEY_PUSH_ACTIVITY, true);
                    }
                    initData();
                }
                break;
            case R.id.rlPushVoucher:
                //获得礼券
                if(isPushAll) {
                    if(isPushVoucher) {
                        isPushVoucher = false;
                        savePreferences(Constant.KEY_PUSH_VOUCHER, false);
                    } else {
                        isPushVoucher = true;
                        savePreferences(Constant.KEY_PUSH_VOUCHER, true);
                    }
                    initData();
                }
                break;
        }
    }

    /** 本类存储至本地属性*/
    public void savePreferences(String key, boolean value){
        PreferencesUtils.putBoolean(context, key, value);
    }
}
