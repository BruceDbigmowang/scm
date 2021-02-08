package com.scm.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scm.pk.PKRoleFunc;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "SCM_RoleFunc")
@IdClass(PKRoleFunc.class)
@JsonIgnoreProperties({"handler" , "hibernateLazyInitializer"})
public class RoleFunc {
    @Id
    int roleId;
    @Id
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
