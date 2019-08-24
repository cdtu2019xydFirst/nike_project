package com.yadong.nike.payment.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.yadong.nike.bean.OmsOrder;
import com.yadong.nike.bean.OmsOrderItem;
import com.yadong.nike.bean.PaymentInfo;
import com.yadong.nike.passportConfig.annotations.LoginRequired;
import com.yadong.nike.payment.config.AlipayConfig;
import com.yadong.nike.payment.feign.OrderFeignService;
import com.yadong.nike.payment.service.PaymentService;
import com.yadong.nike.util.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/22 | 9:54
 **/
@Controller
public class PaymentController {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderFeignService orderFeignService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @LoginRequired(loginSuccess = true)
    public String index(String outTradeNumber, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        modelMap.put("outTradeNumber", outTradeNumber);
        modelMap.put("totalAmount", totalAmount);
        modelMap.put("memberId", memberId);
        modelMap.put("nickname", nickname);
        return "index";
    }

    /*============================================================================================================================================*/
    /*未使用微信支付业务*/
    /*============================================================================================================================================*/
    @RequestMapping("weixin/submit")
    @LoginRequired(loginSuccess = true)/*一下两个参数也可以通过此注释从请求域中获得获得，参考orderController*/
    public String weixin(String outTradeNumber, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        return null;
    }

    /*============================================================================================================================================*/
    /*支付宝支付业务*/
    /*============================================================================================================================================*/
    @RequestMapping(value = "alipay/submit", method = RequestMethod.POST)
    @LoginRequired(loginSuccess = true)/*一下两个参数也可以通过此注释从请求域中获得获得，参考orderController*/
    @ResponseBody
    public String alipay(String outTradeNumber, BigDecimal totalAmount, HttpServletRequest request, ModelMap modelMap) {
        /*获得一个支付宝的请求客户端（是一个封装好的Http的表单请求）*/
        String form = "";
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();/*创建API对应的request*/
        /*回调函数地址*/
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        /*封装信息*/
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("out_trade_no", outTradeNumber);
        hashMap.put("product_code", "FAST_INSTANT_TRADE_PAY");//必须这样写
        hashMap.put("total_amount", totalAmount);
        hashMap.put("subject", "凯锐思DHA配方粮 哈士奇狗粮成犬幼犬专用大型犬营养补钙3-12个月 20kg");
        String param = JSON.toJSONString(hashMap);
        alipayRequest.setBizContent(param);
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
            System.err.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        /*生成并且保存用户的支付信息,查询订单*/
        OmsOrder omsOrder = orderFeignService.getOrderByOutTradeNumber(outTradeNumber);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setId(idWorker.nextId() + "");
        paymentInfo.setOrderSn(outTradeNumber);
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setAlipayTradeNo("");
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setTotalAmount(totalAmount.toString());
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();//后期优化
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            paymentInfo.setSubject(omsOrderItem.getProductName());
        }
        paymentInfo.setCreateTime(new Date());
        /*保存支付信息*/
        paymentService.savePaymentInfo(paymentInfo);
        /*===============================================================================================================================*/
        /*向消息中间件发送一个检查支付状态的延迟消息队列 ，在activeMQ的activemq.xml文件<broker>标签配置schedulerSupport="true"，表示开启延迟对列*/
        /*===============================================================================================================================*/
        /*定义监听循环次数6次*/
        Integer count = 6;
        paymentService.sendDelayPaymentResultCheckQueue(outTradeNumber, count);
        /*提交请求到支付宝*/
        return form;
    }

    /*=========================================================================================================================================*/
    /*如果我们支付成功，支付宝回调时出错，将永远不会走这个方法。所以我们决定在提交支付请求（alipay/submit）的时候主动向蚂蚁金服发送检查支付结果请求*/
    /*=========================================================================================================================================*/
    @RequestMapping("alipay/callback/return")
    @LoginRequired(loginSuccess = true)/*一下两个参数也可以通过此注释从请求域中获得获得，参考orderController*/
    public String alipayCallBackReturn(HttpServletRequest request, ModelMap modelMap) {
        /*回调请求中获取支付宝参数 ， 详情请参考蚂蚁金服的开发者文档alipay*/
        String sign = request.getParameter("sign");/*支付宝签名*/
        String tradeNo = request.getParameter("trade_no");/*支付宝交易凭证号*/
        String outTradeNo = request.getParameter("out_trade_no");
        String tradeStatus = request.getParameter("trade_status");/*订单状态*/
        String totalAmount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String callback_content = request.getQueryString();/*获取连接参数*/
        /*通过支付宝的paramMap进行签名验证 ，2.0版本的接口将paramMap参数去掉了，所以我们这里进行一次假验证*/
        if (StringUtils.isNotBlank(sign)) {
            /*验证成功*/
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setOrderSn(outTradeNo);
            paymentInfo.setPaymentStatus("已付款");
            paymentInfo.setAlipayTradeNo(tradeNo);
            paymentInfo.setCallbackContent(callback_content);
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setConfirmTime(new Date());
            /*=====================================================================================================================================*/
            /*更新用户的支付状态*/
            /*支付成功后 引起的系统服务 ，——> 订单服务更新 ，——> 库存服务 ，——> 物流服务*/
            /*这里我们将引入分布式事务消息中间件ActiveMQ*/
            /*调用ActiveMQ发送支付成功的消息*/
            /*进行支付更新的幂等性检查操作在updatePayment方法里面，防止与PaymentServiceMqListener一起重复更新*/
            /*=====================================================================================================================================*/
            paymentService.updatePayment(paymentInfo);
        }
        return "finish";
    }

}
