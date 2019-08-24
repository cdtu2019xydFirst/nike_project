package com.yadong.nike.passport.feign;

import com.yadong.nike.bean.UmsMember;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/20 | 20:22
 **/
@Component
@FeignClient(value = "springcloud-nike-member")
public interface UserFeignService {

    @RequestMapping(value = "/Member/login", method = RequestMethod.POST)
    UmsMember login(@RequestBody UmsMember umsMemberParam);
}
