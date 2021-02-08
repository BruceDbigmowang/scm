package com.scm.dao;

import com.scm.pojo.Func;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncDAO extends JpaRepository<Func , Integer> {
}
