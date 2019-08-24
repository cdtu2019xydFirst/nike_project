package com.yadong.nike.order.service;

import com.yadong.nike.bean.OmsCartItem;
import com.yadong.nike.bean.OmsOrder;
import com.yadong.nike.bean.OmsOrderItem;
import com.yadong.nike.order.mapper.OmsCartItemMapper;
import com.yadong.nike.order.mapper.OmsOrderItemMapper;
import com.yadong.nike.order.mapper.OmsOrderMapper;
import com.yadong.nike.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/21 | 19:08
 **/
@Service
@Transactional
public class OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Autowired
    private OmsCartItemMapper omsCartItemMapper;

    public String genTradeCode(String memberId) {
        String genTradeCode = "123456789";
        return genTradeCode;
    }

    public String checkTradeCode(String memberId, String tradeCode) {
        return "success";
    }

    public void SaveOrderAndDeletCartItem(OmsOrder omsOrder) {
        /*保存订单*/
        omsOrderMapper.insertSelective(omsOrder);
        /*保存订单详情*/
        List<OmsOrderItem> omsOrderItemList = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItemList) {
            omsOrderItemMapper.insertSelective(omsOrderItem);
        }
        /*删除购物车， 判断isChecked字段（后期优化）*/
        /*Integer isChecked = omsOrder.getIsChecked();*/
        String memberId = omsOrder.getMemberId();
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItemMapper.delete(omsCartItem);
    }

    public OmsOrder getOrderByOutTradeNumber(String outTradeNumber) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNumber);
        OmsOrder order = omsOrderMapper.selectOne(omsOrder);
        OmsOrderItem omsOrderItem = new OmsOrderItem();
        omsOrderItem.setOrderId(order.getId());
        List<OmsOrderItem> omsOrderItemList = omsOrderItemMapper.select(omsOrderItem);
        order.setOmsOrderItems(omsOrderItemList);
        return order;
    }

    public void updataPaymentStatus(String outTradeNumber) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNumber);
        OmsOrder omsOrder1 = omsOrderMapper.selectOne(omsOrder);
        omsOrder1.setPayType("已付款");
        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn", omsOrder1.getOrderSn());
        omsOrderMapper.updateByExampleSelective(omsOrder1, example);
    }
}
