package com.yadong.nike.order.mqListener;

import com.yadong.nike.order.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/24 | 16:55
 **/
@Component/*让springboot启动时扫描此类*/
public class OrderServiceMqListener {

    @Autowired
    private OrderService orderService;

    @JmsListener(destination = "PAYMENT_SUCCESS_QUEUE", containerFactory = "jmsQueueListener")
    public void consumPaymentSuccessResult(MapMessage mapMessage) throws JMSException {
        String outTradeNumber = mapMessage.getString("out_trade_no");
        if (StringUtils.isNotBlank(outTradeNumber)) {
            orderService.updataPaymentStatus(outTradeNumber);
        } else {
            return;
        }
    }
}
