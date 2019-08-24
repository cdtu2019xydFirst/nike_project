package com.yadong.nike.bean;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/23 | 15:16
 **/
public class PmsSearchParam {

    private String catalog3Id;
    private String keyword;
    private List<PmsSkuAttrValue> skuAttrValueList;

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<PmsSkuAttrValue> getSkuAttrValueList() {
        return skuAttrValueList;
    }

    public void setSkuAttrValueList(List<PmsSkuAttrValue> skuAttrValueList) {
        this.skuAttrValueList = skuAttrValueList;
    }
}
