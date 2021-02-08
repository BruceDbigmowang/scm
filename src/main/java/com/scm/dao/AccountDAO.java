package com.scm.dao;

import com.scm.pojo.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountDAO extends JpaRepository<Account , String> {
    List<Account> findByAccountOrPhone(String account , String phone);
}
