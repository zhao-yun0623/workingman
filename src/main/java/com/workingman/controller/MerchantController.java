package com.workingman.controller;

import com.workingman.javaBean.MerchantBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.service.MerchantService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchant")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private Logger logger;

    @GetMapping()
    public ResponseData getFoods(MerchantBean merchantBean, @ModelAttribute("user")UserBean user){
        return merchantService.getFoods(merchantBean,user);
    }
}
