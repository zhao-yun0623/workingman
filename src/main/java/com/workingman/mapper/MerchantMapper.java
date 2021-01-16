package com.workingman.mapper;

import com.workingman.javaBean.MerchantBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantMapper {
    List<MerchantBean> getFoods(MerchantBean merchantBean);
}
