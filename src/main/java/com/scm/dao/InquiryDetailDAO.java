package com.scm.dao;

import com.scm.pojo.InquiryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryDetailDAO extends JpaRepository<InquiryDetail , Integer> {

    /**
     * 根据询价单编号 查询询价单详情
     */
    List<InquiryDetail> findByInquiryID(String inquiryID);
}
