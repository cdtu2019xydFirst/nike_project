package com.yadong.nike.manage.service;

import com.alibaba.fastjson.JSON;
import com.yadong.nike.bean.*;
import com.yadong.nike.manage.mapper.*;
import com.yadong.nike.manage.util.RedisUtil;
import com.yadong.nike.util.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 13:47
 **/
@Service
@Transactional
public class SkuInfoService {

    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;

    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;

    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisUtil redisUtil;

    public BigDataSkuCatalog getAllType(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.select(pmsSkuInfo);
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setId(pmsSkuInfoList.get(0).getCatalog3Id());
        List<PmsBaseCatalog3> pmsBaseCatalog3List = pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setId(pmsBaseCatalog3List.get(0).getCatalog2Id());
        List<PmsBaseCatalog2> pmsBaseCatalog2List = pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);
        PmsBaseCatalog1 pmsBaseCatalog1 = new PmsBaseCatalog1();
        pmsBaseCatalog1.setId(pmsBaseCatalog2List.get(0).getCatalog1Id());
        List<PmsBaseCatalog1> baseCatalog1List = pmsBaseCatalog1Mapper.select(pmsBaseCatalog1);
        BigDataSkuCatalog bigDataSkuCatalog = new BigDataSkuCatalog();
        bigDataSkuCatalog.setCatalog1Id(baseCatalog1List.get(0).getId());
        bigDataSkuCatalog.setCatalog2Id(pmsBaseCatalog2List.get(0).getId());
        bigDataSkuCatalog.setCatalog3Id(pmsBaseCatalog3List.get(0).getId());
        bigDataSkuCatalog.setSkuId(skuId);
        return bigDataSkuCatalog;
    }

    public class constant {
        //常量类
        private static final String PREFIX = "sku:";
        private static final String SUFFIX = ":info";
        private static final String LOCK = ":lock";
    }

    public void addSkuInfo(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfo.setId(idWorker.nextId() + "");
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
    }

    public PmsSkuInfo getSkuById(String id) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        Jedis jedis = null;
        try {
            //连接缓存
            jedis = redisUtil.getJedis();
            //查询缓存
            String skuKey = constant.PREFIX + id + constant.SUFFIX;
            String locKey = constant.PREFIX + id + constant.LOCK;
            String skuJson = jedis.get(skuKey);
            if (StringUtils.isNotBlank(skuJson)) {
                pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            } else {
                //如果缓存中没有，查询数据库
                /*当redis缓存失效后，为了防止高并发对db造成巨大压力，这里我们将设置分布式锁（在实际开发中可独立部署一个redis作为分布式锁服务器）*/
                String token = UUID.randomUUID().toString();
                String OK = jedis.set(locKey, token, "nx", "px", 3 * 1000);
                if (StringUtils.isNotBlank(OK) && "OK".equals(OK)) {
                    /*设置成功，有权利在3秒的过期时间内访问数据库*/
                    pmsSkuInfo.setId(id);
                    pmsSkuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
                    if (pmsSkuInfo != null) {
                        //将数据库查询到的信息保存到缓存中
                        jedis.set(skuKey, JSON.toJSONString(pmsSkuInfo));
                    } else {
                        /*重要：数据库中不存在 ，防止缓存穿透*/
                        jedis.setex(skuKey, 60 * 3, JSON.toJSONString(""));
                    }
                    /*访问mysql后，将mysql的分布锁释放
                    String isNewToken = jedis.get(locKey);
                    if (StringUtils.isNotBlank(isNewToken) && isNewToken.equals(token)){jedis.del(locKey);}
                    */
                    System.out.println("进入lua脚本");/*可以在这里断点进入redis查看locKey变化情况*/
                    String script = "if redis.call('get' , KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList(locKey), Collections.singletonList(token));
                } else {
                    /*设置失败 ，自旋（该线程在睡眠几秒后重新尝试访问getSkuById方法）*/
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /*重要： return不会产生新的线程，这才是自旋。如果不加return，则会产生新的getSkuById（）“孤儿”线程。*/
                    /*return getSkuById(skuId , ip);*/
                    return getSkuById(id);
                }
            }
        } catch (Exception e) {
            System.out.println("redis获取失败");
            e.printStackTrace();
        } finally {
            jedis.close();
            return pmsSkuInfo;
        }
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
