package com.scm.controller.analysis;

import com.scm.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AnalysisSelectController {
    @Autowired
    InquiryService inquiryService;

    /**
     * 点击查看详情后
     * 加载询价单相关信息：
     * 询价单总汇
     * 询价单详情 包括哪些物料
     * 供应商报价状态
     */
    @RequestMapping(value = "findInquiryDetailForAnalysis" , method = RequestMethod.GET)
    public Map<String , Object> getInquiryDetailforanaly(String inquiryID){
        return inquiryService.getInquiryDetailForAnalysis(inquiryID);
    }

    @RequestMapping(value = "findInquiryDetailForAnalysisPriceDesc" , method = RequestMethod.GET)
    public Map<String , Object> getInquiryDetailforanalyPriceDesc(String inquiryID){
        return inquiryService.getInquiryDetailForAnalysisPriceDesc(inquiryID);
    }

    @RequestMapping(value = "findInquiryDetailForAnalysisPriceAsc" , method = RequestMethod.GET)
    public Map<String , Object> getInquiryDetailforanalyPriceAsc(String inquiryID){
        return inquiryService.getInquiryDetailForAnalysisPriceAsc(inquiryID);
    }

    @RequestMapping(value = "findInquiryDetailForAnalysisCycleDesc" , method = RequestMethod.GET)
    public Map<String , Object> getInquiryDetailforanalyCycleDesc(String inquiryID){
        return inquiryService.getInquiryDetailForAnalysisCycleDesc(inquiryID);
    }

    @RequestMapping(value = "findInquiryDetailForAnalysisCycleAsc" , method = RequestMethod.GET)
    public Map<String , Object> getInquiryDetailforanalyCycleAsc(String inquiryID){
        return inquiryService.getInquiryDetailForAnalysisCycleAsc(inquiryID);
    }

    /**
     * 一键分析 生成三种方案
     * 1、成本最低
     * 2、到货最快
     * 3、最优（全部在一家供应商处采购  成本最低）
     */
    @RequestMapping(value = "findSolution" , method = RequestMethod.GET)
    public Map<String , Object> getSolution(String inquiryID){
        return inquiryService.generateSolution(inquiryID);
    }
}
