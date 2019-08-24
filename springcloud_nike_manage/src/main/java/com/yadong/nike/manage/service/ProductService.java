package com.yadong.nike.manage.service;

import com.yadong.nike.bean.PmsProductInfo;
import com.yadong.nike.bean.PmsProductSaleAttr;
import com.yadong.nike.bean.PmsProductSaleAttrValue;
import com.yadong.nike.manage.mapper.PmsProductInfoMapper;
import com.yadong.nike.manage.mapper.PmsProductSaleAttrMapper;
import com.yadong.nike.manage.mapper.PmsProductSaleAttrValueMapper;
import com.yadong.nike.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 12:30
 **/
@Service
@Transactional
public class ProductService {

    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Autowired
    private IdWorker idWorker;

    public void addProductInfo(PmsProductInfo pmsProductInfo) {
        pmsProductInfo.setCatalog3Id(pmsProductInfo.getCatalog3Id());
        pmsProductInfo.setId(idWorker.nextId()+"");
        pmsProductInfoMapper.insertSelective(pmsProductInfo);
    }

    public PmsProductInfo getProductInfoById(String id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setId(id);
        PmsProductInfo pmsProductInfo1 = pmsProductInfoMapper.selectOne(pmsProductInfo);
        return pmsProductInfo1;
    }

    public List<PmsProductInfo> getProductInfo() {
        List<PmsProductInfo> pmsProductInfoList = pmsProductInfoMapper.selectAll();
        return pmsProductInfoList;
    }

    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String skuId, String productId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId, productId);
        return pmsProductSaleAttrList;
    }
}
