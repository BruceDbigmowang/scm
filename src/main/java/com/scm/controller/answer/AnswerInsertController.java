package com.scm.controller.answer;

import com.scm.pojo.*;
import com.scm.service.AnswerService;
import com.scm.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AnswerInsertController {
    @Autowired
    AnswerService answerService;
    @Autowired
    InquiryService inquiryService;

    /**
     * 发送报价单
     *
     * 记录：1、询价单单号  供应商编码
     *       2、各项报价
     */
    @RequestMapping(value = "writeAnswer", method = RequestMethod.POST)
    public String saveAnswer(int num , String[] brands , String[] specifications , String[] prices , String[] totalPrices , String[] cycles , String[] validityTimes){
        Map<String , Object> map = answerService.loadProduct(num);
        InquirySupplier is = answerService.findByNum(num);
        String inquiryID = is.getId();
        String supplierCode = is.getSupplierCode();
        Supplier supplier = (Supplier) map.get("supplier");
        InquiryList inquiryList = (InquiryList)map.get("sum");
        List<InquiryDetail> detailList = answerService.details(inquiryID);
        List<InquiryAnswer> answerList = new ArrayList<>();
        for(int i = 0 ; i < detailList.size() ; i++){

            int no = i + 1;

            InquiryAnswer answer = new InquiryAnswer();
            InquiryDetail detail = detailList.get(i);
            answer.setInquiryID(inquiryID);
            answer.setDid(detail.getId());
            answer.setKcode(detail.getKcode());
            answer.setProductName(detail.getProductName());
            answer.setSpecification(detail.getSpecification());
            answer.setQuantity(detail.getQuantity());
            answer.setUnit(detail.getUnit());
            answer.setSupplierCode(supplierCode);
            answer.setSupplierName(supplier.getSimpleName());
            if(brands.length != 0 && !"".equals(brands[i])){
                answer.setReceiveBrand(brands[i]);
            }
            if(specifications.length != 0 && !"".equals(specifications[i])){
                answer.setReceiveSpecification(specifications[i]);
            }
            if(prices.length != 0 && "".equals(prices[i])){
                return "产品"+no+"含税价格未填写";
            }else{
                try{
                    int quantity = detail.getQuantity();
                    BigDecimal price = new BigDecimal(prices[i]);
                    answer.setPrice(price);
                    answer.setAgreePrice(price);
                    answer.setTotalPrice(price.multiply(new BigDecimal(quantity)));
                    answer.setAgreeTotal(price.multiply(new BigDecimal(quantity)));
                    answer.setTaxRate(BigDecimal.ZERO);
                }catch (Exception e){
                    return "产品"+no+"含税价格填写错误(只能填写数字)";
                }
            }

            if(cycles.length != 0 && "".equals(cycles[i])){
                return "产品"+no+"交货周期未填写";
            }else{
                try{
                    int cycle = Integer.parseInt(cycles[i]);
                    answer.setCycle(cycle);
                    answer.setAgreeCycle(cycle);
                }catch (Exception e){
                    return "产品"+no+"交货周期填写错误(只能填写整数)";
                }
            }
            if(validityTimes.length!=0 && !"".equals(validityTimes[i])){
                try{
                    LocalDate validity = LocalDate.parse(validityTimes[i], DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                    answer.setValidity(validity);
                }catch (Exception e){
                    return "产品"+no+"报有效期填写错误(填写格式：yyyy.MM.dd)";
                }

            }else{
                answer.setValidity(inquiryList.getDeadline());
            }
            answer.setCreateDate(LocalDate.now());
            answerList.add(answer);
        }
        answerService.saveAnswer(answerList , inquiryID , supplierCode);

        return "OK";
    }


}
