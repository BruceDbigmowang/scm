package com.scm.service;

import com.scm.dao.AccountDAO;
import com.scm.dao.AccountRoleDAO;
import com.scm.dao.FuncDAO;
import com.scm.dao.RoleFuncDAO;
import com.scm.pojo.Account;
import com.scm.pojo.AccountRole;
import com.scm.pojo.Func;
import com.scm.pojo.RoleFunc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    AccountRoleDAO accountRoleDAO;
    @Autowired
    RoleFuncDAO roleFuncDAO;
    @Autowired
    FuncDAO funcDAO;

    /**
     *  根据账号查询权限
     *
     *  返回权限的集合
     */
    public List<Func> findPermsByAccount(String account){
        List<Func> funcList = new ArrayList<>();
        List<AccountRole> accountRoleList = accountRoleDAO.findByAccount(account);
        for(AccountRole accountRole : accountRoleList){
            int roleId = accountRole.getRoleId();
            List<RoleFunc> roleFuncList = roleFuncDAO.findByRoleId(roleId);
            for(RoleFunc roleFunc : roleFuncList){
                int funcId = roleFunc.getFuncId();
                Func func = funcDAO.findById(funcId).orElse(null);
                if(func != null && !funcList.contains(func)){
                    funcList.add(func);
                }
            }
        }
        return funcList;
    }

    /**
     * 根据账号或手机号来查询账号信息
     *
     * 若未查询到，则返回空值
     * 若查询到返回对象
     */
    public Account findByAccountOrPhone(String inputStr){
        List<Account> accountList = accountDAO.findByAccountOrPhone(inputStr , inputStr);
        if(accountList.isEmpty()){
            return null;
        }else{
            return accountList.get(0);
        }
    }
}
