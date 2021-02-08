package com.scm.dao;

import com.scm.pojo.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SupplierDAO extends JpaRepository<Supplier , String> {

    /**
     * 根据创建时间 查询供应商
     *
     * 主要为了获取当天创建的供应商数量 用于生成供应商编码
     */
    List<Supplier> findByCreateDate(LocalDate date);

    /**
     * 根据条件并分页查询供应商
     *
     *  +供应商编码/供应商简称/供应商全称
     *  +供应商性质
     *  +供应种类
     *  +认证状态
     *  +禁用状态
     */
    List<Supplier> findAll(Specification<Supplier> querySpec , Pageable pg);     //分页查询
    List<Supplier> findAll(Specification<Supplier> querySpec);                   //用于查询总数

    /**
     * 分页查询所有数据
     *
     * 所有数据按照创建时间降序
     */
    List<Supplier> findAllByOrderByCreateDateDesc(Pageable pageable);

    /**
     * 根据供应商名称来查询供应商
     *
     * 用于批量导入供应商时，排除重复的可能
     */
    List<Supplier> findBySupplierName(String supplier);
}
