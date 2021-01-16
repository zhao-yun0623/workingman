package com.workingman.javaBean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.workingman.javaBean.state.RoleId;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RoleBean implements Serializable {
    private int id;
    private String role;

    public RoleBean() {
    }

    public RoleBean(String role) {
        this.role = role;
        for(RoleId roleId:RoleId.values()){
            if(roleId.getRole().equals(role)){
                this.id=roleId.getValue();
                return;
            }
        }
    }

    public static List<String> getRoles(List<RoleBean> roleBeans){
        List<String> roles=new ArrayList<>();
        for (RoleBean roleBean:roleBeans){
            roles.add(roleBean.getRole());
        }
        return roles;
    }

    /**
     * 判断roleId是否合法
     * @param id：要判断的roleId
     * @reture boolean
     */
    public static Boolean isTureRoleId(int id){
        for(RoleId roleId:RoleId.values()){
            if(roleId.getValue()==id){
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean isMerchant(List<RoleBean> roleBeans){
        for (RoleBean roleBean:
             roleBeans) {
            if(roleBean.getId()==RoleId.MERCHANT.getValue()){
                return true;
            }
        }
        return false;
    }
}
