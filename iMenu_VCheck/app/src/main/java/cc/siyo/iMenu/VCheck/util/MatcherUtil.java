package cc.siyo.iMenu.VCheck.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cc.siyo.iMenu.VCheck.model.Constant;

/***
 * 
 * 正则表达式验证
 * 
 * @author Create by ShangGuan 15-3-11
 */
public class MatcherUtil {
	
	/** 手机号码正则*/
	private static final String MBILE = "[1][3578]\\d{9}";
	/** 邮箱正则*/
	private static final String EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	/** 匹配正整数*/
	private static final String INT = "^[1-9]d*$";
	/** 匹配中文*/
	private static final String STRING = "[u4e00-u9fa5]";
	/** 纯数字*/
	private static final String NUMBER= "^[0-9]*$";
	/** 身份证号(15位、18位数字*/
	private static final String IDCARD= "";
//	/** */
//	private static final String = "";
//	/** */
//	private static final String = "";
//	/** */
//	private static final String = "";
//	/** */
//	private static final String = "";

    /**
     * 使用正则，根据不同类型判断比对格式是否正确
     * @param matchType :Constant.MATCHER_MOBILE | Constant.MATCHER_EMAIL
     * @param text :需要比对的value
     * @return false为格式错误
     */
	public static Boolean Matcher(int matchType, String text){
		Pattern pattern = null;
		switch (matchType) {
		case Constant.MATCHER_MOBILE:
			pattern = Pattern.compile(MBILE); 
			break;
		case Constant.MATCHER_EMAIL:
			pattern = Pattern.compile(EMAIL); 
			break;
		}
		Matcher matcher = pattern.matcher(text); 
		return matcher.matches();
	}
}
