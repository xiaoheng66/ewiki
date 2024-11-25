package com.yhzsk.wiki.resp;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserLoginResp {
    private Long id;

    private String token;


    private String loginName;


    private String name;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "UserLoginResp{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", loginName='" + loginName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}