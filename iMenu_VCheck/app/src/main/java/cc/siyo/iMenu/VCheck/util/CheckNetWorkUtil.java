package cc.siyo.iMenu.VCheck.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * 检查当前网络
 * 
 * @author ShangGuan
 * 
 */
public class CheckNetWorkUtil {
	/**
	 * 检查当前网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isNetwork(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();//可用网络
			}
		}
		return false;
	}
	
	/**
	 * 检查是否有sd_card
	 * **/
    public static boolean sdcardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
	
    /** */
	public void onReceive(Context context, Intent intent) {
		Log.e("CheckNetWorkUtil", "网络状态改变");
		boolean success = false;
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取网络连接状态
		// State state = connManager.getActiveNetworkInfo().getState();
		// 判断是否正在使用WIFI网络
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState(); 
		if (State.CONNECTED == state) { 
			Log.e("CheckNetWorkUtil", "网络状态_TYPE_WIFI");
			success = true;
		}
		// 判断是否正在使用GPRS网络
		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState(); 
		if (State.CONNECTED != state) { 
			Log.e("CheckNetWorkUtil", "网络状态_TYPE_MOBILE");
			success = true;
		}
		if (!success) {
			Toast.makeText(context, "您的网络连接已中断！", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

}
