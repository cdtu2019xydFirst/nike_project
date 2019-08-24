package com.yadong.nike.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/24 | 15:10
 **/
@Component
@FeignClient(value = "springcloud-nike-activemq")
public interface ActiveMQFeignService {

    @RequestMapping(value = "/sendPayment_success_queue/{outTradeNumber}", method = RequestMethod.GET)
    void sendPayment_success_queue(@PathVariable("outTradeNumber") String outTradeNumber);

    @RequestMapping(value = "/sendPayment_checked_queue", method = RequestMethod.GET)
    void sendPayment_checked_queue(@RequestParam("outTradeNumber") String outTradeNumber, @RequestParam("count") Integer count);
}
