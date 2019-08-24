package com.yadong.nike.manage.service;

import com.yadong.nike.bean.PmsBaseCatalog1;
import com.yadong.nike.bean.PmsBaseCatalog2;
import com.yadong.nike.bean.PmsBaseCatalog3;
import com.yadong.nike.manage.mapper.PmsBaseCatalog1Mapper;
import com.yadong.nike.manage.mapper.PmsBaseCatalog2Mapper;
import com.yadong.nike.manage.mapper.PmsBaseCatalog3Mapper;
import com.yadong.nike.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 11:05
 **/
@Service
@Transactional
public class BaseService {

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Autowired
    private IdWorker idWorker;

    public void addCatalog1(PmsBaseCatalog1 pmsBaseCatalog1) {
        pmsBaseCatalog1.setId(idWorker.nextId()+"");
        pmsBaseCatalog1Mapper.insertSelective(pmsBaseCatalog1);
    }

    public List<PmsBaseCatalog1> getCatalog1() {
        List<PmsBaseCatalog1> pmsBaseCatalog1List = pmsBaseCatalog1Mapper.selectAll();
        return pmsBaseCatalog1List;
    }

    public PmsBaseCatalog1 getCatalog1ById(String id) {
        PmsBaseCatalog1 pmsBaseCatalog1 = new PmsBaseCatalog1();
        pmsBaseCatalog1.setId(id);
        PmsBaseCatalog1 baseCatalog1 = pmsBaseCatalog1Mapper.selectOne(pmsBaseCatalog1);
        return baseCatalog1;
    }

    public void addCatalog2(String catalog1Id, PmsBaseCatalog2 pmsBaseCatalog2) {
        pmsBaseCatalog2.setId(idWorker.nextId()+"");
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        pmsBaseCatalog2Mapper.insertSelective(pmsBaseCatalog2);
    }

    public List<PmsBaseCatalog2> getCatalog2() {
        List<PmsBaseCatalog2> pmsBaseCatalog2List = pmsBaseCatalog2Mapper.selectAll();
        return pmsBaseCatalog2List;
    }

    public PmsBaseCatalog2 getCatalog2ById(String id) {
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setId(id);
        PmsBaseCatalog2 baseCatalog2 = pmsBaseCatalog2Mapper.selectOne(pmsBaseCatalog2);
        return baseCatalog2;
    }

    public void addCatalog3(String catalog2Id, PmsBaseCatalog3 pmsBaseCatalog3) {
        pmsBaseCatalog3.setId(idWorker.nextId()+"");
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);
        pmsBaseCatalog3Mapper.insertSelective(pmsBaseCatalog3);
    }

    public List<PmsBaseCatalog3> getCatalog3() {
        List<PmsBaseCatalog3> pmsBaseCatalog3List = pmsBaseCatalog3Mapper.selectAll();
        return pmsBaseCatalog3List;
    }

    public PmsBaseCatalog3 getCatalog3ById(String id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setId(id);
        PmsBaseCatalog3 baseCatalog3 = pmsBaseCatalog3Mapper.selectOne(pmsBaseCatalog3);
        return baseCatalog3;
    }
}
