package cc.siyo.iMenu.VCheck;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
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
import android.os.Handler;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cc.siyo.iMenu.VCheck.adapter.FragmentViewPagerAdapter;
import cc.siyo.iMenu.VCheck.activity.MineActivity;
import cc.siyo.iMenu.VCheck.adapter.RegionAdapter;
import cc.siyo.iMenu.VCheck.fragment.MainFragment;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.Region;
import cc.siyo.iMenu.VCheck.util.AnimationController;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Util;

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
    /** AFINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 记住登录状态成功标石*/
    private static final int LOGIN_TOKEN_SUCCESS = 100;
    /** 记住登录状态失败标石*/
    private static final int LOGIN_TOKEN_FALSE = 200;
    /** 记住登录状态成功标石*/
    private static final int GET_CITY_LIST_SUCCESS = 1000;
    /** 记住登录状态失败标石*/
    private static final int GET_CITY_LIST_FALSE = 2000;
    /** 城市列表*/
    private List<Region> regionList;
    private FinalDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        db = FinalDb.create(MainActivity.this, true);
        if(fragmentsList == null)
            fragmentsList = new ArrayList<Fragment>();
        fragmentsList.add(new MainFragment());
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
        iv_mine = (ImageView) findViewById(R.id.iv_mine);
        ll_choose_city = (LinearLayout) findViewById(R.id.ll_choose_city);
        tv_show_city = (TextView) findViewById(R.id.tv_show_city);
        iv_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转我的界面
                Intent intent = new Intent(MainActivity.this, MineActivity.class);
                startActivity(intent);
            }
        });
        ll_choose_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转选择城市界面
                initPopWindow();
            }
        });

        regionList = new ArrayList<>();

        finalHttp = new FinalHttp();
        UploadAdapter_GetCityList();
        doUploadLoginWithToken();
        //TODO 定位当前所在城市，如定位不到，打开popWindow，手动选择城市，暂时默认西安
        tv_show_city.setText("西安");
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_TOKEN_SUCCESS:
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        String member_id = data.optString("member_id");
                        String token = data.optString("token");
                        savePreferences(member_id, token);
                    }
                    break;
                case GET_CITY_LIST_SUCCESS:
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(data != null){
                            JSONArray jsonArray = data.optJSONArray("region_list");
                            if(jsonArray != null && jsonArray.length() > 0){
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Region region = new Region().parse(jsonArray.optJSONObject(i));
                                    if(db.findById(region.region_id, Region.class) != null){
                                        db.update(region);
                                    }else{
                                        db.save(region);
                                    }
                                }
                                //打印数据库表
                                regionList = db.findAll(Region.class);
                                for (int i = 0; i < regionList.size(); i++) {
                                    Log.e(TAG, "region" + i + "=" + regionList.get(i).region_name);
                                }
                            }
                        }
                    }
                    break;
                case GET_CITY_LIST_FALSE:
                    if(msg.obj != null){
                        regionList = db.findAll(Region.class);
                    }
                    break;
                case LOGIN_TOKEN_FALSE:
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        Util.println(TAG, jsonStatus.error_desc);
                        PreferencesUtils.clear(getApplicationContext());
                    }
                    break;
            }
        }
    };

    /** 记住登录状态请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.LOGIN_WITH_TOKEN);
        ajaxParams.put("token", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.LOGIN_WITH_TOKEN + "\n:" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.request_time_out), Toast.LENGTH_SHORT).show();
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e(TAG, Constant.RESULT +API.LOGIN_WITH_TOKEN + "\n" + t.toString());
                if(!StringUtils.isBlank(t)){
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(LOGIN_TOKEN_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(LOGIN_TOKEN_FALSE, BaseJSONData(t)));
                    }
                }
            }
        });
    }

    /** 获取城市列表请求*/
    private void UploadAdapter_GetCityList() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_REGION_LIST);
        ajaxParams.put("token", "");
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        Log.e(TAG, Constant.REQUEST + API.GET_REGION_LIST + "\n:" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.request_time_out), Toast.LENGTH_SHORT).show();
                regionList = db.findAll(Region.class);
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e(TAG, Constant.RESULT +API.GET_REGION_LIST + "\n" + t.toString());
                if(!StringUtils.isBlank(t)){
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(GET_CITY_LIST_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(GET_CITY_LIST_FALSE, BaseJSONData(t)));
                    }
                }
            }
        });
    }

    /** 初始化popWindow*/
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
            animationController.fadeIn(list_city, 2000, 0);

            final RegionAdapter regionAdapter = new RegionAdapter(this, R.layout.list_item_textview);
            list_city.setAdapter(regionAdapter);
            regionAdapter.getDataList().clear();
            regionAdapter.getDataList().addAll(regionList);
            regionAdapter.notifyDataSetChanged();

            list_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    popupWindow.dismiss();
                    tv_show_city.setText(regionAdapter.getDataList().get(position).getRegion_name());
                    //TODO 切换城市后更新数据
                    mAdapter.notifyDataSetChanged();
                }
            });
        }else{
            if(popupWindow.isShowing()){
                popupWindow.dismiss();
            }else{
                popupWindow.showAsDropDown(ll_choose_city);
                animationController.fadeIn(list_city, 2000, 0);
            }
        }
    }

    /** 判断是否要去请求记住登录状态，否则清空登录信息*/
    private void doUploadLoginWithToken(){
        if (!StringUtils.isBlank(PreferencesUtils.getString(getApplicationContext(), Constant.KEY_TOKEN))
                && !StringUtils.isBlank(PreferencesUtils.getString(getApplicationContext(), Constant.KEY_MEMBER_ID))) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    UploadAdapter();
                }
            });
        } else {
            PreferencesUtils.clear(getApplicationContext());
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

    /***
     * 记住登录状态
     * member_id
     * @return
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(getApplicationContext(), Constant.KEY_MEMBER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /** 本类存储至本地属性*/
    public void savePreferences(String sellerId, String token){
        PreferencesUtils.putString(this, Constant.KEY_TOKEN, token);
        PreferencesUtils.putString(this, Constant.KEY_MEMBER_ID, sellerId);
    }

    /** 公共解析返回数据成功与否
     t:{
     "status": {
     "succeed": "0",
     "error_code": "2001",
     "error_desc": "用户名或密码错误"
     }
     }
     t:{
     "status": {
     "succeed": "1"
     },
     "data": {
     "seller_id": "1",
     "token": "8f0226b243cb63280a069412c121c66d"
     }
     }
     * */
    public JSONStatus BaseJSONData(String t){
        JSONStatus jsonStaus = new JSONStatus();
        try {
            JSONObject obj = new JSONObject(t);
            if(obj != null && obj.length() > 0){
                jsonStaus = new JSONStatus().parse(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStaus;
    }
}
