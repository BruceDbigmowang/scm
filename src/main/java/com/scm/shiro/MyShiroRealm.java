package com.scm.shiro;

import com.scm.pojo.Account;
import com.scm.pojo.Func;
import com.scm.service.AccountService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    AccountService accountService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取用户对象
        Account user = (Account)principalCollection.getPrimaryPrincipal();
        //获取用户权限列表
        List<String> perms = new ArrayList<>();
        //根据用户id获取权限类别
        List<Func> funcList = accountService.findPermsByAccount(user.getAccount());
        if(funcList != null){
            for(Func func:funcList){
                perms.add(func.getFuncCode());
            }
        }
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addStringPermissions(perms); //把用户的所有权限类别添加到对象中
        //authorizationInfo.addRoles(roles); //把所有的用户角色添加到对象中
        return authorizationInfo;
    }


    //认证方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String account = authenticationToken.getPrincipal().toString();
        //使用账号或手机号查询到具体某一个用户
        Account user = accountService.findByAccountOrPhone(account);
        //账号登录
        String password = user.getPassword();
        //把用户名和密码封装到AuthenticationInfo对象中
        ByteSource salt = ByteSource.Util.bytes(user.getSalt());
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user,password,salt ,"shiroRealm");
        return authenticationInfo;
    }

}
