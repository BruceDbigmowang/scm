package com.scm.controller.inquery;

import com.scm.pojo.Supplier;
import com.scm.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class InquerySelectController {
    @Autowired
    InquiryService inquiryService;

    /**
     * 加载进入询价界面（单品询价、批量询价）
     * 加载所有的供应商
     */
    @RequestMapping(value = "findAllSupplier" , method = RequestMethod.GET)
    public List<Supplier> getAll(){
        return inquiryService.getAllSupplier();
    }

    /**
     * 刚载入  查询所有的询价单
     */
    @RequestMapping(value = "findAllInquiry" , method = RequestMethod.GET)
    public Map<String , Object> getAllInquiry(int start){
        return inquiryService.findAll(start);
    }

    /**
     * 根据条件 查询对应的询价单
     */
    @RequestMapping(value = "findSomeInquiry" , method = RequestMethod.GET)
    public Map<String , Object> getSomeInquiry(int start , String project , String type , String status , String begin , String end){
        return inquiryService.findSomeInquery(start , project , type , status , begin , end);
    }

    /**
     * 点击查看详情后
     * 加载询价单相关信息：
     * 询价单总汇
     * 询价单详情 包括哪些物料
     * 供应商报价状态
     */
    @RequestMapping(value = "findInquiryDetail" , method = RequestMethod.GET)
    public Map<String , Object> getInquiryDetail(String inquiryID){
        return inquiryService.getInquiryDetail(inquiryID);
    }

    /**
     * 输入产品名称后  查看所有的规格型号
     *
     * 根据产品名称以及规格型号来确定具体某个物料
     */
    @RequestMapping(value = "findAllSpecification" , method = RequestMethod.GET)
    public List<String> getAllSpecification(String productName){
        return inquiryService.findMaterialByName(productName);
    }

    /**
     * 发送短信
     * 取所有供应商的商务对接人，
     * 根据supplierCode \ inquiryID 来组合不同的地址发送给对应的人
     */
}
