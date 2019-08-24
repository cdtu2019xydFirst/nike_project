package com.yadong.nike.manage.service;

import com.yadong.nike.bean.PmsBaseAttrInfo;
import com.yadong.nike.bean.PmsBaseAttrValue;
import com.yadong.nike.bean.PmsBaseSaleAttr;

import com.yadong.nike.manage.mapper.PmsBaseAttrInfoMapper;
import com.yadong.nike.manage.mapper.PmsBaseAttrValueMapper;
import com.yadong.nike.manage.mapper.PmsBaseSaleAttrMapper;
import com.yadong.nike.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 12:56
 **/
@Service
@Transactional
public class AttrInfoService {

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Autowired
    private IdWorker idWorker;

    public void addAttrInfo(String catalog3Id, PmsBaseAttrInfo pmsBaseAttrInfo) {
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        pmsBaseAttrInfo.setId(idWorker.nextId()+"");
        pmsBaseAttrInfo.setIsEnabled("1");
        pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
    }

    public List<PmsBaseAttrInfo> getAttrInfoList() {
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrInfoMapper.selectAll();
        return pmsBaseAttrInfoList;
    }

    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValueList = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
        return pmsBaseAttrValueList;
    }

    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrList = pmsBaseSaleAttrMapper.selectAll();
        return pmsBaseSaleAttrList;
    }
}
