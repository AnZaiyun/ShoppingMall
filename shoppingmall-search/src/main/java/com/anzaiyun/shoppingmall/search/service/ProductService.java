package com.anzaiyun.shoppingmall.search.service;

import com.anzaiyun.common.to.es.SkuEsModelTo;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    public void productUp(List<SkuEsModelTo> skuEsModelList) throws IOException;
}
