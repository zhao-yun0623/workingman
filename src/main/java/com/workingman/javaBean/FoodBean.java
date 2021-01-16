package com.workingman.javaBean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodBean {
    public interface MerchantId{

    }
    private Integer id;
    @ExcelProperty("窗口")
    private String merchant;
    @NotNull(message = "merchantId不能为空",groups = {MerchantId.class})
    private Integer merchantId;
    @ExcelProperty("菜品")
    private String name;
    @ExcelProperty("价格")
    private double price;
    private String merchantName;
}
