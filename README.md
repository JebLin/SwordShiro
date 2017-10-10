# SwordShiro

Apache Shiro 是 JAVA的一个安全（权限）框架
Shiro可以非常容易的开发出足够好的应用，其不仅可以用在JAVA SE 环境，也可以用在JAVA EE环境中。
Shiro可以完成：认证、授权、加密、会话管理、与Web集成、缓存等等。

下载：http://shiro.apache.org/

<hr />

|Shiro的 | 基本功能点 |
| -------------------| --------------------|
| Authentication | Authorization |
| SessionManagement | Cryptography |
| web Support | Caching |
| Concurrency、Testing | "Run As" 、Remember me| 

```
Authentication ：身份认证 / 登录 ，验证用户是不是拥有相应的身份；
Authorization ：授权，即权限验证。验证某个已认证的用户是否拥有某个权限；即判断用户是否能进行什么操作，如：验证某个用户是否拥有某个角色。或者 颗粒度的验证某个用户对某个资源是否具有某个权限。
Session Manager :会话管理，即用户登录后就是一次会话，在没有退出之前，它的所有信息都在会话中，会话可以是普通的 JAVA SE 环境，也可以是WEB 环境。
cryptography [krɪpˈɑ:grəfi]: 加密，保护数据的安全性，如密码加密存储到数据库，而不是明文存储；
web Support : Web支持，可以非常容易的集成到Web环境。
Caching ： 缓存，比如用户登录后，其用户信息、拥有的角色/权限不必每次去查，这样可以提高效率。
Concurrency : Shiro支持多线程应用的并发验证，即如在一个线程中开启另一个线程，能把权限自动传播过去；
Testing ： 提供测试支持；
Run As  :  允许一个用户假装为另一个用户（如果他们允许）的身份进行访问；
Remember me： 记住我，记住我的登录状态。一次登录下次就不用登录了。
```
<hr />
### Shrio 架构:
Application Code --> Subject (the current "user") --> Shiro SecurityManager (manages all subjects) --> Realm ( access your security data)

```
Subject : 应用代码直接交互的对象就是Subject，也就是说 Shiro的对外 API核心就是Subject。Subject 代表了当前“用户”，这个用户不一定是一个具体的人，与当前应用进行交互的任何东西都是Subject，如网络爬虫，机器人等；与Subject的所有交互都会委托给 SecurityManager;Subject 其实是一个门面，SecurityManager才是实际的执行者。
SecurityManager ： 安全管理器；即所有与安全有关的操作都会与SecurityManager交互；且其管理着所有的Subject；可以看出它是Shiro的核心，它负责与Shiro的其他组件进行交互，它相当于 Spring MVC中的DispatcherServlet角色.
Realm ： Shiro 从Realm 获取安全数据（如 用户、角色、权限），就是说SecurityManager要验证用户身份，那么它需要从Realm获取相应的用户进行比较以确定用户身份是否合法；也需要从Realm得到用户相应的角色/ 权限进行验证用户是否能进行操作；可以把Realm看成DataSource.
```


<hr />
### Shiro 认证思路分析：
```
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

```
<hr />
###　URL配置细节：
```
配置哪些页面需要受保护.
以及访问这些页面需要的权限. 如果没有权限，会自动重定向到login.jsp页面
1). anon 可以被匿名访问 （anonymous 表示不需要登录就可以访问）
2). authc 必须认证(即登录)后才可能访问的页面.(authentication)
3). logout 登出.
4). roles 角色过滤器

[urls]部分的配置，其格式是：“url=拦截器[参数]，拦截器[参数]”
如果当前请求的url匹配[urls]部分的某个url模式，将会执行其配置的拦截器。
URL权限采用第一次匹配优先的方式，即从头开始使用第一个匹配的url模式对应的拦截器链
如：
- /bb/** = filter1
- /bb/aa = filfer2
- /** = filter3
- 如果请求的url是"bb/aa",因为按照声明顺序进行匹配，那么将使用filter1进行拦截

<!--
通配符wildcard，注意通配符匹配不包括目录分隔符 “/”.
？:匹配一个字符 ，如 /admin?  将匹配 /admin1,但是不匹配 /admin 或者子路径 /admin/1
* :匹配一个或者多个字符，如 /admin* 将匹配 /admin /admin1 但是不匹配子路径 /admin/1
**:匹配路径中的零个或者多个路径 ，如 /admin** 将匹配 /admin /admin1 admin/1 admin/a/b 等等-->

```

###　密码
```
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

```


