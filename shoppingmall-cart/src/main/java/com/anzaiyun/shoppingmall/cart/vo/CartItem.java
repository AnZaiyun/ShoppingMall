package com.anzaiyun.shoppingmall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartItem {

    private Long skuId;
    /**
     * 保存是否被勾选
     */
    private int isChecked = 0;
    private String title;
    private String image;
    /**
     * 销售属性
     */
    private List<String> skuAttr;
    private BigDecimal price;
    private Long count;
    private BigDecimal totalPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public int getCheck() {
        return isChecked;
    }

    public void setCheck(int isChecked) {
        this.isChecked = isChecked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * 总价根据 单价*数量计算
     * @return
     */
    public BigDecimal getTotalPrice() {
        return this.getPrice().multiply(new BigDecimal(this.getCount()));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
