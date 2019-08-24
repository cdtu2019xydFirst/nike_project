package com.yadong.nike.manage.service;

import com.yadong.nike.bean.PmsSkuAttrValue;
import com.yadong.nike.bean.PmsSkuInfo;
import com.yadong.nike.manage.mapper.PmsSkuAttrValueMapper;
import com.yadong.nike.manage.mapper.PmsSkuInfoMapper;
import com.yadong.nike.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 13:47
 **/
@Service
@Transactional
public class SkuInfoService {

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private IdWorker idWorker;

    public void addSkuInfo(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfo.setId(idWorker.nextId() + "");
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
    }

    public PmsSkuInfo getSkuById(String id) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(id);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        return pmsSkuInfo1;
    }

    public boolean checkPrice(String productSkuId, String productPrice) {
        boolean b = false;
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        BigDecimal price = pmsSkuInfo1.getPrice();
        if (productPrice != null && price != null && productPrice.equals(price.toString())) {
            b = true;
        }
        return b;
    }

    public List<PmsSkuInfo> getSkuInfo() {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo skuInfo : pmsSkuInfoList) {
            String skuInfoId = skuInfo.getId();
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuInfoId);
            List<PmsSkuAttrValue> pmsSkuAttrValueList = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            skuInfo.setSkuAttrValueList(pmsSkuAttrValueList);
        }
        return pmsSkuInfoList;
    }
}
