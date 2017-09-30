package indi.sword.shiro.factory;

import java.util.LinkedHashMap;

public class FilterChainDefinitionMapBuilder {
    /**
     * @Decription 把appilicationContext.xml里面的配置写到这里来
     * @Author: rd_jianbin_lin
     * @Date : 2017/9/29 15:58
     */

    /*
    <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <property name="securityManager" ref="securityManager"/>
        <property name="loginUrl" value="/login.jsp"/>
        <property name="successUrl" value="/list.jsp"/>
        <property name="unauthorizedUrl" value="/unauthorized.jsp"/>

        <property name="filterChainDefinitionMap" ref="filterChainDefinitionMap"></property>

        <!--
        	配置哪些页面需要受保护.
        	以及访问这些页面需要的权限. 如果没有权限，会自动重定向到login.jsp页面
        	1). anon 可以被匿名访问 （anonymous 表示不需要登录就可以访问）
        	2). authc 必须认证(即登录)后才可能访问的页面.(authentication)
        	3). logout 登出.
        	4). roles 角色过滤器
        -->
        <!--因为用了上面的 filterChainDefinitionMap，于是就不把角色配置写在这里了。-->
        <property name="filterChainDefinitions">
            <value>
                <!--
                    [urls]部分的配置，其格式是：“url=拦截器[参数]，拦截器[参数]”
                    如果当前请求的url匹配[urls]部分的某个url模式，将会执行其配置的拦截器。
                    org.apache.shiro.web.filter.mgt.DefaultFilter 查看拦截器种类
                -->
                /login.jsp = anon
                /shiro/login = anon
                /shiro/logout = logout

                <!--测试用-->
                /shiro/testShiroAnnotation_guest = anon

                /user.jsp = roles[user]
                /admin.jsp = roles[admin]

                <!--# everything else requires authentication:
                通配符wildcard，注意通配符匹配不包括目录分隔符 “/”.
                ？:匹配一个字符 ，如 /admin?  将匹配 /admin1,但是不匹配 /admin 或者子路径 /admin/1
                * :匹配一个或者多个字符，如 /admin* 将匹配 /admin /admin1 但是不匹配子路径 /admin/1
                **:匹配路径中的零个或者多个路径 ，如 /admin** 将匹配 /admin /admin1 admin/1 admin/a/b 等等-->
                /** = authc

            </value>
        </property>
    </bean>
     */
    public LinkedHashMap<String,String> buildFactoryChainDefinationMap(){
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
/*
        map.put("login.jsp","anon");
        map.put("/shiro/login","anon");
        map.put("/shiro/logout","logout");
        map.put("/user.jsp","roles[user]");
        map.put("/admin.jsp","roles[admin]");

        map.put("/**","authc");
*/

        // rememberMe 改造
        /*
            待会用 admin去登录login.jsp ,然后你会发现，你可以进入 user.jsp admin.jsp 各个页面。
            关闭chrome浏览器
            重新开启chrrome浏览器
            复制“http://localhost:8083/shiro_sword/list.jsp”，不用登录就可以进入list.jsp，因为rememberMe了
            但是，当你点击 user.jsp 与 admin.jsp 时候，这时候跳到了login.jsp，说明你进入不了，说明，这两个页面需要授权登录
         */
        map.put("login.jsp","anon");
        map.put("/shiro/login","anon");
        map.put("/shiro/logout","logout");
        map.put("/user.jsp","authc,roles[user]"); // 需要授权登录
        map.put("/admin.jsp","authc,roles[admin]"); // 需要授权登录
        map.put("/list.jsp","user"); // user 用户拦截器，用户已经身份认证/记住我登录都可；示例：“/** = user”

        map.put("/**","authc");

        return map;
    }
}
