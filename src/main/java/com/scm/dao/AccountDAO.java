package com.scm.dao;

import com.scm.pojo.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountDAO extends JpaRepository<Account , String> {
    /**
     * 根据用户账号或手机号来查找用户信息
     * 用于登录
     */
    List<Account> findByAccountOrPhone(String account , String phone);

    /**
     * 根据账号  或  用户名来搜索用户(分页显示)
     * 用于账号管理
     */
    List<Account> findByAccountLikeOrNameLikeOrPhoneLikeOrEmailLike(String account , String name , String phone , String email);

    /**
     * 根据账号的创建时间来查询所有账号（分页显示）
     */
    List<Account> findAllByOrderByCreateTimeDesc(Pageable pg);

    List<Account> findByAccount(String account);
}
