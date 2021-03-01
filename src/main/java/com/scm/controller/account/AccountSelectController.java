package com.scm.controller.account;

import com.scm.pojo.Account;
import com.scm.pojo.Role;
import com.scm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
public class AccountSelectController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "getAccountInfo" , method = RequestMethod.GET)
    public Account findAccountInfo(HttpSession session){
        Account user = (Account)session.getAttribute("user");
        return userService.findUser(user.getAccount());
    }

    @RequestMapping(value = "findAllUser" , method = RequestMethod.GET)
    public Map<String , Object> getAllUser(int start){
        return userService.findUserByPage(start);
    }

    @RequestMapping(value = "findUserByCondition" , method = RequestMethod.GET)
    public List<Account> findByCondition(String search){
        return userService.findByCondition(search);
    }

    @RequestMapping(value = "selectAllRole" , method = RequestMethod.GET)
    public List<Role> findAllRole(){
        return userService.getAllRole();
    }
}
