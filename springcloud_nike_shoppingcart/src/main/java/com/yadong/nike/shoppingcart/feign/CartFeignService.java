package com.yadong.nike.shoppingcart.feign;

import com.yadong.nike.bean.PmsSkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/21 | 9:22
 **/
@Component
@FeignClient(value = "springcloud-nike-manage")
public interface CartFeignService {

    @RequestMapping(value = "/Sku/getSkuById/{id}", method = RequestMethod.GET)
    PmsSkuInfo getSkuById(@PathVariable("id") String id);

}
