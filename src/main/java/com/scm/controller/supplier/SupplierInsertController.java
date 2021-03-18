package com.scm.controller.supplier;

import com.scm.pojo.Account;
import com.scm.pojo.Material;
import com.scm.pojo.Supplier;
import com.scm.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

@RestController
public class SupplierInsertController {

    @Autowired
    SupplierService supplierService;

    /**
     * 创建新的供应商
     *
     * 只保存供应商基本信息
     */
    @RequestMapping(value = "createSupplier" , method = RequestMethod.POST)
    public String saveSupplier(HttpSession session , String supplierName , String simpleName , String supplierNature , String supplyType , String relationship , String area , String website , String postAddress , String addressDetail){
        Supplier supplier = new Supplier();
        String supplierCode = supplierService.generateSupplierCode();
        supplier.setSupplierCode(supplierCode);
        if("".equals(supplierName)){
            return "请填写供应商名称";
        }else{
            supplier.setSupplierName(supplierName);
        }
        if("".equals(simpleName)){
            return "请填写供应商简称";
        }else{
            supplier.setSimpleName(simpleName);
        }
        if("".equals(supplierNature)){
            return "请填写供应商性质";
        }else{
            supplier.setSupplierNature(supplierNature);
        }
        if("".equals(supplyType)){
            return "请填写供应类别";
        }else{
            supplier.setSupplyType(supplyType);
        }
        if(!"".equals(relationship)){
            supplier.setRelationship(relationship);
        }
        if("".equals(area)){
            return "请填写所在地区";
        }else{
            supplier.setArea(area);
        }
        if("".equals(website)){
            return "请填写公司网址";
        }else{
            supplier.setWebsite(website);
        }
        if("".equals(postAddress)){
            return "请填写通讯地址";
        }else {
            supplier.setContactAddress(postAddress);
        }
        if("".equals(addressDetail)){
            return "请填写详细地址";
        }else{
            supplier.setAddressDetail(addressDetail);
        }
        supplier.setCreateDate(LocalDate.now());
        Account user = (Account)session.getAttribute("user");
        supplier.setCreater(user.getAccount());
        supplier.setCreaterName(user.getName());
        String result = supplierService.saveSupplier(supplier);
        return result;
    }

    /**
     * 创建新的联系人
     */
    @RequestMapping(value = "createContact" , method = RequestMethod.POST)
    public String saveContact(String supplierCode , String contactName , String contactWay , String contactType , String dept , String position , String email , String grade , String status) throws SQLException {
        return supplierService.saveContact(supplierCode , contactName , contactWay , contactType , dept , position , email , grade , status);
    }

    /**
     * 保存认证信息
     */
    @RequestMapping(value = "saveAuthenInfo" , method = RequestMethod.POST)
    public String saveAuthencation(String supplierCode , String establishDate , String legalPerson , String registerMoney , String creditCode , String registerAddress , String picName) throws SQLException {

        BigDecimal money = null;
        try{
            money = new BigDecimal(registerMoney);
        }catch (Exception e){
            return "注册资金填写错误（只能填写数字）";
        }
        String suffix = picName.substring(picName.lastIndexOf("."));
        picName = supplierCode+suffix;
        return supplierService.saveAuthentication(supplierCode , establishDate , legalPerson , money , creditCode , registerAddress , picName);
    }

    /**
     * 创建新的标签
     */
    @RequestMapping(value = "createLabel" , method = RequestMethod.POST)
    public String saveLabel(String supplierCode , String labelName) throws SQLException {
        return supplierService.createLabel(supplierCode , labelName);
    }

    /**
     * 批量导入 物料信息
     * 用Excel文件导入，模板见附件
     */
    @RequestMapping(value = "uploadMaterialInfo" , method = RequestMethod.POST)
    public String uploadMaterial(HttpServletRequest request, HttpServletResponse response) throws SQLException {
        String errorInfo = supplierService.checkMaterialrEnter(request , response);
        if(errorInfo.equals("OK")){
            String result = supplierService.uploadMaterial(request , response);
            return result;
        }else{
            return errorInfo;
        }
    }

    /**
     * 新增物料信息
     */
    @RequestMapping(value = "createMaterial" , method = RequestMethod.POST)
    public String saveMaterial(String supplierCode , String kcode , String productName , String specification , BigDecimal price , BigDecimal recentPrice , int cycle , int recentCycle){
        Material material = new Material();
        material.setSupplierCode(supplierCode);
        material.setKcode(kcode);
        material.setMaterialName(productName);
        material.setSpecification(specification);
        material.setRecentPrice(recentPrice);
        material.setStandardPrice(price);
        material.setStandardDeadline(cycle);
        material.setRecentDeadline(recentCycle);
        return supplierService.saveMaterial(material);
    }

    /**
     * 批量导入供应商信息
     * 用Excel文件导入，模板见附件
     */
    @RequestMapping(value = "uploadSupplierInfo" , method = RequestMethod.POST)
    public String uploadSupplier(HttpServletRequest request, HttpServletResponse response , HttpSession session) throws SQLException {
        String errorInfo = supplierService.checkSupplierEnter(request , response);
        if(errorInfo.equals("OK")){
            String result = supplierService.uploadSupplier(request , response , session);
            return result;
        }else{
            return errorInfo;
        }
    }
}
