package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.DetailFragmentViewPagerAdapter;
import cc.siyo.iMenu.VCheck.fragment.LightspotFragment;
import cc.siyo.iMenu.VCheck.fragment.MenuFragment;
import cc.siyo.iMenu.VCheck.fragment.NoticeFragment;
import cc.siyo.iMenu.VCheck.model.Share;
import cc.siyo.iMenu.VCheck.view.TopBar;
import cc.siyo.iMenu.VCheck.view.viewpager_indicator.TabPageIndicator;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * Created by Lemon on 2015/5/6.
 * Desc:详情页面
 */
public class DetailActivity extends FragmentActivity{

    private static final String TAG = "DetailActivity";
    /** 头部*/
    private TopBar topbar;
    /** ViewPager控件*/
    private ViewPager mPager;
    /** 碎片viewPager适配器*/
    private DetailFragmentViewPagerAdapter mAdapter;
    /** 碎片集合数据源*/
    private List<Fragment> fragmentsList;
    /** 提交购买按钮*/
    private TextView tv_detail_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);
        initView();
        initData();
    }

    public void initView() {
        tv_detail_submit = (TextView) findViewById(R.id.tv_detail_submit);
        topbar = (TopBar) findViewById(R.id.topbar);
        topbar.settitleViewText("礼遇详情");
        topbar.setButtonImage(TopBar.RIGHT_BUTTON, R.drawable.abc_ic_menu_share_mtrl_alpha);
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //返回
                finish();
            }
        });
        topbar.setRightButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //分享
                Share share = new Share();
                share.content = "测试分享";
                ShareSDK.initSDK(DetailActivity.this);
                Platform sina = ShareSDK.getPlatform(DetailActivity.this, SinaWeibo.NAME);
                SinaWeibo.ShareParams sinaParams = new SinaWeibo.ShareParams();
                sinaParams.setTitle(share.title);
                sinaParams.setText(share.content);
                sinaParams.setSite(share.description);
                sinaParams.setSiteUrl(share.link);
                sinaParams.setImagePath(share.imagePath);
                sina.setPlatformActionListener(new SinaPlatformActionListener());
                sina.share(sinaParams);
            }
        });

        if(fragmentsList == null)
            fragmentsList = new ArrayList<Fragment>();
        fragmentsList.add(new LightspotFragment());
        fragmentsList.add(new MenuFragment());
        fragmentsList.add(new NoticeFragment());
        /** 只留一个分类,如需多个，需重新增加新的Fragment，FragmentViewPagerAdapter进行加入标题数组即可*/

        mAdapter = new DetailFragmentViewPagerAdapter(getApplicationContext(), this.getSupportFragmentManager(), fragmentsList);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        /** 只留一个分类*/
        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
    }

    private void initData(){
        tv_detail_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交购买信息
                Intent intent = new Intent(getApplicationContext(), OrderWriteActivity.class);
                startActivity(intent);
            }
        });
    }

    /** 新浪回调监听*/
    private class SinaPlatformActionListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Log.e(TAG, "新浪回调onComplete ->");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.e(TAG, "新浪回调onError ->");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Log.e(TAG, "新浪回调onCancel ->");
        }
    }
}
