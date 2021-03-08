package com.scm.controller.account;

import com.scm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountInsertController {
    @Autowired
    UserService userService;

    /**
     * 新增用户
     */
    @RequestMapping(value = "addAccount" , method = RequestMethod.POST)
    public String createAccount(String account , String name , String phone , String email , int roleId){
        return userService.addAccount(account , name , phone , email , roleId);
    }
}
