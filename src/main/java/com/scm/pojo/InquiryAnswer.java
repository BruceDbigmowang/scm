package com.scm.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "SCM_InquiryAnswer")
@JsonIgnoreProperties({"handler" , "hibernateLazyInitializer"})
public class InquiryAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String inquiryID;
    int did;
    String kcode;
    String productName;
    String specification;
    int quantity;
    String unit;
    String supplierCode;
    String supplierName;
    String receiveBrand;
    String receiveSpecification;
    BigDecimal price;
    BigDecimal agreePrice;
    BigDecimal totalPrice;
    BigDecimal agreeTotal;
    BigDecimal taxRate;
    int cycle;
    int agreeCycle;
    LocalDate validity;
    LocalDate createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInquiryID() {
        return inquiryID;
    }

    public void setInquiryID(String inquiryID) {
        this.inquiryID = inquiryID;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public String getKcode() {
        return kcode;
    }

    public void setKcode(String kcode) {
        this.kcode = kcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public String getReceiveBrand() {
        return receiveBrand;
    }

    public void setReceiveBrand(String receiveBrand) {
        this.receiveBrand = receiveBrand;
    }

    public String getReceiveSpecification() {
        return receiveSpecification;
    }

    public void setReceiveSpecification(String receiveSpecification) {
        this.receiveSpecification = receiveSpecification;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAgreePrice() {
        return agreePrice;
    }

    public void setAgreePrice(BigDecimal agreePrice) {
        this.agreePrice = agreePrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getAgreeTotal() {
        return agreeTotal;
    }

    public void setAgreeTotal(BigDecimal agreeTotal) {
        this.agreeTotal = agreeTotal;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getAgreeCycle() {
        return agreeCycle;
    }

    public void setAgreeCycle(int agreeCycle) {
        this.agreeCycle = agreeCycle;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }
}
