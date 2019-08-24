package com.yadong.nike.payment.mqListener;

import com.yadong.nike.bean.PaymentInfo;
import com.yadong.nike.payment.service.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;
import java.util.Map;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/24 | 9:59
 **/
@Component/*让springboot启动时扫描此类*/
public class PaymentServiceMqListener {

    @Autowired
    private PaymentService paymentService;

    @JmsListener(destination = "PAYMENT_CHECKED_QUEUE", containerFactory = "jmsQueueListener")
    public void consumPaymentCheckResult(MapMessage mapMessage) throws JMSException {
        String outTradeNumber = mapMessage.getString("out_trade_no");
        Integer count = mapMessage.getInt("count");
        /*==================================================================================================================================*/
        /*调用阿里的蚂蚁金服的支付查询接口, 检查支付回调结果，在PaymentService实现*/
        /*==================================================================================================================================*/
        Map<String, Object> resultMap = paymentService.checkAlipayPayment(outTradeNumber);
        if (resultMap == null || resultMap.isEmpty()) {
            /*没有结果，继续发送延时消息检查用户支付状态*/
            if (count > 0) {
                count = count - 1;
                paymentService.sendDelayPaymentResultCheckQueue(outTradeNumber, count);
            } else {
                System.out.println("支付宝回调失败，用户支付失败");
                return;
            }
        } else {
            String trade_status = (String) resultMap.get("trade_status");
            /*根据从alipay查询的支付状态结果，判断是否进行下一次的延迟任务还是支付成功更新数据和后续任务*/
            System.err.println(trade_status);
            if (StringUtils.isNotBlank(trade_status) && trade_status.equals("TRADE_SUCCESS")) {
                /*支付成功，更新支付发送支付成功消息队列*/
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setOrderSn(outTradeNumber);
                paymentInfo.setPaymentStatus("已付款");
                paymentInfo.setAlipayTradeNo((String) resultMap.get("trade_no"));
                paymentInfo.setCallbackContent((String) resultMap.get("callback_content"));
                paymentInfo.setCallbackTime(new Date());
                paymentInfo.setConfirmTime(new Date());
                /*======================================================================================================================================*/
                /*进行支付更新的幂等性检查操作在updatePayment方法里面，防止与paymentController一起重复更新*/
                /*======================================================================================================================================*/
                paymentService.updatePayment(paymentInfo);
                return;
            } else {
                /*继续发送延时消息检查用户支付状态*/
                if (count > 0) {
                    count--;
                    System.out.println("检查次数剩余" + count);
                    paymentService.sendDelayPaymentResultCheckQueue(outTradeNumber, count);
                } else {
                    System.out.println("支付宝回调失败，用户支付失败");
                    return;
                }
            }
        }
    }
}
