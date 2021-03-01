package com.scm.controller.buy;

import com.scm.pojo.*;
import com.scm.service.BuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BuyInsertController {

    @Autowired
    BuyService buyService;

    /**
     * 手动选择供应商 生成采购订单
     * @param inquiryID
     * @param suppliers
     * @param session
     * @return
     */
    @RequestMapping(value = "createBuyOrderByChoose" , method = RequestMethod.POST)
    public String saveBuyOrderByChoose(String inquiryID , String[] suppliers , HttpSession session){
        Account user = (Account)session.getAttribute("user");
        InquiryList inquiryList = buyService.findInquirySum(inquiryID);
        List<InquiryDetail> detailList = buyService.findInquiryDetail(inquiryID);
        if(suppliers.length != detailList.size()){
            return "勾选的供应商数量与数据库中的数据不一致";
        }
        List<Supplier> supplierList = buyService.getSupplierInfo(suppliers);
        BuySum buySum = new BuySum();
        String buyID = buyService.generateBuyID();
        buySum.setId(buyID);
        buySum.setInquiryID(inquiryList.getInquiryID());
        buySum.setBuyType(inquiryList.getInquiryType());
        buySum.setProject(inquiryList.getProject());
        buySum.setOperatorAccount(user.getAccount());
        buySum.setOperatorName(user.getName());
        buySum.setOrderStatus("O");
        buySum.setStatusDesc("正常");
        buySum.setCreateDate(LocalDate.now());

        BigDecimal totalMoney = BigDecimal.ZERO;
        List<BuyDetail> details = new ArrayList<>();
        for(int i = 0 ; i < detailList.size() ; i++){

            Supplier supplier = supplierList.get(i);
            BuyDetail buyDetail = new BuyDetail();
            InquiryDetail inquiryDetail = detailList.get(i);
            buyDetail.setBuyID(buyID);
            buyDetail.setKcode(inquiryDetail.getKcode());
            buyDetail.setSupplierCode(supplier.getSupplierCode());
            buyDetail.setSupplierName(supplier.getSimpleName());
            buyDetail.setProductName(inquiryDetail.getProductName());
            buyDetail.setSpecification(inquiryDetail.getSpecification());
            buyDetail.setQuantity(inquiryDetail.getQuantity());
            buyDetail.setBrandCode(inquiryDetail.getBrandCode());
            buyDetail.setBrand(inquiryDetail.getBrand());
            buyDetail.setUnit(inquiryDetail.getUnit());

            /**
             * 根据 inquiryID did supplierCode
             * 找到供应商的报价
             */
            InquiryAnswer inquiryAnswer = buyService.findSupplierAnswer(inquiryID , inquiryDetail.getId() , supplier.getSupplierCode());
            if (inquiryAnswer == null){
                return "未找到供应商报价";
            }
            buyDetail.setTaxRate(inquiryAnswer.getTaxRate());
            buyDetail.setPrice(inquiryAnswer.getPrice());
            buyDetail.setAgreePrice(inquiryAnswer.getAgreePrice());
            buyDetail.setTotal(inquiryAnswer.getAgreeTotal());
            totalMoney = totalMoney.add(inquiryAnswer.getAgreeTotal());
            buyDetail.setDeadline(LocalDate.now().plusDays(inquiryAnswer.getCycle()));
            buyDetail.setAgreeDeadline(LocalDate.now().plusDays(inquiryAnswer.getAgreeCycle()));
            buyDetail.setCreateDate(LocalDate.now());

            details.add(buyDetail);
        }
        buySum.setBuyMoney(totalMoney);
        String result = buyService.createBuyOrder(buySum , details);
        return result;
    }

    /**
     * 根据分析生成的方案
     *
     * 选择某一方案从而生成采购订单
     * 方案类型：
     * 1：成本最低
     * 2：到货最快
     * 3：最优方案
     */
    @RequestMapping(value = "chooseSolution" , method = RequestMethod.POST)
    public String chooseOneSolution(String inquiryID , String type , HttpSession session){
        Account user = (Account)session.getAttribute("user");
        boolean hasSolution = buyService.hasSolution(inquiryID);
        if(hasSolution == false){
            return "没有供应商给出报价";
        }
        InquiryList inquiryList = buyService.findInquirySum(inquiryID);
        BuySum buySum = new BuySum();
        String buyID = buyService.generateBuyID();
        buySum.setId(buyID);
        buySum.setInquiryID(inquiryList.getInquiryID());
        buySum.setBuyType(inquiryList.getInquiryType());
        buySum.setProject(inquiryList.getProject());
        buySum.setOperatorAccount(user.getAccount());
        buySum.setOperatorName(user.getName());
        buySum.setOrderStatus("O");
        buySum.setStatusDesc("正常");
        buySum.setCreateDate(LocalDate.now());

        BigDecimal totalMoney = BigDecimal.ZERO;
        List<BuyDetail> buyDetailList = new ArrayList<>();
        if(type.equals("1")){

            List<InquiryAnswer> answerList = buyService.getCheapestSolution(inquiryID);
            for (int i = 0 ; i < answerList.size() ; i++){
                InquiryAnswer answer = answerList.get(i);
                BuyDetail buyDetail = new BuyDetail();
                buyDetail.setBuyID(buyID);
                buyDetail.setKcode(answer.getKcode());
                buyDetail.setSupplierCode(answer.getSupplierCode());
                buyDetail.setSupplierName(answer.getSupplierName());
                buyDetail.setProductName(answer.getProductName());
                buyDetail.setSpecification(answer.getSpecification());
                buyDetail.setBrand(answer.getReceiveBrand());
                buyDetail.setQuantity(answer.getQuantity());
                buyDetail.setUnit(answer.getUnit());
                buyDetail.setTaxRate(answer.getTaxRate());
                buyDetail.setPrice(answer.getPrice());
                buyDetail.setAgreePrice(answer.getAgreePrice());
                buyDetail.setTotal(answer.getAgreeTotal());
                totalMoney = totalMoney.add(answer.getAgreeTotal());
                buyDetail.setDeadline(LocalDate.now().plusDays(answer.getCycle()));
                buyDetail.setAgreeDeadline(LocalDate.now().plusDays(answer.getAgreeCycle()));
                buyDetail.setCreateDate(LocalDate.now());
                buyDetailList.add(buyDetail);
            }
            buySum.setBuyMoney(totalMoney);

            String ressult = buyService.createBuyOrder(buySum , buyDetailList);

            return ressult;
        }else if(type.equals("2")){

            List<InquiryAnswer> answerList = buyService.getFastestSolution(inquiryID);
            for (int i = 0 ; i < answerList.size() ; i++){
                InquiryAnswer answer = answerList.get(i);
                BuyDetail buyDetail = new BuyDetail();
                buyDetail.setBuyID(buyID);
                buyDetail.setKcode(answer.getKcode());
                buyDetail.setSupplierCode(answer.getSupplierCode());
                buyDetail.setSupplierName(answer.getSupplierName());
                buyDetail.setProductName(answer.getProductName());
                buyDetail.setBrand(answer.getReceiveBrand());
                buyDetail.setQuantity(answer.getQuantity());
                buyDetail.setSpecification(answer.getSpecification());
                buyDetail.setUnit(answer.getUnit());
                buyDetail.setTaxRate(answer.getTaxRate());
                buyDetail.setPrice(answer.getPrice());
                buyDetail.setAgreePrice(answer.getAgreePrice());
                buyDetail.setTotal(answer.getAgreeTotal());
                totalMoney = totalMoney.add(answer.getAgreeTotal());
                buyDetail.setDeadline(LocalDate.now().plusDays(answer.getCycle()));
                buyDetail.setAgreeDeadline(LocalDate.now().plusDays(answer.getAgreeCycle()));
                buyDetail.setCreateDate(LocalDate.now());
                buyDetailList.add(buyDetail);
            }
            buySum.setBuyMoney(totalMoney);

            String ressult = buyService.createBuyOrder(buySum , buyDetailList);

            return ressult;

        }else if (type.equals("3")){
            List<InquiryAnswer> answerList = buyService.getBestSolution(inquiryID);
            for (int i = 0 ; i < answerList.size() ; i++){
                InquiryAnswer answer = answerList.get(i);
                BuyDetail buyDetail = new BuyDetail();
                buyDetail.setBuyID(buyID);
                buyDetail.setKcode(answer.getKcode());
                buyDetail.setSupplierCode(answer.getSupplierCode());
                buyDetail.setSupplierName(answer.getSupplierName());
                buyDetail.setProductName(answer.getProductName());
                buyDetail.setSpecification(answer.getSpecification());
                buyDetail.setBrand(answer.getReceiveBrand());
                buyDetail.setQuantity(answer.getQuantity());
                buyDetail.setUnit(answer.getUnit());
                buyDetail.setTaxRate(answer.getTaxRate());
                buyDetail.setPrice(answer.getPrice());
                buyDetail.setAgreePrice(answer.getAgreePrice());
                buyDetail.setTotal(answer.getAgreeTotal());
                totalMoney = totalMoney.add(answer.getAgreeTotal());
                buyDetail.setDeadline(LocalDate.now().plusDays(answer.getCycle()));
                buyDetail.setAgreeDeadline(LocalDate.now().plusDays(answer.getAgreeCycle()));
                buyDetail.setCreateDate(LocalDate.now());
                buyDetailList.add(buyDetail);
            }
            buySum.setBuyMoney(totalMoney);

            String ressult = buyService.createBuyOrder(buySum , buyDetailList);

            return ressult;
        }else{
            return "方案选择出错";
        }
    }

}
