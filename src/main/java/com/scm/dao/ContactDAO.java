package com.scm.dao;

import com.scm.pojo.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactDAO extends JpaRepository<Contacts , Integer> {

    /**
     * 根据供应商代码查找对应的
     * @param supplierCode
     * @return
     */
    List<Contacts> findBySupplierCode(String supplierCode);

    /**
     * 根据供应商代码、联系人类型、是否是主要联系人 来找到对接人
     */
    List<Contacts> findBySupplierCodeAndTypeAndMainContact(String supplierCode , String type , String mainContact);
}
