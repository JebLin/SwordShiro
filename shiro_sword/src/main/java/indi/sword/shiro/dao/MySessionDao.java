package indi.sword.shiro.dao;

import indi.sword.shiro.util.SerializableUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * @Decription
 * @Author: rd_jianbin_lin
 * @Date : 2017/9/29 21:17
 */
/*
    SessionDao
    •AbstractSessionDAO提供了SessionDAO的基础实现，如生成会话ID等
    •CachingSessionDAO提供了对开发者透明的会话缓存的功能，需要设置相应的CacheManager
    •MemorySessionDAO直接在内存中进行会话维护
    •EnterpriseCacheSessionDAO提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
 */
/*
    Session 缓存
    •如SecurityManager实现了SessionSecurityManager，其会判断SessionManager是否实现了CacheManagerAware接口，如果实现了会把CacheManager设置给它。
    •SessionManager也会判断相应的SessionDAO（如继承自CachingSessionDAO）是否实现了CacheManagerAware，如果实现了会把CacheManager设置给它
    •设置了缓存的SessionManager，查询时会先查缓存，如果找不到才查数据库。
 */
/*
    数据表
    •create table sessions (
    •id varchar(200),
    •session varchar(2000),
    •constraint pk_sessionsprimary key(id)
    •) charset=utf8 ENGINE=InnoDB;
 */
public class MySessionDao extends EnterpriseCacheSessionDAO {

//    @Autowired
    private JdbcTemplate jdbcTemplate; //如果打开注入的话呢，需要去applicationContext.xml 里面配置 jdbcTemplate

    public MySessionDao() {
    }

    // 插入
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);

        String sql = "insert into sessions(id,session) values (?,?)";
        jdbcTemplate.update(sql, sessionId, SerializableUtils.serialize(session));
        return session.getId();
    }

    // 查找
    @Override
    protected Session doReadSession(Serializable sessionId) {

        String sql = "select session from sessions where id =?";
        List<String> sessionStrList = jdbcTemplate.queryForList(sql, String.class, sessionId);
        if (sessionStrList.size() == 0) {
            return null;
        }
        return SerializableUtils.deserialize(sessionStrList.get(0));
    }

    // 更新
    @Override
    protected void doUpdate(Session session) {
        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            return;
        }
        String sql = "update sessions  set session = ? where id =? ";
        jdbcTemplate.update(sql, SerializableUtils.serialize(session), session.getId());
    }

    // 删除
    @Override
    protected void doDelete(Session session) {
        String sql = "delete from sessions where id=?";
        jdbcTemplate.update(sql, session.getId());
    }
}
