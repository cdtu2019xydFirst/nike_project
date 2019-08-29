package com.yadong.nike.manage.controller;

import com.yadong.nike.bean.BigDataSkuCatalog;
import com.yadong.nike.bean.PmsSkuInfo;
import com.yadong.nike.bean.Result;
import com.yadong.nike.bean.StatusCode;
import com.yadong.nike.manage.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 13:43
 **/
@RestController
@CrossOrigin
@RequestMapping("/Sku")
public class SkuController {

    @Autowired
    private SkuInfoService skuInfoService;

    @RequestMapping(value = "/addSkuInfo", method = RequestMethod.POST)
    public Result addSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo) {
        skuInfoService.addSkuInfo(pmsSkuInfo);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "/getSkuById/{id}", method = RequestMethod.GET)
    public PmsSkuInfo getSkuById(@PathVariable("id") String id) {
        PmsSkuInfo pmsSkuInfo = skuInfoService.getSkuById(id);
        return pmsSkuInfo;
    }

    /*================================================================================================================================================*/
    /*其他微服务调用区*/
    /*================================================================================================================================================*/

    @RequestMapping(value = "/checkPrice", method = RequestMethod.GET)
    public boolean checkPrice(@RequestParam("productSkuId") String productSkuId, @RequestParam("productPrice") String productPrice) {
        boolean b = skuInfoService.checkPrice(productSkuId, productPrice);
        return b;
    }

    @RequestMapping(value = "/getSkuInfo", method = RequestMethod.GET)
    public List<PmsSkuInfo> getSkuInfo(){
        List<PmsSkuInfo> pmsSkuInfos = skuInfoService.getSkuInfo();
        return pmsSkuInfos;
    }

    @RequestMapping(value = "/getAllType/{skuId}", method = RequestMethod.GET)
    public BigDataSkuCatalog getAllType(@PathVariable("skuId") String skuId){
         BigDataSkuCatalog bigDataSkuCatalog = skuInfoService.getAllType(skuId);
         return bigDataSkuCatalog;
    }

}
