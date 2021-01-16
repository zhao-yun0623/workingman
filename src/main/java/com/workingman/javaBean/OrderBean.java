package com.workingman.javaBean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class OrderBean {
    public interface Insert{

    }
    public interface Query{

    }

    /**
     * 订单编号
     */
    public interface Number{

    }

    /**
     * 订单编号、状态不能为空
     */
    public interface NumState{

    }
    private Integer id;
    private Integer userId;
    @NotNull(message = "用户姓名不能为空",groups = {Insert.class})
    private String userName;
    @NotNull(message = "用户手机号不能为空",groups = {Insert.class})
    private String userPhone;
    @NotNull(message = "区域不能为空",groups = {Insert.class})
    private String place;
    @NotNull(message = "楼号不能为空",groups = {Insert.class})
    private String tower;
    @NotNull(message = "宿舍号不能为空",groups = {Insert.class})
    private String dorm;
    @NotNull(message = "食物号不能为空",groups = {Insert.class})
    private Integer foodId;
    private String foodName;
    private Double price;
    private Integer merchantId;
    private String merchantName;
    @Min(value = 1,message = "类型只能为1或2",groups = {Integer.class,Query.class})
    @Max(value = 2,message = "类型只能为1或2",groups = {Integer.class,Query.class})
    @NotNull(message = "类型不能为空",groups = {Insert.class})
    private Short type;
    @NotNull(message = "状态不能为空",groups = {NumState.class})
    private Integer state;
    @NotNull(message = "订单编号不能为空",groups = {Number.class,NumState.class})
    private String number;
    private String code;
    private Integer laborerId;
    private String laborerPhone;
    private String comment;
    private Double reward;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp startTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp merchantTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp laborerTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp getTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp sendTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp endTime;


    public void setFoodInformation(FoodBean foodBean){
        this.foodName=foodBean.getName();
        this.price=foodBean.getPrice();
        this.merchantId=foodBean.getMerchantId();
        this.merchantName=foodBean.getMerchantName();
    }
}
