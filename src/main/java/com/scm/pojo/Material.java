package com.scm.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "SCM_MAterial")
@JsonIgnoreProperties({"handler" , "hibernateLazyInitializer"})
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String supplierCode;
    String kcode;
    String materialName;
    String specification;
    BigDecimal standardPrice;
    BigDecimal recentPrice;
    int standardDeadline;
    int recentDeadline;

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

    public String getKcode() {
        return kcode;
    }

    public void setKcode(String kcode) {
        this.kcode = kcode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public BigDecimal getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(BigDecimal standardPrice) {
        this.standardPrice = standardPrice;
    }

    public BigDecimal getRecentPrice() {
        return recentPrice;
    }

    public void setRecentPrice(BigDecimal recentPrice) {
        this.recentPrice = recentPrice;
    }

    public int getStandardDeadline() {
        return standardDeadline;
    }

    public void setStandardDeadline(int standardDeadline) {
        this.standardDeadline = standardDeadline;
    }

    public int getRecentDeadline() {
        return recentDeadline;
    }

    public void setRecentDeadline(int recentDeadline) {
        this.recentDeadline = recentDeadline;
    }
}
