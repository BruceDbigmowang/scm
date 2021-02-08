package com.scm;

import com.scm.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ScmApplicationTests {

    @Autowired
    SupplierService supplierService;

    @Test
    void contextLoads() {
        String supplierCode = supplierService.generateSupplierCode();
        System.out.println(supplierCode);
    }

}
