package com.anzaiyun.shoppingmall.search.service;

import com.anzaiyun.shoppingmall.search.vo.SearchParam;
import com.anzaiyun.shoppingmall.search.vo.SearchResult;

public interface SearchProductService {
    SearchResult search(SearchParam searchParam);
}
