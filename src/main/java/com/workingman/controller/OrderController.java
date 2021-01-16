package com.workingman.controller;

import com.workingman.javaBean.OrderBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.service.OrderService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private Logger logger;

    /**
     * 生成订单
     * @param orderBean
     * @param user
     * @return
     */
    @PreAuthorize("hasRole('user')")
    @PostMapping
    public ResponseData postOrder(@Validated(OrderBean.Insert.class) @RequestBody OrderBean orderBean, @ModelAttribute("user")UserBean user){
        return orderService.postOrder(orderBean,user);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('user')")
    public ResponseData cancelOrder(@Validated(OrderBean.Number.class) OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.cancelOrder(orderBean,user);
    }
    /**
     * 商户订单查询
     * @param orderBean
     * @param user
     * @return
     */
    @PreAuthorize("hasRole('merchant')")
    @GetMapping("/merchant")
    public ResponseData merchantGetOrders(OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.merchantGetOrders(orderBean,user);
    }

    /**
     * 用户订单查询
     * @param orderBean
     * @param user
     * @return
     */
    @PreAuthorize("hasRole('user')")
    @GetMapping("/user")
    public ResponseData userGetOrders(OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.userGetOrders(orderBean,user);
    }

    /**
     * 商户接单
     * @param orderBean
     * @param user
     * @return
     */
    @PreAuthorize("hasRole('merchant')")
    @PostMapping("/merchant")
    public ResponseData merchantReceiveOrder(@Validated(OrderBean.NumState.class)@RequestBody OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.merchantReceiveOrder(orderBean,user);
    }

    /**
     * 打工人接单
     * @param orderBean
     * @param user
     * @return
     */
    @PreAuthorize("hasRole('workingman')")
    @PostMapping("/laborer")
    public ResponseData laborerReceiveOrder(@Validated(OrderBean.Number.class)@RequestBody OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.laborerReceiveOrder(orderBean,user);
    }

    /**
     * 打工人查询
     * @param orderBean
     * @param user
     * @return
     */
    @PreAuthorize("hasRole('workingman')")
    @GetMapping("/laborer/my")
    public ResponseData getLaborerOwnOrder(OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.getLaborerOwnOrder(orderBean,user);
    }

    @PreAuthorize("hasRole('workingman')")
    @GetMapping("/laborer")
    public ResponseData getLaborerOrder(OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.getLaborerOrder(orderBean,user);
    }

    /**
     * 商家通知取餐
     * @param orderBean
     * @param user
     * @return
     */
    @PreAuthorize("hasAnyRole('merchant')")
    @PostMapping("/merchant/getFood")
    public ResponseData changeStateToGetFood(@Validated({OrderBean.Number.class})@RequestBody OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.changeStateToGetFood(orderBean,user);
    }

    /**
     * 已取餐
     * @param orderBean
     * @param user
     * @return
     */
    @PreAuthorize("hasAnyRole('merchant')")
    @PostMapping("/merchant/finish")
    public ResponseData merchantFinishFood(@Validated({OrderBean.Number.class})@RequestBody OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.merchantFinishFood(orderBean,user);
    }

    /**
     * 代取人完成订单
     * @param orderBean
     * @param user
     * @return
     */
    @PreAuthorize("hasRole('workingman')")
    @PostMapping("/laborer/finish")
    public ResponseData finishOrder(@Validated({OrderBean.Number.class})@RequestBody OrderBean orderBean,@ModelAttribute("user")UserBean user){
        return orderService.finishOrder(orderBean,user);
    }
}
