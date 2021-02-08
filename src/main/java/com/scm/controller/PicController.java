package com.scm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
public class PicController {

    @RequestMapping("/saveLicense")
    public String saveVoucherPic(@RequestParam("bussinessLicense") MultipartFile file , HttpServletRequest request , String supplierCode) throws IOException {
        //将图片存储到img/sale目录下
        String path = request.getSession().getServletContext().getRealPath("license/");
        String filename ;
        if (!file.isEmpty()) {
            filename = file.getOriginalFilename();
            File filepath = new File(path, filename);
            if (!filepath.getParentFile().exists()) {
                filepath.getParentFile().mkdirs();
            }
            String suffix = filename.substring(filename.lastIndexOf("."));
            supplierCode = supplierCode+suffix;
            file.transferTo(new File(path + File.separator + supplierCode));
        }
        return "文件上传成功";
    }

    @RequestMapping("/saveProductPic")
    public String saveProductImage(@RequestParam("productPic") MultipartFile file , HttpServletRequest request) throws IOException {
        //将图片存储到img/sale目录下
        String path = request.getSession().getServletContext().getRealPath("productImage/");
        String filename ;
        if (!file.isEmpty()) {
            filename = file.getOriginalFilename();
            File filepath = new File(path, filename);
            if (!filepath.getParentFile().exists()) {
                filepath.getParentFile().mkdirs();
            }

            file.transferTo(new File(path + filename));
        }
        return "OK";
    }

}
