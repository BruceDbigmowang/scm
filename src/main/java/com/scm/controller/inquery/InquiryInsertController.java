package com.scm.controller.inquery;

import com.scm.pojo.Account;
import com.scm.pojo.InquiryDetail;
import com.scm.pojo.InquiryList;
import com.scm.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class InquiryInsertController {
    @Autowired
    InquiryService inquiryService;

    @RequestMapping(value = "writeOneInquiry" , method = RequestMethod.POST)
    public String saveOneInquiry(String project , String type , String productName , String specification , String brand , String brandCode , String quantity , String unit , String deadline , String note , String picName , String suppliers , HttpSession session) throws SQLException {

        //检查是否有输入以及输入是否正确
        if("".equals(type)){
            return "请选择类型";
        }
        if("".equals(productName)){
            return "请填写产品名称";
        }
        if("".equals(specification)){
            return "请填写规格型号";
        }
        if("".equals(quantity)){
            return "请填写需求数量";
        }else{
            try{
                int num = Integer.parseInt(quantity);
            }catch (Exception e){
                return "需求数量填写错误";
            }
        }
        if("".equals(unit)){
            return "请填写数量单位";
        }
        if("".equals(deadline)){
            return "请选择截止日期";
        }else{
            try{
                LocalDate target = LocalDate.parse(deadline, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }catch (Exception e){
                return "日期格式有误";
            }
        }
        if("".equals(suppliers)){
            return "请选择供应商";
        }
        //询价单总汇
        String inquiryID = inquiryService.generateInquiryID();
        InquiryList inquiryList = new InquiryList();
        inquiryList.setInquiryID(inquiryID);
        inquiryList.setProject(project);
        inquiryList.setInquiryType(type);
        inquiryList.setInquiryDate(LocalDate.now());
        inquiryList.setDeadline(LocalDate.parse(deadline, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        inquiryList.setStatus("O");
        inquiryList.setStatusDes("有效");
        Account user = (Account)session.getAttribute("user");
        inquiryList.setInquiryAccount(user.getAccount());
        inquiryList.setInquiryName(user.getName());
        inquiryList.setCreater(user.getAccount());
        inquiryList.setCreaterName(user.getName());
        inquiryList.setCreateDate(LocalDate.now());

        //询价单明细
        InquiryDetail inquiryDetail = new InquiryDetail();
        inquiryDetail.setInquiryID(inquiryID);
        inquiryDetail.setProject(project);
        inquiryDetail.setType(type);
        inquiryDetail.setProductName(productName);
        inquiryDetail.setSpecification(specification);
        String kcode = inquiryService.getKcode(productName , specification);
        inquiryDetail.setKcode(kcode);
        if(!"".equals(brand)){
            inquiryDetail.setBrand(brand);
        }
        if(!"".equals(brandCode)){
            inquiryDetail.setBrandCode(brandCode);
        }
        inquiryDetail.setQuantity(Integer.parseInt(quantity));
        inquiryDetail.setUnit(unit);
        inquiryDetail.setDeadline(LocalDate.parse(deadline, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        if(!"".equals(note)){
            inquiryDetail.setNote(note);
        }
        if(!"".equals(picName)){
            inquiryDetail.setPicName(picName);
        }
        inquiryDetail.setCreater(user.getAccount());
        inquiryDetail.setCreaterName(user.getName());
        inquiryDetail.setCreateDate(LocalDate.now());
        List<InquiryDetail> inquiryDetailList = new ArrayList<>();
        inquiryDetailList.add(inquiryDetail);

        //询价的供应商
        String[] supplyArr = suppliers.split(",");
        List<String> supplierList = Arrays.asList(supplyArr);

        String result = inquiryService.createInquiry(inquiryList , inquiryDetailList , supplierList);
        String result2 = inquiryService.sendMessage(inquiryID , supplierList);
        return result2;

    }

    @RequestMapping(value = "writeSomeInquiry" , method = RequestMethod.POST)
    public String saveSomeInquiry(HttpSession session , HttpServletRequest request , HttpServletResponse response) throws Exception {
        String project = request.getParameter("project");
        String type = request.getParameter("type");
        String deadline = request.getParameter("deadline");
        String suppliers = request.getParameter("suppliers");
        String picArr = request.getParameter("picNames");
        String[] picNames = picArr.split(",");
        //询价单总汇
        String inquiryID = inquiryService.generateInquiryID();
        InquiryList inquiryList = new InquiryList();
        inquiryList.setInquiryID(inquiryID);
        inquiryList.setProject(project);
        inquiryList.setInquiryType(type);
        inquiryList.setInquiryDate(LocalDate.now());
        inquiryList.setDeadline(LocalDate.parse(deadline, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        inquiryList.setStatus("O");
        inquiryList.setStatusDes("有效");
        Account user = (Account)session.getAttribute("user");
        inquiryList.setInquiryAccount(user.getAccount());
        inquiryList.setInquiryName(user.getName());
        inquiryList.setCreater(user.getAccount());
        inquiryList.setCreaterName(user.getName());
        inquiryList.setCreateDate(LocalDate.now());

        //询价单详情
        List<List<String>> materialList = inquiryService.getInquiryEnter(request , response);
        List<InquiryDetail> detailList = new ArrayList<>();
        for(int i = 0 ; i < materialList.size() ; i++){
            List<String> material = materialList.get(i);
            String productName = material.get(0);
            String specification = material.get(1);
            String kcode = material.get(2);
            String brand = material.get(3);
            String brandCode = material.get(4);
            String quantity = material.get(5);
            String unit = material.get(6);
            String note = material.get(7);

            InquiryDetail inquiryDetail = new InquiryDetail();
            inquiryDetail.setInquiryID(inquiryID);
            inquiryDetail.setProject(project);
            inquiryDetail.setType(type);
            inquiryDetail.setProductName(productName);
            inquiryDetail.setSpecification(specification);
            inquiryDetail.setKcode(kcode);
            if(!"".equals(brand)){
                inquiryDetail.setBrand(brand);
            }
            if(!"".equals(brandCode)){
                inquiryDetail.setBrandCode(brandCode);
            }
            inquiryDetail.setQuantity(Integer.parseInt(quantity));
            inquiryDetail.setUnit(unit);
            inquiryDetail.setDeadline(LocalDate.parse(deadline, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if(!"".equals(note)){
                inquiryDetail.setNote(note);
            }

            inquiryDetail.setCreater(user.getAccount());
            inquiryDetail.setCreaterName(user.getName());
            if(picNames.length > i && !"".equals(picNames[i])){
                inquiryDetail.setPicName(picNames[i]);
            }
            inquiryDetail.setCreateDate(LocalDate.now());
            detailList.add(inquiryDetail);
        }

        //供应商
        String[] supplierArr = suppliers.split(",");
        List<String> supplierList = Arrays.asList(supplierArr);

        String result = inquiryService.createInquiry(inquiryList , detailList , supplierList);
        String result2 = inquiryService.sendMessage(inquiryID , supplierList);
        return result2;
    }

    @RequestMapping(value="getExcelContent" , method = RequestMethod.POST)
    public Map<String , Object> getContent(HttpServletRequest request , HttpServletResponse response)throws Exception{
        Map<String , Object> map = new HashMap<>();
        String chechResult = inquiryService.checkInquiryEnter(request , response);
        map.put("result" , chechResult);
        if(chechResult.equals("OK")){
            List<List<String>> materialList = inquiryService.getInquiryEnter(request , response);
            map.put("materialList" , materialList);
        }
        return map;
    }

    /**
     * 创建询价单之后 系统会向供应商的联系人发送询价单网址，供应商点击进入来进行报价
     *
     * 若供应商长时间未报价  用户可向供应商再次发送消息来提醒供应商进行报价
     */
    @RequestMapping(value = "remindSupplier" , method = RequestMethod.POST)
    public String toRemindSupplier(String inquiryID , String[] suppliers){
        System.out.println(inquiryID);
        System.out.println(suppliers[0]);
        List<String> supplierList = Arrays.asList(suppliers);
        return inquiryService.sendMessage(inquiryID , supplierList);
//        return "OK";
    }
}
