package com.workingman.mapper;

import com.workingman.javaBean.FoodBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodMapper {

    /**
     * 查询菜品（可筛选）
     * @param foodBean：筛选信息
     * @return foods
     */
    List<FoodBean> getFoods(FoodBean foodBean);

    void insertFood(FoodBean foodBean);

    FoodBean getFoodById(Integer foodId);
}
