package com.yadong.nike.manage.controller;

import com.yadong.nike.bean.*;
import com.yadong.nike.manage.service.AttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 12:50
 **/
@RestController
@CrossOrigin
@RequestMapping("/Attr")
public class AttrController {

    @Autowired
    private AttrInfoService attrInfoService;

    @RequestMapping(value = "/addAttrInfo/{catalog3Id}", method = RequestMethod.POST)
    public Result addAttrInfo(@PathVariable("catalog3Id") String catalog3Id, @RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        attrInfoService.addAttrInfo(catalog3Id, pmsBaseAttrInfo);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "/getAttrInfoList", method = RequestMethod.GET)
    public Result getAttrInfoList(){
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrInfoService.getAttrInfoList();
        return new Result(true, StatusCode.OK, "查询成功", pmsBaseAttrInfos);
    }

    @RequestMapping(value = "/getAttrValueList/{attrId}", method = RequestMethod.GET)
    public Result getAttrValueList(@PathVariable("attrId") String attrId){
        List<PmsBaseAttrValue> pmsBaseAttrValues = attrInfoService.getAttrValueList(attrId);
        return new Result(true, StatusCode.OK, "查询成功", pmsBaseAttrValues);
    }

    @RequestMapping("/getBaseSaleAttrList")
    public Result getBaseSaleAttrList(){
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = attrInfoService.getBaseSaleAttrList();
        return new Result(true, StatusCode.OK, "查询成功", pmsBaseSaleAttrs);
    }

}
