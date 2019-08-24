package com.yadong.nike.order.feign;

import com.yadong.nike.bean.UmsMemberReceiveAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/21 | 17:49
 **/
@Component
@FeignClient(value = "springcloud-nike-member")
public interface UserFeignService {

    @RequestMapping(value = "/Member/getSourceType/{memberId}", method = RequestMethod.GET)
    String getSourceType(@PathVariable("memberId") String memberId);

    @RequestMapping(value = "/Member/getReceiveAddressByMemberId/{memberId}", method = RequestMethod.GET)
    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(@PathVariable("memberId") String memberId);
}
