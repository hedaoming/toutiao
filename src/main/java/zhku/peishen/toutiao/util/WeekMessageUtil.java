package zhku.peishen.toutiao.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import zhku.peishen.toutiao.async.EventModel;
import zhku.peishen.toutiao.async.EventProducer;
import zhku.peishen.toutiao.async.EventType;
import zhku.peishen.toutiao.model.User;
import zhku.peishen.toutiao.model.WeekMessage;
import zhku.peishen.toutiao.service.WeekMessageService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by ipc on 2017/8/11.
 */
@Component
public class WeekMessageUtil{

    @Autowired
    WeekMessageService weekMessageService;

    @Autowired
    EventProducer eventProducer;


    private static Logger logger = LoggerFactory.getLogger(WeekMessageUtil.class);

    /**
     * 应该开启守护线程，或者多线程之类，
     * 1. 然后一直在运行
     * 2. 时间一到就添加事件进入消息队列
     * 3. 对所有用户进行站内信发送：
     *      3.1 应该是对已登录用户，当用户登录了，且此时是这周的第一次登录，就会发送站内信，这样才不会对所有用户都进行发送，节省资源
     *          每次用户登录都确认是否第一次：通过一个线程局部变量ThreadLocal
     *                 引起内存泄漏：无法解决啊；可以修改简单模式，用一个变量维护登录次数
     *      3.2 要不就邮箱方式，邮箱就是对所有用户都发送的
     *
     */



    public void sendWeekMeesage(User user){
        try{
                if(user != null){
                    List<WeekMessage> list = weekMessageService.getWeekMessages(user.getId());
                    WeekMessage weekMessage = list.get(0);
                    //此周有资讯
                    if(weekMessage!=null){
                        if(weekMessage.getAllComment()==0 && weekMessage.getAllLike()==0 && weekMessage.getNewsCount()==0){

                        }else{
                            eventProducer.fireEvent(new EventModel(EventType.WEEKMESSAGE)
                                    .setEntityId(user.getId())
                                    .setExt("newsCount",String.valueOf(weekMessage.getNewsCount()))
                                    .setExt("likeCount",String.valueOf(weekMessage.getAllLike()))
                                    .setExt("commentCount",String.valueOf(weekMessage.getAllComment())));
                        }
                    }
                }
        }catch(Exception e){
            logger.error("发送周站内信失败"+e.getMessage());
        }
    }

}

