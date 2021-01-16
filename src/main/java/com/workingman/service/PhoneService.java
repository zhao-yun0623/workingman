package com.workingman.service;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class PhoneService {
    public void userGetFood(String number, String code,String phone) {
        Code codeStr=new Code();
        codeStr.setNumber(number);
        codeStr.setCode(Integer.parseInt(code));
        String json=JSON.toJSONString(code);
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4FkA5LtoWfbGFkRbRhkg", "Dl8gjhWT8lyTw3dWOvtiCBc0BzolSK");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "WorkingMan平台");
        request.putQueryParameter("TemplateCode", "SMS_206561429");
        request.putQueryParameter("TemplateParam", json);
        System.out.println(json);
        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Code{
        private Integer code;
        private String phone;
        private String number;
        public Code() {
            code=randCode();
        }

        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
        public String getStringCode(){
            return new String(String.valueOf(code));
        }
    }
    public String getLoginCode(String phone) throws ClientException {
        Code code=new Code();
        String codeJson= JSON.toJSONString(code);
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4FkA5LtoWfbGFkRbRhkg", "Dl8gjhWT8lyTw3dWOvtiCBc0BzolSK");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "WorkingMan平台");
        request.putQueryParameter("TemplateCode", "SMS_206546234");
        request.putQueryParameter("TemplateParam", codeJson);
        System.out.println(codeJson);
        CommonResponse response = client.getCommonResponse(request);
        System.out.println(response.getData());
        return code.getStringCode();

    }
    public String getRegisterCode(String phone) throws ClientException {
        Code code=new Code();
        String codeJson= JSON.toJSONString(code);
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4FkA5LtoWfbGFkRbRhkg", "Dl8gjhWT8lyTw3dWOvtiCBc0BzolSK");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "WorkingMan平台");
        request.putQueryParameter("TemplateCode", "SMS_206546236");
        request.putQueryParameter("TemplateParam", codeJson);
        System.out.println(codeJson);
        CommonResponse response = client.getCommonResponse(request);
        System.out.println(response.getData());
        return code.getStringCode();

    }
    public String getForgetCode(String phone) throws ClientException {
        Code code=new Code();
        String codeJson= JSON.toJSONString(code);
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4FkA5LtoWfbGFkRbRhkg", "Dl8gjhWT8lyTw3dWOvtiCBc0BzolSK");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "WorkingMan平台");
        request.putQueryParameter("TemplateCode", "SMS_206546237");
        request.putQueryParameter("TemplateParam", codeJson);
        System.out.println(codeJson);
        CommonResponse response = client.getCommonResponse(request);
        System.out.println(response.getData());
        return code.getStringCode();

    }
    private Integer randCode(){
        int code= (int) ((Math.random()*9+1)*100000);
        return code;
    }
}
