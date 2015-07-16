package cc.siyo.iMenu.VCheck.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

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
import cc.siyo.iMenu.VCheck.adapter.CollectAdapter;
import cc.siyo.iMenu.VCheck.adapter.OrderAdapter;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.model.MemberOrder;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.PromptDialog;
import cc.siyo.iMenu.VCheck.view.RefreshListView;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/6 15:57.
 * Desc:收藏列表
 */
public class CollectListActivity extends BaseActivity {

    private static final String TAG = "CollectListActivity";
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 列表*/
    @ViewInject(id = R.id.list_collect)private RefreshListView list_collect;
    /** 数据源*/
    private List<Article> articleList;
    /** 适配器*/
    private CollectAdapter collectAdapter;
    /** A FINAL框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;
    /** 获取收藏列表成功标石*/
    private static final int GET_COLLECT_LIST_SUCCESS = 100;
    /** 编辑收藏成功*/
    private static final int EDIT_COLLECT_SUCCESS = 300;
    /** 获取收藏列表失败标石*/
    private static final int GET_COLLECT_LIST_FALSE = 200;
    /** 提示对话框*/
    private PromptDialog promptDialog;
    /** 当前需要删除的菜品IID*/
    private String articleId = "";

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
        return R.layout.activity_collect_list;
    }

    @Override
    public void initView() {
        topbar = (TopBar)findViewById(R.id.topbar);
        topbar.settitleViewText("我收藏的礼遇");
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
        collectAdapter = new CollectAdapter(CollectListActivity.this, R.layout.list_item_collect);
        list_collect.setAdapter(collectAdapter);

        page = Constant.PAGE;
        finalHttp = new FinalHttp();
        isPull = true;
        UploadAdapter();

        list_collect.setOnLoadMoreListenter(new RefreshListView.OnLoadMoreListener() {

            public void onLoadMore() {
                isPull = false;
                if (doNotOver) {
                    page++;
                    UploadAdapter();
                } else {
                    list_collect.onLoadMoreComplete();
                    if (!isTip) {
                        isTip = true;
                        prompt("已经到底了");
                    }
                }
            }
        });
        list_collect.setOnRefreshListener(new RefreshListView.OnRefreshListener() {

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
                case GET_COLLECT_LIST_SUCCESS:
                    closeProgressDialog();
                    list_collect.onRefreshComplete();
                    list_collect.onLoadMoreComplete();
                    if(msg.obj != null) {
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        JSONArray article_list = data.optJSONArray("article_list");
                        if(article_list != null && article_list.length() > 0) {
                            articleList = new ArrayList<>();
                            for (int i = 0; i < article_list.length(); i++) {
                                Article article = new Article().parse(article_list.optJSONObject(i));
                                articleList.add(article);
                            }
                        }
                        if(isPull) {
                            collectAdapter.getDataList().clear();
                        }
                        collectAdapter.getDataList().addAll(articleList);
                        collectAdapter.notifyDataSetChanged();
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
                case EDIT_COLLECT_SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null) {
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        if(jsonStatus.isSuccess) {
                            if(collectAdapter.getDataList() != null && collectAdapter.getDataList().size() > 0) {
                                for (int i = 0; i < collectAdapter.getDataList().size(); i++) {
                                    if(collectAdapter.getDataList().get(i).article_id.equals(articleId)) {
                                        collectAdapter.getDataList().remove(i);
                                    }

                                }
                            }
                            collectAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case GET_COLLECT_LIST_FALSE:
                    closeProgressDialog();
                    list_collect.onRefreshComplete();
                    list_collect.onLoadMoreComplete();
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

    /** 获取收藏列表请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_COLLECTION_PRODUCT_LIST);
        ajaxParams.put("token", PreferencesUtils.getString(CollectListActivity.this, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.GET_COLLECTION_PRODUCT_LIST + "\n" + ajaxParams.toString());
        finalHttp.post(API.server,  ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                list_collect.onRefreshComplete();
                list_collect.onLoadMoreComplete();
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
                    Log.e(TAG, Constant.RESULT + API.GET_COLLECTION_PRODUCT_LIST + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(GET_COLLECT_LIST_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(GET_COLLECT_LIST_FALSE, BaseJSONData(t)));
                    }
                }else{
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 编辑收藏请求
     * */
    public void UploadAdapter(String articleId) {
        this.articleId = articleId;
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.EDIT_COLLECTION_PRODUCT);
        ajaxParams.put("token", PreferencesUtils.getString(CollectListActivity.this, Constant.KEY_TOKEN));
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText(articleId));
        Log.e(TAG, Constant.REQUEST + API.EDIT_COLLECTION_PRODUCT + "\n" + ajaxParams.toString());
        finalHttp.post(API.server,  ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                list_collect.onRefreshComplete();
                list_collect.onLoadMoreComplete();
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
                    Log.e(TAG, Constant.RESULT + API.EDIT_COLLECTION_PRODUCT + "\n" +  t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if(jsonStatus.isSuccess){
                        handler.sendMessage(handler.obtainMessage(EDIT_COLLECT_SUCCESS, BaseJSONData(t)));
                    }else{
                        handler.sendMessage(handler.obtainMessage(GET_COLLECT_LIST_FALSE, BaseJSONData(t)));
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
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("pagination", makeJsonPageText(page, pageSize));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * member_id	会员ID
     * order_id	订单ID
     * operator_type	操作类型(1-添加/2-删除/3-清空)
     * @return json
     */
    private String makeJsonText(String articleId) {
        JSONObject json = new JSONObject();
        JSONArray article_list = new JSONArray();
        JSONObject article_id = new JSONObject();
        try {
            json.put("member_id", PreferencesUtils.getString(context, Constant.KEY_MEMBER_ID));
            json.put("operator_type", "2");
            article_id.put("article_id", articleId);
            article_list.put(0, article_id);
            json.put("article_list", article_list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
