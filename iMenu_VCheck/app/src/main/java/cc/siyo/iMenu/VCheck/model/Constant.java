package cc.siyo.iMenu.VCheck.model;

import android.os.Environment;

/**
 * Created by Lemon on 2015/5/12.
 * Desc:公用常量类
 */
public class Constant {

    /*****************************   标石类   *********************************/
    /** 匹配手机号码*/
    public static final int MATCHER_MOBILE = 1;
    /** 匹配邮箱*/
    public static final int MATCHER_EMAIL = 2;
    /** 匹配昵称*/
    public static final int MATCHER_NICKNAME = 3;
    /** 修改账户资料类别KEY*/
    public static final String EDIT_ACCOUNT = "EDIT_ACCOUNT";
    /** 修改账户资料类别标石：绑定邮箱*/
    public static final int EDIT_EMAIL = 0;
    /** 修改账户资料类别标石：修改昵称*/
    public static final int EDIT_NICKNAME = 1;
    /** 修改账户资料类别标石：修改手机号*/
    public static final int EDIT_MOBILE = 2;
    /** 修改账户资料类别标石：修改密码*/
    public static final int EDIT_PASS = 3;
    /** 分享种类标石:新浪微博分享*/
    public static final int SHARE_TYPE_SINA = 1;
    /** 分享种类标石:人人网分享*/
    public static final int SHARE_TYPE_RENREN = 2;
    /** 分享种类标石:微信分享*/
    public static final int SHARE_TYPE_WECHAT = 3;
    /** 分享种类标石:微信朋友圈分享*/
    public static final int SHARE_TYPE_WECHAT_MOMENT = 4;
    /** 编辑收藏操作标石:1-添加*/
    public static final int COLLECT_TYPE_OPERATOR_ADD = 1;
    /** 编辑收藏操作标石:2-删除*/
    public static final int COLLECT_TYPE_OPERATOR_DELETE = 2;
    /** 编辑收藏操作标石:3-清空)*/
    public static final int COLLECT_TYPE_OPERATOR_CLEAN = 3;
    /** 购物车操作类型标石:1-编辑*/
    public static final int OPERATOR_TYPE_EDIT = 1;
    /** 购物车操作类型标石:2-清空*/
    public static final int OPERATOR_TYPE_CLEAN = 2;

    /****************************        拍照     **********************************/
    /** 作用: SD卡根目录 */
    public static final String PATH = Environment.getExternalStorageDirectory().getPath();
    /** 作用: 数据文件夹 */
    public static final String PATH_ROOT = PATH + "/VCheck";
    /** 作用: 头像文件夹 */
    public static final String PATH_HEADPHOTO_IMG = PATH_ROOT + "/HeadPhoto/";
    /** 作用: 用户选择头像 图片请求码 */
    public static final int USERINFO_IAMGELIB_REQUEST = 108;
    /** 作用: 用户拍照请求码 */
    public static final int USERINFO_CAMERA_REQUEST = 109;
    /** 作用: 剪贴图片页面请求码 */
    public static final int USERINFO_CLIP_REQUEST = 110;
    /** 作用: 用户头像 */
    public static final String USERINFO_HEADIMG = 0 + "";
    /** 作用: 存储路径追加字符串*/
    public static final String  MEMBERICON = "MEMBERICON";

    /*****************************   INTENT CODE   *********************************/
    /** INTENT RESQUEST_CODE*/
    public static final int RESQUEST_CODE = 1000;
    /** INTENT 登录成功返回code*/
    public static final int RESULT_CODE_LOGIN = 2000;
    /** INTENT 修改账户信息返回code*/
    public static final int RESULT_CODE_EDIT_ACCOUNT = 3000;
    /** INTENT 退出登录返回code*/
    public static final int RESULT_CODE_LOGOUT = 4000;

    /*******************************   本地存储 KEY   *************************************/
    /** 用户ID：member_id*/
    public static final String KEY_MEMBER_ID = "member_id";
    /** token*/
    public static final String KEY_TOKEN = "token";
    /** 手机号*/
    public static final String KEY_MOBILE = "mobile";

    /******************************   版本更新   *********************************/
    /** 版本更新APK保存路径*/
    public static final String APK_TARGET = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VCheck.apk";
    /** 强制更新1-强制：need_update*/
    public static final String NEED_UPDATE_YES = "1";
    /** 强制更新0-不强制：need_update*/
    public static final String NEED_UPDATE_NO = "0";
    /** 弹出更新提示1-弹出：is_tips*/
    public static final String IS_TIPS_YES = "1";
    /** 弹出更新提示0-不弹出：is_tips*/
    public static final String IS_TIPS_NO = "0";
    /** android服务器当前软件版本：version_android*/
    public static final String KEY_VERSION_ANDROID = "version_android";
    /** 是否强制更新(1-强制/0-不强制)：need_update*/
    public static final String KEY_NEED_UPDATE = "need_update";
    /** 是否启动弹出更新提示(1-弹出/0-不弹出)：is_tips*/
    public static final String KEY_IS_TIPS = "is_tips";

    /******************************   系统配置   *********************************/
    /** 加密salt_key:siyo_vcheck*/
    public static final String SALT_KEY = "siyo_vcheck";
    /** 设备类型*/
    public static final String DEVICE_TYPE = "20";
    /** 打印时用: 请求返回结果*/
    public static final String RESULT = "| API_RESULT |";
    /** 打印时用: 请求*/
    public static final String REQUEST = "| API_REQUEST |";
    /** 请求数据页数*/
    public static final int PAGE = 1;
    /** 请求数据每页显示条数*/
    public static final int PAGE_SIZE = 5;
    /** 非法TOKEN CODE*/
    public static final String TOKEN_ERROR_CODE = "2002";
    /** 错误码:该手机号已存在*/
    public static final int ERROR_CODE_MOBILE_HERE = 2013;
    /** 错误码:该邮箱已存在*/
    public static final int ERROR_CODE_EMAIL_HERE = 2016;

    /**************************      支付      ****************************/
    /** paymentCode:支付宝支付*/
    public static final String PAMENT_CODE_ALIPAY = "alipay";
    /** paymentCode:微信支付*/
    public static final String PAMENT_CODE_WECHAT = "weixin_pay";
    /********** 微信支付参数 **********/
    /** appid 请同时修改  androidmanifest.xml里面，.PayActivityd里的属性<data android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid*/
    public static final String APP_ID = "wx79252ca0921c523d";
    /** 商户号*/
    public static final String MCH_ID = "1233848001";
    /** API密钥，在商户平台设置*/
    public static final  String API_KEY="412fde4e9c2e2bb619514ecea142e449";
}
