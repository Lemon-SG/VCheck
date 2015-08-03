package cc.siyo.iMenu.VCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import cc.siyo.iMenu.VCheck.util.CrashHandler;
import cc.siyo.iMenu.VCheck.util.LocationUtils;
import cc.siyo.iMenu.VCheck.util.ScreenUtils;

/**
 * 所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 *
 * @author ShangGuan
 */
public class MyApplication extends Application {

	/**
     * MyApplication实例
     */
    private static MyApplication application;
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	public TextView mLocationResult,logMsg;
	public Vibrator mVibrator;
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	/** 在Application里面设置我们的异常处理器为UncaughtExceptionHandler处理器*/
    	 CrashHandler handler = CrashHandler.getInstance(getApplicationContext());
         handler.init(getApplicationContext());

		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

//		LocationUtils.getInstance(this.getApplicationContext());
//		if (!"generic".equalsIgnoreCase(Build.BRAND)) {
//			SDKInitializer.initialize(getApplicationContext());
//		}

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.resetViewBeforeLoading(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY)
				.showImageOnLoading(R.drawable.ic_member)
				.showImageForEmptyUri(R.drawable.ic_member)
				.showImageOnFail(R.drawable.ic_member).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
						// .memoryCache(new WeakMemoryCache()).memoryCacheSize(2 * 1024 * 1024)
				.diskCache(new UnlimitedDiscCache(getExternalFilesDir("/cache")))
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheExtraOptions(480, 320, null).writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
    }

	/**
	 * @param context
	 * @param r
	 * @param  defaultImgRes:默认图片资源
	 * @return
	 */
	public static DisplayImageOptions getDisplayImageOptions(Context context, int r, int defaultImgRes) {
		return new DisplayImageOptions.Builder()
				.displayer(
						new RoundedBitmapDisplayer(ScreenUtils.dpToPxInt(
								context, r))).cacheInMemory(true)
				.cacheOnDisk(true).resetViewBeforeLoading(true)
				.showImageOnLoading(defaultImgRes)
				.showImageOnFail(defaultImgRes)
				.showImageForEmptyUri(defaultImgRes)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
    
    /**
     *  0000	接口测试页面，没有找到测试接口
		1001	参数格式错误
		2001	用户名或密码错误
		2002	非法的TOKEN
		2003	非法用户参数
		2008	非法CODE
		2026	用户不存在
     * @return
     */
    public static Map<String, String> getErroCodeMap(){
    	Map<String, String> errodCodeMap = new HashMap<String, String>();
    	errodCodeMap.put("0000", "接口测试页面，没有找到测试接口");
    	errodCodeMap.put("1001", "参数格式错误");
    	errodCodeMap.put("2001", "用户名或密码错误");
    	errodCodeMap.put("2002", "非法的TOKEN");
    	errodCodeMap.put("2003", "非法用户参数");
    	errodCodeMap.put("2008", "非法CODE");
    	errodCodeMap.put("2026", "用户不存在");
    	return errodCodeMap;
    }
    
    /** 判断本地错误码中是否包含*/
    public static Boolean isInCluding(String code){
    	Boolean isInCluding = false;
    	if(getErroCodeMap().containsKey(code)){
    		isInCluding = true;
    	}
    	return isInCluding;
    }
    
    /** 返回本地存储错误码详情*/
    public static String findErroDesc(String code){
    	String desc = "";
    	if(isInCluding(code)){
    		desc = getErroCodeMap().get(code);
    	}
    	return desc;
    }

	/**
	 * 实现实时位置回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			sb.append("\nlocationdescribe : ");// 位置语义化信息
			sb.append(location.getLocationDescribe());
			List<Poi> list = location.getPoiList();// POI信息
			if (list != null) {
				sb.append("\npoilist size = : ");
				sb.append(list.size());
				for (Poi p : list) {
					sb.append("\npoi= : ");
					sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
				}
			}
			logMsg(sb.toString());
			Log.i("BaiduLocationApiDem", sb.toString());
		}

		/**
		 * 显示请求字符串
		 * @param str
		 */
		public void logMsg(String str) {
			try {
				if (mLocationResult != null)
					mLocationResult.setText(str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
