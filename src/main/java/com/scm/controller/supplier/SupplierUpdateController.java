package com.scm.controller.supplier;

import com.scm.pojo.Supplier;
import com.scm.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

@RestController
public class SupplierUpdateController {

    @Autowired
    SupplierService supplierService;

    /**
     * 将某个联系人设置为主要对接人
     * 1、判断是否存在其他的对接人
     * 2、若有 则修改原来的对接人为一般联系人
     * 3、将前台传来的对接人设置为 对接人
     * 4、修改供应商基础信息表中的对接人信息，方便后面直接使用
     */
    @RequestMapping(value = "setPrincipal" , method = RequestMethod.PUT)
    public String addPrincipal(int contactId){
        return supplierService.setPrincipal(contactId);
    }

    /**
     * 修改联系人信息
     */
    @RequestMapping(value = "changeContact" , method = RequestMethod.PUT)
    public String updateContact(int contactId , String contactName , String contactWay , String contactType , String contactStatus, String score) throws SQLException {
        BigDecimal grade = null;
        try{
            grade = new BigDecimal(score).setScale(2, RoundingMode.HALF_UP);
        }catch (Exception e){
            return "服务评分只能填写数字";
        }
        return supplierService.updateContact(contactId , contactName , contactWay , contactType, contactStatus , grade);
    }

    /**
     * 更新供应商基础信息
     */
    @RequestMapping(value = "changeSupplier" , method = RequestMethod.PUT)
    public String updateSupplier(String supplierCode , String simpleName , String supplierNature , String supplyType , String relationship , String area , String website , String postAddress , String addressDetail){
        Supplier supplier = supplierService.getOne(supplierCode);
        supplier.setSupplierNature(supplierNature);
        supplier.setSimpleName(simpleName);
        supplier.setSupplyType(supplyType);
        supplier.setRelationship(relationship);
        supplier.setArea(area);
        supplier.setWebsite(website);
        supplier.setContactAddress(postAddress);
        supplier.setAddressDetail(addressDetail);
        return supplierService.updateSupplier(supplier);
    }
}
