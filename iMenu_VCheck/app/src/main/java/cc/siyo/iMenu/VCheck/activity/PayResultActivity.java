package cc.siyo.iMenu.VCheck.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import net.tsz.afinal.annotation.view.ViewInject;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.OrderInfo;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.TopBar;

/**
 * Created by Lemon on 2015/7/9 14:48.
 * Desc:支付结果返回页面
 */
public class PayResultActivity extends BaseActivity {

    /** 头部*/
    @ViewInject(id = R.id.topBar)private TopBar topbar;
    /** 菜品名称*/
    @ViewInject(id = R.id.tv_menu_name)private TextView tv_menu_name;
    /** 菜品价格*/
    @ViewInject(id = R.id.tv_price)private TextView tv_price;
    /** 订单号码*/
    @ViewInject(id = R.id.tv_order_number)private TextView tv_order_number;
    /** 查看订单按钮*/
    @ViewInject(id = R.id.tv_into_order)private TextView tv_into_order;
    /** 继续浏览*/
    @ViewInject(id = R.id.tv_go_look)private TextView tv_go_look;
    private static final String TAG = "PayResultActivity";

    @Override
    public int getContentView() {
        return R.layout.activity_pay_result;
    }

    @Override
    public void initView() {
        topbar.settitleViewText("购买成功");
        topbar.setHiddenButton(TopBar.RIGHT_BUTTON);
        topbar.setLeftButtonOnClickListener(new TopBar.ButtonOnClick() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        final OrderInfo orderInfo = (OrderInfo) getIntent().getExtras().getSerializable("orderInfo");

        tv_menu_name.setText(orderInfo.menu.menu_name);
        if(!StringUtils.isBlank(orderInfo.totalPrice.special_price)) {
            tv_price.setText(orderInfo.totalPrice.special_price + orderInfo.totalPrice.price_unit);
        }else {
            tv_price.setText(orderInfo.totalPrice.original_price + orderInfo.totalPrice.price_unit);
        }
        tv_order_number.setText(orderInfo.order_no);
        tv_into_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看订单
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderId", orderInfo.order_id);
                startActivity(intent);
                finish();
            }
        });
        tv_go_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //继续浏览
                finish();
            }
        });
    }
}
