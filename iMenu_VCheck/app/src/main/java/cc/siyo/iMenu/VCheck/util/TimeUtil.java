package cc.siyo.iMenu.VCheck.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lemon on 2015/7/13 14:09.
 * Desc:时间操作类
 */
public class TimeUtil {

    private static final String TAG = "TimeUtil";
    /** 种类为年份 */
    public final static int YEAR = 1;
    /** 种类为月份 */
    public final static int MONTH = 2;
    /** 种类为周 */
    public final static int WEEK = 3;



    /** 文章详情时间判断，返回显示字符串*/
    public static String getTimeDiff(Long time) {
        String msg = "";
        long sevenTime = 7 * 24 * 60 * 60;
        Log.e(TAG, "time->" + time + "sevenTime->" + sevenTime);
        if(time > sevenTime) {
            //一周以上1234350
            msg = "剩余一周以上";
        } else {
            //一周以内233162
            int s = (int) (time % 60);
            int m = (int) (time / 60 % 60);
            int h = (int) (time / 60 / 60 % 60);
            int d = (int) ((time / 3600) / 24);
            Log.e("时间->", d + "天" + h + "时" + m + "分" + s + "秒");
            if(d > 0) {
                msg = "剩余" + d + "天";
                return msg;
            }
            if(h > 0) {
                msg = "剩余" + h + "小时";
                return msg;
            }
            if(m > 0) {
                msg = "剩余" + m + "分";
                return msg;
            }
            if(s > 0) {
                msg = "剩余" + s +"秒";
                return msg;
            }else {
                msg = "";
                return msg;
            }
        }
        return msg;
    }

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

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /***
     * 子项的时间转换为与key相同的形式
     *
     * @param
     * @param
     *            （100=YEAR/200=MONTH/300=WEEK）
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String itemMonthDate(String dynamicDate, int type) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(dynamicDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        switch (type) {
            case YEAR:
                formatter = new SimpleDateFormat("yyyy");
                break;
            case MONTH:
                formatter = new SimpleDateFormat("yyyyMM");
                break;
            case WEEK:
                try {
                    Calendar cal = Calendar.getInstance();
                    /** 这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误 */
                    cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周从周一开始
                    cal.setMinimalDaysInFirstWeek(1); // 设置每周最少为1天
                    cal.setTime(formatter.parse(dynamicDate));
                    int time = cal.get(Calendar.WEEK_OF_YEAR);
                    if (time < 10) {
                        return "0" + time;
                    }
                    return time + "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        String dateString = formatter.format(date);
        return dateString;
    }

    /** 转换时间格式为2006/07/16 */
    @SuppressLint("SimpleDateFormat")
    public static String FormatTime(String time, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    /** 转换时间格式为2006/07/16 */
    @SuppressLint("SimpleDateFormat")
    public static String formatTime(String time, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    /** 转换时间格式String-long */
    @SuppressLint("SimpleDateFormat")
    public static long getLongTime(String time) {
        long timeString;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        timeString = date.getTime();
        return timeString;
    }

    /** 转换时间格式String-long */
    @SuppressLint("SimpleDateFormat")
    public static long getCurrentLongTime(String time) {
        long timeString;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        timeString = (date.getTime() / 1000) - (24 * 60 * 60);
        return timeString;
    }

    /** 获取系统时间 格式为："yyyy/MM/dd " */
    public static String getCurrentDate() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

    /**
     * 获取GPS当前时间
     *
     * @throws MalformedURLException
     */
    @SuppressLint("SimpleDateFormat")
    public static String getGPSCuurentTime() {
        URL urlTime;
        String UserTime = "";
        try {
            // 取得资源对象
            urlTime = new URL("http://www.bjtime.cn");
            URLConnection uc;
            uc = urlTime.openConnection();// 生成连接对象
            uc.connect(); // 发出连接
            long ld = uc.getDate(); // 取得网站日期时间
            Date date = new Date(ld); // 转换为标准时间对象
            SimpleDateFormat sdformat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");// 24小时制,12小时制则HH为hh
            UserTime = sdformat.format(date);
            // 分别取得时间中的小时，分钟和秒，并输出
            Log.i("UserTime：tag", UserTime);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return UserTime;
    }

    /** 获取当前时间String：yyyy-MM-dd hh:mm */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        String str = sdf.format(date);
        if (isToday(str)) {
            long time = TimeUtil.getTime(str, "yyyy-MM-dd HH:mm:ss");
            str = TimeUtil.getStrTime(time, "HH:mm");
            str = "今天" + str;
        } else {
            str = TimeUtil.FormatTime(str, "MM-dd HH:mm");
        }
        return str;
    }

    /** 获取当前时间String：yyyy-MM-dd hh:mm */
    @SuppressLint("SimpleDateFormat")
    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        java.util.Date date = new java.util.Date();
        String str = sdf.format(date);
        return str;
    }

    /** 获取当前时间String：yyyy-MM-dd hh:mm */
    @SuppressLint("SimpleDateFormat")
    public static String get24Time() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date date = new java.util.Date();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /** 判断是否为过期订单，用时间戳进行对比 */
    public static boolean isExpire(String date) {
        // 此时此刻
        long moment = System.currentTimeMillis();
        long time = getTime(date, "yyyy-MM-dd HH:mm:ss");
        if (time < moment) {
            return true;
        }
        return false;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将字符串转为时间戳 "yyyy年MM月dd日HH时mm分ss秒"
     *
     * @param user_time
     * @param model
     * @return
     */
    public static long getTime(String user_time, String model) {
        // "yyyy年MM月dd日HH时mm分ss秒"
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(model);
        Date d = null;
        try {
            d = sdf.parse(user_time);
            // long l = d.getTime();
            // String str = String.valueOf(l);
            // re_time = str.substring(0, 10);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long strTime = 0;
        if (d != null) {
            strTime = d.getTime();
        }
        return strTime;
    }

    /**
     * 将时间戳转为字符串 "yyyy年MM月dd日HH时mm分ss秒"
     *
     * @param lon
     * @param model
     * @return
     */
    public static String getStrTime(Long lon, String model) {
        // "yyyy年MM月dd日HH时mm分ss秒"
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(model);
        // 例如：cc_time=1291778220
        // long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lon));
        return re_StrTime;
    }

    /**动态发布时间判断，返回显示字符串*/
    public static String getTimeDifference(Long timeLong){
        long now = System.currentTimeMillis();
        long difference = now - timeLong;
        Log.i("TAG",timeLong+"/"+now+"/"+difference);
        if (difference/1000/60<=1) {
            return "刚刚";
        }else if(difference/1000/60<=60){
            return difference/1000/60 + "分钟前";
        }else if(difference/1000/60/60<=24){
            return difference/1000/60/60 + "小时前";
        }else if(difference/1000/60/60/24<=10){
            return difference/1000/60/60/24 + "天前";
        }else {
            return getStrTime(timeLong, "yyyy-MM-dd HH:mm");
        }
    }


}
