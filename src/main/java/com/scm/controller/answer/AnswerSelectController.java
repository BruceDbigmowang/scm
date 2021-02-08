package com.scm.controller.answer;

import com.scm.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AnswerSelectController {
    @Autowired
    AnswerService answerService;

    /**
     * 供应商点击进入到报价界面
     *   1、加载询价单状态信息
     *   2、加载供应商详情
     *   3、加载询价单总汇信息
     *   4、加载询价单明细信息
     */
    @RequestMapping(value = "getInquiryDetail" , method = RequestMethod.GET)
    public Map<String , Object> loadDetail(String inquiryID , String supplierCode){
        return answerService.loadProduct(inquiryID , supplierCode);
    }

    /**
     * 加载进入到供应商报价详情界面中，需要加载
     *      1、供应商简略信息
     *      2、根据询价单 查询供应商报价明细
     */
    @RequestMapping(value = "getSupplierAnswer" , method = RequestMethod.GET)
    public Map<String , Object> findSupplierAnswer(String supplierCode , String inquiryID){
        return answerService.getAnswerDetail(supplierCode , inquiryID);
    }


}
