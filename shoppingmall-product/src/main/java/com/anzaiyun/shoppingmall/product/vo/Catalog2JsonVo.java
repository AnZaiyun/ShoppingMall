package com.anzaiyun.shoppingmall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catalog2JsonVo {

    /**
     * 1级分类id，父分类id
     */
    private Long catalog1Id;
    /**
     * 3级分类列表
     */
    private List<Catalog3Vo> catalog3List;
    /**
     * 2级分类id
     */
    private Long id;
    /**
     * 2级分类名称
     */
    private String name;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catalog3Vo{
        private Long catalog2Id; //2级分类id，父分类id
        private Long id;  //3级分类id
        private String name; //3级分类名称

    }

}
