package com.scm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    /**
     * 跳转至登录界面
     */
    @RequestMapping("/")
    public String load(){
        return "redirect:/foreLogin";
    }

    /**
     * 跳转至登录界面
     */
    @RequestMapping("/foreLogin")
    public String toLogin(){
        return "fore/login/login";
    }

    /**
     * 跳转至主页
     */
    @RequestMapping("/foreIndex")
    public String toLndex(){
        return "fore/main/index";
    }

    /**
     * 跳转至供应商查询界面
     */
    @RequestMapping("/foreAllSupplier")
    public String toAllSupplier(){
        return "fore/main/suppliers";
    }

    /**
     * 跳转至供应商详情界面
     */
    @RequestMapping("/forSupplierDetail")
    public String toSupplierDetail(){
        return "fore/main/supplierDetail";
    }

    /**
     * 跳转至询价单查询界面
     */
    @RequestMapping("/foreQueryInquiry")
    public String toQueryInquiry(){
        return "fore/main/queryInquiry";
    }

    /**
     * 跳转至单品查询界面
     */
    @RequestMapping("/foreInquiryOne")
    public String toInquiryOne(){
        return "fore/main/inquiryOne";
    }

    /**
     * 跳转至批量询价界面
     */
    @RequestMapping("/foreInquirySome")
    public String toInquirySome(){
        return "fore/main/inquirySome";
    }

    /**
     * 跳转至询价详情界面
     */
    @RequestMapping("/foreInquiryDetail")
    public String toInquiryDetail(){
        return "fore/main/inquiryDetail";
    }

    /**
     * 跳转至供应商询价界面
     */
    @RequestMapping("/foreAnswer")
    public String toAnswer(){
        return "fore/main/answer";
    }

    /**
     * 跳转至供应商报价详情界面
     */
    @RequestMapping("/foreSupplierInquiry")
    public String toSupplierInquiry(){
        return "fore/main/supplierInquiry";
    }

}
