package cc.siyo.iMenu.VCheck.activity.setting;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.annotation.view.ViewInject;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.BaseActivity;
import cc.siyo.iMenu.VCheck.activity.VideoActivity;
import cc.siyo.iMenu.VCheck.util.PackageUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/17 15:21.
 * Desc:关于界面
 */
public class AboutUsActivity extends BaseActivity {

    private static final String TAG = "AboutUsActivity";
    @ViewInject(id = R.id.tvVer)private TextView tvVer;
    @ViewInject(id = R.id.iv_movie)private ImageView iv_movie;
    @ViewInject(id = R.id.topBar)private TopBar topBar;

    @Override
    public int getContentView() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        topBar.settitleViewText("关于我们");
        topBar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvVer.setText("Ver." + PackageUtils.getAppVersionName(context));
    }

    @Override
    public void initData() {
        iv_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, VideoActivity.class));
            }
        });
    }
}
