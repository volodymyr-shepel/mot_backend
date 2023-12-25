package com.mot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/product/customer")
public class CustomerController {

    @GetMapping(path = "/test")
    public String test(){
        return "CUSTOMER SUCCESS";
    }
}
