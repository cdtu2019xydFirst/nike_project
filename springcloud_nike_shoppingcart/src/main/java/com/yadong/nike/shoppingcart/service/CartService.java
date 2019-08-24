package com.yadong.nike.shoppingcart.service;

import com.yadong.nike.bean.OmsCartItem;
import com.yadong.nike.shoppingcart.mapper.OmsCartItemMapper;
import com.yadong.nike.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/21 | 8:51
 **/
@Service
@Transactional
public class CartService {

    @Autowired
    private OmsCartItemMapper omsCartItemMapper;

    @Autowired
    private IdWorker idWorker;

    public OmsCartItem ifCartsExistByUser(String memberId, String productSkuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(productSkuId);
        OmsCartItem omsCartItemParam = omsCartItemMapper.selectOne(omsCartItem);
        return omsCartItemParam;
    }

    /*更新商品数量*/
    public void updataCartItemQuantity(OmsCartItem omsCartItemFromDb, BigDecimal quantity) {
        BigDecimal quantity1 = omsCartItemFromDb.getQuantity();
        BigDecimal quantityTotall = quantity.add(quantity1);
        omsCartItemFromDb.setQuantity(quantityTotall);
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id", omsCartItemFromDb.getId());
        omsCartItemMapper.updateByExample(omsCartItemFromDb, example);
    }

    public List<OmsCartItem> cartList(String memberId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItemList = omsCartItemMapper.select(omsCartItem);
        return omsCartItemList;
    }

    public void addCart(OmsCartItem omsCartItem) {
        omsCartItem.setId(idWorker.nextId()+"");
        omsCartItemMapper.insertSelective(omsCartItem);
    }

}
