package com.anzaiyun.shoppingmall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DoneItem {
    //采购需求id
    @NotNull
    private Long itemId;

    //完成状态
    private Integer status;

    //采购失败原因
    private String reason;
}
