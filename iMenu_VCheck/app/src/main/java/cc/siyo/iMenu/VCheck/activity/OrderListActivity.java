package cc.siyo.iMenu.VCheck.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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
import cc.siyo.iMenu.VCheck.view.PromptDialog;
import cc.siyo.iMenu.VCheck.view.RefreshListView;
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
    @ViewInject(id = R.id.list_order)private RefreshListView list_order;
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
    private static final int EDIT_ORDER_SUCCESS = 300;
    /** 获取订单失败标石*/
    private static final int GET_ORDER_LIST_FALSE = 200;
    /** 当前需要删除的订单IID*/
    private String orderId = "";

    /** 是否到最后一页*/
    boolean doNotOver = true;
    /** 是否已经提醒过一遍*/
    boolean isTip = false;
    /** 是否是下拉刷新，清空数据*/
    private boolean isPull = false;
    private int page = Constant.PAGE;
    private int pageSize = Constant.PAGE_SIZE;

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
                finish();//返回
            }
        });
    }

    @Override
    public void initData() {
        page = Constant.PAGE;
        finalHttp = new FinalHttp();
        isPull = true;
        UploadAdapter();

        orderAdapter = new OrderAdapter(OrderListActivity.this, R.layout.list_item_order);
        list_order.addFooterView((LayoutInflater.from(mContext)).inflate(R.layout.list_item_footview, null));
        list_order.setAdapter(orderAdapter);
        list_order.setOnLoadMoreListenter(new RefreshListView.OnLoadMoreListener() {

            public void onLoadMore() {
                isPull = false;
                if (doNotOver) {
                    page++;
                    UploadAdapter();
                } else {
                    list_order.onLoadMoreComplete();
                    if (!isTip) {
                        isTip = true;
                        prompt("已经到底了");
                    }
                }
            }
        });
        list_order.setOnRefreshListener(new RefreshListView.OnRefreshListener() {

            public void onRefresh() {
                page = Constant.PAGE;
                isPull = true;
                UploadAdapter();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_ORDER_LIST_SUCCESS:
//                    closeProgressDialog();
                    list_order.onRefreshComplete();
                    list_order.onLoadMoreComplete();
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
                            if(isPull) {
                                orderAdapter.getDataList().clear();
                            }
                            orderAdapter.getDataList().addAll(memberOrderList);
                            orderAdapter.notifyDataSetChanged();
                        }
                        if(jsonStatus.pageInfo != null) {
                            String more = jsonStatus.pageInfo.more;
                            if(more.equals("1")) {
                                //有下一页
                                doNotOver = true;
                            } else {
                                //最后一页
                                doNotOver = false;
                            }
                        }
                    }
                    break;
                case EDIT_ORDER_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null) {
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess) {
                            if(orderAdapter.getDataList() != null && orderAdapter.getDataList().size() > 0) {
                                for (int i = 0; i < orderAdapter.getDataList().size(); i++) {
                                    if(orderAdapter.getDataList().get(i).order_info.order_id.equals(orderId)) {
                                        orderAdapter.getDataList().remove(i);
                                    }

                                }
                            }
                            orderAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case GET_ORDER_LIST_FALSE:
                    closeProgressDialog();
                    list_order.onRefreshComplete();
                    list_order.onLoadMoreComplete();
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
                list_order.onRefreshComplete();
                list_order.onLoadMoreComplete();
                prompt("无网络");
                System.out.println("errorNo:" + errorNo + ",strMsg:" + strMsg);
            }

            @Override
            public void onStart() {
                super.onStart();
//                showProgressDialog(getResources().getString(R.string.loading));
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

    /** 编辑订单请求
     * */
    public void UploadAdapter(String orderId) {
        this.orderId = orderId;
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EDIT_ORDER);
        ajaxParams.put("token", PreferencesUtils.getString(OrderListActivity.this, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText(orderId));
        Log.e(TAG, Constant.REQUEST + API.EDIT_ORDER + "\n" + ajaxParams.toString());
        finalHttp.post(API.server,  ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                list_order.onRefreshComplete();
                list_order.onLoadMoreComplete();
                prompt("无网络");
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
                    Log.e(TAG, Constant.RESULT + API.EDIT_ORDER + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(EDIT_ORDER_SUCCESS, BaseJSONData(t)));
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
            json.put("pagination", makeJsonPageText(page, pageSize));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * member_id	会员ID
     * order_id	订单ID
     * operator_type	操作类型(1-删除订单)
     * @return json
     */
    private String makeJsonText(String orderId) {
        JSONObject json = new JSONObject();
        Log.e("makeJsonText", orderId);
        try {
            json.put("member_id", PreferencesUtils.getString(mContext, Constant.KEY_MEMBER_ID));
            json.put("operator_type", "1");
            json.put("order_id", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "resultCode ->" + Constant.RESULT_CODE_ORDER_DETAIL);
        if(requestCode == Constant.RESQUEST_CODE) {
            switch (resultCode) {
                case Constant.RESULT_CODE_LOGIN:
                case Constant.RESULT_CODE_ORDER_DETAIL:
                    Log.e(TAG, "resultCode ->" + resultCode);
                    //返回当前页面，需要加载标石
                    UploadAdapter();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(StringUtils.isBlank(PreferencesUtils.getString(context, Constant.KEY_TOKEN))
                && StringUtils.isBlank(PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID))) {
            //未登录状态
            startActivityForResult(new Intent(context, LoginActivity.class), Constant.RESQUEST_CODE);
        }
    }
}
