package com.scm.utils;

import com.scm.entity.BuySumDTO;
import com.scm.entity.InquiryDTO;
import com.scm.entity.SupplierDTO;
import com.scm.pojo.BuySum;
import com.scm.pojo.InquiryList;
import com.scm.pojo.Supplier;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QueryUtil{

    /**
     * SupplierDTO 是supplier查询条件类
     * 属性如下：
     *     String supplierCode;
     *     String supplierName;
     *     String simpleName;
     *     String supplierNature;
     *     String supplyType;
     *     String auth;
     *     String forbidden;
     */

    public static Specification<Supplier> getListSpec(SupplierDTO supplierDTO) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            // 根据供应商的供应商编码/供应商名称/供应商简称来搜索
            List<Predicate> usernameOrAgePredicate = new ArrayList<>();
            String supplierCode = supplierDTO.getSupplierCode();
            if (!StringUtils.isEmpty(supplierCode)) {
                // 供应商编码 这里，用模糊匹配
                usernameOrAgePredicate.add(criteriaBuilder.like(root.get("supplierCode"), "%" + supplierCode + "%"));
            }
            String supplierName = supplierDTO.getSupplierName();
            if (!StringUtils.isEmpty(supplierName)) {
                // 供应商名称 这里，用模糊匹配
                usernameOrAgePredicate.add(criteriaBuilder.like(root.get("supplierName"), "%" + supplierName + "%"));
            }
            String simpleName = supplierDTO.getSimpleName();
            if (!StringUtils.isEmpty(simpleName)) {
                // 供应商简称 这里，用模糊匹配
                usernameOrAgePredicate.add(criteriaBuilder.like(root.get("simpleName"), "%" + simpleName + "%"));
            }

            /* 下面这一行代码很重要。
             * criteriaBuilder.or(Predicate... restrictions) 接收多个Predicate，可变参数；
             * 这多个 Predicate条件之间，是使用OR连接的；该方法最终返回 一个Predicate对象；
             */
            /**
             * *****************************
             * ******** 注****意 ***********
             * *****************************
             * 若 Or 里面的字段没有填写，就不用将usernameOrAgePredicate加入到查询语句中
             * 否则会在查询语句的条件中多出 0 = 1
             * 这样会导致查询不到任何数据
             *
             * 所以需要添加条件 供应商编码不为空 或 供应商名称不为空 或 供应商简称不为空
             * 满足以上任意一个条件则 往查询语句中加入 Or
             */
            if(!StringUtils.isEmpty(supplierCode) || !StringUtils.isEmpty(supplierName) || !StringUtils.isEmpty(simpleName)){
                predicateList.add(criteriaBuilder.or(usernameOrAgePredicate.toArray(new Predicate[0])));
            }
            // 供应商性质 查询
            String supplierNature = supplierDTO.getSupplierNature();
            if(!"".equals(supplierNature)){
                predicateList.add(criteriaBuilder.like(root.get("supplierNature"), "%" + supplierNature + "%"));
            }
            //供应类别 查询
            String supplyType = supplierDTO.getSupplyType();
            if(!"".equals(supplyType)){
                predicateList.add(criteriaBuilder.like(root.get("supplyType"), "%" + supplyType + "%"));
            }
            //是否认证查询
            String auth = supplierDTO.getAuth();
            if(!"".equals(auth)){
                predicateList.add(criteriaBuilder.like(root.get("auth"), "%" + auth + "%"));
            }
            //是否禁用查询
            String forbidden = supplierDTO.getForbidden();
            if(!"".equals(forbidden)){
                predicateList.add(criteriaBuilder.like(root.get("forbidden"), "%" + forbidden + "%"));
            }

            // 最终，使用AND 连接 多个 Predicate 查询条件
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }

    /**
     * 用于查询询价单
     */
    public static Specification<InquiryList> getInquiryListSpec(InquiryDTO inquiryDTO) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            // 根据询价单的 项目 /  状态 / 类型 / 时间段

            //询价单 项目
            String project = inquiryDTO.getProject();
            if(!"".equals(project)){
                predicateList.add(criteriaBuilder.like(root.get("project"), "%" + project + "%"));
            }
            //询价单 状态
            String status = inquiryDTO.getStatusDes();
            if(!"".equals(status)){
                predicateList.add(criteriaBuilder.like(root.get("statusDes"), "%" + status + "%"));
            }
            //询价单 类型
            String type = inquiryDTO.getInquiryType();
            if(!"".equals(type)){
                predicateList.add(criteriaBuilder.like(root.get("inquiryType"), "%" + type + "%"));
            }
            //询价单 日期
            LocalDate begin = inquiryDTO.getBegin();
            LocalDate end = inquiryDTO.getEnd();
            if (begin != null && end != null) {
                predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), begin));
                predicateList.add(criteriaBuilder.lessThanOrEqualTo(root.get("createDate"), end));
            }

            // 最终，使用AND 连接 多个 Predicate 查询条件
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }


    /**
     * 用于根据条件查询采购单
     * 条件：
     * 1、采购单号 或 询价单号 或 经办人账号/姓名
     * 2、项目
     * 3、采购类型
     * 4、订单状态
     */
    public static Specification<BuySum> getBuySumSpec(BuySumDTO buySumDTO) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            // 根据供应商的供应商编码/供应商名称/供应商简称来搜索
            List<Predicate> usernameOrAgePredicate = new ArrayList<>();
            String buyId = buySumDTO.getId();
            if (!StringUtils.isEmpty(buyId)) {
                // 采购单号 这里，模糊查询
                usernameOrAgePredicate.add(criteriaBuilder.like(root.get("id"), "%" + buyId + "%"));
            }
            String inquiryID = buySumDTO.getInquiryID();
            if (!StringUtils.isEmpty(inquiryID)) {
                // 询价单号 这里，用模糊匹配
                usernameOrAgePredicate.add(criteriaBuilder.like(root.get("inquiryID"), "%" + inquiryID + "%"));
            }
            String operatorAccount = buySumDTO.getOperatorAccount();
            if (!StringUtils.isEmpty(operatorAccount)) {
                // 经办人账号 这里，用模糊匹配
                usernameOrAgePredicate.add(criteriaBuilder.like(root.get("operatorAccount"), "%" + operatorAccount + "%"));
            }
            String operatorName = buySumDTO.getOperatorName();
            if (!StringUtils.isEmpty(operatorName)) {
                // 经办人姓名 这里，用模糊匹配
                usernameOrAgePredicate.add(criteriaBuilder.like(root.get("operatorName"), "%" + operatorName + "%"));
            }

            /* 下面这一行代码很重要。
             * criteriaBuilder.or(Predicate... restrictions) 接收多个Predicate，可变参数；
             * 这多个 Predicate条件之间，是使用OR连接的；该方法最终返回 一个Predicate对象；
             */
            /**
             * *****************************
             * ******** 注****意 ***********
             * *****************************
             * 若 Or 里面的字段没有填写，就不用将usernameOrAgePredicate加入到查询语句中
             * 否则会在查询语句的条件中多出 0 = 1
             * 这样会导致查询不到任何数据
             *
             * 所以需要添加条件 供应商编码不为空 或 供应商名称不为空 或 供应商简称不为空
             * 满足以上任意一个条件则 往查询语句中加入 Or
             */
            if(!StringUtils.isEmpty(buyId) || !StringUtils.isEmpty(inquiryID) || !StringUtils.isEmpty(operatorAccount) || !StringUtils.isEmpty(operatorName)){
                predicateList.add(criteriaBuilder.or(usernameOrAgePredicate.toArray(new Predicate[0])));
            }
            // 项目 查询
            String project = buySumDTO.getProject();
            if(!"".equals(project)){
                predicateList.add(criteriaBuilder.like(root.get("project"), "%" + project + "%"));
            }
            //采购类型 查询
            String buyType = buySumDTO.getBuyType();
            if(!"".equals(buyType)){
                predicateList.add(criteriaBuilder.like(root.get("buyType"), "%" + buyType + "%"));
            }
            //订单状态查询
            String status = buySumDTO.getOrderStatus();
            if(!"".equals(status)){
                predicateList.add(criteriaBuilder.like(root.get("orderStatus"), "%" + status + "%"));
            }

            // 最终，使用AND 连接 多个 Predicate 查询条件
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
