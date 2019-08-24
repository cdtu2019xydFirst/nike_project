package com.yadong.nike.manage.controller;

import com.yadong.nike.bean.*;
import com.yadong.nike.manage.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 10:59
 **/
@RestController
@CrossOrigin
@RequestMapping("/Base")
public class BaseController {

    @Autowired
    private BaseService baseService;

    @RequestMapping(value = "/addCatalog1", method = RequestMethod.POST)
    public Result addCatalog1(@RequestBody PmsBaseCatalog1 pmsBaseCatalog1){
        baseService.addCatalog1(pmsBaseCatalog1);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "/getCatalog1", method = RequestMethod.GET)
    public Result getCatalog1(){
        List<PmsBaseCatalog1> pmsBaseCatalog1s = baseService.getCatalog1();
        return new Result(true, StatusCode.OK, "查询成功", pmsBaseCatalog1s);
    }

    @RequestMapping(value = "/getCatalog1ById/{id}", method = RequestMethod.GET)
    public Result getCatalog1ById(@PathVariable("id") String id){
        PmsBaseCatalog1 pmsBaseCatalog1 = baseService.getCatalog1ById(id);
        return new Result(true, StatusCode.OK, "查询成功", pmsBaseCatalog1);
    }

    @RequestMapping(value = "/addCatalog2/{catalog1Id}", method = RequestMethod.POST)
    public Result addCatalog2(@PathVariable("catalog1Id") String catalog1Id, @RequestBody PmsBaseCatalog2 pmsBaseCatalog2){
        baseService.addCatalog2(catalog1Id, pmsBaseCatalog2);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "/getCatalog2", method = RequestMethod.GET)
    public Result getCatalog2(){
        List<PmsBaseCatalog2> pmsBaseCatalog2s = baseService.getCatalog2();
        return new Result(true, StatusCode.OK, "查询成功", pmsBaseCatalog2s);
    }

    @RequestMapping(value = "/getCatalog2ById/{id}", method = RequestMethod.GET)
    public Result getCatalog2ById(@PathVariable("id") String id){
        PmsBaseCatalog2 pmsBaseCatalog2 = baseService.getCatalog2ById(id);
        return new Result(true, StatusCode.OK, "查询成功", pmsBaseCatalog2);
    }

    @RequestMapping(value = "/addCatalog3/{catalog2Id}", method = RequestMethod.POST)
    public Result addCatalog3(@PathVariable("catalog2Id") String catalog2Id, @RequestBody PmsBaseCatalog3 pmsBaseCatalog3){
        baseService.addCatalog3(catalog2Id, pmsBaseCatalog3);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "/getCatalog3", method = RequestMethod.GET)
    public Result getCatalog3(){
        List<PmsBaseCatalog3> pmsBaseCatalog3s = baseService.getCatalog3();
        return new Result(true, StatusCode.OK, "查询成功", pmsBaseCatalog3s);
    }

    @RequestMapping(value = "/getCatalog3ById/{id}", method = RequestMethod.GET)
    public Result getCatalog3ById(@PathVariable("id") String id){
        PmsBaseCatalog3 pmsBaseCatalog3 = baseService.getCatalog3ById(id);
        return new Result(true, StatusCode.OK, "查询成功", pmsBaseCatalog3);
    }

}
