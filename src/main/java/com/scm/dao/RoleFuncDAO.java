package com.scm.dao;

import com.scm.pk.PKRoleFunc;
import com.scm.pojo.RoleFunc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleFuncDAO extends JpaRepository<RoleFunc , PKRoleFunc> {
    List<RoleFunc> findByRoleId(int roleId);
}
