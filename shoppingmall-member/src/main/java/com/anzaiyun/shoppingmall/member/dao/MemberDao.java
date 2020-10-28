package com.anzaiyun.shoppingmall.member.dao;

import com.anzaiyun.shoppingmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-28 20:52:53
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
