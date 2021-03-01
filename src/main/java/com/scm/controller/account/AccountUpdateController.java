package com.scm.controller.account;

import com.scm.pojo.Account;
import com.scm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class AccountUpdateController {

    @Autowired
    UserService userService;

    /**
     * 修改账号密码
     * 输入的两次新密码在前台验证了
     * 传入到后台的只有旧密码与新密码
     */
    @RequestMapping(value = "changePassword" , method = RequestMethod.PUT)
    public String updatePassword(HttpSession session , String oldPwd , String newPwd){
        return userService.changePwd(session , oldPwd , newPwd);
    }

    /**
     * 修改手机号
     * 输入的手机号检查  在前台完成
     * 1、输入不能为空
     * 2、长度只能是11位
     * 3、必须都是数字
     */
    @RequestMapping(value = "changePhone" , method = RequestMethod.PUT)
    public String updatePhone(HttpSession session , String phone){
        Account user = (Account)session.getAttribute("user");
        String account = user.getAccount();
        return userService.changePhone(account , phone);
    }

    /**
     * 修改邮箱
     * 邮箱地址的验证在前台完成
     * 后台直接修改数据
     */
    @RequestMapping(value = "changeEmail" , method = RequestMethod.PUT)
    public String updateEmail(HttpSession session , String email){
        Account user = (Account)session.getAttribute("user");
        String account = user.getAccount();
        return userService.changeMail(account , email);
    }

    /**
     * 禁用或启用某一账号
     * 即 修改账号的状态
     */
    @RequestMapping(value = "changeAccountStatus" , method = RequestMethod.PUT)
    public String updateStatus(String account , String status){
        return userService.closeUse(account , status);
    }

    /**
     * 重置密码
     */
    @RequestMapping(value = "restPassword" , method = RequestMethod.PUT)
    public String restUserPassword(String account){
        return userService.restPwd(account);
    }
}
