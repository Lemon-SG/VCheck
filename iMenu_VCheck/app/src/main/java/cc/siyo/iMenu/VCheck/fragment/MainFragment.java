package cc.siyo.iMenu.VCheck.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.siyo.iMenu.VCheck.MyApplication;
import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.DetailActivity;
import cc.siyo.iMenu.VCheck.adapter.MainAdapter;
import cc.siyo.iMenu.VCheck.model.API;
import cc.siyo.iMenu.VCheck.model.Article;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.util.StringUtils;
/**
 * Created by Lemon on 2015/4/29.
 * Desc:主页界面
 */
public class MainFragment extends BaseFragment{

    private static final String TAG = "MainFragment";
    private int page = Constant.PAGE;
    private int count = Constant.PAGE_SIZE;
    /** LIST VIEW*/
    private ListView store_list;
    /** Adapter*/
    private MainAdapter mainAdapter;
    /** Context*/
    private Context mContext;
    /** 数据源*/
    private List<Article> articleList;
    /** A FINAL 框架的HTTP请求工具 */
    private FinalHttp finalHttp;
    /** 封装参数的键值对 */
    private AjaxParams ajaxParams;

    @Override
    public int getContentView() {
        mContext = getActivity();
        return R.layout.fram_store;
    }

    @Override
    public void initView(View v) {
        store_list = (ListView) v.findViewById(R.id.store_list);
        store_list.addFooterView((LayoutInflater.from(mContext)).inflate(R.layout.list_item_footview, null));
        store_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        finalHttp = new FinalHttp();
//        UploadAdapter();
        mainAdapter = new MainAdapter(getActivity(), R.layout.list_item_main);
        store_list.setAdapter(mainAdapter);

        mainAdapter.getDataList().clear();
        mainAdapter.getDataList().addAll(getArticleList());
        mainAdapter.notifyDataSetChanged();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    closeProgressDialog();
                    if(msg.obj != null){
                        JSONStatus jsonStatus = (JSONStatus) msg.obj;
                        JSONObject data = jsonStatus.data;
                        if(data.optJSONArray("article_list") != null && data.optJSONArray("article_list").length() > 0){
                            articleList = new ArrayList<>();
                            for (int i = 0; i < data.optJSONArray("article_list").length(); i++) {
                                Article article = new Article().parse(data.optJSONArray("article_list").optJSONObject(i));
                                articleList.add(article);
                            }
                            mainAdapter.getDataList().clear();
                            mainAdapter.getDataList().addAll(articleList);
                            mainAdapter.notifyDataSetChanged();
                        }else{
                            prompt(getResources().getString(R.string.request_no_data));
                        }
                    }
                    break;
                case FAILURE:
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

    /** 获取产品列表请求*/
    private void UploadAdapter() {
        ajaxParams = new AjaxParams();
        ajaxParams.put("route", API.GET_PRODUCT_LIST);
        ajaxParams.put("token", token);
        ajaxParams.put("device_type", Constant.DEVICE_TYPE);
        ajaxParams.put("jsonText", makeJsonText());
        Log.e(TAG, Constant.REQUEST + API.GET_PRODUCT_LIST + "\n" + ajaxParams.toString());
        finalHttp.post(API.server, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                closeProgressDialog();
                prompt(getResources().getString(R.string.request_time_out));
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
                if (!StringUtils.isBlank(t)) {
                    Log.e(TAG, Constant.RESULT + API.GET_PRODUCT_LIST + "\n" + t.toString());
                    JSONStatus jsonStatus = BaseJSONData(t);
                    if (jsonStatus.isSuccess) {
                        handler.sendMessage(handler.obtainMessage(SUCCESS, BaseJSONData(t)));
                    } else {
                        handler.sendMessage(handler.obtainMessage(FAILURE, BaseJSONData(t)));
                    }
                } else {
                    prompt(getResources().getString(R.string.request_no_data));
                }
            }
        });
    }

    /** 请求数据封装*/
    private String makeJsonText() {
        JSONObject json = new JSONObject();
        try {
            json.put("filter_info", makeJsonText_filterInfo());
            json.put("pagination", makeJsonText_pagination());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * filter_info:{filter_value:搜索关键字+region_id:地区ID} |
     * @return json
     */
    private String makeJsonText_filterInfo() {
        JSONObject json = new JSONObject();
        try {
            json.put("filter_value", "");
            json.put("region_id", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /***
     * pagination:{page；页+count：数量}
     * @return json
     */
    private String makeJsonText_pagination() {
        JSONObject json = new JSONObject();
        try {
            json.put("page", page + "");
            json.put("count", count + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    //TODO 假数据
    /** 假数据封装*/
    private List<Article> getArticleList(){
        List<Article> articles = new ArrayList<>();
        String t = "{\"article_id\":\"1\",\"title\":\"在美式旧日氛围里喝一杯自酿啤酒 地下酒馆双人菜单\",\"sub_title\":\"风味烤物自酿啤酒 夏日宵夜首选\",\"summary\":\"地下酒馆空间不大，设计思路参考了1920年代颁布禁酒令后的美国地下酒馆。几百种进口啤酒，美味的下酒小吃，还有幽默风趣愿意和你喝一杯的店主。夏日已至，约上爱酒好友，来此共度漫漫长夜。双人烤肉啤酒菜单，ENJOY 独享69折\",\"article_date\":\"2015-05-26 09:47:25\",\"menu_info\":{\"menu_id\":\"00\"},\"store_info\":{\"store_id\":\"2222\",\"store_name\":\"赵锋的店-在线付\",\"address\":\"金花北路26号\",\"longitude_num\":\"108.899122\",\"latitude_num\":\"34.222339\",\"tel_1\":\"029-88372520\",\"tel_2\":\"18602951905\",\"per\":\"300.00\",\"icon_image\":{\"thumb\":\"http://218.244.158.175/imenu_test/image/data/store/2222/thumb/221ECE640_thumb.jpg\",\"source\":\"http://218.244.158.175/imenu_test/image/data/store/2222/221ECE640.jpg\"}},\"member_info\":{\"member_id\":\"2\",\"member_name\":\"122\",\"sex\":\"1\",\"email\":\"moxiso@163.com\",\"mobile\":\"18602951906\",\"icon_image\":{\"thumb\":\"http://218.244.158.175/imenu_test/image/data/member/2/icon/thumb/73D842AEF_thumb.jpg\",\"source\":\"http://218.244.158.175/imenu_test/image/data/member/2/icon/73D842AEF.jpg\"}},\"tips_info\":\"00\"}\");";
        try {
            JSONObject jsonObject = new JSONObject(t);
            articles.add(new Article().parse(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "TITLE ->" + articles.get(0).title);
        return  articles;
    }
}
