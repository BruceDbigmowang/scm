package com.scm.dao;

import com.scm.pojo.InquiryList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InquiryListDAO extends JpaRepository<InquiryList , String> {
    /**
     * 根据日期来查询当天创建的询价单数量
     *
     * 用于生成询价单编号
     */
    List<InquiryList> findByCreateDate(LocalDate now);

    /**
     * 页面刚载入时，加载所有数据，根据创建时间排序
     */
    List<InquiryList> findAllByOrderByCreateDateDesc(Pageable pageable);

    /**
     * 多条件查询询价单
     *
     * 用于询价单管理中 搜索
     */
    List<InquiryList> findAll(Specification<InquiryList> querySpec , Pageable pg);
    List<InquiryList> findAll(Specification<InquiryList> querySpec); //用于查询总数

    /**
     * 根据日期  询价单状态查询当前时间应关闭的询价单
     */
    List<InquiryList> findByDeadlineAndStatus(LocalDate now , String status);

    /**
     * 截止日期在当前时间之前 关闭询价单
     *
     * 目的：以防之前由于系统出现问题，导致某些询价单在截止时间没有关闭
     * 系统恢复正常之后，第二天重新关闭过期询价单
     */
    List<InquiryList> findByDeadlineBeforeAndStatus(LocalDate now , String status);
}
