package com.workingman.javaBean;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MerchantBean {
    private Integer id;
    private Integer userId;
    @ExcelProperty("窗口")
    private String merchant;
    private List<FoodBean> foods;
}
