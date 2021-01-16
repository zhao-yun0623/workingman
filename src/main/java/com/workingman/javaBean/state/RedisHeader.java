package com.workingman.javaBean.state;

public enum RedisHeader {
    TOKEN("token"),
    REFRESH_TOKEN("refreshToken"),
    REGISTER_CODE("registerCode"),
    LOGIN_CODE("loginCode"),
    FORGET_CODE("forget_code"),
    LOGOFF_CODE("logoff_code"),
    CHANGE_PHONE_CODE("change_phone_code"),
    ROLE("role"),//用户角色
    ORDER_CODE("order_code"),
    OPEN_ID("open_id"),
    ;
    private String header;

    RedisHeader(String header) {
        this.header = "workingman"+header;
    }

    public String getHeader() {
        return header;
    }
}
