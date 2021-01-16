package com.workingman.javaBean.state;

public enum ResponseState {
    SUCCESS("操作成功",200),
    TOKEN_NOT_PROVIDE("未传入token",101),
    TOKEN_IS_ERROR("token错误",102),
    TOKEN_IS_EXPIRED("token已过期",103),
    REFRESH_TOKEN_IS_ERROR("refreshToken错误",104),
    REFRESH_TOKEN_IS_EXPIRED("refreshToken已过期",105),
    USER_NOT_EXIST("用户不存在",106),
    PASSWORD_IS_ERROR("密码错误",107),
    USER_IS_EXIST("该手机号已被注册",108),
    CODE_NOT_EXIST("验证码未获取或已过期",109),
    CODE_IS_ERROR("验证码错误",110),
    PARAM_IS_ERROR("参数错误",111),
    FILE_FORMAT_ERROR("文件格式错误",112),
    NOT_CAN_GET_FILE_STREAM("获取文件流失败",113),
    ACCOUNT_IS_ILLEGAL("该账号涉嫌违规",114),
    INFORMATION_NOT_EXIST("该用户未完善 v g信息或该用户不存在",115),
    OPERATION_HAI_FINISH("该操作已经完成，请勿重复操作",116),
    WITHOUT_PERMISSION("该用户无此权限",117),
    SOME_INFORMATION_INSERT_ERROR("部分信息插入失败",118),
    EXCEL_IS_ERROR("该EXCEL里没有数据",119),
    USER_IS_NORMAL("该用户状态正常，未被封禁",120),
    HAS_BE_WORKINGMAN("该用户已成为打工人",121),
    APPLY_NOT_EXIST("未提交申请",122),
    THIS_STATE_CANNOT_RECEIVE("当前订单状态无法进行该操作",123),
    OPEN_ID_GET_ERROR("openId获取失败",124),
    PREPAY_ID_GET_ERROR("prepayId获取失败",125),
    INFORMATION_GET_ERROR("信息获取失败",126),
    ORDER_NOT_FINISH("当前尚有订单未完成",127),
    ORDER_CAN_NOT_CANCEL("该订单正在进行，无法取消",128),
    ORDER_NOT_EXIST("该订单不存在",129),
    ORDER_TIME_ERROR("点餐时间为9：00-10：30，3：00-4：30",130),
    ERROR("操作失败",100);

    private String message;
    private int value;
    ResponseState(String message,int value)
    {
        this.message=message;
        this.value=value;
    }

    public String getMessage() {
        return message;
    }

    public int getValue() {
        return value;
    }
}
