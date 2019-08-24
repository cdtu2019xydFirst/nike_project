package com.yadong.nike.search.controller;

import com.yadong.nike.bean.PmsSearchParam;
import com.yadong.nike.bean.PmsSearchSkuInfo;
import com.yadong.nike.search.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/23 | 12:16
 **/
@Controller
public class SearchController {

    @Autowired
    private EsSearchService esSearchService;

    @RequestMapping(value = "/index.html", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/list.html", method = RequestMethod.GET)
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {
        /*调用搜索服务，返回搜索结果*/
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = esSearchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList", pmsSearchSkuInfos);
        return "list";
    }


}
