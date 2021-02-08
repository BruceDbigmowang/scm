package com.scm.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scm.pk.PKInquirySupplier;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "SCM_InquirySupplier")
@IdClass(PKInquirySupplier.class)
@JsonIgnoreProperties({"handler" , "hibernateLazyInitializer"})
public class InquirySupplier {
    @Id
    String id;
    @Id
    String supplierCode;
    String supplierName;
    String status;
    String statusDes;

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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDes() {
        return statusDes;
    }

    public void setStatusDes(String statusDes) {
        this.statusDes = statusDes;
    }
}
