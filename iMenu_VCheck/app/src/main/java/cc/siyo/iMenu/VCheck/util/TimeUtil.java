package cc.siyo.iMenu.VCheck.util;

import android.util.Log;

import java.util.Date;

/**
 * Created by Lemon on 2015/7/13 14:09.
 * Desc:时间操作类
 */
public class TimeUtil {
    private static final String TAG = "TimeUtil";
    /**
     * 要转换的毫秒数
     * @param mss
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        String dayMsg = "";
        String hoursMsg = "";
        String minutesMsg = "";
        String secondsMsg = "";
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        Log.e(TAG, days + " 天 " + hours + "时" + minutes + "分"  + seconds + "秒");
        if(days != 0) {
            dayMsg = days + "天";
        }
        if(hours != 0) {
            hoursMsg = hours + "时";
        }
        if(minutes != 0) {
            minutesMsg = minutes + "分";
        }
        if(seconds != 0) {
            secondsMsg = seconds + "秒";
        }
        return dayMsg + hoursMsg + minutesMsg + secondsMsg;
    }
    /**
     *
     * @param begin 时间段的开始
     * @param end	时间段的结束
     * @return	输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     * @author fy.zhang
     */
    public static String formatDuring(Date begin, Date end) {
        return formatDuring(end.getTime() - begin.getTime());
    }

}
