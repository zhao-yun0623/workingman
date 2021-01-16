package com.workingman.controller;

import com.workingman.javaBean.FoodBean;
import com.workingman.javaBean.ResponseData;
import com.workingman.javaBean.UserBean;
import com.workingman.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/food")
public class FoodController {
    @Autowired
    private FoodService foodService;

    /**
     * 查询菜品信息，可筛选
     * @param foodBean：可包含id,name,window
     * @param user：当前登录的用户
     * @return foods
     */
    @GetMapping
    public ResponseData getFoods(@Validated(FoodBean.MerchantId.class) FoodBean foodBean, @ModelAttribute("user")UserBean user){
        return foodService.getFoods(foodBean,user);
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/file")
    public ResponseData insertFoodByExcel(@RequestParam(name = "file") MultipartFile file,@ModelAttribute("user")UserBean user){
        return foodService.insertFoodByExcel(file,user);
    }
}
