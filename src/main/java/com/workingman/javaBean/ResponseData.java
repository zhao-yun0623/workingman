package com.workingman.javaBean;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@ApiModel("请求返回类")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {
    @ApiModelProperty("状态信息")
    private String message;
    @ApiModelProperty("状态码")
    private int state;
    @ApiModelProperty("access_token")
    private String token;
    @ApiModelProperty("refresh_token")
    private String refreshToken;
    @ApiModelProperty("返回数据")
    private Map<String,Object> data=new HashMap<>();

    public ResponseData() {
    }

    public ResponseData(String message, int state, String key,Object value) {
        this.message = message;
        this.state = state;
        this.data.put(key,value);
    }

    public ResponseData(String message, int state, String token, String refreshToken, String key, Object value) {
        this.message = message;
        this.state = state;
        this.token = token;
        this.refreshToken=refreshToken;
        this.data.put(key,value);
    }

    public ResponseData(String message, int state) {
        this.message = message;
        this.state = state;
    }

    public void setData(String name, Object value){
        data.put(name,value);
    }

    public void setMessageState(String message, int state) {
        this.message=message;
        this.state=state;
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public void setTokens(String token, String refreshToken) {
        this.token=token;
        this.refreshToken=refreshToken;
    }
}
