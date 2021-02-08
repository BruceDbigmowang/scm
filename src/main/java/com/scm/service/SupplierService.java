package com.scm.service;

import com.scm.dao.*;
import com.scm.entity.SupplierDTO;
import com.scm.pojo.*;
import com.scm.utils.ExcelUtils;
import com.scm.utils.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SupplierService {

    @Autowired
    SupplierDAO supplierDAO;
    @Autowired
    ContactDAO contactDAO;
    @Autowired
    AuthenticationDAO authenticationDAO;
    @Autowired
    LabelDAO labelDAO;
    @Autowired
    MaterialDAO materialDAO;

    /**
     * 创建供应商编号
     * knowhy + "yyyyMMdd" + 四位流水号
     */
    public String generateSupplierCode(){

        List<Supplier> supplierList = supplierDAO.findByCreateDate(LocalDate.now());
        String num = String.valueOf(supplierList.size());

        String number = "";
        for(int i = 0 ; i < 4 - num.length() ; i++){
            number = number + "0";
        }
        number = "knowhy"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))+number + num;

        return number;
    }

    /**
     * 根据供应商编码获取具体供应商信息
     */
    public Supplier getOne(String supplierCode){
        return supplierDAO.findById(supplierCode).get();
    }

    /**
     * 新建一个新的供应商或更新供应商信息
     *
     * 成功 ： 返回“OK"
     * 失败 : 返回报错信息
     */
    @Transactional
    public String saveSupplier(Supplier supplier){
        try{
            supplier.setLabel("");
            supplier.setAuth("否");
            supplier.setForbidden("F");
            supplier.setRisk("无");
            supplierDAO.save(supplier);
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";
    }

    /**
     * 更新供应商信息
     */
    @Transactional
    public String updateSupplier(Supplier supplier){
        try{
            supplierDAO.save(supplier);
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";
    }

    /**
     * 添加联系人信息
     * 默认状态为：在职
     * 是否是主要联系人：F(false)
     */
    @Transactional
    public String saveContact(String supplierCode , String name , String phone , String type)throws SQLException{
        Contacts contacts = new Contacts();
        contacts.setSupplierCode(supplierCode);
        contacts.setContactName(name);
        contacts.setContactWay(phone);
        contacts.setType(type);
        contacts.setStatus("正常");
        contacts.setGrade(BigDecimal.ZERO);
        contacts.setMainContact("F");
        contactDAO.save(contacts);
        return "OK";
    }

    /**
     * 更新联系人信息
     */
    @Transactional
    public String updateContact(int contactId , String name , String phone , String type, String status , BigDecimal grade)throws SQLException{
        Contacts contacts = contactDAO.findById(contactId).get();
        String mainCon = contacts.getMainContact();
        String type2 = contacts.getType();
        String supplierCode = contacts.getSupplierCode();
        contacts.setContactName(name);
        if(!type2.equals(type)){
            return "联系人类型不可修改";
        }
        contacts.setType(type);
        contacts.setContactWay(phone);
        contacts.setStatus(status);
        contacts.setGrade(grade);
        contactDAO.save(contacts);
        //若该联系人为某一供应商的商务对接人，则还需要修改供应商表中的信息
        if(type.equals("bussiness") && mainCon.equals("T")){
            Supplier supplier = supplierDAO.findById(supplierCode).get();
            supplier.setContact(name);
            supplier.setPhone(phone);
            supplierDAO.save(supplier);
        }
        return "OK";
    }

    /**
     * 设置某个联系人为主要对接人
     * 某一供应商的商务/技术对接人只有一个
     * 设置为主要对接人时：
     * 1、将之前的对接人变为一般联系人（即:将mainContacts改为F）
     * 2、将目标联系人改为对接人（即:将mainContacts改为T）
     */
    @Transactional
    public String setPrincipal(int contactId){
        Contacts contacts = contactDAO.findById(contactId).get();
        String supplierCode = contacts.getSupplierCode();
        String type = contacts.getType();
        List<Contacts> contactsList = contactDAO.findBySupplierCodeAndTypeAndMainContact(supplierCode , type , "T");
        for(Contacts contacts1 : contactsList){
            contacts1.setMainContact("F");
            contactDAO.save(contacts1);
        }
        contacts.setMainContact("T");
        contactDAO.save(contacts);
        //如果设置为商务对接人，更新总表中的供应商联系人信息
        if(type.equals("bussiness")){
            Supplier supplier = supplierDAO.findById(supplierCode).get();
            supplier.setContact(contacts.getContactName());
            supplier.setPhone(contacts.getContactWay());
            supplierDAO.save(supplier);
        }
        return "OK";
    }

    /**
     * 添加认证信息
     */
    @Transactional
    public String saveAuthentication(String supplierCode , String establishDate , String leagalPerson , BigDecimal registerMoney , String creditCode , String registerAddress , String picName)throws SQLException{
        //将数据写入到认证信息表中
        Authentication authentication = new Authentication();
        authentication.setSupplierCode(supplierCode);
        authentication.setEstablishDate(establishDate);
        authentication.setLegalPerson(leagalPerson);
        authentication.setRegisterMoney(registerMoney);
        authentication.setCreditCode(creditCode);
        authentication.setRegisterAddress(registerAddress);
        authentication.setPicName(picName);
        authenticationDAO.save(authentication);
        //修改供应商表中的认证状态
        Supplier supplier = supplierDAO.findById(supplierCode).get();
        supplier.setAuth("是");
        supplierDAO.save(supplier);
        return "OK";
    }

    /**
     * 设置新标签
     * 标签与每个供应商对应
     * 每个供应商不能存在名称相同的标签
     */
    @Transactional
    public String createLabel(String supplierCode , String labelName)throws SQLException{
        List<Label> labelList = labelDAO.findBySupplierCodeAndLabelName(supplierCode , labelName);
        if(labelList.isEmpty()){
            Label label = new Label();
            label.setSupplierCode(supplierCode);
            label.setLabelName(labelName);
            labelDAO.save(label);
            //标签添加成功后，更新供应商表中的标签信息
            Supplier supplier = supplierDAO.findById(supplierCode).get();
            String labelDes = supplier.getLabel();
            if(labelDes == null || "".equals(labelDes)){
                supplier.setLabel(labelName);
            }else{
                labelDes = labelDes+","+labelName;
                supplier.setLabel(labelDes);
            }
            supplierDAO.save(supplier);
            return "OK";
        }else{
            return "标签名称不可重复";
        }
    }

    /**
     * 删除某一标签
     */
    @Transactional
    public String deleteLabel(String supplierCode , String labelName)throws SQLException{
        List<Label> labelList = labelDAO.findBySupplierCodeAndLabelName(supplierCode , labelName);

        //某一标签删除后，需要更新供应商表中的标签的信息（删除目标字段）
        Supplier supplier = supplierDAO.findById(supplierCode).get();
        String labelStr = supplier.getLabel();
        String[] labelArr = labelStr.split(",");
        List<String> labels = new ArrayList<>(Arrays.asList(labelArr));
        for(int i = 0 ; i < labelArr.length ; i++){
            String target = labelArr[i];
            if(target.equals(labelName)){
                labels.remove(target);
                break;
            }
        }
        String last = "";
        for(int i = 0 ; i < labels.size() ; i++){
            if(i == 0){
                last = last +labels.get(i);
            }else{
                last = last + "," +labels.get(i);
            }
        }
        supplier.setLabel(last);
        supplierDAO.save(supplier);
        for (Label label : labelList){
            labelDAO.delete(label);
        }
        return "OK";
    }

    /**
     * 添加物料信息
     *首先检验Excel表格中的所有物料信息是否正确
     */
    public String checkMaterialrEnter(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("materialExcel");
        InputStream in = null;
        try {
            in = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<Object>> listob = null;
        try {
            listob = new ExcelUtils().getBankListByExcel(in, file.getOriginalFilename());
        } catch (Exception e) {
            return "请选择需要导入的文件";
        }

        List<String> supplierList = new ArrayList<>();
        for (int i = 0; i < listob.size(); i++) {
            int target = i + 1;
            List<Object> lo = listob.get(i);
            //第一列值为 kcode
            String kcode = String.valueOf(lo.get(0)).trim();
            if("".equals(kcode)){
                return "第"+target+"行k编码未填写";
            }else{

                if(supplierList.contains(kcode)){
                    return "第"+target+"行供应商信息重复";
                }else{
                    supplierList.add(kcode);
                }
            }
            //第二列的值为物料名称
            String materialName = String.valueOf(lo.get(1)).trim();
            if("".equals(materialName)){
                return "第"+target+"行物料名称未填写";
            }
            //第三列的值为规格型号
            String specification = String.valueOf(lo.get(2)).trim();
            if("".equals(specification)){
                return "第"+target+"行规格型号未填写";
            }
            //第四列的值为标准价格
            String standardPrice = String.valueOf(lo.get(3)).trim();
            if("".equals(standardPrice)){
                return "第"+target+"行标准价格未填写";
            }else{
                try{
                    BigDecimal no = new BigDecimal(standardPrice);
                }catch (Exception e){
                    return "第"+target+"行标准价格填写错误（只能填写数字）";
                }
            }
            //第五列的值为近期交易价格
            String recentPrice = String.valueOf(lo.get(4)).trim();
            if("".equals(recentPrice)){
                return "第"+target+"行最近采购价格未填写";
            }else{
                try{
                    BigDecimal no = new BigDecimal(recentPrice);
                }catch (Exception e){
                    return "第"+target+"行最近采购价格填写错误（只能填写数字）";
                }
            }
            //第六行的值为标准交货期
            String standardDeadline = String.valueOf(lo.get(5)).trim();
            System.out.println("标准交货期："+standardDeadline);
            if("".equals(standardDeadline)){
                return "第"+target+"行标准交货期未填写";
            }else {
                try{
                    int no = Integer.parseInt(standardDeadline);

                }catch (Exception e){
                    return "第"+target+"行标准交货期填写错误（只能填写整数）";
                }

            }
            //第七行的值为最近采购交货期
            String recentDeadline = String.valueOf(lo.get(6)).trim();
            if("".equals(recentDeadline)){
                return "第"+target+"行最近采购交货期未填写";
            }else {
                try{
                    int no = Integer.parseInt(recentDeadline);
                }catch (Exception e){
                    return "第"+target+"行最近采购交货期填写错误（只能填写整数）";
                }

            }

        }
        return "OK";
    }

    /**
     * 添加物料信息
     *检查完成之后，循环表格中的数据直接插入
     */
    public String uploadMaterial(HttpServletRequest request, HttpServletResponse response ) throws SQLException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("materialExcel");
        String supplierCode = request.getParameter("supplierCode");
        if (file.isEmpty()) {
            try {
                throw new Exception("文件不存在！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        InputStream in = null;
        try {
            in = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<Object>> listob = null;
        try {
            listob = new ExcelUtils().getBankListByExcel(in, file.getOriginalFilename());
        } catch (Exception e) {
            return "请选择需要导入的文件";
        }
        int targetNum = 0;
        for (int i = 0; i < listob.size(); i++) {
            List<Object> lo = listob.get(i);
            String kcode = String.valueOf(lo.get(0)).trim();
            String materialName = String.valueOf(lo.get(1)).trim();
            String specification = String.valueOf(lo.get(2)).trim();
            BigDecimal standardPrice = new BigDecimal(String.valueOf(lo.get(3)).trim());
            BigDecimal recentPrice = new BigDecimal(String.valueOf(lo.get(4)).trim());
            int standardDeadline = Integer.parseInt(String.valueOf(lo.get(5)).trim());
            int recentDeadline = Integer.parseInt(String.valueOf(lo.get(6)).trim());

            Material material = new Material();
            material.setSupplierCode(supplierCode);
            material.setKcode(kcode);
            material.setMaterialName(materialName);
            material.setSpecification(specification);
            material.setStandardPrice(standardPrice);
            material.setRecentPrice(recentPrice);
            material.setStandardDeadline(standardDeadline);
            material.setRecentDeadline(recentDeadline);

            materialDAO.save(material);
            targetNum = targetNum + 1;
        }
        return "成功导入"+targetNum+"条数据";
    }


    /**
     * 导入供应商信息
     * 首先先检查Excel中信息是否齐全，是否有重复信息，或输入是否正确
     */
    public String checkSupplierEnter(HttpServletRequest request , HttpServletResponse response ){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("supplierExcel");
        InputStream in = null;
        try {
            in = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<Object>> listob = null;
        try {
            listob = new ExcelUtils().getBankListByExcel(in, file.getOriginalFilename());
        } catch (Exception e) {
            return "请选择需要导入的文件";
        }

        List<String> supplierList = new ArrayList<>();
        for (int i = 0; i < listob.size(); i++) {
            int target = i + 1;
            List<Object> lo = listob.get(i);
            //第一列值为 供应商名称
            String supplierName = String.valueOf(lo.get(0)).trim();
            if("".equals(supplierName)){
                continue;
            }else{
                if(supplierList.contains(supplierName)){
                    return "第"+target+"行供应商信息重复";
                }else{
                    supplierList.add(supplierName);
                }

                //判断数据库中是否有数据
                List<Supplier> suppliers = supplierDAO.findBySupplierName(supplierName);
                if(!suppliers.isEmpty()){
                    return "供应商"+supplierName+"数据库中已存在";
                }
            }
            //第二列的值为 供应商简称
            String simpleName = String.valueOf(lo.get(1)).trim();
            if("".equals(simpleName)){
                return "第"+target+"行供应商简称未填写";
            }
            //第三列的值为供应商性质
            String supplierNature = String.valueOf(lo.get(2)).trim();
            if("".equals(supplierNature)){
                return "第"+target+"行供应商性质未填写";
            }
            //第四列的值为供应类别
            String supplyType = String.valueOf(lo.get(3)).trim();
            if("".equals(supplyType)){
                return "第"+target+"行供应类别未填写";
            }
            //第五列的值为供应商关系
            String relationship = String.valueOf(lo.get(4)).trim();
            if("".equals(relationship)){
                return "第"+target+"行供应商关系未填写";
            }
            //第六行的值为所在地区
            String area = String.valueOf(lo.get(5)).trim();
            if("".equals(area)){
                return "第"+target+"行所在地区未填写";
            }
            //第七行的值为公司网址
            String website = String.valueOf(lo.get(6)).trim();
            if("".equals(website)){
                return "第"+target+"行公司网址未填写";
            }
            //第八行的值为通讯地址
            String postAddress = String.valueOf(lo.get(7)).trim();
            if("".equals(postAddress)){
                return "第"+target+"行通讯地址未填写";
            }
            //第九行的值为通讯地址
            String addressDetail = String.valueOf(lo.get(8)).trim();
            if("".equals(addressDetail)){
                return "第"+target+"行地址详情未填写";
            }
        }
        return "OK";
    }

    @Transactional
    public String uploadSupplier(HttpServletRequest request, HttpServletResponse response , HttpSession session){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("supplierExcel");
        if (file.isEmpty()) {
            try {
                throw new Exception("文件不存在！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        InputStream in = null;
        try {
            in = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<Object>> listob = null;
        try {
            listob = new ExcelUtils().getBankListByExcel(in, file.getOriginalFilename());
        } catch (Exception e) {
            return "请选择需要导入的文件";
        }
        int targetNum = 0;
        for (int i = 0; i < listob.size(); i++) {
            List<Object> lo = listob.get(i);
            String supplierCode = generateSupplierCode();
            String supplierName = String.valueOf(lo.get(0)).trim();
            if("".equals(supplierName)){
                continue;
            }
            String simpleName = String.valueOf(lo.get(1)).trim();
            String supplierNature = String.valueOf(lo.get(2)).trim();
            String supplyType = String.valueOf(lo.get(3)).trim();
            String relationship = String.valueOf(lo.get(4)).trim();
            String area = String.valueOf(lo.get(5)).trim();
            String website = String.valueOf(lo.get(6)).trim();
            String postAddress = String.valueOf(lo.get(7)).trim();
            String addressDetail = String.valueOf(lo.get(8)).trim();

            Supplier supplier = new Supplier();
            supplier.setSupplierCode(supplierCode);
            supplier.setSupplierName(supplierName);
            supplier.setSimpleName(simpleName);
            supplier.setSupplierNature(supplierNature);
            supplier.setSupplyType(supplyType);
            supplier.setRelationship(relationship);
            supplier.setArea(area);
            supplier.setWebsite(website);
            supplier.setContactAddress(postAddress);
            supplier.setAddressDetail(addressDetail);
            Account user = (Account)session.getAttribute("user");
            supplier.setCreater(user.getAccount());
            supplier.setCreaterName(user.getName());
            supplier.setCreateDate(LocalDate.now());
            /**
             * 直接保存
             */
            String result = saveSupplier(supplier);
            if(!result.equals("OK")){
                return result;
            }
            targetNum = targetNum + 1;
        }
        return "成功导入"+targetNum+"条数据";
    }

    /**
     * 分页查询所有
     */
    public List<Supplier> findAllByPage(int start){
        PageRequest pr = PageRequest.of(start, 10);
        List<Supplier> supplierList = supplierDAO.findAllByOrderByCreateDateDesc(pr);
        for(Supplier supplier : supplierList){
            String forbidden = supplier.getForbidden();
            if(forbidden.equals("F")){
                supplier.setForbidden("未禁用");
            }else{
                supplier.setForbidden("已禁用");
            }
        }
        return supplierList;
    }

    /**
     * 查询所有时
     * 获取总页数
     */
    public int getAllPage(){
        List<Supplier> supplierList = supplierDAO.findAll();
        if(supplierList.isEmpty()){
            return 0;
        }else{
            if(supplierList.size() % 10 == 0){
                int pages = supplierList.size() / 10;
                return pages;
            }else{
                int pages = supplierList.size() / 10 + 1;
                return pages;
            }
        }
    }

    /**
     * 多条件查询对应的供应商
     * 若某些条件未填写则为空
     */
    public List<Supplier> findBySomeCondition(int start , String name , String nature , String type , String auth , String forbidden){
        PageRequest pr = PageRequest.of(start, 10);
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setSupplierCode(name);
        supplierDTO.setSupplierName(name);
        supplierDTO.setSimpleName(name);
        supplierDTO.setSupplierNature(nature);
        supplierDTO.setSupplyType(type);
        supplierDTO.setAuth(auth);
        supplierDTO.setForbidden(forbidden);
        Specification<Supplier> querySpec = QueryUtil.getListSpec(supplierDTO);
        List<Supplier> supplierList = supplierDAO.findAll(querySpec , pr);
        for(Supplier supplier : supplierList){
            String forbid = supplier.getForbidden();
            if(forbid.equals("F")){
                supplier.setForbidden("未禁用");
            }else{
                supplier.setForbidden("已禁用");
            }
        }
        return supplierList;
    }

    /**
     * 多条件查询获取总页数
     */
    public int getSomePage(String name , String nature , String type , String auth , String forbidden){
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setSupplierCode(name);
        supplierDTO.setSupplierName(name);
        supplierDTO.setSimpleName(name);
        supplierDTO.setSupplierNature(nature);
        supplierDTO.setSupplyType(type);
        supplierDTO.setAuth(auth);
        supplierDTO.setForbidden(forbidden);
        Specification<Supplier> querySpec = QueryUtil.getListSpec(supplierDTO);
        List<Supplier> supplierList = supplierDAO.findAll(querySpec);
        if(supplierList.isEmpty()){
            return 0;
        }else{
            if(supplierList.size() % 10 == 0){
                int pages = supplierList.size() / 10;
                return pages;
            }else{
                int pages = supplierList.size() / 10 + 1;
                return pages;
            }
        }
    }

    /**
     * 根据供应商编号来查询具体的供应商详情
     * 详情包括：
     * 1、基础信息
     * 2、联系人及联系方式
     * 3、认证信息
     * 4、供应商标签
     * 5、财务信息（未给出具体逻辑字段）(暂不添加)
     * 6、其它信息（未给出具体逻辑字段）(暂不添加)
     */
    public Map<String , Object> getSupplierDetail(String supplierCode){

        Map<String , Object> map = new HashMap<>();
        Supplier supplier = supplierDAO.findById(supplierCode).get();
        map.put("supplier" , supplier);
        List<Contacts> contactsList = contactDAO.findBySupplierCode(supplierCode);
        if(contactsList.isEmpty()){
            map.put("contactInfo" , "no");
        }else{
            map.put("contactInfo" , "ok");
            map.put("contacts" , contactsList);
        }
        List<Authentication> authentications = authenticationDAO.findBySupplierCode(supplierCode);
        if(authentications.isEmpty()){
            map.put("authenInfo" , "no");
        }else{
            map.put("authenInfo" , "ok");
            map.put("authentications" , authentications);
        }
        List<Label> labelList = labelDAO.findBySupplierCode(supplierCode);
        if(labelList.isEmpty()){
            map.put("labelInfo" , "no");
        }else{
            map.put("labelInfo" , "ok");
            map.put("labels" , labelList);
        }
        List<Material> materialList = materialDAO.findBySupplierCode(supplierCode);
        if(materialList.isEmpty()){
            map.put("materialInfo" , "no");
        }else{
            map.put("materialInfo" , "ok");
            map.put("materials" , materialList);
        }
        return map;
    }

    /**
     * 获取联系人信息
     */
    public Contacts getContactDetail(int contactId){
        return contactDAO.findById(contactId).get();
    }

}
