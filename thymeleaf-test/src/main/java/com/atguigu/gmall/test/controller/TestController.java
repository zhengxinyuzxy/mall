package com.atguigu.gmall.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String showPage(Model model) {
        model.addAttribute("abc", "hello");
        return "test";
    }
}
