package cc.siyo.iMenu.VCheck.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.ArticleContent;
import cc.siyo.iMenu.VCheck.model.Store;
import cc.siyo.iMenu.VCheck.util.StringUtils;
/**
 * Created by Lemon on 2015/5/6.
 * Desc:须知界面
 */
public class NoticeFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "NoticeFragment";
    private Context context;
    /** 商家图片*/
    private ImageView iv_notice_icon;
    /** 商家名称*/
    private TextView tv_notice_store_name;
    /** 商家地址layout*/
    private RelativeLayout rl_notice_address;
    /** 商家地址*/
    private TextView tv_notice_address;
    /** 商家电话layout*/
    private RelativeLayout rl_notice_tel;
    /** 商家电话*/
    private TextView tv_notice_tel;
    /** 微信客服账号点击复制*/
    private TextView tv_notice_weChat;
    /** 商家实体数据*/
    private Store store;

    @Override
    public int getContentView() {
        context = getActivity();
        return R.layout.fram_notice;
    }

    @Override
    public void initView(View v) {
        iv_notice_icon = (ImageView) v.findViewById(R.id.iv_notice_icon);
        tv_notice_store_name = (TextView) v.findViewById(R.id.tv_notice_store_name);
        tv_notice_address = (TextView) v.findViewById(R.id.tv_notice_address);
        tv_notice_tel = (TextView) v.findViewById(R.id.tv_notice_tel);
        tv_notice_weChat = (TextView) v.findViewById(R.id.tv_notice_weChat);
        tv_notice_weChat.setOnClickListener(this);
        rl_notice_address = (RelativeLayout) v.findViewById(R.id.rl_notice_address);
        rl_notice_address.setOnClickListener(this);
        rl_notice_tel = (RelativeLayout) v.findViewById(R.id.rl_notice_tel);
        rl_notice_tel.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if(getArguments() != null){
            store = (Store) getArguments().getSerializable("store");
            tv_notice_store_name.setText(store.store_name);
            tv_notice_address.setText(store.address);
            if(!StringUtils.isBlank(store.tel_1)){
                tv_notice_tel.setText(store.tel_1);
            }else{
                tv_notice_tel.setText(store.tel_2);
            }
        }
    }

    public static final NoticeFragment newInstance(Store store){
        NoticeFragment noticeFragment = new NoticeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("store", store);
        noticeFragment.setArguments(bundle);
        return noticeFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_notice_address:
                //商家地址

                break;
            case R.id.rl_notice_tel:
                //商家电话

                break;
            case R.id.tv_notice_weChat:
                //微信客服复制，并弹出提示框

                break;
        }
    }
}
