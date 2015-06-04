package cc.siyo.iMenu.VCheck.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cc.siyo.iMenu.VCheck.activity.LoginActivity;
import cc.siyo.iMenu.VCheck.model.Constant;
import cc.siyo.iMenu.VCheck.model.JSONStatus;
import cc.siyo.iMenu.VCheck.util.PreferencesUtils;
import cc.siyo.iMenu.VCheck.util.StringUtils;
import cc.siyo.iMenu.VCheck.view.LoadingDialog;

/**
 * 
 * @author Created by ShangGuan on 15-03-05.
 */
public abstract class BaseFragment extends Fragment {

    public Context context;
    protected final static int SUCCESS = 100;
    protected final static int FAILURE = 101;
    protected String token;
    protected String seller_id;
//    /**每页显示几条数据*/
//    protected final static int PAGESIZE = Constant.PAGE_SIZE;
//    /**显示第几页*/
//    protected final static int PAGE = Constant.PAGE;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
    	context = getActivity();
//    	token = PreferencesUtils.getString(context, "token");
//    	seller_id = PreferencesUtils.getString(context, "seller_id");
    	View v = inflater.inflate(getContentView(),null);
		initView(v);
		initData();
    	return v;
    }

    /**
     * 返回当前所需要的布局文件 *
     */
    public abstract int getContentView();

    /**
     * 统一初始化view的方法
     *
     * @author ShangGuan *
     */
    public abstract void initView(View v);

    /**
     * 统一初始化数据的方法
     *
     * @author ShangGuan *
     */
    public abstract void initData();

    /**
     * 非阻塞提示方式 *
     */
    public void prompt(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 进行耗时阻塞操作时,需要调用改方法,显示等待效果
     *
     * @author Sylar *
     */
    public void showProgressDialog(String content) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context, content);
        }
        if (!getActivity().isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void showProgressDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context,"加载中...");
        }
        if (!getActivity().isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * 耗时阻塞操作结束时,需要调用改方法,关闭等待效果
     *
     * @author Sylar *
     */
    public void closeProgressDialog() {
        if (loadingDialog != null && !getActivity().isFinishing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
//        if (loadingDialog != null && !isFinishing()) {
//            loadingDialog.dismiss();
//        }
    }

    /** 公共解析返回数据成功与否
		t:{
		    "status": {
		        "succeed": "0",
		        "error_code": "2001",
		        "error_desc": "用户名或密码错误"
		    }
		}
		t:{
		    "status": {
		        "succeed": "1"
		    },
		    "data": {
		        "seller_id": "1",
		        "token": "8f0226b243cb63280a069412c121c66d"
		    }
		}
     * */
    public JSONStatus BaseJSONData(String t){
    	JSONStatus jsonStaus = new JSONStatus();
    	try {
			JSONObject obj = new JSONObject(t);
			if(obj != null && obj.length() > 0){
				jsonStaus = new JSONStatus().parse(obj);
				if(!StringUtils.isBlank(jsonStaus.error_desc)){
					if(jsonStaus.error_code.equals(Constant.TOKEN_ERROR_CODE)){
						prompt("登录过期，请重新登录");
						System.out.println("TOKEN_ERROR_CODE");
						PreferencesUtils.clear(context);
						Intent intent = new Intent(context, LoginActivity.class);
						startActivity(intent);
						getActivity().finish();
						return null;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return jsonStaus;
    }
    
//    /**
//     * token过期显示对话框，两个按钮
//     *
//     * @param context
//     * @param msg
//     * @param lister 确定
//     * @param lister2 取消
//     * @return
//     */
//    public void showTokenDialog(Activity context, String msg, OnClickListener lister) {
//    	if(context != null){//android:Theme.Holo.Light.Dialog
//            Builder builder = new Builder(context, cc.siyo.imenu_seller.R.style.LightDialog);
//            builder.setNegativeButton("取消", null);
//            builder.setPositiveButton("确定", lister);
//            builder.setMessage(msg);
//            builder.setTitle("提示");
//            AlertDialog dialog = builder.create();
//            if(dialog != null && !dialog.isShowing()){
//            	dialog.show();
//            }
//    	}
//    }
    
//    /***
//     * 显示对户框，只有一个确定按钮
//     * @param context
//     * @param msg
//     * @param lister 确定
//     */
//    public void showDialog(Activity context, String msg, OnClickListener lister) {
//    	if(context != null){//android:Theme.Holo.Light.Dialog
//            Builder builder = new Builder(context, cc.siyo.imenu_seller.R.style.LightDialog);
//            builder.setPositiveButton("确定", lister);
//            builder.setMessage(msg);
//            builder.setTitle("提示");
//            AlertDialog dialog = builder.create();
//            if(dialog != null && !dialog.isShowing()){
//            	dialog.show();
//            }
//    	}
//    }
}
