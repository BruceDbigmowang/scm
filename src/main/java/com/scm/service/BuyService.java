package com.scm.service;

import com.scm.dao.*;
import com.scm.entity.BuySumDTO;
import com.scm.pojo.*;
import com.scm.utils.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BuyService {

    @Autowired
    BuySumDAO buySumDAO;
    @Autowired
    BuyDetailDAO buyDetailDAO;
    @Autowired
    InquiryDetailDAO detailDAO;
    @Autowired
    InquiryListDAO inquiryListDAO;
    @Autowired
    SupplierDAO supplierDAO;
    @Autowired
    InquiryAnswerDAO answerDAO;
    @Autowired
    InquirySupplierDAO isDAO;

    /**
     * 判断是否有供应商给出报价
     */
    public boolean hasSolution(String inquiryID){
        List<InquiryAnswer> ias = answerDAO.findByInquiryID(inquiryID);
        if(ias.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 将生成的采购单数据保存
     */
    @Transactional
    public String createBuyOrder(BuySum sum , List<BuyDetail> details){
        try{
            buySumDAO.save(sum);
            for(BuyDetail detail : details){
                buyDetailDAO.save(detail);
            }
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";
    }

    /**
     * 自动生成采购单号
     */
    public String generateBuyID(){
        List<BuySum> inquiryLists = buySumDAO.findByCreateDate(LocalDate.now());
        String num = String.valueOf(inquiryLists.size() + 1);

        String number = "";
        for(int i = 0 ; i < 2 - num.length() ; i++){
            number = number + "0";
        }
        number = "NO.CGDD-"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))+number + num;

        return number;
    }

    /**
     * 获取供应商信息
     * @param suppliers
     * @return
     */
    public List<Supplier> getSupplierInfo(String[] suppliers){
        List<Supplier> supplierList = new ArrayList<>();
        for(int i = 0 ; i < suppliers.length ; i++){
            Supplier supplier = supplierDAO.findById(suppliers[i].trim()).get();
            supplierList.add(supplier);
        }
        return supplierList;
    }

    /**
     * 根据询价单单号  查询出所有的询价单总汇
     */
    public InquiryList findInquirySum(String inquiryID){
        return inquiryListDAO.findById(inquiryID).get();
    }

    /**
     * 根据询价单单号  查询出所有的询价单明细
     */
    public List<InquiryDetail> findInquiryDetail(String inquiryID){
        return detailDAO.findByInquiryID(inquiryID);
    }

    /**
     * 根据inquiryID did supplierCode  查询供应商给出的报价
     */
    public InquiryAnswer findSupplierAnswer(String inquiryID , int did , String supplierCode){
        List<InquiryAnswer> answerList = answerDAO.findByInquiryIDAndDidAndSupplierCode(inquiryID , did , supplierCode);
        if(answerList.isEmpty()){
            return null;
        }else{
            return answerList.get(0);
        }
    }

    /**
     * 查询成本最低的方案
     */
    public List<InquiryAnswer> getCheapestSolution(String inquiryID){
        List<InquiryDetail> detailList = detailDAO.findByInquiryID(inquiryID);
        List<InquiryAnswer> answerList1 = new ArrayList<>();
        for(int i = 0 ; i < detailList.size() ; i++){
            InquiryDetail detail = detailList.get(i);
            List<InquiryAnswer> answerList = answerDAO.findByInquiryIDAndDidOrderByAgreePriceAsc(inquiryID , detail.getId());
            List<InquiryAnswer> answers = new ArrayList<>();
            BigDecimal lowestPrice = BigDecimal.ZERO;
            for(int j = 0 ; j < answerList.size() ; j++){
                InquiryAnswer answer = answerList.get(j);
                if(j == 0){
                    answers.add(answer);
                    lowestPrice = answer.getAgreePrice();
                }else{
                    if(lowestPrice.equals(answer.getAgreePrice())){
                        answers.add(answer);
                    }
                }
            }
            //若存在给出最低价格的供应商有多个  则随机取一个
            Random random = new Random();
            int n = random.nextInt(answers.size());
            answerList1.add(answers.get(n));
        }
        return answerList1;
    }

    /**
     * 查询到货最快的方案
     */
    public List<InquiryAnswer> getFastestSolution(String inquiryID){
        List<InquiryDetail> detailList = detailDAO.findByInquiryID(inquiryID);
        List<InquiryAnswer> answerList2 = new ArrayList<>();
        for(int i = 0 ; i < detailList.size() ; i++){
            InquiryDetail detail = detailList.get(i);
            List<InquiryAnswer> answerList = answerDAO.findByInquiryIDAndDidOrderByAgreeCycleAsc(inquiryID , detail.getId());
            List<InquiryAnswer> answers = new ArrayList<>();
            int fastest = 0;
            for(int j = 0 ; j < answerList.size() ; j++){
                InquiryAnswer answer = answerList.get(j);
                if(j == 0){
                    answers.add(answer);
                    fastest = answer.getAgreeCycle();
                }else{
                    if(fastest == answer.getAgreeCycle()){
                        answers.add(answer);
                    }
                }
            }
            //若存在给出最低价格的供应商有多个  则随机取一个
            Random random = new Random();
            int n = random.nextInt(answers.size());
            answerList2.add(answers.get(n));
        }
        return answerList2;
    }

    /**
     * 查询最优方案
     */
    public List<InquiryAnswer> getBestSolution(String inquiryID){
        //最优方案
        List<InquiryAnswer> answerList3 = new ArrayList<>();
        List<InquirySupplier> isList = isDAO.findByIdAndAndStatus(inquiryID , "C");
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(int i = 0 ; i < isList.size() ; i++) {
            String supplierCode = isList.get(i).getSupplierCode();
            List<InquiryAnswer> answerList = answerDAO.findByInquiryIDAndSupplierCode(inquiryID, supplierCode);
            if (i == 0) {
                for (int j = 0; j < answerList.size(); j++) {
                    totalPrice = totalPrice.add(answerList.get(j).getAgreeTotal());
                }
                answerList3.clear();
                answerList3 = answerList;
            } else {
                BigDecimal newTotal = BigDecimal.ZERO;
                for (int j = 0; j < answerList.size(); j++) {
                    newTotal = newTotal.add(answerList.get(j).getAgreeTotal());
                }
                if (totalPrice.compareTo(newTotal) != -1) {
                    totalPrice = newTotal;
                    answerList3.clear();
                    answerList3 = answerList;
                }
            }
        }
        return answerList3;
    }

    /**
     * 根据条件来查询相关数据
     * 条件：
     * 1、询价单号/采购单号/经办人
     * 2、项目
     * 3、采购类型
     * 4、订单状态
     *
     * Or 与 And 联合使用
     */
    public Map<String , Object> findBuySumByCondition(String id , String project , String buyType , String orderStatus){
        Map<String , Object> map = new HashMap<>();
        BuySumDTO buySumDTO = new BuySumDTO();
        buySumDTO.setId(id);
        buySumDTO.setInquiryID(id);
        buySumDTO.setOperatorAccount(id);
        buySumDTO.setOperatorName(id);
        buySumDTO.setProject(project);
        buySumDTO.setBuyType(buyType);
        buySumDTO.setOrderStatus(orderStatus);

        Specification<BuySum> querySpec = QueryUtil.getBuySumSpec(buySumDTO);
        List<BuySum> sumList = buySumDAO.findAll(querySpec);
        map.put("sumList" , sumList);
        BigDecimal total = BigDecimal.ZERO;
        for(BuySum buySum : sumList){
            BigDecimal totalPrice = buySum.getBuyMoney();
            total = total.add(totalPrice);
        }
        String result = "当前条件下：共"+sumList.size()+"个采购订单，总采购金额（含税）"+total+"元";
        map.put("result" , result);
        return map;
    }

    /**
     * 分页查询所有的采购单
     * @param start
     * @return
     */
    public Map<String , Object> findAllBuySum(int start){
        Map<String , Object> map = new HashMap<>();
        PageRequest pr = PageRequest.of(start, 10);
        List<BuySum> sumList = buySumDAO.findAllByOrderByCreateDateDesc(pr);
        map.put("sumList" , sumList);
        List<BuySum> buySumList = buySumDAO.findAll();
        int pages = 0;
        if(buySumList.size() % 10 == 0){
            pages = buySumList.size() / 10;
        }else{
            pages = buySumList.size() / 10 + 1;
        }
        map.put("pages" , pages);
        return map;
    }

    /**
     * 点击查询详情
     * 根据 id 查询采购单总汇以及采购单详情信息
     */
    public Map<String , Object> getBuyDetail(String buyId){
        Map<String , Object> map = new HashMap<>();
        BuySum buySum = buySumDAO.findById(buyId).get();
        map.put("sum" , buySum);
        List<BuyDetail> detailList = buyDetailDAO.findByBuyID(buyId);
        map.put("details" , detailList);
        return map;
    }


    /**
     * 关闭采购单
     * 即  修改状态，状态描述
     */
    @Transactional
    public String closeOrder(String buyId){
        BuySum buySum = buySumDAO.findById(buyId).get();
        buySum.setOrderStatus("C");
        buySum.setStatusDesc("禁用");
        try{
            buySumDAO.save(buySum);
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";
    }

    /**
     * 修改采购单信息
     * 1、税率
     * 2、商定单价
     * 3、商定交货期
     * 4、备注
     */
    @Transactional
    public String changeOrderInfo(String buyID , String[] taxRates , String[] agreePrices , String[] agreeDeadline , String[] notes){
        List<BuyDetail> buyDetailList = buyDetailDAO.findByBuyID(buyID);
        if(buyDetailList.size() != taxRates.length){
            return "保存数据与数据库中不一致,请刷新界面后再操作";
        }else{
            //检查并将所有更新后的数据放入detailList中
            List<BuyDetail> detailList = new ArrayList<>();
            for(int i = 0 ; i < buyDetailList.size() ; i++){
                int target = i + 1;
                BuyDetail buyDetail = buyDetailList.get(i);
                if(!"".equals(taxRates[i])){
                    try{
                        BigDecimal taxRate = new BigDecimal(taxRates[i]).divide(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP);
                        buyDetail.setTaxRate(taxRate);
                    }catch (Exception e){
                        return "第"+target+"行税率填写错误（只能填写数字）";
                    }
                }
                if(!"".equals(agreePrices[i])){
                    try{
                        BigDecimal agreePrice = new BigDecimal(agreePrices[i]).setScale(2,BigDecimal.ROUND_HALF_UP);
                        buyDetail.setAgreePrice(agreePrice);
                        int quantity = buyDetail.getQuantity();
                        BigDecimal total = agreePrice.multiply(new BigDecimal(quantity)).setScale(2,BigDecimal.ROUND_HALF_UP);
                        buyDetail.setTotal(total);
                    }catch (Exception e){
                        return "第"+target+"行商定单价填写错误（只能填写数字）";
                    }
                }
                if(!"".equals(agreeDeadline[i])){
                    try {
                        LocalDate deadline = LocalDate.parse(agreeDeadline[i], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        buyDetail.setAgreeDeadline(deadline);
                    }catch (Exception e){
                        return "第"+target+"行商定交货日期填写错误（格式为“yyyy-MM-dd”）";
                    }
                }
                if(!"".equals(notes[i])){
                    buyDetail.setNote(notes[i]);
                }
                detailList.add(buyDetail);
            }

            //保存所有更新后的数据
            for(int i = 0 ; i < detailList.size() ; i++){
                int target = i + 1;
                BuyDetail detail = detailList.get(i);
                try{
                    buyDetailDAO.save(detail);
                }catch (Exception e){
                    return "第"+target+"行:"+e.getMessage();
                }
            }
            return "OK";
        }
    }
}
