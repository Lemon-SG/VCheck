package cc.siyo.iMenu.VCheck.model;

/**
 * Created by Lemon on 2015/5/12.
 * Desc:请求路径
 */
public class API {

    public static final String server = "http://218.244.158.175/imenu_test/app_interface_vcheck/index.php";
//    public static final String server = "http://www.imenu.so/app_interface_vcheck/index.php";
    /** LOGO图片路径*/
    public static final String LOGO = "http://www.imenu.so/app_interface_vcheck/view/image/icon.png";

    /** 基本-获取配置信息 */
    public static final String GET_CLIENT_CONFIG = "base/client_config/getClientConfig";
    /** 登录*/
    public static final String LOGIN = "member/member/login";
    /** 获取首页广告图片*/
    public static final String GET_INDEX_IMAGE = "base/info/getIndexImage";
    /** 记住登录状态*/
    public static final String LOGIN_WITH_TOKEN = "member/member/loginWithToken";
    /** 登出*/
    public static final String LOGOUT = "member/member/logout";
    /** 校验用户名是否存在*/
    public static final String VALIDATE_MEMBER_INFO = "member/member/validateMemberInfo";
    /** 获取验证码*/
    public static final String GET_VERIFY_CODE = "base/tools/getVerifyCode";
    /** 密码重置*/
    public static final String RESET_PASSWORD = "member/member/resetPassword";
    /** 注册*/
    public static final String REGISTER = "member/member/register";
    /** 快速登录注册*/
    public static final String QUICK_LOGIN = "member/member/quickLogin";
    /** 提交反馈*/
    public static final String SUBMIT_FEEDBACK_INFO = "base/feedback/submitFeedbackInfo";
    /** 获取个人详情*/
    public static final String GET_MEMBER_DETAIL = "member/member/getMemberDetail";
    /** 编辑个人信息*/
    public static final String EDIT_MEMBER_INFO = "member/member/editMemberInfo";
    /** 获取城市列表*/
    public static final String GET_REGION_LIST = "base/region/getRegionList";
    /** 获取产品品列表*/
    public static final String GET_PRODUCT_LIST = "product/product/getProductList";
    /** 获取产品详情*/
    public static final String GET_PRODUCT_DETAIL = "product/product/getProductDetail";
    /** 获取商家详情*/
    public static final String GET_STORE_DETAIL = "store/store/getStoreDetail";
    /** 获取菜品详情*/
    public static final String GET_MENU_DETAIL = "store/menu/getMenuDetail";
    /** 编辑收藏产品*/
    public static final String EDIT_COLLECTION_PRODUCT = "member/collection/editCollectionProduct";
    /** 编辑头像*/
    public static final String EDIT_MEMBER_ICON = "member/member/editMemberIcon";
    /** 编辑购物车*/
    public static final String EDIT_CART = "sale/order/editCart";
    /** 提交订单*/
    public static final String ADD_ORDER = "sale/order/addOrder";
    /** 编辑结算信息*/
    public static final String CHECKOUT = "sale/order/checkout";
    /** 生成支付数据*/
    public static final String GENERATE_PAY_DATA = "sale/order/generatePayData";
    /** 提交支付订单*/
    public static final String SUBMIT_PAY_ORDER = "sale/order/submitPayOrder";
    /** 获取订单列表*/
    public static final String GET_ORDER_LIST = "sale/order/getOrderList";
    /** 获取订单详情*/
    public static final String GET_ORDER_DETAIL = "sale/order/getOrderDetail";
    /** 编辑订单*/
    public static final String EDIT_ORDER = "sale/order/editOrder";
    /** 提交退款申请*/
    public static final String SUBMIT_RETURN = "sale/return/submitReturn";
    /** 获取退款原因*/
    public static final String GET_RETURN_REASON_LIST = "base/return/getReturnReasonList";
    /** 获取收藏列表申请*/
    public static final String GET_COLLECTION_PRODUCT_LIST = "member/collection/getCollectionProductList";
    /** 提交推送设备信息*/
    public static final String EDIT_PUSH_DEVICE = "device/push/editPushDevice";
    /** 获取退款订单详情*/
    public static final String GET_RETURN_DETAIL = "sale/return/getReturnDetail";
    /** 获取礼券列表*/
    public static final String GET_VOUCHER_LIST = "member/voucher/getVoucherList";
    /** 激活礼券*/
    public static final String EXCHANGE_VOUCHER = "member/voucher/exchangeVoucher";
    /** 微信登录*/
    public static final String LOGIN_WITH_WX = "member/member_thirdpart/loginWithWx";
    /** 微信注册*/
    public static final String REGISTER_WITH_WX = "member/member_thirdpart/registerWithWx";
    /** 获取消息列表*/
    public static final String GET_MESSAGE_LIST = "member/message/getMessageList";
    /** 编辑微信信息*/
    public static final String EDIT_WITH_WX = "member/member_thirdpart/editWithWx";
    /** 获取首页广告列表*/
    public static final String GET_APP_BANNER_LIST = "base/info/getAppBannerList";
}
