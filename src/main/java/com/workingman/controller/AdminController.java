package com.workingman.controller;



import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.service.AdminService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@Validated
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private Logger logger;
    /**
     * 管理員修改用戶的密碼
     * @param userBean：需要修改密碼的用戶，包括phone,password
     * @param user：當前登錄的管理員
     * @return responseData
     */
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/password")
    public ResponseData changePasswordByAdmin(@Validated(value = UserBean.Insert.class) @RequestBody UserBean userBean, @ApiIgnore @ModelAttribute("user") UserBean user){
        return adminService.changePasswordByAdmin(userBean,user);
    }

    /**
     * 修改用户状态
     * @param userBean：包括用户ID或手机号，state
     * @param user：当前登录的用户
     * @return responseData
     */
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/state")
    public ResponseData changeState(@Validated(value = UserBean.ID.class) @RequestBody UserBean userBean,@ApiIgnore @ModelAttribute("user") UserBean user){
        return adminService.changeState(userBean,user);
    }

    /**
     * 查寻用户
     * @param userBean：可包含id,phone,state,roleId
     * @param user：当前登录的用户
     * @return users
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping()
    public ResponseData getUsers(UserBean userBean,@ApiIgnore @ModelAttribute("user") UserBean user){
        return adminService.getUsers(userBean,user);
    }

    /**
     * 插入商户账号
     * @param userBean：商户账号
     * @param user：当前登录的用户
     * @return responseData
     */
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/merchant")
    public ResponseData insertMerchant(@Validated(UserBean.Insert.class)@RequestBody UserBean userBean,@ModelAttribute("user") UserBean user){
        try {
            return adminService.insertMerchant(userBean,user);
        } catch (Exception e) {
            logger.error("操作失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

}
