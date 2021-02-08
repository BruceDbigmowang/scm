package com.scm.dao;

import com.scm.pk.PKAccountRole;
import com.scm.pojo.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRoleDAO extends JpaRepository<AccountRole , PKAccountRole> {
    List<AccountRole> findByAccount(String account);
}
