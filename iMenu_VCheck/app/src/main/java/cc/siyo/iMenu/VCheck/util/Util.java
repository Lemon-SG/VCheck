package cc.siyo.iMenu.VCheck.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import junit.framework.Assert;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import cc.siyo.iMenu.VCheck.model.Constant;

/**
 * Created by Lemon on 2015/5/6.
 * Desc:工具类
 */
public class Util {

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

    /** 更换TextView旁图标 */
    public static void ChangeTextImage(Context context, TextView textView, int drawableId) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    /** 返回当前订单状态，用于显示给用户*/
    public static String ShowOrderStatus(String orderType) {
        String msg = "";
        if(orderType.equals(Constant.ORDER_TYPE_NO_PAY)) {
            //未付款订单,可取消，可删除，可支付订单，列表显示：等待付款
            msg = "等待付款";
        }
        if(orderType.equals(Constant.ORDER_TYPE_PAY_NO_SPEND)) {
            //已支付未消费订单，只能申请退款，且不可删除，列表实现：等待消费
            msg = "等待消费";
        }
        if(orderType.equals(Constant.ORDER_TYPE_PAY_SPEND)) {
            //已支付已消费订单，只能删除，列表显示：已消费
            msg = "已消费";
        }
        if(orderType.equals(Constant.ORDER_TYPE_RETURN_IN)) {
            //退款中订单，无操作，且不可删除，列表显示：退款中
            msg = "退款中";
        }
        if(orderType.equals(Constant.ORDER_TYPE_RETURN_OVER)) {
            //退款完成订单，只能删除，列表显示：已退款
            msg = "已退款";
        }
        if(orderType.equals(Constant.ORDER_TYPE_NO_PAY_TIMEOUT)) {
            //待付款过期订单，只能删除，列表显示：交易关闭
            msg = "交易关闭";
        }
        if(orderType.equals(Constant.ORDER_TYPE_PAY_TIMEOUT)) {
            //已付款过期订单，只能申请退款，且不能删除，列表显示：订单已过期，请申请退款
            msg = "订单已过期，请申请退款";
        }
        return msg;
    }

    /** 返回剩余时间*/
    public static String GetRemainTime(long time) {
        String msg = "";
        long sevenTime = 7 * 24 * 60 * 60;
        Log.e(TAG, "time->" + time);
        if(time > sevenTime) {
            //一周以上
            msg = "剩余一周以上";
        } else {
            //一周以内
            int s = (int) (time % 60);
            int m = (int) (time / 60 % 60);
            int h = (int) (time / 3600);
            int d = (int) ((time / 3600) % 24);
            Log.e("时间->", d + "天" + h + "时" + m + "分" + s + "秒");
            if(d > 0) {
                Log.e("天数->", ">" + d);
                msg = d + "天" + h + "小时" + m + "分" + s +"秒";
                return msg;
            }
            if(h > 0) {
                msg = h + "小时" + m + "分" + s +"秒";
                return msg;
            }
            if(m > 0) {
                msg = m + "分" + s +"秒";
                return msg;
            }
            if(s > 0) {
                msg = s +"秒";
                return msg;
            }else {
                msg = "";
                return msg;
            }
        }
        Log.e("时间->", "msg->" + msg);
        return msg;
    }

    /**
     * 用户拍照后的头像保存文件
     */
    public static File getFileHeadPoto() {
        File ss = new File(Constant.PATH_HEADPHOTO_IMG);
        if (!ss.isDirectory()) {
            ss.mkdirs();
        }
        File file = new File(ss, Constant.MEMBERICON);
        return file;
    }

    /**
     * 保存修剪过的用户头像
     */
    public static void saveMyBitmap(String bitPathName, Bitmap mBitmap)
            throws IOException {
        File f = new File(bitPathName);
        f.createNewFile();
        FileOutputStream fOut = null;
        fOut = new FileOutputStream(f);
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
    }

    /**
     * 头像裁剪
     *
     * @param uri
     * @param requestCode
     * @param context
     */
    public static void startPhotoZoom(Uri uri, int requestCode, Activity context) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 93);
        intent.putExtra("outputY", 93);
        intent.putExtra("return-data", true);
        context.startActivityForResult(intent, requestCode);
    }


    /************** 微信支付工具 **************/
    private static final String TAG = "SDK_Sample.Util";

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] httpGet(final String url) {
        if (url == null || url.length() == 0) {
            Log.e(TAG, "httpGet, url is null");
            return null;
        }

        HttpClient httpClient = getNewHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse resp = httpClient.execute(httpGet);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.e(TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
                return null;
            }

            return EntityUtils.toByteArray(resp.getEntity());

        } catch (Exception e) {
            Log.e(TAG, "httpGet exception, e = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] httpPost(String url, String entity) {
        if (url == null || url.length() == 0) {
            Log.e(TAG, "httpPost, url is null");
            return null;
        }

        HttpClient httpClient = getNewHttpClient();

        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new StringEntity(entity));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse resp = httpClient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.e(TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
                return null;
            }

            return EntityUtils.toByteArray(resp.getEntity());
        } catch (Exception e) {
            Log.e(TAG, "httpPost exception, e = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain,	String authType) throws java.security.cert.CertificateException {
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,	port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static byte[] readFromFile(String fileName, int offset, int len) {
        if (fileName == null) {
            return null;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "readFromFile: file not found");
            return null;
        }

        if (len == -1) {
            len = (int) file.length();
        }

        Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));

        if(offset <0){
            Log.e(TAG, "readFromFile invalid offset:" + offset);
            return null;
        }
        if(len <=0 ){
            Log.e(TAG, "readFromFile invalid len:" + len);
            return null;
        }
        if(offset + len > (int) file.length()){
            Log.e(TAG, "readFromFile invalid file len:" + file.length());
            return null;
        }

        byte[] b = null;
        try {
            RandomAccessFile in = new RandomAccessFile(fileName, "r");
            b = new byte[len]; // ´´½¨ºÏÊÊÎÄ¼þ´óÐ¡µÄÊý×é
            in.seek(offset);
            in.readFully(b);
            in.close();

        } catch (Exception e) {
            Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
            e.printStackTrace();
        }
        return b;
    }

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
    public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
        Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;

            Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed");
                return null;
            }

            Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            Log.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    public static String sha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes());

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> stringsToList(final String[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (int i = 0; i < src.length; i++) {
            result.add(src[i]);
        }
        return result;
    }
}
