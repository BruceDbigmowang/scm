package com.scm.controller.buy;

import com.scm.pojo.BuySum;
import com.scm.service.BuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BuySelectController {

    @Autowired
    BuyService buyService;

    /**
     * 分页  无需根据条件筛选
     * @param start
     * @return
     */
    @RequestMapping(value = "findBuySumByPage" , method = RequestMethod.GET)
    public Map<String , Object> getBuySumByPage(int start){
        return buyService.findAllBuySum(start);
    }

    /**
     * 根据条件筛选相关信息
     * @param search
     * @param project
     * @param buyType
     * @param orderStatus
     * @return
     */
    @RequestMapping(value = "findBuySumByCondition" , method = RequestMethod.GET)
    public Map<String , Object> getBuySumByCondition(String search , String project , String buyType , String orderStatus){
        return buyService.findBuySumByCondition(search , project , buyType , orderStatus);
    }


    /**
     * 点击查询详情，查询出来的信息包括：
     * 1、采购单总汇
     * 2、采购单详情
     */
    @RequestMapping(value = "getBuyOrderDetail" , method = RequestMethod.GET)
    public Map<String , Object> getBuyDetail(String buyID){
        return buyService.getBuyDetail(buyID);
    }

}
