package com.yadong.nike.manage.controller;

import com.yadong.nike.bean.PmsProductInfo;
import com.yadong.nike.bean.PmsProductSaleAttr;
import com.yadong.nike.bean.Result;
import com.yadong.nike.bean.StatusCode;
import com.yadong.nike.manage.service.ProductService;
import com.yadong.nike.manage.util.PmsUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 12:25
 **/
@Controller
@CrossOrigin
@RequestMapping("/Product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    @ResponseBody
    public Result addProductInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        productService.addProductInfo(pmsProductInfo);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @RequestMapping(value = "/getProductInfoById/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result getProductInfoById(@PathVariable("id") String id) {
        PmsProductInfo pmsProductInfo = productService.getProductInfoById(id);
        return new Result(true, StatusCode.OK, "查询成功", pmsProductInfo);
    }

    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {//用二进制流的方式接收文件参数
        /*1.将图片或者音视频上传到分布式的文件存储系统*/
        /*2.将图片的存储路径返回给页面*/
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);
        return imgUrl;
    }

    @RequestMapping(value = "/getProductInfo", method = RequestMethod.GET)
    @ResponseBody
    public Result getProductInfo() {
        List<PmsProductInfo> pmsProductInfos = productService.getProductInfo();
        return new Result(true, StatusCode.OK, "查询成功", pmsProductInfos);
    }

    @RequestMapping(value = "/spuSaleAttrListCheckBySku", method = RequestMethod.GET)
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(@RequestParam("skuId") String skuId, @RequestParam("productId") String productId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrs = productService.spuSaleAttrListCheckBySku(skuId, productId);
        return pmsProductSaleAttrs;
    }

}
