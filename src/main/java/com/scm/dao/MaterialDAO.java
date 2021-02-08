package com.scm.dao;

import com.scm.pojo.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialDAO extends JpaRepository<Material , Integer> {
    /**
     * 根据供应商编码查询出所有的无聊信息
     */
    List<Material> findBySupplierCode(String supplierCode);

    /**
     * 根据产品名称  列出该产品的所有规格型号
     */
    List<Material> findByMaterialName(String materialName);

    /**
     * 根据产品名称 + 规格型号 找到对应的物料
     */
    List<Material> findByMaterialNameAndSpecification(String materialName , String specification);
}
