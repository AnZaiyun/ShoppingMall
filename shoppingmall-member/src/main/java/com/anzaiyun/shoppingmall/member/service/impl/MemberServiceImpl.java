package com.anzaiyun.shoppingmall.member.service.impl;

import com.anzaiyun.shoppingmall.member.entity.MemberLevelEntity;
import com.anzaiyun.shoppingmall.member.service.MemberLevelService;
import com.anzaiyun.shoppingmall.member.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.member.dao.MemberDao;
import com.anzaiyun.shoppingmall.member.entity.MemberEntity;
import com.anzaiyun.shoppingmall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(UserRegistVo userRegistVo) {
        MemberEntity member = new MemberEntity();
        member.setUsername(userRegistVo.getUName());
        member.setPassword(userRegistVo.getUPwd());
        member.setMobile(userRegistVo.getUPhone());

        //设置用户的等级id，需要为用户等级表ums_member_level中的默认用户等级
        MemberLevelEntity defaultMemberLevel = memberLevelService.getDefaultLevel();
        member.setLevelId(defaultMemberLevel.getId());

        member.setCreateTime(new Date());

        this.save(member);

    }

}