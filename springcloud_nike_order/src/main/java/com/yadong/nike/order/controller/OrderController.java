package com.yadong.nike.order.controller;

import com.alibaba.fastjson.JSON;
import com.yadong.nike.bean.OmsCartItem;
import com.yadong.nike.bean.OmsOrder;
import com.yadong.nike.bean.OmsOrderItem;
import com.yadong.nike.bean.UmsMemberReceiveAddress;
import com.yadong.nike.order.feign.CartFeignService;
import com.yadong.nike.order.feign.UserFeignService;
import com.yadong.nike.order.service.OrderService;
import com.yadong.nike.order.feign.SkuFeignService;
import com.yadong.nike.order.util.CookieUtil;
import com.yadong.nike.passportConfig.annotations.LoginRequired;
import com.yadong.nike.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/21 | 14:24
 **/
@Controller
public class OrderController {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SkuFeignService skuFeignService;

    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @RequestMapping("/toTrade")/*去结算页面*/
    @LoginRequired(loginSuccess = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        String sourceType = userFeignService.getSourceType(memberId);
        if ("1".equals(sourceType)) {
            sourceType = "妮可用户";
        } else if ("2".equals(sourceType)) {
            sourceType = "新浪用户";
        } else if ("3".equals(sourceType)) {
            sourceType = "腾讯用户";
        } else if ("4".equals(sourceType)) {
            sourceType = "百度用户";
        } else if ("5".equals(sourceType)) {
            sourceType = "阿里用户";
        } else if ("6".equals(sourceType)) {
            sourceType = "京东用户";
        } else {
            sourceType = "游客";
        }
        modelMap.put("sourceType", sourceType);
        modelMap.put("memberId", memberId);
        modelMap.put("nickname", nickname);
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        /*收货人地址集合，一个人可能存在多个收货地址*/
        List<UmsMemberReceiveAddress> receiveAddressByMemberId = userFeignService.getReceiveAddressByMemberId(memberId);
        modelMap.put("receiveAddress", receiveAddressByMemberId);
        /*将购物车集合转换成清单结算集合*/
        omsCartItems = cartFeignService.getCartList(memberId);
        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        int ALL_PRICE = 0;/*总价*/
        for (OmsCartItem omsCartItem : omsCartItems) {
            /*            *//*每循环一个已经被勾选的购物车对象，就封装一个商品详情到omsOrderItems*//*
            if (omsCartItem.getIsChecked() != null){*//*选中*//*
                if (omsCartItem.getIsChecked().equals("1")){
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setProductName(omsCartItem.getProductName());*//*还可以继续封装其他参数。。。。*//*
                    omsOrderItems.add(omsOrderItem);
               }
            }else {*//*用户没有选择购物车中的商品*//*
                omsOrderItems.add(null);
            }*//*这里由于没有写isChecked()方法就不判断了*/
            OmsOrderItem omsOrderItem = new OmsOrderItem();
            omsOrderItem.setProductPrice(omsCartItem.getPrice());
            omsOrderItem.setOrderSn(omsCartItem.getProductSn());
            omsOrderItem.setProductQuantity(omsCartItem.getQuantity().toString());
            ALL_PRICE = ALL_PRICE + (Integer.parseInt(omsCartItem.getPrice()) * Integer.parseInt(omsCartItem.getQuantity().toString()));
            omsOrderItem.setProductName(omsCartItem.getProductName());/*还可以继续封装其他参数。。。。*/
            omsOrderItems.add(omsOrderItem);

        }
        modelMap.put("totalAmount", ALL_PRICE);
        modelMap.put("sourceType", sourceType);
        modelMap.put("omsOrderItems", omsOrderItems);

        /*============================重要：生成交易编码，防止用户回退重复提交订单 ，在提交订单前做交易编码校验=================================*/
        String tradeCode = orderService.genTradeCode(memberId);/*后期改进*/
        modelMap.put("tradeCode", tradeCode);

        return "toTrade";
    }

    /*提交订单*/
    @RequestMapping(value = "/submitOrder", method = RequestMethod.POST)
    @LoginRequired(loginSuccess = true)
    public ModelAndView submitOrder(String receiveAddressId, BigDecimal totalAmount, String tradeCode, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        String sourceType = userFeignService.getSourceType(memberId);
        String outTradeNumber = "nike";
        outTradeNumber = outTradeNumber + System.currentTimeMillis();/*将毫秒时间戳拼接到外部订单号*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMDDHHmmss");
        outTradeNumber = outTradeNumber + simpleDateFormat.format(new Date());/*将时间字符串拼接到外部订单号*/
        /*先假定收货地址每个人只有一个*/
        List<UmsMemberReceiveAddress> receiveAddressByMemberId = userFeignService.getReceiveAddressByMemberId(memberId);
        /*检查交易编码*/
        String success = orderService.checkTradeCode(memberId, tradeCode);
        if (success != null && success.equals("success")) {
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setId(idWorker.nextId() + "");
            omsOrder.setMemberId(memberId);
            omsOrder.setCouponId("8008208820");
            omsOrder.setOrderSn(outTradeNumber);
            omsOrder.setCreateTime(new Date());
            omsOrder.setPaymentTime(new Date());
            omsOrder.setDeliveryTime(new Date());
            omsOrder.setMemberUsername(nickname);
            omsOrder.setTotalAmount(totalAmount.toString());
            omsOrder.setPayAmount("0");
            omsOrder.setFreightAmount("0");//运费
            omsOrder.setPromotionAmount("10");
            omsOrder.setIntegrationAmount("19");
            omsOrder.setCouponAmount("3");
            omsOrder.setDiscountAmount("5");
            omsOrder.setPayType("未支付");
            omsOrder.setSourceType(sourceType);
            omsOrder.setStatus("待发货");
            omsOrder.setOrderType("秒杀订单");
            omsOrder.setDeliveryCompany("未分配");
            omsOrder.setDeliverySn("");
            omsOrder.setAutoConfirmDay("7");//七天自动收货，用Qutraz任务调用技术实现
            omsOrder.setIntegration("520");
            omsOrder.setGrowth("600");
            omsOrder.setPromotionInfo("秒杀活动，限量抢购");
            omsOrder.setBillType("电子发票");
            omsOrder.setBillHeader("妮可平台限时活动");
            omsOrder.setBillContent("秒杀限量，买不了吃亏，买不了上当，赶紧行动起立吧！");
            omsOrder.setBillReceiverPhone("12345678910");
            omsOrder.setBillReceiverEmail("1578465208@qq.com");
            omsOrder.setReceiverName(receiveAddressByMemberId.get(0).getName());
            omsOrder.setReceiverPhone(receiveAddressByMemberId.get(0).getPhoneNumber());
            omsOrder.setReceiverPostCode(receiveAddressByMemberId.get(0).getPostCode());
            omsOrder.setReceiverProvince(receiveAddressByMemberId.get(0).getProvince());
            omsOrder.setReceiverCity(receiveAddressByMemberId.get(0).getCity());
            omsOrder.setReceiverRegion(receiveAddressByMemberId.get(0).getRegion());
            omsOrder.setReceiverDetailAddress(receiveAddressByMemberId.get(0).getDetailAddress());
            omsOrder.setNote("亲，记得嗮图评论哟！");
            omsOrder.setConfirmStatus("0");
            omsOrder.setDeleteStatus("0");
            omsOrder.setUseIntegration("520");
            /*根据用户id获得要购买的商品列表（从购物车中取出商品列表）和总价格*/
            List<OmsCartItem> omsCartItems = cartFeignService.getCartList(memberId);
            /*这里可以检验是否勾选，，，这里就不操作了*/
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            for (OmsCartItem omsCartItem : omsCartItems) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();

                omsOrderItem.setId(idWorker.nextId() + "");
                omsOrderItem.setOrderId(omsOrder.getId());
                omsOrderItem.setOrderSn(outTradeNumber);/*外部订单号，用来和其他系统进行交互，比如连接Alipay（支付宝）时使用*/
                omsOrderItem.setProductBrand("nike");
                omsOrderItem.setProductCategoryId("1163666603787816960");/*可以调用sku查看*/
                omsCartItem.setCreateDate(new Date());
                omsOrderItem.setProductId(omsCartItem.getProductId());
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductPrice(omsCartItem.getPrice());
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity().toString());
                omsOrderItem.setProductSkuCode("1561232548");
                omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                omsOrderItem.setProductSn(omsCartItem.getProductSn());
                omsOrderItem.setSp1(omsCartItem.getSp1());
                omsOrderItem.setSp2(omsCartItem.getSp2());
                omsOrderItem.setSp3(omsCartItem.getSp3());

                /*检验价格,防止在整个过程中出现干扰问题，导致商品价格计算，传递出错，这是绝不允许的*/
                boolean b = skuFeignService.checkPrice(omsOrderItem.getProductSkuId(), omsOrderItem.getProductPrice());
                if (b == false) {
                    /*返回失败页面，提示用户服务错误，请重新选择购买*/
                    ModelAndView modelAndView = new ModelAndView("fail");
                    return modelAndView;
                } else {
                    omsOrderItems.add(omsOrderItem);
                }
                /*检验库存 , 远程调用库存系统*/


            }
            omsOrder.setOmsOrderItems(omsOrderItems);
            /*将订单和订单详情写入数据库  ==== 删除购物车对应的被勾选的商品*/
            orderService.SaveOrderAndDeletCartItem(omsOrder);
            /*删除redis缓存中购物车数据*/
            /*cartFeignService.delCartByMemberid(memberId);*/
            /*重定向到支付系统*/
            ModelAndView modelAndView = new ModelAndView("redirect:http://localhost:9007/index");
            modelAndView.addObject("outTradeNumber", outTradeNumber);
            modelAndView.addObject("totalAmount", totalAmount);
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("fail");
            return modelAndView;
        }
    }

    /*===============================================================================================================================================*/
    /*其他微服务调用区*/
    /*===============================================================================================================================================*/

    @ResponseBody
    @RequestMapping(value = "/getOrderByOutTradeNumber/{outTradeNumber}", method = RequestMethod.GET)
    public OmsOrder getOrderByOutTradeNumber(@PathVariable("outTradeNumber") String outTradeNumber){
        OmsOrder omsOrder = orderService.getOrderByOutTradeNumber(outTradeNumber);
        return omsOrder;
    }

}
