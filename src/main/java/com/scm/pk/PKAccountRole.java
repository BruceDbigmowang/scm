package com.scm.pk;

import java.io.Serializable;

public class PKAccountRole implements Serializable {
    String account;

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
