package com.scm.controller.inquery;

import com.scm.pojo.InquiryList;
import com.scm.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InquiryUpdateController {
    @Autowired
    InquiryService inquiryService;

    /**
     * 关闭询价单，将状态设置为无效
     */
    @RequestMapping(value = "closeInquiry" , method = RequestMethod.PUT)
    public String updateInquiry(String inquiryID){
        InquiryList inquiryList = inquiryService.findInquiryById(inquiryID);
        inquiryList.setStatus("C");
        inquiryList.setStatusDes("无效");
        String result = inquiryService.updateInquiry(inquiryList);
        return result;
    }
}
