package indi.sword.shiro.handlers;

import indi.sword.shiro.service.ShiroService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @Decription
 * @Author: rd_jianbin_lin
 * @Date : 2017/9/28 14:57
 */
/*
    Shiro 认证思路分析：
    1、获取当前的Subject，调用SecurityUtils.getSubject();
    2、测试当前的用户Subject是否被认证，即是否已经登录，调用Subject 的 isAuthenticated()
    3、若没有被认证，则把用户名和密码封装为usernamePasswordToken对象
        a.创建一个表单页面
        b.把请求提交到SpringMVC的Handler
        c.获取用户名和密码
    4、执行登录：调用Subject的login（AuthenticationToken）方法
    5、自定义Realm方法，从数据库中获取对应的记录，返回给Shiro.
        a.实际上需要继承 org.apache.shiro.realm.AuthenticatingRealm 类
        b.实现doGetAuthenticationInfo（AuthenticationToken）方法
    6、由Shiro完成对密码的比对
 */
@Controller
@RequestMapping("/shiro")
public class ShiroHandler {

    @Autowired
    private ShiroService shiroService;

    /**
     * @Decription 测试权限
     * @Author: rd_jianbin_lin
     * @Date : 2017/9/29 16:57
     */
    @RequestMapping("/testShiroAnnotation_admin")
    public String testShiroAnnotation_admin(){
        System.out.println("testShiroAnnotation_admin ... ");
        shiroService.role_method_admin();
        return "redirect:/list.jsp";
    }

    /**
     * @Decription 这个用来测试 session传递的。session本来只能在controller层，现在传递到了serviece层
     * @Author: rd_jianbin_lin
     * @Date : 2017/9/29 16:56
     */
    @RequestMapping("/testShiro_Session")
    public String testShiro_Session(HttpSession session){
        System.out.println("testShiro_Session ...");
        session.setAttribute("k1","v1");
        shiroService.method_Shiro_Session();
        return "redirect:/list.jsp";
    }


    @RequestMapping("/testShiroAnnotation_guest")
    public String testShiroAnnotation_guest(){
        System.out.println("testShiroAnnotation_guest ... ");
        shiroService.role_method_guest();
        return "redirect:/list.jsp";
    }

    @RequestMapping(value = "/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password){
        Subject currentUser = SecurityUtils.getSubject();

        if (!currentUser.isAuthenticated()) {
            // 把用户名和密码封装为 UsernamePasswordToken 对象
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            // rememberme
            token.setRememberMe(true);
            try {
//                System.out.println("token.hashCode -> " + token.hashCode());
                // 执行登录.
                currentUser.login(token);
            }
            // ... catch more exceptions here (maybe custom ones specific to your application?
            // 所有认证时异常的父类.
            catch (AuthenticationException ae) {
                //unexpected condition?  error?
//                System.out.println("登录失败: " + ae.getMessage());
                System.out.println("登录失败 ... " );
            }
        }

        return "redirect:/list.jsp";
    }
}
