package com.scm.pk;

import java.io.Serializable;

public class PKInquirySupplier implements Serializable {
    String id;
    String supplierCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
}
