package com.anzaiyun.shoppingmall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cart {
    /**
     * 具体商品
     */
    private List<CartItem> cartItems;

    /**
     * 商品总数量
     */
    private Long countNum;

    /**
     * 商品按类型区分的数量
     */
    private Long countType;

    /**
     * 商品总价
     */
    private BigDecimal totalAmount;

    /**
     * 优惠减免金额
     */
    private BigDecimal reduce=new BigDecimal(0);

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Long getCountNum() {
        if(cartItems==null || cartItems.size()==0){
            return 0L;
        }
        Long count = 0L;
        for (CartItem cartItem : this.cartItems) {
            count = count + cartItem.getCount();
        }
        return count;
    }

    public void setCountNum(Long countNum) {
//        this.countNum = countNum;
    }

    public Integer getCountType() {
        return cartItems==null?0:cartItems.size();
    }

    public void setCountType(Long countType) {
//        this.countType = countType;
    }

    public BigDecimal getTotalAmount() {
        if(cartItems==null || cartItems.size()==0){
            return new BigDecimal(0);
        }
        BigDecimal price = new BigDecimal(0);
        for (CartItem cartItem : this.cartItems) {
            price = price.add(cartItem.getTotalPrice());
        }
        return price;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
//        this.totalAmount = totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
