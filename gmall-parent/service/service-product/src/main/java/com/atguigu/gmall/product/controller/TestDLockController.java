package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.product.service.TestDLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test/product/testlock")
public class TestDLockController {

    @Autowired
    private TestDLockService testDLockService;

    @GetMapping("/setValue")
    public void setValue() {
        testDLockService.setValue();
    }

    @GetMapping("/setValueRedisson")
    public void setValueRedisson() {
        testDLockService.setValueRedisson();
    }

}
