package com.anzaiyun.shoppingmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-28 20:52:53
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

