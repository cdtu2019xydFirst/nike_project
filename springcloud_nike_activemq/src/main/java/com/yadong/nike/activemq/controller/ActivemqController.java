package com.yadong.nike.activemq.controller;

import com.yadong.nike.activemq.service.ActiveMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/24 | 14:50
 **/
@Controller
public class ActivemqController {

    @Autowired
    private ActiveMQService activeMQService;

    @RequestMapping(value = "/sendPayment_success_queue/{outTradeNumber}", method = RequestMethod.GET)
    @ResponseBody
    public void sendPayment_success_queue(@PathVariable("outTradeNumber") String outTradeNumber) {
        activeMQService.sendPayment_success_queue(outTradeNumber);
        return;
    }

    @RequestMapping(value = "/sendPayment_checked_queue", method = RequestMethod.GET)
    @ResponseBody
    public void sendPayment_checked_queue(@RequestParam("outTradeNumber") String outTradeNumber, @RequestParam("count") Integer count) {
        activeMQService.sendPayment_checked_queue(outTradeNumber, count);
        return;
    }

}
