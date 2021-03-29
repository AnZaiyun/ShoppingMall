package com.anzaiyun.shoppingmall.search.controller.web;

import com.anzaiyun.shoppingmall.search.service.SearchProductService;
import com.anzaiyun.shoppingmall.search.vo.SearchParam;
import com.anzaiyun.shoppingmall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

    @Autowired
    SearchProductService searchProductService;

    @RequestMapping("/list.html")
    public String list(SearchParam searchParam, Model model){

        SearchResult searchResult = searchProductService.search(searchParam);
        model.addAttribute("result",searchResult);

        return "list";
    }
}
