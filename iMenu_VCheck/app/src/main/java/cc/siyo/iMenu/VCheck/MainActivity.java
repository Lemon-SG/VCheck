package cc.siyo.iMenu.VCheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cc.siyo.iMenu.VCheck.activity.CollectListActivity;
import cc.siyo.iMenu.VCheck.activity.DetailActivity;
import cc.siyo.iMenu.VCheck.activity.MineActivity;
import cc.siyo.iMenu.VCheck.activity.OrderDetailActivity;
import cc.siyo.iMenu.VCheck.activity.OrderListActivity;
import cc.siyo.iMenu.VCheck.activity.VoucherListActivity;
import cc.siyo.iMenu.VCheck.activity.WebViewActivity;
import cc.siyo.iMenu.VCheck.activity.setting.MessageActivity;
import cc.siyo.iMenu.VCheck.adapter.FragmentViewPagerAdapter;
import cc.siyo.iMenu.VCheck.adapter.RegionAdapter;
import cc.siyo.iMenu.VCheck.fragment.MainFragment;
import cc.siyo.iMenu.VCheck.http.LHttpLib;
import cc.siyo.iMenu.VCheck.http.LHttpResponseHandler;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.LinkPushParams;
import cc.siyo.iMenu.VCheck.model.Region;
import cc.siyo.iMenu.VCheck.server.LocationSvc;
import cc.siyo.iMenu.VCheck.util.AnimationController;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    /** 碎片viewPager适配器*/
    private FragmentViewPagerAdapter mAdapter;
    /** 碎片集合数据源*/
    private List<Fragment> fragmentsList;
    /** 会员按钮*/
    private ImageView iv_mine;
    /** 选择城市按钮*/
    private LinearLayout ll_choose_city;
    /** 显示当前城市名称*/
    private TextView tv_show_city;
    /** 城市名称*/
    private ListView list_city;
    private PopupWindow popupWindow;
    /** 动画控制类*/
    private AnimationController animationController;
    /** 城市列表*/
    private List<Region> regionList;
    private FinalDb db;
    private Handler handler = new Handler() {};
    private Region region;
    LocationBroadcastReceiver locationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        db = FinalDb.create(MainActivity.this, true);
        region = db.findById(29, Region.class);

        initView();
        if(fragmentsList == null)
            fragmentsList = new ArrayList<Fragment>();
        fragmentsList.add(new MainFragment().newInstance(region));
        /** 只留一个分类,如需多个，需重新增加新的Fragment，FragmentViewPagerAdapter进行加入标题数组即可*/
        //        fragmentsList.add(new StoreFragment());
        //        fragmentsList.add(new DynamicFragment());
        //        fragmentsList.add(new SearchFragment());
        mAdapter = new FragmentViewPagerAdapter(this.getSupportFragmentManager(), fragmentsList);
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(mAdapter);
        /** 只留一个分类*/
        //        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        //        indicator.setViewPager(pager);
        animationController = new AnimationController();

        if(getIntent().getExtras() != null
                && getIntent().getExtras().getSerializable("linkPushParams") != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switchPage(MainActivity.this, (LinkPushParams) getIntent().getExtras().getSerializable("linkPushParams"));
                }
            }, 2000);
        }

        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.LOCATION_ACTION);
        locationBroadcastReceiver  = new LocationBroadcastReceiver();
        this.registerReceiver(locationBroadcastReceiver, filter);

        // 启动服务
        Intent intent = new Intent();
        intent.setClass(this, LocationSvc.class);
        startService(intent);
    }

    private void initView() {
        tv_show_city = (TextView) findViewById(R.id.tv_show_city);
        ll_choose_city = (LinearLayout) findViewById(R.id.ll_choose_city);
        iv_mine = (ImageView) findViewById(R.id.iv_mine);

        tv_show_city.setText("定位");
        regionList = new ArrayList<>();
        regionList = db.findAll(Region.class);
        //TODO 定位当前所在城市，如定位不到，打开popWindow，手动选择城市，暂时默认西安
        tv_show_city.setText(region.region_name);


        ll_choose_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择城市界面
                initPopWindow();
            }
        });
        iv_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转我的界面
                Intent intent = new Intent(MainActivity.this, MineActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initPopWindow(){
        if(popupWindow == null){
            popupWindow = new PopupWindow();
            View view = (LayoutInflater.from(MainActivity.this)).inflate(R.layout.layout_choose_city, null);
            popupWindow.setContentView(view);
            list_city = (ListView) view.findViewById(R.id.list_city);
            setPopupWindowContent(view);
            // 使其聚集
            popupWindow.setFocusable(true);
            // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setWindowLayoutMode(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            popupWindow.showAsDropDown(ll_choose_city);
//            animationController.fadeIn(list_city, 2000, 0);

            final RegionAdapter regionAdapter = new RegionAdapter(this, R.layout.list_item_textview);
            list_city.setAdapter(regionAdapter);
            regionAdapter.getDataList().clear();
            regionAdapter.getDataList().addAll(regionList);
            regionAdapter.notifyDataSetChanged();

            list_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(regionAdapter.getDataList().get(position).is_open.equals("1")) {
                        //开通状态
                        popupWindow.dismiss();
                        tv_show_city.setText(regionAdapter.getDataList().get(position).getRegion_name());
                        //TODO 切换城市后更新数据
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }else{
            if(popupWindow.isShowing()){
                popupWindow.dismiss();
            } else{
                popupWindow.showAsDropDown(ll_choose_city);
//                animationController.fadeIn(list_city, 2000, 0);
            }
        }
    }

    /** 加载popWindow内容及监听*/
    private void setPopupWindowContent(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭popWindow
                if(popupWindow != null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
    }

    private boolean isQuit = false;
    private Timer timer = new Timer();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return Key_Down(keyCode, event);
    }

    public boolean Key_Down(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isQuit == false) {
                isQuit = true;
                Toast.makeText(getBaseContext(), "再按一次返回键退出程序",
                        Toast.LENGTH_SHORT).show();
                TimerTask task = null;
                task = new TimerTask() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                };
                timer.schedule(task, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
        return isQuit;
    }

    /** 根据参数进行跳转*/
    private void switchPage(Context context, LinkPushParams linkPushParams) {
        if(linkPushParams.link_route.equals(Constant.LINK_WEB)) {
            //打开网页链接
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(Constant.INTENT_WEB_URL, linkPushParams.link_value);
            intent.putExtra(Constant.INTENT_WEB_NAME, Constant.INTENT_WEB_NAME_WEB);
            startActivity(intent);
        }
        if(linkPushParams.link_route.equals(Constant.LINK_HOME)) {
            //打开首页，不做操作
            startActivity(new Intent(context, MainActivity.class));
        }
        if(linkPushParams.link_route.equals(Constant.LINK_ARTICLE)) {
            //打开文章详情,传递ID
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(Constant.INTENT_ARTICLE_ID, linkPushParams.id);
            startActivity(intent);
        }
        if(linkPushParams.link_route.equals(Constant.LINK_MEMBER)) {//打开用户中心
            startActivity(new Intent(context, MineActivity.class));
        }
        if(linkPushParams.link_route.equals(Constant.LINK_MESSAGE)) {//打开消息列表
            startActivity(new Intent(context, MessageActivity.class));
        }
        if(linkPushParams.link_route.equals(Constant.LINK_COLLECTION)) {//打开收藏列表
            startActivity(new Intent(context, CollectListActivity.class));
        }
        if(linkPushParams.link_route.equals(Constant.LINK_ORDER_LIST)) {//打开订单列表
            startActivity(new Intent(context, OrderListActivity.class));
        }
        if(linkPushParams.link_route.equals(Constant.LINK_ORDER_DETAIL)) {//打开订单详情,传递ID
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", linkPushParams.id);
            startActivity(intent);
        }
        if(linkPushParams.link_route.equals(Constant.LINK_VOUCHER)) {//打开礼券列表
            startActivity(new Intent(context, VoucherListActivity.class));
        }
    }

    public class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Constant.LOCATION_ACTION)) return;
            String locationInfo = intent.getStringExtra(Constant.LOCATION);
            Log.e(TAG, "locationInfo->" + locationInfo);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationBroadcastReceiver);// 不需要时注销
    }
}
