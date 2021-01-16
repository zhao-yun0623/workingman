package com.workingman.javaBean.state;

public enum  RoleId {
    USER("user",1),
    WORKINGMAN("workingman",2),
    MERCHANT("merchant",3),
    ADMIN("admin",4);

    private String role;
    private int value;

    RoleId(String role, int value) {
        this.role = role;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getRole() {
        return role;
    }
}
