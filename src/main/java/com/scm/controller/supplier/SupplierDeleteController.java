package com.scm.controller.supplier;

import com.scm.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class SupplierDeleteController {

    @Autowired
    SupplierService supplierService;

    /**
     * 删除某一标签
     */
    @RequestMapping(value = "deleteLabel" , method = RequestMethod.DELETE)
    public String deleteLab(String supplierCode , String labelName) throws SQLException {
        return supplierService.deleteLabel(supplierCode , labelName);
    }
}
