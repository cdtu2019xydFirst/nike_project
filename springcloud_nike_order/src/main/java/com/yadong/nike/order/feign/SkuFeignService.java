package com.yadong.nike.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/21 | 20:39
 **/
@Component
@FeignClient(value = "springcloud-nike-manage")
public interface SkuFeignService {

    @RequestMapping(value = "/Sku/checkPrice", method = RequestMethod.GET)
    boolean checkPrice(@RequestParam("productSkuId") String productSkuId, @RequestParam("productPrice") String productPrice);

}
