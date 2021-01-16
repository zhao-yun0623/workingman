package com.workingman.mapper;


import com.workingman.javaBean.InformationBean;
import com.workingman.javaBean.UserBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationMapper {

    void unbindUser(int userId);

    void delInformation(int id);

    void postInformation(InformationBean informationBean);

    InformationBean getInformation(UserBean userBean);

    Boolean updateInformation(InformationBean informationBean);

    /**
     * 修改信息中的手机号
     * @param phone
     * @param userId
     */
    void changePhone(@Param("phone")String phone, @Param("userId")int userId);

    /**
     * 通过ID获取信息
     * @param userId
     * @return
     */
    InformationBean getInformationById(Integer userId);

    /**
     * 修改打工人申请状态
     * @param userId
     * @param state
     */
    void updateState(@Param("userId") Integer userId, @Param("state") Integer state);
}
