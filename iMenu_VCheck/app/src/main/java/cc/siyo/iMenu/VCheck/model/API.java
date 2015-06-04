package cc.siyo.iMenu.VCheck.model;

/**
 * Created by Lemon on 2015/5/12.
 * Desc:请求路径
 */
public class API {

    private static final String TAG = "API";
    public static final String server = "http://218.244.158.175/imenu_test/app_interface_vcheck/index.php";

    /** 基本-获取配置信息 */
    public static final String GET_CLIENT_CONFIG = "base/client_config/getClientConfig";
    /** 登录*/
    public static final String LOGIN = "member/member/login";
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
}
