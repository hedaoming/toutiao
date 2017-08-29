package zhku.peishen.toutiao.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import zhku.peishen.toutiao.model.News;

import java.util.*;

/**
 * Created by ipc on 2017/8/3.
 */
@Component
public class JedisAdapter implements InitializingBean{
    private static Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    public JedisPool pool = null;
    Jedis jedis = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool();
    }

    private Jedis getJedis(){
        return pool.getResource();
    }

    public String get(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(key);
        }catch(Exception e){
            logger.error("redis操作发生异常"+e.getMessage());
            return null;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    public String set(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.set(key,value);
        }catch(Exception e){
            logger.error("redis操作发生异常"+e.getMessage());
            return null;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    public long sadd(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }catch(Exception e){
            logger.error("redis操作发生异常"+e.getMessage());
            return 0;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    public long srem(String key,String members){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key,members);
        }catch(Exception e){
            logger.error("redis操作发生异常"+e.getMessage());
            return 0;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        }catch(Exception e){
            logger.error("redis操作发生异常"+e.getMessage());
            return false;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    public long scard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch(Exception e){
            logger.error("redis操作发生异常"+e.getMessage());
            return 0;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }
    }
    public long lpush(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key,value);
        }catch(Exception e){
            logger.error("redis操作发生异常"+e.getMessage());
            return 0;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    /**
     * 删除当前键的所有内容
     * @param key 键
     * @return
     */
    public long sdel(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.del(key);
        }catch(Exception e){
            logger.error("jedis操作发生异常："+e.getMessage());
            return 0;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    /**
     * 阻塞方式取出里面的内容，空则阻塞直到有数据为止
     * @param timeout 等待时间
     * @param key
     * @return 获取的list
     */
    public List<String> brpop(int timeout, String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout,key);
        }catch(Exception e){
            logger.error("redis操作发生异常"+e.getMessage());
            return null;
        }finally{
            if(jedis!=null) {
                jedis.close();
            }
        }
    }

    /**
     * 将对象序列化为json串，保存到redis中
     * @param key
     * @param obj
     */
    public void setObject(String key,Object obj){
        set(key, JSONObject.toJSONString(obj));
    }

    public <T> T getObject(String key,Class<T> clazz){
        String value = get(key);
        if(value!=null){
            return JSONObject.parseObject(value,clazz);
        }
        return null;
    }

    /**
     * 排序后的实体
     * @param key 键
     * @param score 权重
     * @param obj 对象
     * @return
     */
    public long zadd(String key,double score,Object obj){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.zadd(key,score,JSONObject.toJSONString(obj));
        }catch(Exception e){
            logger.error("redis操作发生异常：zadd(),"+e.getMessage());
            return 0;
        }finally{
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    /**
     * 得到redis中排序后的资讯:降序
     * @param key
     * @return
     */
    public List<News> zrevrange(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            Set<String> set = jedis.zrevrange(key,0,-1);
            List<News> list = new ArrayList<News>();
            for(String str:set){
                News news = JSONObject.parseObject(str,News.class);
                list.add(news);
            }
            return list;
        }catch(Exception e){
            logger.error("redis操作异常，zrevrange(),"+e.getMessage());
            return null;
        }
    }

    /**
     * 得到指定key的所有成员：升序
     * @param key
     * @return
     */
    public List<News> zrange(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            Set<String> set = jedis.zrange(key,0,-1);
            List<News> list = new ArrayList<News>();
            for(String str:set){
                News news = JSONObject.parseObject(str,News.class);
                list.add(news);
            }
            return list;
        }catch(Exception e){
            logger.error("redis操作异常，zrange(),"+e.getMessage());
            return null;
        }
    }
    /**
     * 取出set中指定key的所有的成员
     * @param key
     * @return
     */
    public List<News> smembers(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            List<News> list = new ArrayList<News>();
            Set<String> strs = jedis.smembers(key);
            for(String str:strs){
                News news = JSONObject.parseObject(str,News.class);
                list.add(news);
            }
            return list;
        }catch(Exception e){
            logger.error("redis操作发生异常，smembers(),"+e.getMessage());
            return null;
        }
    }

}
