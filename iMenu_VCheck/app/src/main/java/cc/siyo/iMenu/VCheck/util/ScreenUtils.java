package cc.siyo.iMenu.VCheck.util;

import android.content.Context;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * ScreenUtils
 * <ul>
 * <strong>Convert between dp and sp</strong>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-14
 */
public class ScreenUtils {

    public static float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int dpToPxInt(Context context, float dp) {
        return (int) (dpToPx(context, dp) + 0.5f);
    }

    public static int pxToDpCeilInt(Context context, float px) {
        return (int) (pxToDp(context, px) + 0.5f);
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return ScreenResolution
     */
    public static ScreenResolution getScreenResolution(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return new ScreenResolution(display.getWidth(), display.getHeight());
    }

    public static class ScreenResolution {
        private int width;
        private int height;

        public ScreenResolution() {
        }

        public ScreenResolution(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    }

    /***
     * 根据屏幕宽度返回图片的宽高layoutParam值（16:9）
     * @param widthMarginDp 图片距离屏幕两侧的宽度总和
     * @return
     */
    public static ViewGroup.LayoutParams getImageParam(Context context, int widthMarginDp) {
        ScreenResolution screenResolution = getScreenResolution(context);
        int width = (screenResolution.getWidth() - ScreenUtils.dpToPxInt(context, widthMarginDp));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, (int) (width * 0.5625));
//        layoutParams.setMargins(0, ScreenUtils.dpToPxInt(context, 10), 0, -(ScreenUtils.dpToPxInt(context, 40)));
        return  layoutParams;
    }
}
