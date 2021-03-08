package com.scm.dao;

import com.scm.pk.PKAccountRole;
import com.scm.pojo.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRoleDAO extends JpaRepository<AccountRole , PKAccountRole> {
    /**
     * 根据账号来查询该账号的所有角色
     * @param account
     * @return
     */
    List<AccountRole> findByAccount(String account);
}
