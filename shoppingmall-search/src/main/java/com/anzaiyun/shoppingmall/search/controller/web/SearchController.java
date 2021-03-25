package com.anzaiyun.shoppingmall.search.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

    @RequestMapping("/list.html")
    public String list(){
        return "list";
    }
}
