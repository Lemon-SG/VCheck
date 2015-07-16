package cc.siyo.iMenu.VCheck.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cc.siyo.iMenu.VCheck.R;
import cc.siyo.iMenu.VCheck.model.Constant;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);

		api.handleIntent(getIntent(), this);
	}

    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.e(TAG + "onPayFinish, errCode->", String.valueOf(resp.errCode));
        if(resp.errCode != -1) {
            Constant.isWeChetPay = true;
            finish();
        }
//        showProgressDialog("正在获取支付结果..");
//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage(getString(R.string.pay_result_callback_msg) + resp.errStr +";code=" + String.valueOf(resp.errCode));
//			builder.show();
//		}
	}
}