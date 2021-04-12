package com.anzaiyun.shoppingmall.member.service;

import com.anzaiyun.shoppingmall.member.exception.PhoneExsitException;
import com.anzaiyun.shoppingmall.member.exception.UserNameExsitException;
import com.anzaiyun.shoppingmall.member.vo.UserRegistVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-28 20:52:53
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(UserRegistVo userRegistVo);

    /**
     * 检查手机号是否唯一，如果不唯一则直接抛出异常
     * @param uPhone
     */
    void checkPhoneUnique(String uPhone) throws PhoneExsitException;

    /**
     * 检查用户名是否唯一，如果不唯一则直接抛出异常
     * @param uName
     */
    void checkUserNameUnique(String uName) throws UserNameExsitException;
}

