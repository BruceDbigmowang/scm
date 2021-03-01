package com.scm.dao;

import com.scm.pojo.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryAnswerDAO extends JpaRepository<InquiryAnswer , Integer> {

    /**
     * 根据询价单单号  供应商编码 查询供应商的报价详情
     */
    List<InquiryAnswer> findByInquiryIDAndSupplierCode(String inquiryId , String supplierCode);

    /**
     * 根据询价单单号  询价明细编号 查找某一询价单中某一物件的所有报价详情
     */
    List<InquiryAnswer> findByInquiryIDAndDid(String inquiryID , int did);

    /**
     * 根据条件来进行排序
     */
    //1、含税单价（降序）
    List<InquiryAnswer> findByInquiryIDAndDidOrderByAgreePriceDesc(String inquiryID , int did);
    //2、含税单价（升序）
    List<InquiryAnswer> findByInquiryIDAndDidOrderByAgreePriceAsc(String inquiryID , int did);
    //3、交货周期（降序）
    List<InquiryAnswer> findByInquiryIDAndDidOrderByAgreeCycleDesc(String inquiryID , int did);
    //4、交货周期（升序）
    List<InquiryAnswer> findByInquiryIDAndDidOrderByAgreeCycleAsc(String inquiryID , int did);

    /**
     * 根据 inquiryID  did  supplierCode 查询出某供应商对某一件商品的报价
     */
    List<InquiryAnswer> findByInquiryIDAndDidAndSupplierCode(String inquiryID , int did , String supplierCode);

    /**
     * 根据inquiryID 查找报价
     * 若未查询到任何信息  则没有任何供应商给出报价
     * 若有信息  则有供应商给出报价
     */
    List<InquiryAnswer> findByInquiryID(String inquiryID);

}
