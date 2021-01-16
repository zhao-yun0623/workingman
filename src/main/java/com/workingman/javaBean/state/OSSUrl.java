package com.workingman.javaBean.state;

public enum OSSUrl {
    LABORER("laborer/");
    private String url;

    OSSUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
