package com.yadong.nike.manage.mapper;

import com.yadong.nike.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 18:33
 **/
public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {

    List<PmsProductSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("skuId") String skuId, @Param("productId") String productId);

}
