package indi.sword.shiro.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.Date;

/**
 * @Decription  权限控制
 * @Author: rd_jianbin_lin
 * @Date : 2017/9/29 17:37
 */
/*

    权限注解
    •@RequiresAuthentication：表示当前Subject已经通过login 进行了身份验证；即Subject. isAuthenticated() 返回true
    •@RequiresUser：表示当前Subject 已经身份验证或者通过记住我登录的。
    •@RequiresGuest：表示当前Subject没有身份验证或通过记住我登录过，即是游客身份。
    •@RequiresRoles(value={“admin”, “user”}, logical= Logical.AND)：表示当前Subject 需要角色admin 和user
    •@RequiresPermissions(value={“user:a”, “user:b”}, logical= Logical.OR)：表示当前Subject 需要权限user:a或user:b。
 */
public class ShiroService {

    @RequiresRoles({"admin"}) // 加上角色，权限控制
    public void role_method_admin() {
        System.out.println("role_method_admin,time = " + new Date());
    }

    @RequiresRoles({"guest"})
    public void role_method_guest() {
        System.out.println("role_method_guest,time = " + new Date());
    }

    public void method_Shiro_Session() {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession(); // session从controller传递到service
        System.out.println("method_Shiro_Session,time = " + new Date());
        System.out.println("session.getAttribute() = " + session.getAttribute("k1"));
    }
}
