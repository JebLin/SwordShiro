package indi.sword.shiro.util;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.Session;

import java.io.*;

/**
 * @Decription 对象序列化工具
 * @Author: rd_jianbin_lin
 * @Date : 2017/9/29 21:21
 */
public class SerializableUtils {

    /**
     * @Decription 对象序列化
     * @Author: rd_jianbin_lin
     * @Date : 2017/9/29 21:23
     */
    public static String serialize(Session session){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(session);
            return Base64.encodeToString(bos.toByteArray());
        } catch (IOException e) {
            throw  new RuntimeException("Serialize session error ",e);
        }
    }

    /**
     * @Decription 对象反序列化
     * @Author: rd_jianbin_lin
     * @Date : 2017/9/29 21:29
     */
    public static Session deserialize(String sessionStr){
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(sessionStr));
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Session)ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("deserialize session error",e);
        }
    }
}
