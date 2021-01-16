package com.workingman.javaBean.state;

public enum OrderState {
    START(0),//订单开始
    COPY_SUCCESS(1),//已支付
    MERCHANT_GET_ORDER(2),//商家已接单
    WORKINGMAN_GET_ORDER(3),//打工人已接单
    FOOD_HAS_COOK(4),//请取餐
    WORKINGMAN_GET_FOOD(5),//打工人已取餐
    ORDER_FINISH(6),//订单完成
    USER_NOT_GET_FOOD(7),//用户未到商户处取餐
    WORKINGMAN_IS_FAIL(8),//打工人未按规定时间送达
    COPY_ERROR(9),//支付失败
    MERCHANT_NOT_GET_ORDER(10),//商家拒绝接单
    ORDER_HAS_CANCEL(11);//用户已取消订单

    private int value;

    OrderState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
