package zhku.peishen.toutiao.async;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.RedisKeyUtil;

/**
 * Created by ipc on 2017/8/5.
 *
 */
@Service
public class EventProducer {

    private static Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 添加到消息队列：将事件序列化，存储到redis中
     * @param model 事件
     * @return 是否添加成功
     */
    public boolean fireEvent(EventModel model){
        try{
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, JSONObject.toJSONString(model));
            return true;
        }catch(Exception e){
            logger.error("EventProducer发生异常："+e.getMessage());
            return false;
        }
    }
}
