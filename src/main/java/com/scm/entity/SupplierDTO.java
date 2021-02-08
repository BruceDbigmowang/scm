package com.scm.entity;

public class SupplierDTO {
    String supplierCode;
    String supplierName;
    String simpleName;
    String supplierNature;
    String supplyType;
    String auth;
    String forbidden;

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

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getSupplierNature() {
        return supplierNature;
    }

    public void setSupplierNature(String supplierNature) {
        this.supplierNature = supplierNature;
    }

    public String getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(String supplyType) {
        this.supplyType = supplyType;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getForbidden() {
        return forbidden;
    }

    public void setForbidden(String forbidden) {
        this.forbidden = forbidden;
    }
}
