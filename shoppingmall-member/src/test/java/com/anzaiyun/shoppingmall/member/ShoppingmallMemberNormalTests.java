package com.anzaiyun.shoppingmall.member;

import com.anzaiyun.common.errorenum.AuthServerExceptionInfoEnum;
import org.junit.jupiter.api.Test;

public class ShoppingmallMemberNormalTests {

    @Test
    public void testErrorInfoEnum(){
        System.out.println(AuthServerExceptionInfoEnum.PHONE_EXIST_ERROR.getMsg());
        System.out.println(AuthServerExceptionInfoEnum.USERNAME_EXIST_ERROR.getMsg());

    }
}
