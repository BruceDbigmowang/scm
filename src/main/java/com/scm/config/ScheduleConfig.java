package com.scm.config;

import com.scm.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleConfig {
    @Autowired
    InquiryService inquiryService;

    /**
     * 每天早晨6点钟  定时执行
     * 查询所有截止日期为当前时间  状态为“O” 的询价单
     * 将询价单状态调整为“B(已过期)”
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void setBeyond(){
        inquiryService.closeInquiry();
    }
}
