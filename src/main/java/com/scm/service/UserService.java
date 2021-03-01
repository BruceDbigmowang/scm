package com.scm.service;

import com.scm.dao.AccountDAO;
import com.scm.dao.AccountRoleDAO;
import com.scm.dao.RoleDAO;
import com.scm.pojo.Account;
import com.scm.pojo.AccountRole;
import com.scm.pojo.Role;
import com.scm.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    AccountRoleDAO accountRoleDAO;
    @Autowired
    RoleDAO roleDAO;

    /**
     * 用户自己更改密码
     * 需要输入：
     * 1、旧密码
     * 2、新密码
     * 3、再次输入新密码
     * @return
     */
    @Transactional
    public String changePwd(HttpSession session , String oldPwd , String newPwd){
        Account user = (Account)session.getAttribute("user");
        String password = user.getPassword();
        oldPwd = MD5Util.addMD5(oldPwd).toString();
        if(!password.equals(oldPwd)){
            return "旧密码输入错误";
        }else{
            newPwd = MD5Util.addMD5(newPwd).toString();
            user.setPassword(newPwd);
            try{
                accountDAO.save(user);
            }catch (Exception e){
                return e.getMessage();
            }
            return "OK";
        }
    }

    /**
     * 重置账号密码
     * 管理员操作，重置后的密码：123456
     */
    @Transactional
    public String restPwd(String account){
        Account user = accountDAO.findById(account).get();
        String pwd = MD5Util.addMD5("123456").toString();
        user.setPassword(pwd);
        try{
            accountDAO.save(user);
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";

    }

    /**
     * 禁用 或 启用某一账号
     * 管理员操作
     * 禁用  将账号状态设置为"N"
     * 启用  将账号状态设置为"Y"
     */
    @Transactional
    public String closeUse(String account , String status){
        Account user = accountDAO.findById(account).get();
        user.setStatus(status);
        try{
            accountDAO.save(user);
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";

    }

    /**
     * 创建一个账号
     * 新增 Account
     * 新增 AccountRole
     */
    @Transactional
    public String addAccount(String account , String name, String mobile , String email , int roleId){
        List<Account> users = accountDAO.findByAccount(account);
        if(!users.isEmpty()){
            return "账号："+account+"已存在";
        }
        Account usr = new Account();
        usr.setAccount(account);
        usr.setName(name);
        usr.setPassword(MD5Util.addMD5("123456").toString());
        usr.setPhone(mobile);
        usr.setEmail(email);
        usr.setSalt("abc");
        usr.setStatus("Y");
        usr.setCreateTime(new Date());

        AccountRole ar = new AccountRole();
        ar.setAccount(account);
        ar.setRoleId(roleId);
        try{
            accountDAO.save(usr);
            accountRoleDAO.save(ar);
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";
    }

    /**
     * 修改账号的手机号
     */
    @Transactional
    public String changePhone(String account , String phone){
        Account user = accountDAO.findById(account).get();
        user.setPhone(phone);
        try{
            accountDAO.save(user);
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";
    }

    /**
     * 修改账号的邮箱
     */
    @Transactional
    public String changeMail(String account , String email){
        Account user = accountDAO.findById(account).get();
        user.setEmail(email);
        try{
            accountDAO.save(user);
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";
    }

    /**
     * 根据账号来查找账号的相关信息
     */
    public Account findUser(String account){
        return accountDAO.findById(account).get();
    }

    /**
     * 分页查询所有用户
     */
    public Map<String , Object> findUserByPage(int start){
        Map<String , Object> map = new HashMap<>();
        PageRequest pr = PageRequest.of(start, 10);
        List<Account> accountList = accountDAO.findAllByOrderByCreateTimeDesc(pr);
        for(int i = 0 ; i < accountList.size() ; i++){
            Account user = accountList.get(i);
            String status = user.getStatus();
            if(status.equals("Y")){
                user.setStatus("正常");
            }else if(status.equals("N")){
                user.setStatus("禁用");
            }
        }
        map.put("users" , accountList);
        List<Account> all = accountDAO.findAll();
        int pages = 0;
        if(all.size() % 10 == 0){
            pages = all.size() / 10;
        }else{
            pages = all.size() / 10 + 1;
        }
        map.put("pages" , pages);
        return map;
    }

    /**
     * 根据条件查询
     */
    public List<Account> findByCondition(String search){
        return accountDAO.findByAccountLikeOrNameLikeOrPhoneLikeOrEmailLike(search , search , search , search);
    }

    /**
     * 查询所有角色
     */
    public List<Role> getAllRole(){
        return roleDAO.findAll();
    }

}
