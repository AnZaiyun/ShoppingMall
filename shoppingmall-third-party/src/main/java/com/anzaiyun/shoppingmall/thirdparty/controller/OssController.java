package com.anzaiyun.shoppingmall.thirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.anzaiyun.common.utils.R;
import com.anzaiyun.shoppingmall.thirdparty.component.OssComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/oss")
public class OssController {

    @Autowired
    OssComponent ossComponent;

    @RequestMapping("/policy")
    public R policy(){

        Map<String, String> respMap = ossComponent.policy();

        return R.ok().put("data",respMap);

    }
}
