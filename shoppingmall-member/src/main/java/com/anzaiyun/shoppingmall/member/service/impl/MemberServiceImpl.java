package com.anzaiyun.shoppingmall.member.service.impl;

import com.anzaiyun.shoppingmall.member.entity.MemberLevelEntity;
import com.anzaiyun.shoppingmall.member.exception.PhoneExsitException;
import com.anzaiyun.shoppingmall.member.exception.UserNameExsitException;
import com.anzaiyun.shoppingmall.member.service.MemberLevelService;
import com.anzaiyun.shoppingmall.member.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        /**
         * 这里密码不能直接保存明文，应该保存密文，加密方式md5（加盐方式）
         * BCryptPasswordEncoder.encode()
         * md5加密是不可逆的，所以只能通过对字符串b加密，然后比对a，b加密后的字符串是否相同来判断a，b是否相同
         * BCryptPasswordEncoder.match()
         */
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodePwd = passwordEncoder.encode(userRegistVo.getUPwd());
        member.setPassword(encodePwd);
        member.setMobile(userRegistVo.getUPhone());

        //设置用户的等级id，需要为用户等级表ums_member_level中的默认用户等级
        MemberLevelEntity defaultMemberLevel = memberLevelService.getDefaultLevel();
        member.setLevelId(defaultMemberLevel.getId());

        member.setCreateTime(new Date());

        this.save(member);

    }

    /**
     * 如果查询到手机号相同的账号，返回存在异常
     * @param uPhone
     * @throws PhoneExsitException
     */
    @Override
    public void checkPhoneUnique(String uPhone) throws PhoneExsitException {
        int count = this.count(new QueryWrapper<MemberEntity>().eq("mobile", uPhone));
        if (count>0){
            throw new PhoneExsitException();
        }
    }

    /**
     * 如果查询到名字相同的账号，返回存在异常
     * @param uName
     * @throws UserNameExsitException
     */
    @Override
    public void checkUserNameUnique(String uName) throws UserNameExsitException {
        int count = this.count(new QueryWrapper<MemberEntity>().eq("username", uName));
        if (count>0){
            throw new UserNameExsitException();
        }
    }

}