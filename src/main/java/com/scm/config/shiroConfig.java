package com.scm.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.scm.shiro.MyShiroRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class shiroConfig {
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /**
         * 设置权限过滤器
         */
        Map<String , String> perms = new HashMap<String , String>();
        perms.put("/foreLogin" , "anon");
        perms.put("/foreIndex" , "authc");
        perms.put("/foreAllSupplier" , "authc");
        perms.put("/forSupplierDetail" , "authc");
        perms.put("/foreQueryInquiry" , "authc");
        perms.put("/foreInquiryOne" , "authc");
        perms.put("/foreInquirySome" , "authc");
        perms.put("/foreInquiryDetail" , "authc");
        perms.put("/foreSupplierInquiry" , "authc");
        perms.put("/foreAnalysis" , "authc");
        perms.put("/foreAnalysisDetail" , "authc");
        perms.put("/foreBuyList" , "authc");
        perms.put("/foreBuyDetail" , "authc");
        perms.put("/foreUserCenter" , "authc");
        perms.put("/foreUserManage" , "authc");
        shiroFilterFactoryBean.setLoginUrl("foreLogin");
        shiroFilterFactoryBean.setUnauthorizedUrl("foreLogin");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(perms);

        return shiroFilterFactoryBean;
    }

    //创建securityManage类的注入bean
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("shiroRealm") MyShiroRealm shiroRealm){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(shiroRealm);
        return defaultWebSecurityManager;
    }

    //创建自定义realm域类的注入bean
    @Bean(name = "shiroRealm")
    public MyShiroRealm getMyShiroRealm(){
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("MD5");
        credentialsMatcher.setHashIterations(6);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        myShiroRealm.setCredentialsMatcher(credentialsMatcher);
        return myShiroRealm;
    }

    @Bean
    public SessionDAO sessionDAO(){
        return new MemorySessionDAO();
    }

    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("jeesite.session.id");
        return simpleCookie;
    }


    @Bean //提供对thymeleaf模板引擎的页面中的shiro自定义标签的支持
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }
}
