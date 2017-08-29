package zhku.peishen.toutiao.async;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.RedisKeyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * Created by ipc on 2017/8/5.
 * 一直在运行：其中有while死循环一直在执行事件
 * 从redis队列中取出消息--》判断是哪一类型--》调用对应的Handler处理
 */
@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{

    private static Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    private ApplicationContext applicationContext;
    private Map<EventType,List<EventHandler>> config = new HashMap<>();

    /**
     * 处理消费事件：执行消息队列中的事件
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans!=null){
            //遍历出map内的内容
            for(Map.Entry<String,EventHandler> entry:beans.entrySet()){
                List<EventType> types = entry.getValue().getSupportEventTypes();
                for(EventType eventType:types){
                    if(!config.containsKey(eventType)){
                        //初始化
                        config.put(eventType,new ArrayList<EventHandler>());
                    }
                    //注册每个事件的处理函数
                    config.get(eventType).add(entry.getValue());
                }
            }
        }


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> messages = jedisAdapter.brpop(0,key);
                    for(String message : messages){
                        if(message.equals(key)){
                            continue;
                        }
                        EventModel eventModel = JSONObject.parseObject(message,EventModel.class);
                        if(!config.containsKey(eventModel.getEventType())){
                            logger.error("不能识别的事件");
                            continue;
                        }
                        for(EventHandler handler : config.get(eventModel.getEventType())){
                            handler.doHandle(eventModel);
                        }

                    }
                }
            }
        });
        thread.start();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
