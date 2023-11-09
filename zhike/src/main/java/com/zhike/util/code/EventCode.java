package com.zhike.util.code;

//事件状态码
public class EventCode {
    //=================登录状态码==================
    public static  final String LOGIN_EMAIL_PASSWORD_SUCCESS="L_001";//登录成功（邮箱和密码）
    public static  final String LOGIN_FAIL="L_002";//登录失败
    public static  final String LOGIN_LOGIN_CREATE_EXCEPTION="L_003";//登录日志创建异常
    public static  final String LOGIN_LOGIN_CREATE_FAIL="L_004";//登录日志创建失败

    public static  final String LOGIN_SAVE_USER_REDIS_EXCEPTION="L_005";//登录成功存储用户信息至redis失败
    public static  final String LOGIN_OUT_EXCEPITION="L_006";//退出登录异常
    public static  final String LOGIN_OUT_SUCCESS="L_007";//退出登录成功
    public static  final String LOGIN_INVALID="L_008";//登陆失效

    //=========================SQL 业务状态码====================
    public static  final String SELECT_SUCCESS="S_001";//查询成功
    public static  final String SELECT_EXCEPTION="S_002";//查询异常
    public static  final String SELECT_ERROR="S_003";//查询错误
    public static  final String SELECT_NONE="S_004";//未查到信息
    public static  final String INSERT_EXCEPITON="S_005";//新增异常
    public static  final String INSERT_ERROR="S_006";//新增失败
    public static  final String UPDATE_EXCEPITION="S_007";//更新异常
    public static  final String UPDATE_ERROR="S_008";//更新失败
    public static  final String UPDATE_SUCCESS="S_009";//更新成功
    //==================关于账号的状态码=============
    public static  final String ACCOUNT_CLOCK="A_001";//账号被锁定
    public static  final String ACCOUNT_EMAIL_WRONG="A_002";//账号邮箱有误
    public static  final String ACCOUNT_PASSWORD_WRONG="A_003";//账号密码有误
    public static  final String ACCOUNT_EMAIL_REGISTERED="A_004";//邮箱账号已被注册
    public static  final String ACCOUNT_EMAIL_REGISTER_SUCCESS="A_005";//邮箱账号注册成功
    public static  final String ACCOUNT_EMAIL_REGISTER_LOG_EXCEPITON="A_006";//邮箱账号注册日志异常
    public static  final String ACCOUNT_EMAIL_REGISTER_LOG_ERROR="A_007";//邮箱账号注册日志错误
    //==================邮箱服务的状态码=============
    public static  final String EMAIL_SEND_VC_SUCCESS="E_001";//邮箱验证码发送成功
    public static  final String EMAIL_SEND_VC_ERROR="E_002";//邮箱验证码发送失败
    public static  final String EMAIL_SEND_VC_SAVE_REDIS_ERROR="E_003";//邮箱验证码存储失败
    public static  final String EMAIL_SEND_INIT_PASSWORD_EXCEPITION="E_004";//邮箱发送初始密码失败
    //==================验证码的状态码=============
    public static  final String VC_INVALID="V_001";//验证码已失效
    public static  final String VC_MATCH_ERROR="V_002";//验证码匹配错误

    //==================验证参数的状态码=============
    public static  final String PARAM_VC_WRONG="P_001";//验证码参数有误
    public static  final String PARAM_VC_KEY_ERROR="P_002";//验证码查询关键词有误
    public static  final String PARAM_VC_KEY_WRONG="P_003";//验证码查询关键词参数有误
    public static  final String PARAM_VC_KEY_EMAIL_WRONG="P_004";//邮箱注册账号和获取验证码的邮箱不匹配
    public static  final String PARAM_USER_TOKEN_WRONG="P_005";//登录userToken有误
    public static  final String PARAM_TOP_WRONG="P_006";//置顶参数有误
    public static  final String PARAM_THING_ID_WRONG="P_007";//小记参数有误
    public static  final String PARAM_DELETE_COMPLETE_WRONG="P_008";//删除参数有误（是否为彻底删除的参数）
    public static  final String PARAM_DELETE_RECYCLE_BIN_WRONG="P_009";//删除参数有误（是否为回收站的操作）
    public static  final String PARAM_THING_TITLE_WRONG="P_010";//小记标题参数有误
    public static  final String PARAM_THING_TOP_WRONG="P_011";//小记置顶参数有误
    public static  final String PARAM_THING_TAGS_WRONG="P_012";//小记标签参数有误
    public static  final String PARAM_THING_CONTENT_WRONG="P_013";//小记内容参数有误
    public static  final String PARAM_THING_FINISHED_WRONG="P_014";//小记完成参数有误
    public static  final String PARAM_ID_WRONG="P_0015";//笔记参数有误
    //==================redis服务的状态码=============
    public static final String REDIS_SERVE_ERROR="R_001";//redis服务错误
    //==================小记业务的状态码=============
    public static final String THING_TOP_SUCCESS="T_001";//置顶小记成功
    public static final String THING_TOP_FAILED="T_002";//置顶小记失败
    public static final String THING_CANCEL_TOP_SUCCESS="T_003";//取消置顶小记成功
    public static final String THING_CANCEL_TOP_FAILED="T_004";//取消置顶小记失败
    public static final String THING_DELETE_SUCCESS="T_005";//删除小记成功
    public static final String THING_COMPLETE_DELETE_SUCCESS="T_006";//彻底删除小记成功
    public static final String THING_CREATE_SUCCESS="T_007";//创建小记成功
    public static final String THING_CREATE_EXCIPITION="T_008";//创建小记异常
    public static final String THING_CREATE_ERROR="T_009";//创建小记错误
    public static final String THING_UPDATE_SUCCESS="T_010";//修改小记成功

    //==================日志业务的状态码=============
    public static  final String LOG_CREATE_EXCEPITION="LOG_001";//日志新增异常
    public static  final String LOG_CREATE_ERROR="LOG_002";//日志新增错误

    //==================笔记业务的状态码=============
    public static final String NOTE_TOP_SUCCESS="N_001";//置顶笔记成功
    public static final String NOTE_TOP_FAILED="N_002";//置顶笔记失败
    public static final String NOTE_CANCEL_TOP_SUCCESS="N_003";//取消置顶笔记成功
    public static final String NOTE_CANCEL_TOP_FAILED="N_004";//取消置顶笔记失败
    public static final String NOTE_DELETE_SUCCESS="N_005";//删除笔记成功
    public static final String NOTE_COMPLETE_DELETE_SUCCESS="N_006";//彻底删除笔记成功
    public static final String NOTE_CREATE_SUCCESS="N_007";//创建笔记成功
    public static final String NOTE_CREATE_EXCIPITION="N_008";//创建笔记异常
    public static final String NOTE_CREATE_ERROR="N_009";//创建笔记错误
    public static final String NOTE_UPDATE_SUCCESS="N_010";//修改笔记成功
}
