package com.scm.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "SCM_InquiryList")
@JsonIgnoreProperties({"handler" , "hibernateLazyInitializer"})
public class InquiryList {
    @Id
    String inquiryID;

    LocalDate inquiryDate;
    String project;
    String inquiryType;
    LocalDate deadline;
    String status;
    String statusDes;
    String inquiryAccount;
    String inquiryName;
    String creater;
    String createrName;
    LocalDate createDate;

    public String getInquiryID() {
        return inquiryID;
    }

    public void setInquiryID(String inquiryID) {
        this.inquiryID = inquiryID;
    }

    public LocalDate getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(LocalDate inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getInquiryType() {
        return inquiryType;
    }

    public void setInquiryType(String inquiryType) {
        this.inquiryType = inquiryType;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
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

    public String getInquiryAccount() {
        return inquiryAccount;
    }

    public void setInquiryAccount(String inquiryAccount) {
        this.inquiryAccount = inquiryAccount;
    }

    public String getInquiryName() {
        return inquiryName;
    }

    public void setInquiryName(String inquiryName) {
        this.inquiryName = inquiryName;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }
}
