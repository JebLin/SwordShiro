package indi.sword.shiro.realms;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashSet;
import java.util.Set;

/**
 * @Decription
 * @Author: rd_jianbin_lin
 * @Date : 2017/9/28 14:48
 */
/*
    密码
    密码的对比：
    通过AuthenticatingRealm 的 CredentialsMatcher 进行比对的

    如何把一个字符串加密为MD5？
    替换当前Realm的CredentialsMatcher 属性，直接使用HashedCredentialsMatcher 对象，并设置加密算法即可

    为什么使用MD5盐值加密？
    salt 盐值加密，虽然两个人的原始密码是一样的，但是我不想MD5加密后一样，加强保密性。就像两个人在炒菜一样，加盐量不同，出来的味道就不同。
    如何使用？
    a.在Realm 的 doGetAuthenticationInfo 方法返回值创建 SimpleAuthenticationInfo对象的时候，需要使用SimpleAuthenticationInfo(principal,credentials,credentialsSalt,realmName)构造器
    b.使用ByteSource.Util.bytes() 来计算盐值
    c.盐值需要唯一：一般使用随机字符串user_id
    d.使用new SimpleHash(hashAlgorithmName,credentials,salt,hashIterations);来计算盐值加密后的密码的值。
*/
/*
    身份验证
    •身份验证：一般需要提供如身份ID 等一些标识信息来表明登录者的身份，如提供email，用户名/密码来证明。
    •在shiro中，用户需要提供principals （身份）和credentials（证明）给shiro，从而应用能验证用户身份：
    •principals：身份，即主体的标识属性，可以是任何属性，如用户名、邮箱等，唯一即可。一个主体可以有多个principals，但只有一个Primary principals，一般是用户名/邮箱/手机号。
    •credentials：证明/凭证，即只有主体知道的安全值，如密码/数字证书等。
    •最常见的principals 和credentials 组合就是用户名/密码了

    身份验证基本流程
    •1、收集用户身份/凭证，即如用户名/密码
    •2、调用Subject.login进行登录，如果失败将得到相应的AuthenticationException异常，根据异常提示用户错误信息；否则登录成功
    •3、创建自定义的Realm 类，继承org.apache.shiro.realm.AuthorizingRealm类，实现doGetAuthenticationInfo() 方法

    身份认证流程
    •1、首先调用Subject.login(token) 进行登录，其会自动委托给SecurityManager
    •2、SecurityManager负责真正的身份验证逻辑；它会委托给Authenticator 进行身份验证；
    •3、Authenticator 才是真正的身份验证者，ShiroAPI 中核心的身份认证入口点，此处可以自定义插入自己的实现；
    •4、Authenticator 可能会委托给相应的AuthenticationStrategy进行多Realm 身份验证，默认ModularRealmAuthenticator会调用AuthenticationStrategy进行多Realm 身份验证；
    •5、Authenticator 会把相应的token 传入Realm，从Realm 获取身份验证信息，如果没有返回/抛出异常表示身份验证失败了。此处
*/
/*
    Authenticator
    •Authenticator 的职责是验证用户帐号，是ShiroAPI 中身份验证核心的入口点：如果验证成功，将返回AuthenticationInfo验证信息；此信息中包含了身份及凭证；如果验证失败将抛出相应的AuthenticationException异常
    •SecurityManager接口继承了Authenticator，另外还有一个ModularRealmAuthenticator实现，其委托给多个Realm 进行验证，验证规则通过AuthenticationStrategy接口指定

    AuthenticationStrategy
    •AuthenticationStrategy接口的默认实现：
    •FirstSuccessfulStrategy：只要有一个Realm 验证成功即可，只返回第一个Realm 身份验证成功的认证信息，其他的忽略；
    •AtLeastOneSuccessfulStrategy：只要有一个Realm验证成功即可，和FirstSuccessfulStrategy不同，将返回所有Realm身份验证成功的认证信息；
    •AllSuccessfulStrategy：所有Realm验证成功才算成功，且返回所有Realm身份验证成功的认证信息，如果有一个失败就失败了。
    •ModularRealmAuthenticator默认是AtLeastOneSuccessfulStrategy策略
 */
/*
    Realm
    •Realm：Shiro从Realm 获取安全数据（如用户、角色、权限），即SecurityManager要验证用户身份，那么它需要从Realm 获取相应的用户进行比较以确定用户身份是否合法；也需要从Realm得到用户相应的角色/权限进行验证用户是否能进行操作
    •一般继承AuthorizingRealm（授权）即可；其继承了AuthenticatingRealm（即身份验证），而且也间接继承了CachingRealm（带有缓存实现）。

    Realm 缓存
    •Shiro提供了CachingRealm，其实现了CacheManagerAware接口，提供了缓存的一些基础实现；
    •AuthenticatingRealm及AuthorizingRealm也分别提供了对AuthenticationInfo和AuthorizationInfo信息的缓存。

    CacheManagerAware接口
    •Shiro内部相应的组件（DefaultSecurityManager）会自动检测相应的对象（如Realm）是否实现了CacheManagerAware并自动注入相应的CacheManager。
*/


//public class ShiroRealm extends AuthenticatingRealm { // 这个不涉及授权，只有认证的方法
public class ShiroRealm extends AuthorizingRealm {

    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
//        System.out.println("doGetAuthenticationInfo:token.hashCode() -> " + authenticationToken.hashCode());
        System.out.println("[FirstRealm] doGetAuthenticationInfo");

        //1、把AuthenticationToken 转换为 UsernamePasswordToken
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        // 2、从 UsernamePasswordToken 里面获取 username
        String username = token.getUsername();

        // 3、调用数据库的方法，从数据库中查询 Username 对应的用户记录
        System.out.println("从数据库中获取 username : " + username + "所对应的用户信息。");

        // 4、若用户不存在，则可以抛出 UnknownAccountException 异常
        if("unknown".equals(username)){
            throw new UnknownAccountException("用户不存在");
        }

        // 5、根据用户信息，决定是否抛出其他 AuthenticationException 异常。
        if("monster".equals(username)){
            throw new LockedAccountException("用户被锁定");
        }

        // 6、根据用户的情况，来构建 AuthenticationInfo 对象并且返回，通常使用的实现类为：SimpleAuthenticationInfo
        // 以下信息是从数据库中获取的。
        // a.principal : 认证的实体信息，可以是username，也可以是数据库表对应的用户的实体类对象。
        Object principal = username;

        // b. credentials : 密码。
//        Object credentials = "fc1709d0a95a6be30bc5926fdb7f22f4"; // "123456" 这是没加盐值的
        Object credentials = null;
        if("admin".equals(username)){
            credentials = "038bdaf98f2037b31f1e75b5b4c9b26e";
        }else if("user".equals(username)){
            credentials = "098d2c478e9c11555ce2823231e02ec1";
        }

        // c. realmName : 当前的realm 对象的name，调用父类的getName() 方法即可
        String realmName = super.getName();

        // d. 盐值
        ByteSource credentialSalt = ByteSource.Util.bytes(username);

//        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal,credentials,realmName); // 这是没有加盐值的
        SimpleAuthenticationInfo info =  new SimpleAuthenticationInfo(principal,credentials,credentialSalt,realmName);

        return info;
    }

    public static void main(String[] args) {
        String hashAlgorithmName = "MD5";
        Object credentials = "123456"; // 这个是密码

        String username = "admin"; // 密码加盐 加密后的结果 038bdaf98f2037b31f1e75b5b4c9b26e
        String username2 = "user"; // 密码加盐 加密后的结果 098d2c478e9c11555ce2823231e02ec1
//        Object salt = null;  // 没加盐 123456 -> fc1709d0a95a6be30bc5926fdb7f22f4
        Object salt = ByteSource.Util.bytes(username2);
        int hashIterations = 1024; // 加密迭代次数，加密再加密再加密
        Object result = new SimpleHash(hashAlgorithmName,credentials,salt,hashIterations);
        System.out.println(result);
    }

    // 授权   会被Shiro回调的方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("doGetAuthorizationInfo...");

        // 1. 从PrincipalCollection 中获取用户登陆信息
        Object principal = principalCollection.getPrimaryPrincipal();

        // 2. 利用登录的用户信息来 获取 当前用户的权限或角色（可能需要查询数据库）
        Set<String> roles = new HashSet<>();
        roles.add("user");
        if("admin".equals(principal)){
            roles.add("admin");
        }

        // 3. 创建SimpleAuthorizationInfo ,并设置其roles属性
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roles);

        // 4. 返回SimpleAuthorizationInfo 对象

        return info;
    }
}
