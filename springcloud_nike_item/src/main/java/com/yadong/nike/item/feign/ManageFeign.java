package com.yadong.nike.item.feign;

import com.yadong.nike.bean.PmsProductSaleAttr;
import com.yadong.nike.bean.PmsSkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 15:41
 **/
@Component
@FeignClient(value = "springcloud-nike-manage")
public interface ManageFeign {

    @RequestMapping(value = "/Sku/getSkuById/{id}", method = RequestMethod.GET)
    PmsSkuInfo getSkuById(@PathVariable("id") String id);

    @RequestMapping(value = "/Product/spuSaleAttrListCheckBySku", method = RequestMethod.GET)
    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(@RequestParam("skuId") String skuId, @RequestParam("productId") String productId);
}
