package com.workingman.service;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.workingman.javaBean.InformationBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.javaBean.state.RoleId;
import com.workingman.mapper.InformationMapper;
import com.workingman.mapper.UserMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class InformationService {
    @Autowired
    private InformationMapper informationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private Logger logger;
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * 用户完善个人信息
     * @param informationBean：用户信息
     * @param user：当前登录的用户
     * @return responseData
     */
    public ResponseData postInformation(InformationBean informationBean, UserBean user) {
        logger.info("手机号为"+user.getPhone()+"的用户正在完善个人信息");
        try {
            informationBean.setUserId(user.getId());
            informationBean.setPhone(user.getPhone());
            informationMapper.postInformation(informationBean);
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
        } catch (DuplicateKeyException e) {
            logger.warn("信息已被完善，请勿重复完善");
            logger.warn(e.getMessage());
            return new ResponseData(ResponseState.OPERATION_HAI_FINISH.getMessage(),ResponseState.OPERATION_HAI_FINISH.getValue());
        } catch (Exception e) {
            logger.error("发生错误");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 查询个人信息
     * @param userBean：要查询的用户信息，为空表示查询个人信息，可包含id,phone
     * @param user：当前登录的用户
     * @return informationBean
     */
    public ResponseData getInformation(UserBean userBean, UserBean user) {
        if(userBean.getId()==null&&userBean.getPhone()==null){
            logger.info("手机号为"+user.getPhone()+"的用户正在查询个人信息");
            userBean=new UserBean();
            userBean.setPhone(user.getPhone());
        }else {
            logger.info("手机号为"+user.getPhone()+"的用户正在查询手机号为"+userBean.getPhone()+"或userId为"+userBean.getId()+"用户的个人信息");
        }
        try {
            InformationBean informationBean=informationMapper.getInformation(userBean);
            if(informationBean==null){
                logger.warn(ResponseState.INFORMATION_NOT_EXIST.getMessage());
                return new ResponseData(ResponseState.INFORMATION_NOT_EXIST.getMessage(),ResponseState.INFORMATION_NOT_EXIST.getValue());
            }
            logger.info("查询成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue(),"information",informationBean);
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 修改个人信息
     * @param informationBean：至少包括一个要修改的参数
     * @param user：当前登录的用户
     * @return responseData
     */
    public ResponseData updateInformation(InformationBean informationBean, UserBean user) {
        logger.info(user.getPhone()+"的用户正在修改个人信息");
        informationBean.setPhone(user.getPhone());
        try {
            Boolean isSuccess=informationMapper.updateInformation(informationBean);
            if(!isSuccess){
                logger.warn(ResponseState.INFORMATION_NOT_EXIST.getMessage());
                return new ResponseData(ResponseState.INFORMATION_NOT_EXIST.getMessage(),ResponseState.INFORMATION_NOT_EXIST.getValue());
            }
            logger.info("修改成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
        } catch(BadSqlGrammarException e){
            logger.error("未传入要修改的参数");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.PARAM_IS_ERROR.getMessage(),ResponseState.PARAM_IS_ERROR.getValue());
        } catch(Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

}
