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

    @RequestMapping(value = "setPrincipal" , method = RequestMethod.PUT)
    public String addPrincipal(int contactId){
        return supplierService.setPrincipal(contactId);
    }

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
