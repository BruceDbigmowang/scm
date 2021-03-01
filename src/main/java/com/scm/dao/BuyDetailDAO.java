package com.scm.dao;

import com.scm.pojo.BuyDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuyDetailDAO extends JpaRepository<BuyDetail , Integer> {
    /**
     * 根据采购单号  查询出所有明细
     */
    List<BuyDetail> findByBuyID(String buyID);
}
