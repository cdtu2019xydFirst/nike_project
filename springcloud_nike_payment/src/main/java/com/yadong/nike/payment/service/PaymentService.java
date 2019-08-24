package com.yadong.nike.payment.service;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.yadong.nike.bean.PaymentInfo;
import com.yadong.nike.payment.feign.ActiveMQFeignService;
import com.yadong.nike.payment.mapper.PaymentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/22 | 10:58
 **/
@Service
@Transactional
public class PaymentService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private ActiveMQFeignService activeMQFeignService;

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    public void updatePayment(PaymentInfo paymentInfo) {
        /*======================================================================================================================================*/
        /*进行支付更新的幂等性检查操作在updatePayment方法里面，防止paymentController 与 PaymentServiceMqListener 一起重复更新*/
        /*======================================================================================================================================*/
        PaymentInfo paymentInfoParam = new PaymentInfo();
        paymentInfoParam.setOrderSn(paymentInfo.getOrderSn());
        PaymentInfo paymentInfoResult = paymentInfoMapper.selectOne(paymentInfoParam);
        if (paymentInfoResult != null && "已付款".equals(paymentInfoResult.getPaymentStatus())) {
            /*说明在paymentController 与 PaymentServiceMqListener里面其中一个已经执行了更新操作*/
            return;
        } else {
            Example example = new Example(PaymentInfo.class);
            example.createCriteria().andEqualTo("orderSn", paymentInfo.getOrderSn());
            paymentInfoMapper.updateByExampleSelective(paymentInfo, example);
            /*更新成功后，发送更新成功的消息,order订单服务接搜消息，更改支付状态。。。后续可能还有库存服务，物流服务。。。。。。。。。。。。。*/
            String outTradeNumber = paymentInfo.getOrderSn();
            /*调用activeMQ微服务，让他发送一条对列消息更新order订单服务接搜消息，更改支付状态。。。后续可能还有库存服务，物流服务。。。。。。。。。。。。。*/
            activeMQFeignService.sendPayment_success_queue(outTradeNumber);
        }
    }


    public void sendDelayPaymentResultCheckQueue(String outTradeNumber, Integer count) {
        /*调用activeMQ微服务，让他发送一条延迟对列消息*/
        activeMQFeignService.sendPayment_checked_queue(outTradeNumber, count);
    }


    public Map<String, Object> checkAlipayPayment(String outTradeNumber) {

        Map<String, Object> resultMap = new HashMap<>();

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("out_trade_no", outTradeNumber);
        request.setBizContent(JSON.toJSONString(requestMap));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            System.out.println("调用成功");
            resultMap.put("out_trade_no", response.getOutTradeNo());
            resultMap.put("trade_no", response.getTradeNo());
            resultMap.put("trade_status", response.getTradeStatus());
            resultMap.put("callback_content", response.getMsg());
        } else {
            System.out.println("有可能交易未创建调用失败");
        }
        return resultMap;
    }
}
