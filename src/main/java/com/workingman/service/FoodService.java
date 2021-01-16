package com.workingman.service;

import com.alibaba.excel.EasyExcel;
import com.workingman.javaBean.FoodBean;
import com.workingman.javaBean.MerchantBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.javaBean.state.RedisHeader;
import com.workingman.javaBean.state.ResponseState;
import com.workingman.mapper.FoodMapper;
import com.workingman.mapper.UserMapper;
import com.workingman.service.easyexcel.FoodListener;
import com.workingman.service.utils.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FoodService {
    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private Logger logger;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserMapper userMapper;

    /**
     * 查询菜品信息，可筛选
     * @param foodBean：可包含id,name,window
     * @param user：当前登录的用户
     * @return foods
     */
    public ResponseData getFoods(FoodBean foodBean, UserBean user) {
        logger.info(user.getPhone()+"正在查询菜品信息");
        try {
            List<FoodBean> foods=foodMapper.getFoods(foodBean);
            logger.info("查询成功");
            return new ResponseData(ResponseState.SUCCESS.getMessage(), ResponseState.SUCCESS.getValue(),"foods",foods);
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

    public ResponseData insertFoodByExcel(MultipartFile file, UserBean user) {
        logger.info(user.getPhone()+"的管理员正在插入菜品信息");
        FoodListener foodListener=new FoodListener();
        try {
            EasyExcel.read(file.getInputStream(), FoodBean.class,foodListener).sheet().doRead();
            List<FoodBean> foodBeans=foodListener.getFoodList();
            System.out.println(foodBeans);
            List<FoodBean> fails=new ArrayList<>();
            for(FoodBean foodBean:foodBeans){
                try {
                    foodMapper.insertFood(foodBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        } catch (Exception e) {
            logger.error(ResponseState.ERROR.getMessage());
            logger.error(e.getMessage());
            return new ResponseData(ResponseState.ERROR.getMessage(), ResponseState.ERROR.getValue());
        }
    }

}
