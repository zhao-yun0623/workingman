package com.workingman.service;


import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.RoleBean;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.RedisHeader;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.javaBean.state.RoleId;
import com.workingman.mapper.UserMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private Logger logger;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 管理員修改用戶的密碼
     * @param userBean：需要修改密碼的用戶，包括phone,password
     * @param user：當前登錄的管理員
     * @return responseData
     */
    public ResponseData changePasswordByAdmin(UserBean userBean, UserBean user) {
        logger.info("手机号为"+user.getPhone()+"的管理员正在修改手机号为"+userBean.getPhone()+"的用户的密码");
        try {
            userBean.setPassword(passwordEncoder.encode(userBean.getPassword()));
            Boolean isSuccess=userMapper.changePassword(userBean);
            if(isSuccess){
                logger.info("修改成功");
                return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
            }else {
                logger.warn("该用户不存在");
                return new ResponseData(ResponseState.USER_NOT_EXIST.getMessage(),ResponseState.USER_NOT_EXIST.getValue());
            }
        } catch (Exception e) {
            logger.error("系統錯誤");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     * 改变用户的状态，1为正常，-1为违规
     * @param userBean：包括state,至少包括id和phone的一种
     * @param user：当前登录的管理员
     * @return responseData
     */
    public ResponseData changeState(UserBean userBean, UserBean user) {
        logger.info("手机号为"+user.getPhone()+"的管理员正在修改手机号为"+userBean.getPhone()+"的用户的状态");
        try {
            if(userBean.getId()==null&&userBean.getPhone()==null){
                logger.warn("id和phone都为空");
                return new ResponseData(ResponseState.PARAM_IS_ERROR.getMessage(), ResponseState.PARAM_IS_ERROR.getValue());
            }
            if(userBean.getState()!=1&&userBean.getState()!=-1){
                logger.error("参数state传入值错误");
                return new ResponseData("参数state传入值错误",ResponseState.PARAM_IS_ERROR.getValue());
            }
            Boolean isSuccess=userMapper.changeState(userBean);
            if(userBean.getState()==-1){
                stringRedisTemplate.delete(RedisHeader.TOKEN.getHeader()+userBean.getPhone());
                stringRedisTemplate.delete(RedisHeader.REGISTER_CODE.getHeader()+userBean.getPhone());
                stringRedisTemplate.delete(RedisHeader.ROLE.getHeader()+userBean.getPhone());
            }
            if(isSuccess){
                logger.info("修改成功");
                return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue());
            }else {
                logger.warn("该用户不存在");
                return new ResponseData(ResponseState.USER_NOT_EXIST.getMessage(),ResponseState.USER_NOT_EXIST.getValue());
            }
        } catch (Exception e) {
            logger.error("系统错误");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    /**
     *获取用户
     * @param userBean：包含筛选信息,id,phone,roleId,state
     * @return users
     */
    public ResponseData getUsers(UserBean userBean,UserBean user) {
        logger.info(user.getPhone()+"的管理员正在查询用户");
        try {
            List<UserBean> users=userMapper.getUsers(userBean);
            logger.info(ResponseState.SUCCESS.getMessage());
            return new ResponseData(ResponseState.SUCCESS.getMessage(),ResponseState.SUCCESS.getValue(),"users",users);
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(),ResponseState.ERROR.getValue());
        }
    }

    public ResponseData insertMerchant(UserBean userBean, UserBean user) {
        logger.info(user.getPhone()+"的管理员正在插入商户账号");
        userBean.setPassword(passwordEncoder.encode(userBean.getPassword()));
        //判断是否该手机号以被用户注册，若被注册，修改密码，未注册则进行注册
        UserBean odlUser=userMapper.getUserByPhone(userBean.getPhone());
        if(odlUser!=null){
            userMapper.changePassword(userBean);
            userBean.setId(odlUser.getId());
        }else {
            userMapper.insertUser(userBean);
        }
        userMapper.insertRole(userBean.getId(), RoleId.MERCHANT.getValue());
        logger.info("创建成功");
        return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue());
    }



}
