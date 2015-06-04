package cc.siyo.iMenu.VCheck;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

import cc.siyo.iMenu.VCheck.util.CrashHandler;

/**
 * 
 * @author ShangGuan
 */
public class MyApplication extends Application {

	/**
     * MyApplication实例
     */
    private static MyApplication application;
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	/** 在Application里面设置我们的异常处理器为UncaughtExceptionHandler处理器*/
    	 CrashHandler handler = CrashHandler.getInstance(getApplicationContext());
         handler.init(getApplicationContext());
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
    
}
