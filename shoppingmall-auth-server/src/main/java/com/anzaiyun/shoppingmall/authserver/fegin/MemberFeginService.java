package com.anzaiyun.shoppingmall.authserver.fegin;

import com.anzaiyun.common.utils.R;
import com.anzaiyun.shoppingmall.authserver.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("shoppingmall-member")
public interface MemberFeginService {

    @RequestMapping("member/member/regist")
    public R regist(@RequestBody UserRegistVo userRegistVo);
}
