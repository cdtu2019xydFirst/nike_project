package com.yadong.nike.bean;

import java.io.Serializable;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/29 | 13:05
 **/
public class Nikedata implements Serializable {

    private String reportTime;
    private String pv;
    private String uv;
    private String newIp;
    private String newUser;

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public String getUv() {
        return uv;
    }

    public void setUv(String uv) {
        this.uv = uv;
    }

    public String getNewIp() {
        return newIp;
    }

    public void setNewIp(String newIp) {
        this.newIp = newIp;
    }

    public String getNewUser() {
        return newUser;
    }

    public void setNewUser(String newUser) {
        this.newUser = newUser;
    }
}
