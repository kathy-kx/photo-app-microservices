package com.kxzhu.photoapp.api.account.ui.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AccountController
 * @Description TODO
 * @Author zhukexin
 * @Date 2024-06-03 6:07 PM
 */
@RestController
@RequestMapping("/account")
public class AccountController {
    @RequestMapping(method = RequestMethod.GET, path = "/status/check")
    public String status(){
        return "Working..";
    }
}
