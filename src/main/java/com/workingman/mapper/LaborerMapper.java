package com.workingman.mapper;

import com.workingman.javaBean.InformationBean;
import com.workingman.javaBean.ResponseData;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Repository
public interface LaborerMapper {


    void applyForLaborer(InformationBean informationBean);

    /**
     * 查询不同申请状态的打工人
     * @param state
     * @return
     */
    List<InformationBean> getApplyUsers(int state);
}
