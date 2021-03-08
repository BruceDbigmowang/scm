package com.scm.utils;

import org.apache.shiro.crypto.hash.SimpleHash;

public class MD5Util {
    /**
     * MD5加密
     * 盐：“abc”
     * 加密次数：6
     */
    public static SimpleHash addMD5(String password){
        String salt = "abc";
        int hashIterations = 6;

        //Md5Hash md5Hash = new Md5Hash(password , salt , hashIterations);
        SimpleHash simpleHash = new SimpleHash("md5" , password , salt , hashIterations);
        System.out.println(simpleHash);
        return simpleHash;
    }
}
