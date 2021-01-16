package com.workingman.mapper;

import com.workingman.javaBean.OrderBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {
    void postOrder(OrderBean orderBean);

    /**
     * 获取商户的订单
     * @param orderBean
     * @return
     */
    List<OrderBean> merchantGetOrders(OrderBean orderBean);

    /**
     * 获取用户的订单
     * @param orderBean
     * @return
     */
    List<OrderBean> userGetOrders(OrderBean orderBean);

    /**
     * 修改订单
     * @param orderBean
     */
    void changeOrder(OrderBean orderBean);

    OrderBean getOrderByNumber(String number);

    List<OrderBean> getLaborerOrder();

    List<OrderBean> getLaborerOwnOrder(OrderBean orderBean);

    List<OrderBean> getNowOrder(int userId);
}
