package cc.siyo.iMenu.VCheck.util;

import android.content.Context;
import android.util.Log;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {

    private Context mContext;
    private static volatile CrashHandler instance;
//    private ErrorLogDao errorLogDao;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d("CrashHandler", "uncaughtException, thread: " + thread
                + " name: " + thread.getName() + " id: " + thread.getId() + "exception: "
                + ex);
        ex.printStackTrace();
//        ErrorLogEntity logEntity = new ErrorLogEntity();
        if (ex != null) {
            StackTraceElement[] elements = ex.getStackTrace();
            if (elements.length > 0) {
                StringBuffer errorMsg = new StringBuffer();
                errorMsg.append(ex.toString()).append("\r\n");
                for (StackTraceElement element : elements) {
                    errorMsg.append(element.toString()).append("\r\n");
                }
//                logEntity.message = errorMsg.toString();
            }
        }
//        logEntity.os = String.valueOf(Build.VERSION.SDK_INT);
//        logEntity.rom = Build.MODEL;
//        errorLogDao.saveErrorLog(logEntity);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private CrashHandler(Context context) {
//        errorLogDao = new ErrorLogDaoImpl(context);
        this.mContext = context;
    }

    public static synchronized CrashHandler getInstance(Context context) {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler(context);
                }
            }
        }
        return instance;
    }

    public void init(Context ctx) {
        //把当前对象设置成UncaughtExceptionHandler的处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
}