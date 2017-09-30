package indi.sword.shiro.realms;

import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;

/**
 * @Decription
 * @Author: rd_jianbin_lin
 * @Date : 2017/9/28 15:18
 */
public class SecondRealm extends AuthenticatingRealm {

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
//        System.out.println("doGetAuthenticationInfo:token.hashCode() -> " + authenticationToken.hashCode());
        System.out.println("[SecondRealm] doGetAuthenticationInfo");

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
        String hashAlgorithmName = "SHA1";
        Object credentials = "123456"; // 这个是密码

        String username = "admin"; // 密码加盐 加密后的结果 038bdaf98f2037b31f1e75b5b4c9b26e
        String username2 = "user"; // 密码加盐 加密后的结果 098d2c478e9c11555ce2823231e02ec1
//        Object salt = null;  // 没加盐 123456 -> fc1709d0a95a6be30bc5926fdb7f22f4
        Object salt = ByteSource.Util.bytes(username2);
        int hashIterations = 1024; // 加密迭代次数，加密再加密再加密
        Object result = new SimpleHash(hashAlgorithmName,credentials,salt,hashIterations);
        System.out.println(result);
    }
}
