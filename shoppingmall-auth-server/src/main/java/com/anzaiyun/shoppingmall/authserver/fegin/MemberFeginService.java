package com.anzaiyun.shoppingmall.authserver.fegin;

import com.anzaiyun.common.utils.R;
import com.anzaiyun.shoppingmall.authserver.vo.UserLoginVo;
import com.anzaiyun.shoppingmall.authserver.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("shoppingmall-member")
public interface MemberFeginService {

    @RequestMapping("member/member/regist")
    public R regist(@RequestBody UserRegistVo userRegistVo);

    @RequestMapping("member/member/beforeRegist")
    public Map<String,String> beforeRegist(@RequestBody UserRegistVo userRegistVo);

    @RequestMapping("member/member/beforeLogin")
    Map<String,String> beforeLogin(@RequestBody UserLoginVo userLoginVo);
}
