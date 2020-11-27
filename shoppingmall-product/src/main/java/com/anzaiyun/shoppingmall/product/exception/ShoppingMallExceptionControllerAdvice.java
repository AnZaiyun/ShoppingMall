package com.anzaiyun.shoppingmall.product.exception;

import com.anzaiyun.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.anzaiyun.shoppingmall.product.controller")
public class ShoppingMallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){

        log.error("数据校验出现问题{},异常类型:{}", e.getMessage(), e.getClass());

        BindingResult bindingResult = e.getBindingResult();

        Map<String,String> errorMsg = new HashMap<>();
        bindingResult.getFieldErrors().forEach((item)->{
            errorMsg.put(item.getField(),item.getDefaultMessage());
        });

        return R.error(400,"提交数据不合法").put("data",errorMsg);
    }
}
