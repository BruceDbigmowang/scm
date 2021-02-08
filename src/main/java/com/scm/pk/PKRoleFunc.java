package com.scm.pk;

import java.io.Serializable;

public class PKRoleFunc implements Serializable {
    int roleId;
    int funcId;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getFuncId() {
        return funcId;
    }

    public void setFuncId(int funcId) {
        this.funcId = funcId;
    }
}
