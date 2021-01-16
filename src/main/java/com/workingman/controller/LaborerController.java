package com.workingman.controller;

import com.workingman.javaBean.InformationBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.service.LaborerService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/laborer")
public class LaborerController {
    @Autowired
    private LaborerService laborerService;
    @Autowired
    private Logger logger;

    /**
     * 申请成为打工人
     * @param user：当前登录的用户
     * @return responseData
     */
    @PostMapping
    public ResponseData applyForLaborer(@RequestParam(name = "face",required = true) MultipartFile face,@RequestParam(name = "identification",required = true) MultipartFile identification, @ModelAttribute("user")UserBean user){
        return laborerService.applyForLaborer(face,identification,user);
    }

    /**
     * 审核打工人的申请
     * @param param：包含申请人ID，state(1为通过，-1为拒绝)
     * @param user：当前登录的用户
     * @return responseData
     */
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/check")
    public ResponseData checkLaborer(@RequestBody Map<String,Integer> param,@ModelAttribute("user")UserBean user){
        try {
            return laborerService.checkLaborer(param.get("userId"),param.get("state"),user);
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    /**
     * 根据申请状态查询用户信息
     * @param state：申请状态
     * @param user：当前登录的用户
     * @return inforamtions
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping
    public ResponseData getApplyUsers(int state,@ModelAttribute("user")UserBean user){
        return laborerService.getApplyUsers(state,user);
    }



}
