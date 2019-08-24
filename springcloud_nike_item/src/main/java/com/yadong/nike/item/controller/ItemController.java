package com.yadong.nike.item.controller;

import com.yadong.nike.bean.PmsProductSaleAttr;
import com.yadong.nike.bean.PmsSkuInfo;
import com.yadong.nike.bean.Result;
import com.yadong.nike.item.feign.ManageFeign;
import com.yadong.nike.passportConfig.annotations.LoginRequired;
import com.yadong.nike.passportConfig.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 14:24
 **/
@Controller
public class ItemController {

    @Autowired
    private ManageFeign managerFeign;

    @RequestMapping("/{skuId}.html")
    @LoginRequired(loginSuccess = false)
    public String toItem(@PathVariable("skuId") String skuId, ModelMap modelMap, HttpServletRequest request){
        PmsSkuInfo pmsSkuInfo = managerFeign.getSkuById(skuId);
        modelMap.put("skuInfo", pmsSkuInfo);
        /*获取当前sku销售属性列表，并且标记isChecked字段*/
        List<PmsProductSaleAttr> pmsProductSaleAttrs = managerFeign.spuSaleAttrListCheckBySku(pmsSkuInfo.getId() , pmsSkuInfo.getProductId());
        modelMap.put("spuSaleAttrListCheckBySku" , pmsProductSaleAttrs);
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        modelMap.put("oldToken", oldToken);
        /*查询当前的sku的spu的其他sku集合的hash表*/


        return "item";
    }

}
