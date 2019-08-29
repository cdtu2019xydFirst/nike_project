package com.yadong.nike.item.feign;

import com.yadong.nike.bean.BigDataSkuCatalog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/28 | 9:49
 **/
@Component
@FeignClient(value = "springcloud-nike-manage")
public interface BigDataFeignService {

    @RequestMapping(value = "/Sku/getAllType/{skuId}", method = RequestMethod.GET)
    BigDataSkuCatalog getAllType(@PathVariable("skuId") String skuId);
}
