package com.scm.login;

import com.scm.pojo.Account;
import com.scm.service.AccountService;
import com.scm.utils.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    AccountService accountService;


    public static final String CAPTCHA_KEY = "session_captcha";

    /**
     * 登录页面生成验证码
     */
    @RequestMapping("captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        // 生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);

        // 生成图片
        int w = 135, h = 42;
        VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);

        // 将验证码存储在session以便登录时校验
        session.setAttribute(CAPTCHA_KEY, verifyCode.toLowerCase());
    }


    /**
     * 登陆功能
     * 根据账号 、 密码 、 验证码 进行登录（账号不能被冻结）
     * 账号允许是账号 手机号
     *
     */
    @RequestMapping(value="logins",method = RequestMethod.POST)
    @ResponseBody
    public Result loginCheck(@RequestBody Map<String,String> userData , HttpSession session, HttpServletRequest req) throws UnknownHostException {
        String userName=userData.get("name");
        String password=userData.get("password");
        String ca=userData.get("validateCode").toLowerCase();
        String sesionCode = (String) req.getSession().getAttribute(CAPTCHA_KEY);
        if(!ca.equals(sesionCode.toLowerCase())){
            System.out.println("验证码输入错误!");
            return Result.fail("验证码输入错误!");
        }
        /*
         * 将用户名分开查找；用户名或者电话号码；
         * */
        Account accountList = accountService.findByAccountOrPhone(userName);
        if(accountList == null){
            return Result.fail("用户："+userName+"不存在");
        }

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName , password);
        try{
            subject.login(token);
            boolean flag = subject.isAuthenticated();
            if(flag){
                System.out.println("登录成功！");
                Account user = (Account)subject.getPrincipal();
                subject.getSession().setAttribute("user",user);

                if(user.getStatus()== "N"){
                    System.out.println("账号已被冻结!");
                    return Result.fail("账号已被冻结!");
                }
                Object sessionId=session.getAttribute("userId");
                System.out.println(user);

                return Result.success();
            }else{

                return Result.fail("用户名或密码不正确!");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return Result.fail("用户名或密码不正确!!!");
        }
    }

    //退出登录
    @RequestMapping("/logout")
    public String outSys(){
        Subject subject = SecurityUtils.getSubject();
        Account iUser = (Account)subject.getSession().getAttribute("user");
        if(iUser == null){
            return "redirect:foreLogin";
        }

        if(subject.isAuthenticated())
            subject.logout();
        return "redirect:foreLogin";
    }

}
