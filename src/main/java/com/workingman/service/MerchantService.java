package com.workingman.service;

import com.workingman.javaBean.MerchantBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.mapper.MerchantMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private Logger logger;

    public ResponseData getFoods(MerchantBean merchantBean, UserBean user) {
        logger.info(user.getPhone()+"正在查询商户的菜品信息");
        try {
            List<MerchantBean> merchants=merchantMapper.getFoods(merchantBean);
            logger.info("查询成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue(),"merchants",merchants);
        } catch (Exception e) {
            logger.error("查询失败");
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }


}
