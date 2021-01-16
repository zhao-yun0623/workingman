package com.workingman.javaBean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.workingman.javaBean.state.RoleId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("user类")
public class UserBean {

    /**
     * 只检查格式，不管是否为空
     */
    public interface Query{

    }

    public interface Insert{

    }

    /**
     * 验证手机号
     */
    public interface Phone{

    }

    /**
     * 验证手机号是否为空、格式，密码是否为空、格式
     */
    public interface Update{

    }
    public interface ID{

    }
    @Min(value = 1,message = "ID值不能小于1",groups = Query.class)
    private Integer id;
    @NotNull(message = "用户名不能为空",groups = {Insert.class})
    private String name;
    @ApiModelProperty("手机号")
    @NotNull(message ="手机号不能为空",groups = {Insert.class,Update.class,Phone.class})
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式错误",groups = {Insert.class,Update.class,Phone.class,Query.class})
    private String phone;
    @ApiModelProperty("密码")
    @NotNull(message = "密码不能为空",groups = {Insert.class,Update.class})
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$",message = "密码长度必须在8-16位之间,至少1个大写字母，1个小写字母和1个数字,不能包含特殊字符",groups = {Insert.class,Update.class,Query.class})
    private String password;
    @ApiModelProperty("短信验证码")
    private String code;
    @JsonIgnore
    private int roleId;
    @ApiModelProperty(value = "用户角色信息",hidden = true)
    private List<RoleBean> roles;
//    private String wxCode;
    @ApiModelProperty(value = "用户状态",example = "100")
    private int state;

    public UserBean() {
    }

    public UserBean(int id, String phone) {
        this.id = id;
        this.phone = phone;
    }

    public UserBean(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    /**
     * 获取角色名
     * @return roleNames
     */
    @JsonIgnore
    public String[] getRolesName(){
        String[] roleNames=new String[roles.size()];
        for (int i=0;i<roles.size();i++){
            roleNames[i]=roles.get(i).getRole();
        }
        return roleNames;
    }

    public List<RoleBean> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleBean> roles) {
        this.roles = roles;
    }

    //

}
