package com.workingman.mapper;


import com.workingman.javaBean.RoleBean;
import com.workingman.javaBean.UserBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户管理dao层
 *
 * @author 赵云
 * @date 2020/09/10
 */

@Repository
public interface UserMapper {
    /**
     * 通过手机号查询用户
     * @param phone：手机号
     * @return user
     */
    UserBean getUserByPhone(String phone);

    /**
     * 插入新用户
     * @param userBean：用户信息
     */
    void insertUser(UserBean userBean);

    /**
     * 修改密码
     * @param userBean：包含id和手机号
     */
    Boolean changePassword(UserBean userBean);


    /**
     * 为用户插入角色
     * @param userId：用户ID
     * @param roleId：角色ID
     */
    void insertRole(@Param("userId") int userId, @Param("roleId") int roleId);

    /**
     * 获取用户的角色信息
     * @param userId ：用户ID
     * @return String[]
     */
    List<RoleBean> getRoles(int userId);

    /**
     * 更换手机号
     * @param userBean：包括用户ID，要更换成的手机号
     */
    void changePhone(UserBean userBean);

    Boolean changeState(UserBean userBean);

    List<UserBean> getUsers(UserBean userBean);

    Boolean changeRole(@Param("userId") int userId, @Param("roleId") int roleId);
}
