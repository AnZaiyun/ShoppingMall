package com.anzaiyun.shoppingmall.cart.vo;

import lombok.Data;

/**
 * 当前用户是临时用户还是登陆用户
 */
@Data
public class UserStatus {

    private Long userId;
    private String userKey;

    /**
     * 是否创建了临时用户
     */
    private Boolean createTmpUser=true;
}
