package cc.siyo.iMenu.VCheck.http;

import cc.siyo.iMenu.VCheck.model.JSONStatus;

/**
 * Created by Lemon on 2015/8/7 15:23.
 * Desc:网络请求返回结果线程
 */
public interface LHttpResponseHandler {

    public abstract void onStart();

    public abstract void onLoading(long count, long current);

    public abstract void onSuccess(JSONStatus jsonStatus);

    public abstract void onFailure(Throwable t, int errorNo, String strMsg);
}
