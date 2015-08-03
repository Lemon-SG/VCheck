package cc.siyo.iMenu.VCheck.util;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationUtils {
    private static LocationUtils instance = null;
    private static BDLocation mLocation;
    private Context mContext;
    private static LocationClient mLocationClient;

    private BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLocation = bdLocation;
        }

//        @Override
//        public void onReceivePoi(BDLocation bdLocation) {
//
//        }
    };

    private LocationUtils(Context context) {
        mContext = context;
        mLocationClient = new LocationClient(mContext);
        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setOpenGps(true);
        option.disableCache(true);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(mLocationListener);
        mLocationClient.start();
    }

    public static synchronized LocationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new LocationUtils(context);
        }
        return instance;
    }

    public BDLocation getLocation() {
        if (mLocation == null) {
            mLocation = mLocationClient.getLastKnownLocation();
        }
        return mLocation;
    }

    public void requestLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        }
    }

    public void stop() {
        mLocationClient.unRegisterLocationListener(mLocationListener);
        mLocationClient.stop();
    }
}
