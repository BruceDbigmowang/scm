package com.scm.dao;

import com.scm.pojo.BuySum;
import com.scm.pojo.InquiryList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BuySumDAO extends JpaRepository<BuySum , String> {
    /**
     * 根据创建日期查询采购单
     * 用于统计数量  生成采购单单号
     */
    List<BuySum> findByCreateDate(LocalDate createDate);

    /**
     * 多条件查询
     * Or 与 And联合使用
     */
    List<BuySum> findAll(Specification<BuySum> querySpec);

    /**
     * 分页查询所有
     */
    List<BuySum> findAllByOrderByCreateDateDesc(Pageable pg);
}
