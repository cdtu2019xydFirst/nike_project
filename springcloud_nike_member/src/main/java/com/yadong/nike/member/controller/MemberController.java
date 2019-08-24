package com.yadong.nike.member.controller;

import com.yadong.nike.bean.Result;
import com.yadong.nike.bean.StatusCode;
import com.yadong.nike.bean.UmsMember;
import com.yadong.nike.bean.UmsMemberReceiveAddress;
import com.yadong.nike.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/19 | 19:51
 **/
@RestController
@CrossOrigin
@RequestMapping("/Member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        List<UmsMember> umsMembersList = memberService.findAll();
        return new Result(true, StatusCode.OK, "查询成功", umsMembersList);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable("id") String id){
        UmsMember umsMember = memberService.findById(id);
        return new Result(true, StatusCode.OK, "查询成功", umsMember);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody UmsMember umsMember){
        memberService.save(umsMember);
        return new Result(true, StatusCode.OK, "保存成功");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result updata(@PathVariable("id") String id, @RequestBody UmsMember umsMember){
        umsMember.setId(id);
        memberService.updata(umsMember);
        return new Result(true, StatusCode.OK, "更新成功");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable("id") String id){
        memberService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public UmsMember login(@RequestBody UmsMember umsMemberParam){
        UmsMember umsMember = memberService.login(umsMemberParam);
        return umsMember;
    }

    @RequestMapping(value = "/getSourceType/{memberId}", method = RequestMethod.GET)
    public String getSourceType(@PathVariable("memberId") String memberId){
        UmsMember umsMember = memberService.getSourceType(memberId);
        return umsMember.getSourceType();
    }

    @RequestMapping(value = "/getReceiveAddressByMemberId/{memberId}", method = RequestMethod.GET)
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(@PathVariable("memberId") String memberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = memberService.getReceiveAddressByMemberId(memberId);
        return umsMemberReceiveAddresses;
    }

}
