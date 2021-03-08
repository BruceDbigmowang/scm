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

    /**
     * 获取账号信息
     */
    @RequestMapping(value = "getAccountInfo" , method = RequestMethod.GET)
    public Account findAccountInfo(HttpSession session){
        Account user = (Account)session.getAttribute("user");
        return userService.findUser(user.getAccount());
    }

    /**
     * 查询所有的用户
     * @param start
     * @return
     */
    @RequestMapping(value = "findAllUser" , method = RequestMethod.GET)
    public Map<String , Object> getAllUser(int start){
        return userService.findUserByPage(start);
    }

    /**
     * 根据条件来查询用户
     * @param search
     * @return
     */
    @RequestMapping(value = "findUserByCondition" , method = RequestMethod.GET)
    public List<Account> findByCondition(String search){
        return userService.findByCondition(search);
    }

    /**
     * 查看所有角色
     * 截止到2021-03 角色有两种：管理员  一般用户
     * 该功能只为创建用户时，查询出所有角色让人选择
     * @return
     */
    @RequestMapping(value = "selectAllRole" , method = RequestMethod.GET)
    public List<Role> findAllRole(){
        return userService.getAllRole();
    }
}
