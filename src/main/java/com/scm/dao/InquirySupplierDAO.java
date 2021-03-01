package com.scm.dao;

import com.scm.pk.PKInquirySupplier;
import com.scm.pojo.InquirySupplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquirySupplierDAO extends JpaRepository<InquirySupplier , PKInquirySupplier> {
    /**
     * 根据询价单编号来查询询价单供应商报价状态
     */
    List<InquirySupplier> findById(String id);

    /**
     * 根据询价单单号以及报价状态 来查询已报价供应商
     */
    List<InquirySupplier> findByIdAndAndStatus(String id , String status);

}
