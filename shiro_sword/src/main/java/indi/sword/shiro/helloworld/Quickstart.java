package indi.sword.shiro.helloworld;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * Simple Quickstart application showing how to use Shiro's API.
 *
 * 编写shiro.ini，测试类
 * @Author:rd_jianbin_lin
 * @Date: 13:10 2017/9/24
 */
public class Quickstart {

    private static final transient Logger logger = LoggerFactory.getLogger(Quickstart.class);


    public static void main(String[] args) {

        // The easiest way to create a Shiro SecurityManager with configured
        // realms, users, roles and permissions is to use the simple INI config.
        // We'll do that by using a factory that can ingest a .ini file and
        // return a SecurityManager instance:

        // Use the shiro.ini file at the root of the classpath
        // (file: and url: prefixes load from files and urls respectively):

        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();

        // for this simple example quickstart, make the SecurityManager
        // accessible as a JVM singleton.
        // Most applications wouldn't do this and instead rely on their container configuration or web.xml for webapps.
        // That is outside the scope of this simple quickstart, so
        // we'll just do the bare minimum so you can continue to get a feel
        // for things.
        SecurityUtils.setSecurityManager(securityManager);

        // Now that a simple Shiro environment is set up, let's see what you can do:

        // get the currently executing user:
        // 获取当前的 Subject. 调用 SecurityUtils.getSubject();
        Subject currentUser = SecurityUtils.getSubject();
        
        // Do some stuff with a Session (no need for a web or EJB container!!!)
        // 测试使用 Session
        // 获取 Session: Subject#getSession()
        Session session = currentUser.getSession();
        session.setAttribute("someKey","aValue");
        String value = (String) session.getAttribute("someKey");
        if(value.equals("aValue")){
            logger.info("----> Retrieve the correct value ! [ {} ]",value);
        }

        // let's loggerin the current user so we can check against roles and permissions:
        // 测试当前的用户是否已经被认证. 即是否已经登录.
        // 调动 Subject 的 isAuthenticated()
        if (!currentUser.isAuthenticated()) {
            // 把用户名和密码封装为 UsernamePasswordToken 对象
            UsernamePasswordToken token = new UsernamePasswordToken("ljb","123456");
            // rememberme
            token.setRememberMe(true);
            try {
                // 执行登录.
                currentUser.login(token);
            }
            // 若没有指定的账户, 则 shiro 将会抛出 UnknownAccountException 异常.
            catch (UnknownAccountException uae) {
                logger.info("----> There is no user with username of " + token.getPrincipal());
                return;
            }
            // 若账户存在, 但密码不匹配, 则 shiro 会抛出 IncorrectCredentialsException 异常。
            catch (IncorrectCredentialsException ice) {
                logger.info("----> Password for account " + token.getPrincipal() + " was incorrect!");
                return;
            }
            // 用户被锁定的异常 LockedAccountException
            catch (LockedAccountException lae) {
                logger.info("The account for username " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.");
            }
            // ... catch more exceptions here (maybe custom ones specific to your application?
            // 所有认证时异常的父类.
            catch (AuthenticationException ae) {
                //unexpected condition?  error?
            }
        }

        //say who they are:
        //print their identifying principal (in this case, a username):
        logger.info("----> User [{}] logged in successfully ." ,currentUser.getPrincipal());

        //test a role:
        // 测试是否有某一个角色. 调用 Subject 的 hasRole 方法.
        String role = "goodguy";
        if(currentUser.hasRole(role)){
            logger.info("----> May the {} be with you!",role);
        } else {
            logger.info("----> Hello, mere mortal.");
            return;
        }

        //test a typed permission (not instance-level)
        // 测试用户是否具备某一个行为. 调用 Subject 的 isPermitted() 方法。
        String permit = "programme:*";
        if(currentUser.isPermitted(permit)){
            logger.info("----> You may use a {}.  Use it wisely.",permit);
        } else {
            logger.info("Sorry, you don't have the {} permission.",permit);
        }

        //a (very powerful) Instance Level permission:
        // 测试用户是否具备某一个行为.
        String permit_specific = "user:delete:zhangsan";
        if (currentUser.isPermitted(permit_specific)) {
            logger.info("----> You are permitted to {} ,Here are the keys - have fun!",permit_specific);
        } else {
            logger.info("Sorry, you aren't allowed to {}!",permit_specific);
        }

        //all done - logger out!
        // 执行登出. 调用 Subject 的 logout() 方法.
        logger.info("---->" + currentUser.isAuthenticated());

        currentUser.logout();

        logger.info("---->" + currentUser.isAuthenticated());

        System.exit(0);
    }
}
