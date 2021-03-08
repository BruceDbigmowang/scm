package com.scm.service;

import com.scm.dao.*;
import com.scm.pk.PKInquirySupplier;
import com.scm.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnswerService {
    @Autowired
    SupplierDAO supplierDAO;
    @Autowired
    InquiryListDAO inquiryListDAO;
    @Autowired
    InquiryDetailDAO inquiryDetailDAO;
    @Autowired
    InquiryAnswerDAO answerDAO;
    @Autowired
    InquirySupplierDAO inquirySupplierDAO;

    /**
     * 加载进入到报价界面
     *加载以下信息
     * 询价单状态信息
     * 供应商信息
     * 询价单总汇信息
     * 询价单明细信息
     */
    public Map<String , Object> loadProduct(int num){
        Map<String , Object> map = new HashMap<>();
        InquirySupplier is = inquirySupplierDAO.findById(num).get();
        String inquiryID = is.getId();
        String supplierCode = is.getSupplierCode();
        Supplier supplier = supplierDAO.findById(supplierCode).get();
        map.put("supplier" , supplier);
        InquiryList inquiryList = inquiryListDAO.findById(inquiryID).get();
        map.put("sum" , inquiryList);
        LocalDate deadline = inquiryList.getDeadline();
        if(deadline.isEqual(LocalDate.now()) || deadline.isAfter(LocalDate.now())){
            if(inquiryList.getStatus().equals("O")){
                List<InquirySupplier> isList = inquirySupplierDAO.findByIdAndSupplierCode(inquiryID , supplierCode);
                if(!isList.isEmpty()){
                    InquirySupplier inquirySupplier = isList.get(0);
                    if(inquirySupplier.getStatus().equals("O")){
                        map.put("result" , "OK");
                    }else{
                        map.put("result" , "3");
                    }
                }else{
                    map.put("result","数据查询错误");
                }


            }else{
                map.put("result" , "1");
            }
        }else{
            map.put("result" , "2");
        }
        List<InquiryDetail> detailList = inquiryDetailDAO.findByInquiryID(inquiryID);
        for(int i = 0 ; i < detailList.size() ; i++){
            InquiryDetail inquiryDetail = detailList.get(i);
            if(inquiryDetail.getBrand() == null){
                inquiryDetail.setBrand("");
            }
            if(inquiryDetail.getNote() == null){
                inquiryDetail.setNote("");
            }
        }
        map.put("details" , detailList);
        return map;
    }

    /**
     * 根据inquiryID查询相关明细
     */
    public List<InquiryDetail> details(String inquiryID){
        return inquiryDetailDAO.findByInquiryID(inquiryID);
    }

    /**
     * 将报价详情写入到数据库中
     * 并且还需要改变SCM_InquirySupplier表中的状态
     */
    @Transactional
    public String saveAnswer(List<InquiryAnswer> answerList , String inquiryID , String supplierCode){
        List<InquirySupplier> isList = inquirySupplierDAO.findByIdAndSupplierCode(inquiryID , supplierCode);
        InquirySupplier inquirySupplier = isList.get(0);
        if(inquirySupplier.getStatus().equals("C")){
            return "报价单已填写完成";
        }

        for(InquiryAnswer answer : answerList){
            answerDAO.save(answer);
        }

        inquirySupplier.setStatus("C");
        inquirySupplier.setStatusDes("已报价");
        inquirySupplierDAO.save(inquirySupplier);

        return "OK";
    }

    /**
     * 加载进入到供应商报价详情界面中，需要加载
     *      1、供应商信息
     *      2、报价明细
     */
    public Map<String , Object> getAnswerDetail(String supplierCode , String inquiryID){
        Map<String , Object> map = new HashMap<>();
        Supplier supplier = supplierDAO.findById(supplierCode).get();
        map.put("supplier" , supplier);
        List<InquiryAnswer> answerList = answerDAO.findByInquiryIDAndSupplierCode(inquiryID , supplierCode);
        for(int i = 0 ; i < answerList.size(); i++){
            InquiryAnswer inquiryAnswer = answerList.get(i);
            BigDecimal tax = inquiryAnswer.getTaxRate().multiply(new BigDecimal(100)).setScale(2 , BigDecimal.ROUND_HALF_UP);
            inquiryAnswer.setTaxRate(tax);
        }
        map.put("answerList" , answerList);
        return map;
    }

    @Transactional
    public String saveAnswerChange(String[] aIds , String[] agreePrices , String[] agreeCycles , String[] taxRates){
        for(int i = 0 ; i < aIds.length ; i++){
            int aId = Integer.parseInt(aIds[i]);
            InquiryAnswer inquiryAnswer = answerDAO.findById(aId).get();
           if(!"".equals(agreePrices[i])){
               BigDecimal agreePrice = new BigDecimal(agreePrices[i]).setScale(2,BigDecimal.ROUND_HALF_UP);
               inquiryAnswer.setAgreePrice(agreePrice);
               int quantity = inquiryAnswer.getQuantity();
               BigDecimal agreeTotal = agreePrice.multiply(new BigDecimal(quantity)).setScale(2 , BigDecimal.ROUND_HALF_UP);
               inquiryAnswer.setAgreeTotal(agreeTotal);
           }
            if(!"".equals(agreeCycles[i])){
                int agreeCycle = Integer.parseInt(agreeCycles[i]);
                inquiryAnswer.setAgreeCycle(agreeCycle);
            }
            if(!"".equals(taxRates[i])){
                BigDecimal taxRate = new BigDecimal(taxRates[i]);
                taxRate = taxRate.divide(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP);
                inquiryAnswer.setTaxRate(taxRate);
            }
            answerDAO.save(inquiryAnswer);
        }
        return "OK";
    }

    public InquirySupplier findByNum(int num){
        return inquirySupplierDAO.findById(num).get();

    }
}
