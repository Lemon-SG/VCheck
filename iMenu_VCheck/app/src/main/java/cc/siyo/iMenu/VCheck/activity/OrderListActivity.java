package cc.siyo.iMenu.VCheck.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.adapter.OrderAdapter;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.MemberOrder;
import cc.siyo.iMenu.VCheck.model.OrderInfo;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/6 15:57.
 * Desc:我的订单列表
 */
public class OrderListActivity extends BaseActivity {

    private static final String TAG = "OrderListActivity";
    private Context mContext;
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 列表*/
    @ViewInject(id = R.id.list_order)private ListView list_order;
    /** 数据源*/
    private List<MemberOrder> memberOrderList;
    /** 适配器*/
    private OrderAdapter orderAdapter;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 获取订单成功标石*/
    private static final int GET_ORDER_LIST_SUCCESS = 100;
    /** 获取订单失败标石*/
    private static final int GET_ORDER_LIST_FALSE = 200;

    @Override
    public int getContentView() {
        mContext = this;
        return R.layout.activity_order_list;
    }

    @Override
    public void initView() {
        topbar = (TopBar)findViewById(R.id.topbar);
        topbar.settitleViewText("我的订单");
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                //返回
                finish();
            }
        });
    }

    @Override
    public void initData() {
        finalHttp = new FinalHttp();
        orderAdapter = new OrderAdapter(OrderListActivity.this, R.layout.list_item_order);
        list_order.setAdapter(orderAdapter);
        UploadAdapter();
        list_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderId", orderAdapter.getDataList().get(position).order_info.order_id);
                startActivity(intent);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_ORDER_LIST_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null) {
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        JSONArray member_order_list = data.optJSONArray("member_order_list");
                        if(member_order_list != null && member_order_list.length() > 0) {
                            memberOrderList = new ArrayList<>();
                            for (int i = 0; i < member_order_list.length(); i++) {
                                MemberOrder memberOrder = new MemberOrder().parse(member_order_list.optJSONObject(i));
                                memberOrderList.add(memberOrder);
                            }
                        }
                        orderAdapter.getDataList().clear();
                        orderAdapter.getDataList().addAll(memberOrderList);
                        orderAdapter.notifyDataSetChanged();
                    }
                    break;
                case GET_ORDER_LIST_FALSE:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(!StringUtils.isBlank(jsonStatus.error_desc)){
                            prompt(jsonStatus.error_desc);
                        }else{
                            if(!StringUtils.isBlank(jsonStatus.error_code)){
                                prompt(getResources().getString(R.string.request_erro) + MyApplication.findErroDesc(jsonStatus.error_code));
                            }else{
                                prompt(getResources().getString(R.string.request_erro));
                            }
                        }
                    }
                    break;
            }
        }
    };

    /** 获取订单列表请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_ORDER_LIST);
        ajaxParams.put("token", PreferencesUtils.getString(OrderListActivity.this, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.GET_ORDER_LIST + "\n" + ajaxParams.toString());
        finalHttp.post(API.server,  ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog(getResources().getString(R.string.loading));
            }

            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(!StringUtils.isBlank(t)){
                    Log.e(TAG, Constant.RESULT + API.GET_ORDER_LIST + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(GET_ORDER_LIST_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(GET_ORDER_LIST_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /***
     * member_id	会员ID
     * order_type	订单类型
     * @return json
     */
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(mContext, Constant.KEY_MEMBER_ID));
//            json.put("order_type", "");
            json.put("pagination", makeJsonPageText(Constant.PAGE, Constant.PAGE_SIZE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
