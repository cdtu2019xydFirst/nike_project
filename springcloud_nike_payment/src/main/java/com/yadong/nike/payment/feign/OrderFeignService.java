package com.yadong.nike.payment.feign;

import com.yadong.nike.bean.OmsOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/22 | 10:28
 **/
@Component
@FeignClient(value = "springcloud-nike-order")
public interface OrderFeignService {

    @RequestMapping(value = "/getOrderByOutTradeNumber/{outTradeNumber}", method = RequestMethod.GET)
    OmsOrder getOrderByOutTradeNumber(@PathVariable("outTradeNumber") String outTradeNumber);
}
