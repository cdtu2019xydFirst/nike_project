package com.yadong.nike.member.service;

import com.yadong.nike.bean.UmsMember;
import com.yadong.nike.bean.UmsMemberReceiveAddress;
import com.yadong.nike.member.mapper.MemberMapper;
import com.yadong.nike.member.mapper.UmsMemberReceiveAddressMapper;
import com.yadong.nike.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/19 | 20:19
 **/
@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    private IdWorker idWorker;

    public List<UmsMember> findAll() {
        return memberMapper.selectAll();
    }

    public UmsMember findById(String id) {
        UmsMember umsMember1 = new UmsMember();
        umsMember1.setId(id);
        UmsMember umsMember = memberMapper.selectOne(umsMember1);
        return umsMember;
    }

    public void save(UmsMember umsMember) {
        umsMember.setId(idWorker.nextId() + "");
        memberMapper.insertSelective(umsMember);
    }

    public void updata(UmsMember umsMember) {
        Example example = new Example(UmsMember.class);
        example.createCriteria().andEqualTo("id", umsMember.getId());
        memberMapper.updateByExampleSelective(umsMember, example);
    }

    public void deleteById(String id) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(id);
        memberMapper.delete(umsMember);
    }

    public UmsMember login(UmsMember umsMemberParam) {
        UmsMember umsMember = memberMapper.selectOne(umsMemberParam);
        return umsMember;
    }

    public UmsMember getSourceType(String memberId) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(memberId);
        UmsMember member = memberMapper.selectOne(umsMember);
        return member;
    }

    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> addressList = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
        return addressList;
    }
}
