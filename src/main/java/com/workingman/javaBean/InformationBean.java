package com.workingman.javaBean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class InformationBean {

    /**
     * 只检验格式，不检验是否为空
     */
    public interface Query{

    }

    /**
     * 验证打工人身份申请
     */
    public interface Laborer{

    }
    /**
     * 验证所有字段
     */
    public interface Insert{

    }
    public interface Update{

    }
    @Min(value = 1,message = "ID值不能小于1",groups = UserBean.Query.class)
    private Integer id;
    @Min(value = 1,message = "用户ID值不能小于1",groups = UserBean.Query.class)
    private Integer userId;
    @NotNull(message = "姓名不能为null",groups = {Insert.class})
    private String name;
    private String phone;
    @NotNull(message = "性别不能为null",groups = {Insert.class})
    private Short sex;
    @Pattern(regexp = "^[0-9]{10}$",message = "学号格式错误",groups = {Insert.class,Query.class})
    @NotNull(message = "学号不能为null",groups = {Insert.class})
    private String stuNum;
    @NotNull(message = "区域不能为null",groups = {Insert.class})
    private String place;
    @NotNull(message = "楼号不能为null",groups = {Insert.class})
    private String tower;
    @NotNull(message = "宿舍号不能为null",groups = {Insert.class})
    private String dorm;
    @NotNull(message = "打工人人脸照片不能为null",groups = {Laborer.class})
    private String face;
    @NotNull(message = "打工人证件照片不能为null",groups = {Laborer.class})
    private String identification;
    /**
     * 用于打工人身份审核，0为未申请，2为正在申请，1为申请成功，-1为申请失败
     */
    private Integer state;
}
