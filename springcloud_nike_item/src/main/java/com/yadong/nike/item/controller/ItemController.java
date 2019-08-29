package com.yadong.nike.item.controller;

import com.alibaba.fastjson.JSONObject;
import com.yadong.nike.bean.*;
import com.yadong.nike.item.feign.BigDataFeignService;
import com.yadong.nike.item.feign.ManageFeign;
import com.yadong.nike.item.util.BrowserInfoUtil;
import com.yadong.nike.item.util.CookieUtil;
import com.yadong.nike.item.util.IpUtil;
import com.yadong.nike.passportConfig.annotations.LoginRequired;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 14:24
 **/
@Controller
public class ItemController {

    @Autowired
    private ManageFeign managerFeign;

    @Autowired
    private BigDataFeignService bigDataFeignService;

    @RequestMapping("/{skuId}.html")
    @LoginRequired(loginSuccess = false)
    public String toItem(@PathVariable("skuId") String skuId, ModelMap modelMap, HttpServletRequest request){
        /*====================================================================================================================================*/
        /**
         * 大数据前端埋点
         * 频道ID ,一级分类Id catalog1Id
         * 二级分类ID catalog2Id
         * 三级分类ID catalog3Id
         * 产品ID ，skuId
         * 用户ID ，memberId
         * 用户昵称 ，nickname
         * 用户IP，真实ip地址
         * 获取浏览器信息
         */
        /*====================================================================================================================================*/
        Logger logger = Logger.getLogger(ItemController.class.getName());

        BigDataProductScanlog bigDataProductScanlog = new BigDataProductScanlog();
        BigDataSkuCatalog bigDataSkuCatalog = bigDataFeignService.getAllType(skuId);
        AreaAndNetwork areaAndNetwork = new AreaAndNetwork();
        String ip = IpUtil.GetIpAddress(request);
        String osAndBrowserInfo = BrowserInfoUtil.getOsAndBrowserInfo(request);
        String[] temps = osAndBrowserInfo.split("---");
        if (CookieUtil.getCookieValue(request,"oldToken", true) != null){
            String memberId = request.getAttribute("memberId").toString();
            String nickname = request.getAttribute("nickname").toString();
            bigDataProductScanlog.setMemberId(memberId);
            bigDataProductScanlog.setNickname(nickname);
        }else {
            bigDataProductScanlog.setMemberId("");
            bigDataProductScanlog.setNickname("游客");
        }
        /*获取产品相关信息 这里封装一个实体类BigDataSkuCatalog*/
        bigDataProductScanlog.setSkuId(bigDataSkuCatalog.getSkuId());
        bigDataProductScanlog.setCatalog1Id(bigDataSkuCatalog.getCatalog1Id());
        bigDataProductScanlog.setCatalog2Id(bigDataSkuCatalog.getCatalog2Id());
        bigDataProductScanlog.setCatalog3Id(bigDataSkuCatalog.getCatalog3Id());
        /*iP地址 , 操作系统，浏览器信息*/
        bigDataProductScanlog.setIp(ip);
        bigDataProductScanlog.setOs(temps[0].trim());
        bigDataProductScanlog.setBrowser(temps[1].trim());
        /*模拟地区和运营商*/
        bigDataProductScanlog.setCountry(areaAndNetwork.getCountry());
        bigDataProductScanlog.setProvince(areaAndNetwork.getProvince());
        bigDataProductScanlog.setCity(areaAndNetwork.getCity());
        bigDataProductScanlog.setRegion(areaAndNetwork.getRegion());
        bigDataProductScanlog.setDetailedAddress(areaAndNetwork.getDetailedAddress());
        bigDataProductScanlog.setNetwork(areaAndNetwork.getNetwork());
        /*将封装类传输出去*/
        String bigDataProductScanlogString = JSONObject.toJSONString(bigDataProductScanlog);
        logger.info("INFO"+bigDataProductScanlogString);
        System.out.println(logger);


        PmsSkuInfo pmsSkuInfo = managerFeign.getSkuById(skuId);
        modelMap.put("skuInfo", pmsSkuInfo);
        /*获取当前sku销售属性列表，并且标记isChecked字段*/
        List<PmsProductSaleAttr> pmsProductSaleAttrs = managerFeign.spuSaleAttrListCheckBySku(pmsSkuInfo.getId() , pmsSkuInfo.getProductId());
        modelMap.put("spuSaleAttrListCheckBySku" , pmsProductSaleAttrs);
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        modelMap.put("oldToken", oldToken);
        /*查询当前的sku的spu的其他sku集合的hash表*/


        return "item";
    }

}
