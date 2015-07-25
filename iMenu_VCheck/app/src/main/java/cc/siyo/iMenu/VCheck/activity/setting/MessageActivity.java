package cc.siyo.iMenu.VCheck.activity.setting;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import net.tsz.afinal.annotation.view.ViewInject;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.activity.BaseActivity;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/21 12:12.
 * Desc:消息中心
 */
public class MessageActivity extends BaseActivity {

    private static final String TAG = "MessageActivity";
    /** 列表*/
    @ViewInject(id = R.id.list_message)private ListView list_message;
    /** 头部*/
    @ViewInject(id = R.id.topbar)private TopBar topbar;
    /** 适配器*/
    /** 数据源*/

    @Override
    public int getContentView() {
        return R.layout.activity_message_center;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("我的消息");
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        View emptyView = LayoutInflater.from(context).inflate(R.layout.list_item_empty, null);
        list_message.setEmptyView(emptyView);
    }

    @Override
    public void initData() {

    }
}
