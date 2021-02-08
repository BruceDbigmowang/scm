package com.scm.dao;

import com.scm.pojo.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryAnswerDAO extends JpaRepository<InquiryAnswer , Integer> {

    /**
     * 根据询价单单号  供应商编码 查询供应商的报价详情
     */
    List<InquiryAnswer> findByInquiryIDAndSupplierCode(String inquiryId , String supplierCode);
}
