package com.scm.dao;

import com.scm.pojo.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDAO extends JpaRepository<Role , Integer> {
}
