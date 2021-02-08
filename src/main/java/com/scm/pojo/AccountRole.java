package com.scm.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scm.pk.PKAccountRole;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "SCM_AccountRole")
@IdClass(PKAccountRole.class)
@JsonIgnoreProperties({"handler" , "hibernateLazyInitializer"})
public class AccountRole {

    @Id
    String account;

    @Id
    int roleId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
