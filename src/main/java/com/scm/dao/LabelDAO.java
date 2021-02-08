package com.scm.dao;

import com.scm.pojo.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabelDAO extends JpaRepository<Label , Integer> {
    /**
     * 根据供应商代码查询出所有与供应商相关的标签名称
     */
    List<Label> findBySupplierCode(String supplierCode);

    /**
     * 根据供应商编码、标签名称，查询具体的供应商
     * 要保证。任何一个供应商不能存在标签名称相同的
     */
    List<Label> findBySupplierCodeAndLabelName(String supplierCode , String labelName);
}
