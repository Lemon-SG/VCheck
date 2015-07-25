package cc.siyo.iMenu.VCheck.activity.setting;

import android.view.View;

import net.tsz.afinal.annotation.view.ViewInject;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.BaseActivity;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/21 10:46.
 * Desc: 应用设置界面
 */
public class AppSetActivity extends BaseActivity {

    private static final String TAG = "AppSetActivity";
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;

    @Override
    public int getContentView() {
        return R.layout.activity_app_set;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("应用设置");
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }
}
