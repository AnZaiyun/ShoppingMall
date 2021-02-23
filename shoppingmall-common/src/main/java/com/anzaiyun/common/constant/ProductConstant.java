package com.anzaiyun.common.constant;

/**
 * @author AnZaiyun
 */
public class ProductConstant {

    public enum AttrEnum{
        /**
         *
         */
        ATTR_TYPE_BASE(1,"基本属性"), ATTR_TYPE_SALE(2,"销售属性");

        private int code;
        private String msg;

        AttrEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
