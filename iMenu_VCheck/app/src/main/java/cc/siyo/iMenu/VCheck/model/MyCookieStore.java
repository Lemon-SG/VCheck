package cc.siyo.iMenu.VCheck.model;

import org.apache.http.client.CookieStore;
/**
 * Created by Lemon on 2015/5/21.
 * Desc:用于存储CookieStore，请求处于同一session
 */
public class MyCookieStore {

    private static final String TAG = "MyCookieStore";
    public static CookieStore cookieStore=null;
}
