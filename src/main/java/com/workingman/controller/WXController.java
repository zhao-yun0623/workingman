package com.workingman.controller;

import com.alibaba.fastjson.JSONObject;
import com.workingman.config.WxConfig;
import com.workingman.service.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/wx")
public class WXController {

    /**
     * 获取access_token
     *
     * @param code
     * @return
     */
    @GetMapping("openId")
    public String getOpenId(String code) {
//        loadData();
        String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
        String param = "appid=" + WxConfig.appId + "&secret=" + WxConfig.appSecret + "&code="
                + code + "&grant_type=authorization_code";
        String result = HttpRequest.sendGet(accessTokenUrl, param);
        Map<String,String> res = (Map<String, String>) JSONObject.parse(result);
        System.out.println(res);
        String openid = res.get("openid") + "";
        return openid;
    }
}
