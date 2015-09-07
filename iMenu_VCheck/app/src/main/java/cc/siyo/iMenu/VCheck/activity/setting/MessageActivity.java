package cc.siyo.iMenu.VCheck.activity.setting;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.tsz.afinal.annotation.view.ViewInject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.BaseActivity;
import cc.siyo.iMenu.VCheck.activity.CollectListActivity;
import cc.siyo.iMenu.VCheck.activity.DetailActivity;
import cc.siyo.iMenu.VCheck.activity.LoginActivity;
import cc.siyo.iMenu.VCheck.activity.MineActivity;
import cc.siyo.iMenu.VCheck.activity.OrderDetailActivity;
import cc.siyo.iMenu.VCheck.activity.OrderListActivity;
import cc.siyo.iMenu.VCheck.activity.VoucherListActivity;
import cc.siyo.iMenu.VCheck.adapter.MessageAdapter;
import cc.siyo.iMenu.VCheck.http.LHttpLib;
import cc.siyo.iMenu.VCheck.http.LHttpResponseHandler;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.LinkPushParams;
import cc.siyo.iMenu.VCheck.model.MemberOrder;
import cc.siyo.iMenu.VCheck.model.Message;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.RefreshListView;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/21 12:12.
 * Desc:消息中心
 */
public class MessageActivity extends BaseActivity {

    private static final String TAG = "MessageActivity";
    /** 列表*/
    @ViewInject(id = R.id.list_message)private RefreshListView list_message;
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    @ViewInject(id = R.id.tvListEmpty)private TextView tvListEmpty;
    /** 适配器*/
    private MessageAdapter messageAdapter;
    /** 数据源*/
    private List<Message> messageList;
    /** 是否到最后一页*/
    boolean doNotOver = true;
    /** 是否已经提醒过一遍*/
    boolean isTip = false;
    /** 是否是下拉刷新，清空数据*/
    private boolean isPull = false;
    private int page = Constant.PAGE;

    @Override
    public int getContentView() {
        return R.layout.activity_message_center;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("信息中心");
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        list_message.addFooterView((LayoutInflater.from(context)).inflate(R.layout.list_item_footview, null));
        messageAdapter = new MessageAdapter(MessageActivity.this, R.layout.list_item_message);
        list_message.setAdapter(messageAdapter);
        list_message.setOnLoadMoreListenter(new RefreshListView.OnLoadMoreListener() {

            public void onLoadMore() {
                isPull = false;
                if (doNotOver) {
                    page++;
                    UploadAdapter(page);
                } else {
                    list_message.onLoadMoreComplete();
                    if (!isTip) {
                        isTip = true;
                        prompt("已经到底了");
                    }
                }
            }
        });
        list_message.setOnRefreshListener(new RefreshListView.OnRefreshListener() {

            public void onRefresh() {
                page = Constant.PAGE;
                isPull = true;
                UploadAdapter(page);
            }
        });
        list_message.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchPage(messageAdapter.getDataList().get(position - 1).link_info);
            }
        });
        UploadAdapter(page);
    }

    /** 根据页数请求获取消息列表*/
    private void UploadAdapter(int page) {
        LHttpLib.getMessageList(context, PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID),
                "0", makeJsonPageText(page, Constant.PAGE_SIZE), new LHttpResponseHandler() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }

                    @Override
                    public void onSuccess(JSONStatus jsonStatus) {
                        list_message.onRefreshComplete();
                        list_message.onLoadMoreComplete();
                        if (jsonStatus.isSuccess) {
                            JSONObject data = jsonStatus.data;
                            JSONArray message_list = data.optJSONArray("message_list");
                            if (message_list != null && message_list.length() > 0) {
                                messageList = new ArrayList<>();
                                for (int i = 0; i < message_list.length(); i++) {
                                    Message message = new Message().parse(message_list.optJSONObject(i));
                                    messageList.add(message);
                                }
                                if (isPull) {
                                    messageAdapter.getDataList().clear();
                                }
                                messageAdapter.getDataList().addAll(messageList);
                                messageAdapter.notifyDataSetChanged();
                                tvListEmpty.setVisibility(View.GONE);
                            } else {
                                tvListEmpty.setVisibility(View.VISIBLE);
                            }
                            if (jsonStatus.pageInfo != null) {
                                String more = jsonStatus.pageInfo.more;
                                if (more.equals("1")) {
                                    //有下一页
                                    doNotOver = true;
                                } else {
                                    //最后一页
                                    doNotOver = false;
                                }
                            }
                        } else {
                            if (!StringUtils.isBlank(jsonStatus.error_desc)) {
                                prompt(jsonStatus.error_desc);
                            } else {
                                if (!StringUtils.isBlank(jsonStatus.error_code)) {
                                    prompt(getResources().getString(R.string.request_erro) + MyApplication.findErroDesc(jsonStatus.error_code));
                                } else {
                                    prompt(getResources().getString(R.string.request_erro));
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        list_message.onRefreshComplete();
                        list_message.onLoadMoreComplete();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.RESQUEST_CODE) {
            switch (resultCode) {
                case Constant.RESULT_CODE_LOGIN:
                    Log.e(TAG, "resultCode ->" + resultCode);

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

    /** 根据参数进行跳转*/
    private void switchPage(LinkPushParams linkPushParams) {
        if(linkPushParams.link_route.equals(Constant.LINK_WEB)) {
            //打开网页链接
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(linkPushParams.link_value));
            startActivity(intent);
        }
        if(linkPushParams.link_route.equals(Constant.LINK_HOME)) {
            //打开首页，不做操作
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
}
