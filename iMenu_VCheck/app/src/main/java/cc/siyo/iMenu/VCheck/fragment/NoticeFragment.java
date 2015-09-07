package cc.siyo.iMenu.VCheck.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import java.io.Serializable;
import java.util.List;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.DetailActivity;
import cc.siyo.iMenu.VCheck.activity.MapActivity;
import cc.siyo.iMenu.VCheck.model.ArticleContent;
import cc.siyo.iMenu.VCheck.model.Store;
import cc.siyo.iMenu.VCheck.model.Tips;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.util.Util;

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
    /** 须知标题*/
    private TextView tv_notice_title;
    /** 须知内容*/
    private TextView tv_notice1;
    /** 商家电话layout*/
    private RelativeLayout rl_notice_tel;
    /** 商家电话*/
    private TextView tv_notice_tel;
    /** 微信客服账号点击复制*/
    private TextView tv_notice_weChat;
    /** 商家实体数据*/
    private Store store;
    /** 提示实体数据*/
    private Tips tips;
    private FinalBitmap finalBitmap;

    @Override
    public int getContentView() {
        context = getActivity();
        finalBitmap = FinalBitmap.create(context);
        finalBitmap.configLoadingImage(R.drawable.default_member);
        finalBitmap.configLoadfailImage(R.drawable.default_member);
        return R.layout.fram_notice;
    }

    @Override
    public void initView(View v) {
        iv_notice_icon = (ImageView) v.findViewById(R.id.iv_notice_icon);
        tv_notice_store_name = (TextView) v.findViewById(R.id.tv_notice_store_name);
        tv_notice_address = (TextView) v.findViewById(R.id.tv_notice_address);
        tv_notice_title = (TextView) v.findViewById(R.id.tv_notice_title);
        tv_notice1 = (TextView) v.findViewById(R.id.tv_notice1);
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
            tips = (Tips) getArguments().getSerializable("tips");
            finalBitmap.display(iv_notice_icon, store.icon_image.thumb);
            tv_notice_store_name.setText(store.store_name);
            tv_notice_address.setText(store.address);
            tv_notice_title.setText(tips.title);
            if(!StringUtils.isBlank(store.tel_1)){
                tv_notice_tel.setText(store.tel_1);
            }else{
                tv_notice_tel.setText(store.tel_2);
            }
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < tips.content.length; i++) {
                if(i + 1 == tips.content.length) {
                    stringBuffer.append("· " + tips.content[i]);
                } else {
                    stringBuffer.append("· " + tips.content[i] + "\n");
                }
            }
            tv_notice1.setText(stringBuffer);
        }
    }

    public static final NoticeFragment newInstance(Store store, Tips tips){
        NoticeFragment noticeFragment = new NoticeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("store", store);
        bundle.putSerializable("tips", tips);
        noticeFragment.setArguments(bundle);
        return noticeFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_notice_address:
                //商家地址
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("lng", store.longitude_num);
                intent.putExtra("lat", store.latitude_num);
                intent.putExtra("storeName", store.store_name);
                startActivity(intent);
                break;
            case R.id.rl_notice_tel:
                //商家电话
                Util.ShowTelDialog(getActivity(), store.tel_1, store.tel_1);
                break;
            case R.id.tv_notice_weChat:
                //微信客服复制，并弹出提示框
                Util.Copy(getActivity(), "知味精选限量美食", "已帮您复制微信号，可打开微信添加服务号");
                break;
        }
    }
}
