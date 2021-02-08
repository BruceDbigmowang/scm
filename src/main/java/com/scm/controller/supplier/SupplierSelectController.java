package com.scm.controller.supplier;

import com.scm.pojo.Contacts;
import com.scm.pojo.Supplier;
import com.scm.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SupplierSelectController {

    @Autowired
    SupplierService supplierService;

    /**
     * 分页查询所有数据  每页显示10条数据
     */
    @RequestMapping(value = "findAllSupplier" , method = RequestMethod.POST)
    public Map<String , Object> getAllSupplier(int start){
        Map<String , Object> map = new HashMap<>();
        List<Supplier> supplierList = supplierService.findAllByPage(start);
        map.put("suppliers" , supplierList);
        int pages = supplierService.getAllPage();
        map.put("pages" , pages);
        return map;
    }

    /**
     * 根据条件并分页查询相关数据，每页显示10条数据
     */
    @RequestMapping(value = "findSomeSupplier" , method = RequestMethod.POST)
    public Map<String , Object> findSupplierByCondition(int start , String name , String nature , String type , String auth , String forbidden ){
        Map<String , Object> map = new HashMap<>();
        List<Supplier> supplierList = supplierService.findBySomeCondition(start , name , nature , type , auth , forbidden);
        map.put("suppliers" , supplierList);
        int pages = supplierService.getSomePage(name , nature , type , auth , forbidden);
        map.put("pages" , pages);
        return map;
    }

    /**
     * 根据供应商编码  查看供应商详细信息
     */
    @RequestMapping(value = "findSupplierDetail" , method = RequestMethod.POST)
    public Map<String , Object> getDetail(String supplierCode){
        return supplierService.getSupplierDetail(supplierCode);
    }

    /**
     * 根据联系人编号来查找联系人具体信息
     */
    @RequestMapping(value = "getContactDetail" , method = RequestMethod.POST)
    public Contacts getContact(int contactId){
        return supplierService.getContactDetail(contactId);
    }
}
