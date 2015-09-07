package cc.siyo.iMenu.VCheck.model;

import android.os.Environment;

/**
 * Created by Lemon on 2015/5/12.
 * Desc:公用常量类
 */
public class Constant {

    /** 用于判断微信支付返回结果:true->是微信支付返回，false->不是*/
    public static boolean isWeChetPay = false;

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

    /** 提交推送设备信息操作类型标石:1-添加/2-删除/3-清空(默认为1)*/
    public static final int OPERATOR_TYPE_ADD = 1;
    /** 提交推送设备信息操作类型标石:2-删除*/
    public static final int OPERATOR_TYPE_DEL = 2;
    /** 提交推送设备信息操作类型标石:3-清空*/
    public static final int OPERATOR_TYPE_CELEAR = 3;

    /** 购物车操作类型标石:3-清空*/
    public static final int OPERATOR_TYPE_EDIT = 1;
    /** 购物车操作类型标石:2-清空*/
    public static final int OPERATOR_TYPE_CLEAN = 2;

    /** 首页广告图片尺寸:1-hdpi,2-mdpi,3-xhdpi,4-xxhdpi*/
    public static final int LAUNACH_IMG_TYPE_H = 1;
    /** 首页广告图片尺寸:1-hdpi,2-mdpi,3-xhdpi,4-xxhdpi*/
    public static final int LAUNACH_IMG_TYPE_M = 2;
    /** 首页广告图片尺寸:1-hdpi,2-mdpi,3-xhdpi,4-xxhdpi*/
    public static final int LAUNACH_IMG_TYPE_XH = 3;
    /** 首页广告图片尺寸:1-hdpi,2-mdpi,3-xhdpi,4-xxhdpi*/
    public static final int LAUNACH_IMG_TYPE_XXH = 4;

    /** 优惠券状态(1-未使用)*/
    public static final int VOUCHER_STATUS_NO_SPEND = 1;
    /** 优惠券状态(2-已使用)*/
    public static final int VOUCHER_STATUS_SPENDED = 2;
    /** 优惠券状态(0-无效)*/
    public static final int VOUCHER_STATUS_NO = 0;
    /** 优惠券状态(12-未使用未开始)*/
    public static final int VOUCHER_STATUS_NO_START = 12;
    /** 优惠券状态(13-未使用过期)*/
    public static final int VOUCHER_STATUS_NO_GUO = 13;

    /** 推送开关：1->开启*/
    public static final int PUSH_ON = 1;
    /** 推送开关：0->关闭*/
    public static final int PUSH_OFF = 0;

    /** 消息类型:1->活动消息;*/
    public static final int MESSAGE_ACTIVITY = 1;
    /** 消息类型:2->消费确认;*/
    public static final int MESSAGE_PAY = 2;
    /** 消息类型:3->退款提醒;*/
    public static final int MESSAGE_RETURN = 3;
    /** 消息类型:4->获得礼券;*/
    public static final int MESSAGE_VOUCHER = 4;
    /** 消息类型:5->开售提醒*/
    public static final int MESSAGE_SALE = 5;

    /** 微信与微博绑定标石：0->未绑定*/
    public static final String BIND_NO_WECHAT_SINA = "0";
    /** 微信与微博绑定标石：1->已绑定*/
    public static final String BIND_WECHAT_SINA = "1";

    /** 菜品状态标石:10->销售中*/
    public static final int MENU_STATUS_SALE = 10;
    /** 菜品状态标石:11->已售罄*/
    public static final int MENU_STATUS_OUT = 11;
    /** 菜品状态标石:20->已结束*/
    public static final int MENU_STATUS_OVER = 20;

    /** 定位广播action*/
    public static final String LOCATION = "location";
    public static final String LOCATION_ACTION = "locationAction";

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
    /** INTENT 申请退款返回code*/
    public static final int RESULT_CODE_RETURN = 5000;
    /** INTENT 订单详情返回需要加载列表code*/
    public static final int RESULT_CODE_ORDER_DETAIL = 6000;
    /** INTENT 跳入详情传递参数*/
    public static final String INTENT_ARTICLE_ID = "article_id";
    /** INTENT 跳入邀请好友传递分享邀请实体参数*/
    public static final String INTENT_INVITE = "INTENT_INVITE";

    /** INTENT web跳转路径为 首页*/
    public static final String INTENT_HOME = "home";
    /** INTENT web跳转路径为 文章详情*/
    public static final String INTENT_ARTICLE = "article";
    /** INTENT 跳转到礼券列表页面标石*/
    public static final String INTENT_VOUCHER_TYPE = "INTENT_VOUCHER_TYPE";
    /** INTENT 跳转到礼券列表页面标石：0->确认订单进入，选择礼券操作*/
    public static final int INTENT_VOUCHER_CHOOSE = 0;
    /** INTENT 跳转到礼券列表页面标石：1->我的页面进入，正常展示操作*/
    public static final int INTENT_VOUCHER_SHOW = 1;
    /** INTENT 从礼券选择返回到确认订单页面，选择礼券或选择不使用礼券类型*/
    public static final String RESULT_VOUCHER_TYPE = "RESULT_VOUCHER_TYPE";
    /** INTENT 从礼券选择返回到确认订单页面，选择礼券或选择不使用礼券标石： 100->使用礼券*/
    public static final int RESULT_VOUCHER_SPEND = 100;
    /** INTENT 从礼券选择返回到确认订单页面，选择礼券或选择不使用礼券标石： 101->bu使用礼券*/
    public static final int RESULT_VOUCHER_NO_SPEND = 101;
    /** 第三方登录跳转至注册页面标石*/
    public static final String INTENT_REG_WX_SINA = "INTENT_REG_WX_SINA";
    /** 跳转WEBACTIVITY传递参数路径KEY*/
    public static final String INTENT_WEB_URL = "INTENT_WEB_URL";
    /** 跳转WEBACTIVITY传递参数名称KEY*/
    public static final String INTENT_WEB_NAME = "INTENT_WEB_NAME";
    /** 跳转WEBACTIVITY传递参数名称-用户协议*/
    public static final String INTENT_WEB_NAME_NOTICE = "INTENT_WEB_NAME_NOTICE";
    /** 跳转WEBACTIVITY传递参数名称-web跳转*/
    public static final String INTENT_WEB_NAME_WEB = "INTENT_WEB_NAME_WEB";

    /*******************************   本地存储 KEY   *************************************/
    /** 是否是第一次启动标石：默认true值->是第一次安装*/
    public static final String KEY_IS_LAUNCH = "KEY_IS_LAUNCH";
    /** 用户ID：member_id*/
    public static final String KEY_MEMBER_ID = "member_id";
    /** token*/
    public static final String KEY_TOKEN = "token";
    /** 手机号*/
    public static final String KEY_MOBILE = "mobile";
    /** 总开关状态*/
    public static final String KEY_PUSH_ALL = "KEY_PUSH_ALL";
    /** 消费确认开关状态*/
    public static final String KEY_PUSH_PAY = "KEY_PUSH_PAY";
    /** 退款提醒开关状态*/
    public static final String KEY_PUSH_RETURN = "KEY_PUSH_RETURN";
    /** 活动消息开关状态*/
    public static final String KEY_PUSH_ACTIVITY = "KEY_PUSH_ACTIVITY";
    /** 获得礼券开关状态*/
    public static final String KEY_PUSH_VOUCHER = "KEY_PUSH_VOUCHER";
    /** 邀请码*/
    public static final String KEY_INVITE_CODE = "KEY_INVITE_CODE";

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
    public static final int PAGE_SIZE = 8;
    /** 非法TOKEN CODE*/
    public static final String TOKEN_ERROR_CODE = "2002";
    /** 错误码:该手机号已存在*/
    public static final int ERROR_CODE_MOBILE_HERE = 2013;
    /** 错误码:该邮箱已存在*/
    public static final int ERROR_CODE_EMAIL_HERE = 2016;
    /** 链接跳转：网页*/
    public static final String LINK_WEB = "web";
    /** 链接跳转：首页*/
    public static final String LINK_HOME = "home";
    /** 链接跳转：文章详情*/
    public static final String LINK_ARTICLE = "article";
    /** 链接跳转：用户中心*/
    public static final String LINK_MEMBER = "member_center";
    /** 链接跳转：消息列表*/
    public static final String LINK_MESSAGE = "message_list";
    /** 链接跳转：订单列表*/
    public static final String LINK_ORDER_LIST = "order_list";
    /** 链接跳转：订单详情*/
    public static final String LINK_ORDER_DETAIL = "order_detail";
    /** 链接跳转：收藏列表*/
    public static final String LINK_COLLECTION = "collection_list";
    /** 链接跳转：礼券列表*/
    public static final String LINK_VOUCHER = "voucher_list";

    /**************************      支付      ****************************/
    /** paymentCode:支付宝支付*/
    public static final String PAMENT_CODE_ALIPAY = "alipay";
    /** paymentCode:微信支付*/
    public static final String PAMENT_CODE_WECHAT = "weixin_pay";
    /********** 微信支付参数 **********/
    /** appid 请同时修改  androidmanifest.xml里面，.PayActivity里的属性<data android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid*/
    public static final String APP_ID = "wx79252ca0921c523d";
    /** 商户号*/
    public static final String MCH_ID = "1233848001";
    /** API密钥，在商户平台设置*/
    public static final  String API_KEY="412fde4e9c2e2bb619514ecea142e449";

    /************************      订单类型    **************************/
    /** 待付款订单-->可取消，可删除，可支付订单，列表显示：等待付款*/
    public static final String ORDER_TYPE_NO_PAY = "10";
    /** 待付款过期订单-->只能删除，列表显示：交易关闭*/
    public static final String ORDER_TYPE_NO_PAY_TIMEOUT = "51";
    /** 已付款未消费订单-->只能申请退款，且不可删除，列表实现：等待消费*/
    public static final String ORDER_TYPE_PAY_NO_SPEND = "21";
    /** 已付款已消费订单-->只能删除，列表显示：已消费*/
    public static final String ORDER_TYPE_PAY_SPEND = "22";
    /** 已付款未消费过期订单-->只能申请退款，且不能删除，列表显示：订单已过期，请申请退款*/
    public static final String ORDER_TYPE_PAY_TIMEOUT = "52";
    /** 退款中订单-->无操作，且不可删除，列表显示：退款中*/
    public static final String ORDER_TYPE_RETURN_IN = "31";
    /** 已退款订单-->只能删除，列表显示：已退款*/
    public static final String ORDER_TYPE_RETURN_OVER = "32";
}
