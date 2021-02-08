package com.scm.service;

import com.scm.dao.*;
import com.scm.entity.InquiryDTO;
import com.scm.pojo.*;
import com.scm.utils.ExcelUtils;
import com.scm.utils.QueryUtil;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InquiryService {
    @Autowired
    MaterialDAO materialDAO;
    @Autowired
    InquiryListDAO listDAO;
    @Autowired
    InquiryDetailDAO detailDAO;
    @Autowired
    InquirySupplierDAO isDao;
    @Autowired
    SupplierDAO supplierDAO;

    /**
     * 根据物料名称获取所有的规格型号
     */
    public List<String> findMaterialByName(String materialName){
        List<Material> materialList = materialDAO.findByMaterialName(materialName);
        List<String> specificationList = new ArrayList<>();
        if(materialList.isEmpty()){
            return null;
        }else{
            for(Material material:materialList){
                String specification = material.getSpecification();
                if(!specificationList.contains(specification)){
                    specificationList.add(specification);
                }
            }
            return specificationList;
        }
    }

    /**
     * 相同的产品名称 相同的规格型号，默认为同一种产品
     *
     * 相同的产品，K编码相同
     */
    public String getKcode(String materialName , String specification){
        List<Material> materialList = materialDAO.findByMaterialNameAndSpecification(materialName , specification);
        if(materialList.isEmpty()){
            return null;
        }else{
            return materialList.get(0).getKcode();
        }
    }

    /**
     * 生成询价单单号
     *
     * 由固定字符串:"88" + 日期 + 流水号组成
     */
    public String generateInquiryID(){
        List<InquiryList> inquiryLists = listDAO.findByCreateDate(LocalDate.now());
        String num = String.valueOf(inquiryLists.size() + 1);

        String number = "";
        for(int i = 0 ; i < 2 - num.length() ; i++){
            number = number + "0";
        }
        number = "KWXJ"+LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd"))+number + num;

        return number;
    }


    /**
     * 创建询价单
     *
     * 创建询价单  包括以下几个方面
     * 1、询价单总汇
     * 2、询价单详情
     * 3、供应商与询价单之间的关系
     */
    @Transactional
    public String createInquiry(InquiryList inquiryList , List<InquiryDetail> details , List<String> supplierList)throws SQLException{
        //保存总汇信息
        listDAO.save(inquiryList);
        //保存询价单详情
        for(InquiryDetail inquiryDetail : details){
            detailDAO.save(inquiryDetail);
        }
        //保存供应商与询价单之间的关系
        for(String supplierCode : supplierList){
            String id = inquiryList.getInquiryID();
            InquirySupplier is = new InquirySupplier();
            is.setId(id);
            is.setSupplierCode(supplierCode);
            String supplierName = supplierDAO.findById(supplierCode).get().getSimpleName();
            is.setSupplierName(supplierName);
            is.setStatus("O");
            is.setStatusDes("未报价");
            isDao.save(is);
        }
        return "OK";
    }

    /**
     * 根据inquId获取询价单总汇信息
     */
    public InquiryList findInquiryById(String inquiryID){
        return listDAO.findById(inquiryID).get();
    }

    /**
     * 根据询价单编号来查询详情
     * 详情中包括：
     * 询价单总汇信息
     * 询价单明细
     * 各个供应商报价状态
     */
    public Map<String , Object> getInquiryDetail(String inquiryID){
        Map<String , Object> map = new HashMap<>();
        InquiryList inquiryList = listDAO.findById(inquiryID).get();
        map.put("sum" , inquiryList);
        List<InquiryDetail> detailList = detailDAO.findByInquiryID(inquiryID);
        map.put("details" , detailList);
        List<InquirySupplier> isList = isDao.findById(inquiryID);
        int no = 0;
        for(int i = 0 ; i < isList.size() ; i++){
            InquirySupplier is = isList.get(i);
            if(is.getStatus().equals("C")){
                no = no + 1;
            }
        }
        String result = "供应商报价状态（"+no+"/"+isList.size()+"）";
        map.put("suppliers" , isList);
        map.put("result" , result);
        return map;
    }

    /**
     * 刚载入时加载所有
     */
    public Map<String , Object> findAll(int start){
        Map<String , Object> map = new HashMap<>();
        PageRequest pr = PageRequest.of(start , 10);
        List<InquiryList> list = listDAO.findAllByOrderByCreateDateDesc(pr);
        map.put("lists" , list);
        int total = listDAO.findAll().size();
        Integer pages = null;
        if(total % 10 == 0){
            pages = total / 10;
        }else{
            pages = total / 10 + 1;
        }
        map.put("pages" , pages);
        return map;
    }

    /**
     * 多条件查询询价单
     */
    public Map<String , Object> findSomeInquery(int start , String project , String type , String status , String begin , String end){
        Map<String , Object>map = new HashMap<>();
        PageRequest pr = PageRequest.of(start, 10);
        InquiryDTO inquiryDTO = new InquiryDTO();
        inquiryDTO.setProject(project);
        inquiryDTO.setInquiryType(type);
        inquiryDTO.setStatusDes(status);

        /**
         * 对时间的设定
         * 1、若开始时间,结束时间均为填写
         *  开始时间为 2021-01-01
         *  结束时间为当前时间
         *
         * 2、若开始时间填写，结束时间未填写
         *  开始时间为设定时间
         *  结束时间为当前时间
         *
         * 3、若开始时间未填写，结束时间填写
         *  开始时间为2021-01-01
         *  结束时间为设定日期
         *
         *  4、若开始时间、结束时间都填写了
         *  开始时间为设定的开始时间
         *  结束时间为设定的结束时间
         */
        if("".equals(begin) && "".equals(end)){
            inquiryDTO.setBegin(LocalDate.of(2021, 1, 1));
            inquiryDTO.setEnd(LocalDate.now());
        }else if(!"".equals(begin) && "".equals(end)){
            inquiryDTO.setBegin(LocalDate.parse(begin, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            inquiryDTO.setEnd(LocalDate.now());
        }else if("".equals(begin) && !"".equals(end)){
            inquiryDTO.setBegin(LocalDate.of(2021, 1, 1));
            inquiryDTO.setEnd(LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }else{
            inquiryDTO.setBegin(LocalDate.parse(begin, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            inquiryDTO.setEnd(LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        Specification<InquiryList> querySpec = QueryUtil.getInquiryListSpec(inquiryDTO);
        List<InquiryList> list = listDAO.findAll(querySpec , pr);
        map.put("lists" , list);
        int total = listDAO.findAll(querySpec).size();
        Integer pages = null;
        if(total % 10 == 0){
            pages = total / 10;
        }else{
            pages = total / 10 + 1;
        }
        map.put("pages" , pages);
        return map;
    }

    /**
     * 手动关闭询价单
     * 询价单状态有三种：
     * 有效：发起询价后的正常状态
     * 无效：发起询价后 手动关闭的状态
     * 过期：询价单超过截止日期的状态，由系统后台修改
     */
    @Transactional
    public String updateInquiry(InquiryList inquiryList){
        try{
            listDAO.save(inquiryList);
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";
    }

    /**
     * 查询所有的供应商
     */
    public List<Supplier> getAllSupplier(){
        return supplierDAO.findAll();
    }

    /**
     * 导入供应商信息
     * 首先先检查Excel中信息是否齐全，是否有重复信息，或输入是否正确
     */
    public String checkInquiryEnter(HttpServletRequest request , HttpServletResponse response ){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("inquiryFile");
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

        List<List<String>> materialList = new ArrayList<>();
        for (int i = 0; i < listob.size(); i++) {
            List<String> material = new ArrayList<>();
            int target = i + 1;
            List<Object> lo = listob.get(i);
            //第一列值为 产品名称
            String productName = String.valueOf(lo.get(0)).trim();
            if("".equals(productName)){
                continue;
            }else{
                material.add(productName);
            }
            //第二列的值为 规格型号
            String specification = String.valueOf(lo.get(1)).trim();
            if("".equals(specification)){
                return "第"+target+"行规格/型号未填写";
            }else{
                material.add(specification);
            }
            //第三列的值为K编码
            String kcode = String.valueOf(lo.get(2)).trim();
            if("".equals(kcode)){
                return "第"+target+"行K编码未填写";
            }else{
                String kcode2 = getKcode(productName , specification);
                if(kcode2 == null || !kcode.equals(kcode2)){
                    return "第"+target+"行K编码填写错误";
                }else{
                    material.add(kcode);
                }
            }
            //第四列的值为 品牌
            String brand = String.valueOf(lo.get(3)).trim();
            material.add(brand);
            //第五列的值为 品牌编码
            String brandCode = String.valueOf(lo.get(4)).trim();
            material.add(brandCode);
            //第六行的值为 所需数量
            String quantity = String.valueOf(lo.get(5)).trim();
            if("".equals(quantity)){
                return "第"+target+"行所需数量未填写";
            }else{
                try{
                    int num = Integer.parseInt(quantity);
                    material.add(quantity);
                }catch (Exception e){
                    return "第"+target+"行所需数量填写错误（只能填写整数）";
                }
            }
            //第七行的值为 单位
            String unit = String.valueOf(lo.get(6)).trim();
            if("".equals(unit)){
                return "第"+target+"行单位未填写";
            }else{
                material.add(unit);
            }
            String note = String.valueOf(lo.get(7)).trim();
            material.add(note);
            if(materialList.contains(material)){
                return "第"+target+"行数据重复";
            }
        }
        return "OK";
    }

    /**
     * 上一步检查Excel中的内容
     */
    public List<List<String>> getInquiryEnter(HttpServletRequest request , HttpServletResponse response ) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("inquiryFile");
        InputStream in = null;
        try {
            in = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<List<Object>> listob = null;
        listob = new ExcelUtils().getBankListByExcel(in, file.getOriginalFilename());

        List<List<String>> materialList = new ArrayList<>();
        for (int i = 0; i < listob.size(); i++) {
            List<String> material = new ArrayList<>();
            int target = i + 1;
            List<Object> lo = listob.get(i);
            //第一列值为 产品名称
            String productName = String.valueOf(lo.get(0)).trim();
            if("".equals(productName)){
                continue;
            }else{
                material.add(productName);
            }
            //第二列的值为 规格型号
            String specification = String.valueOf(lo.get(1)).trim();
            material.add(specification);
            //第三列的值为K编码
            String kcode = String.valueOf(lo.get(2)).trim();
            material.add(kcode);
            //第四列的值为 品牌
            String brand = String.valueOf(lo.get(3)).trim();
            material.add(brand);
            //第五列的值为 品牌编码
            String brandCode = String.valueOf(lo.get(4)).trim();
            material.add(brandCode);
            //第六行的值为 所需数量
            String quantity = String.valueOf(lo.get(5)).trim();
            material.add(quantity);
            //第七行的值为 单位
            String unit = String.valueOf(lo.get(6)).trim();
            material.add(unit);
            String note = String.valueOf(lo.get(7)).trim();
            material.add(note);

            materialList.add(material);
        }
        return materialList;
    }

    /**
     * 发送邮件
     * 取所有的商务对接人
     * 用 supplierCode / inquiryID 组合生成对应的网址并发送给对应的人
     */
    public String sendMessage(String inquiryID , List<String> suppliers){
        for(int i = 0 ; i < suppliers.size() ; i++){
            Supplier supplier = supplierDAO.findById(suppliers.get(i)).get();
            String phone = supplier.getPhone();
            String website = "http://212.129.134.123:8083/scm/foreAnswer?inquiryID="+inquiryID+"&&supplierCode="+suppliers.get(i);

        }
        return "OK";
    }
}