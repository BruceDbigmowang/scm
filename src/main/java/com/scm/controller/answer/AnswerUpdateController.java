package com.scm.controller.answer;

import com.scm.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class AnswerUpdateController {

    @Autowired
    AnswerService answerService;

    @RequestMapping(value = "updatePrice" , method = RequestMethod.PUT)
    public String saveChanges(String[] aIds , String[] agreePrices , String[] agreeCycles , String[] taxRates){
        for(int i = 0 ; i < aIds.length ; i++){
            int no = i + 1;
            if(!"".equals(agreePrices[i])){
                try{
                    BigDecimal agreePrice = new BigDecimal(agreePrices[i]);
                }catch (Exception e){
                    return "第"+no+"行商定单价填写错误(只能填写数字)";
                }
                try{
                    int agreeCycle = Integer.parseInt(agreeCycles[i]);
                }catch (Exception e){
                    return "第"+no+"行商定交货周期填写错误(只能填写整数)";
                }
                try{
                    BigDecimal taxRate = new BigDecimal(taxRates[i]);
                }catch (Exception e){
                    return "第"+no+"行税率填写错误(只能填写数字)";
                }
            }
        }
        String result = answerService.saveAnswerChange(aIds , agreePrices , agreeCycles , taxRates);
        return result;
    }
}
