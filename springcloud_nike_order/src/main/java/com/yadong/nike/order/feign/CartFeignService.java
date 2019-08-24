package com.yadong.nike.order.feign;

import com.yadong.nike.bean.OmsCartItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/21 | 18:33
 **/
@Component
@FeignClient(value = "springcloud-nike-shoppingcart")
public interface CartFeignService {

    @RequestMapping(value = "/springcloud/cartList/{memberId}", method = RequestMethod.GET)
    List<OmsCartItem> getCartList(@PathVariable("memberId") String memberId);

}
