package cc.siyo.iMenu.VCheck.util;

import android.graphics.Paint;
import android.widget.TextView;

/**
 * Created by Lemon on 2015/5/6.
 * Desc:工具类
 */
public class Util {

    private static final String TAG = "Util";

    /** 打印控制台*/
    public static void println(String TAG, String msg){
        System.out.println(TAG + "===" + msg);
    }

    /** 文本添加删除线*/
    public static void PaintTvAddStrike(TextView textView){
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        //Paint.UNDERLINE_TEXT_FLAG 下划线
    }

    /** 文本添加下划线*/
    public static void PaintTvAddUnderline(TextView textView){
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }
}
