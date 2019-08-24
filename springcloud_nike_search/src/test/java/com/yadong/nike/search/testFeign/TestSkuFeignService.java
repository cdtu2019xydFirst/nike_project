package com.yadong.nike.search.testFeign;

import com.yadong.nike.bean.PmsSkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/23 | 8:21
 **/
@Component
@FeignClient(value = "springcloud-nike-manage")
public interface TestSkuFeignService {


    @RequestMapping(value = "/Sku/getSkuInfo", method = RequestMethod.GET)
    List<PmsSkuInfo> getSkuInfo();

}
