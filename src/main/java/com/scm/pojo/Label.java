package com.scm.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "SCM_Label")
@JsonIgnoreProperties({"handler" , "hibernateLazyInitializer"})
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String supplierCode;
    String labelName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
