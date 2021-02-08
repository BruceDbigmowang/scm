package com.scm.dao;

import com.scm.pojo.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthenticationDAO extends JpaRepository<Authentication , Integer> {
    /**
     * 根据供应商代码查找对应的认证消息
     */
    List<Authentication> findBySupplierCode(String supplierCode);
}
