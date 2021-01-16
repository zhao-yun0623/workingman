package com.workingman.controller;


import com.workingman.javaBean.InformationBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.service.InformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;

@Api(value = "信息模块")
@RestController
@RequestMapping("/information")
public class InformationController {
    @Autowired
    private InformationService informationService;
    @Autowired
    private Logger logger;
    /**
     * 用户完善个人信息
     * @param informationBean：用户信息
     * @param user：当前登录的用户
     * @return responseData
     */
    @ApiOperation("完善个人信息")
    @PostMapping
    public ResponseData postInformation(@Validated(value = InformationBean.Insert.class) @RequestBody InformationBean informationBean, @ApiIgnore @ModelAttribute("user") UserBean user){
        return informationService.postInformation(informationBean,user);
    }

    /**
     * 查询个人信息
     * @param userBean：要查询的用户信息，为空表示查询个人信息，可包含id,phone
     * @param user：当前登录的用户
     * @return informationBean
     */
    @ApiOperation("查询用户信息")
    @GetMapping
    public ResponseData getInformation(@ApiParam(name = "user",value ="可为空，可包含id,phone字段")@Validated(UserBean.Query.class) UserBean userBean, @ApiIgnore @ModelAttribute("user")UserBean user){
        return informationService.getInformation(userBean,user);
    }

    /**
     * 修改个人信息
     * @param informationBean：至少包括一个要修改的参数
     * @param user：当前登录的用户
     * @return responseData
     */
    @ApiOperation("修改个人信息")
    @PutMapping
    public ResponseData updateInformation(@ApiParam(name = "information",value ="至少包含一个要修改的字段，phone,userId不能修改" )@Validated(value = InformationBean.Query.class)@RequestBody InformationBean informationBean,@ModelAttribute("user")UserBean user){
        return informationService.updateInformation(informationBean,user);
    }


}
