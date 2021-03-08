package com.scm.controller.buy;

import com.scm.service.BuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuyUpdateController {

    @Autowired
    BuyService buyService;


    @RequestMapping(value = "closeBuyOrder" , method = RequestMethod.PUT)
    public String changeOrderStatus(String buyID){
        String result = buyService.closeOrder(buyID);
        return result;
    }

    /**
     * 修改采购订单
     */
    @RequestMapping(value = "changeBuyOrder" , method = RequestMethod.PUT)
    public String updateBuyOrder(String buyID , String[] taxRates , String[] agreePrices , String[] agreeDeadlines , String[] notes){
        return buyService.changeOrderInfo(buyID , taxRates , agreePrices , agreeDeadlines , notes);
    }
}
