package com.yadong.nike.bean;

/**
 * @author : 熊亚东
 * @description :大数据模拟地区和运营商
 * @date : 2019/8/15 | 14:27
 **/
public class AreaAndNetwork {

    private String country = "中国";
    private String province = "四川省";
    private String city = "成都市";
    private String region = "郫都区";
    private String DetailedAddress = "郫都区中信大道二段一号";
    private String network = "中国电信";

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getDetailedAddress() {
        return DetailedAddress;
    }

    public String getNetwork() {
        return network;
    }
}
