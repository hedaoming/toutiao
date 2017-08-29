package zhku.peishen.toutiao.time;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import zhku.peishen.toutiao.util.JedisAdapter;

/**
 * Created by ipc on 2017/8/15.
 * 每周固定时间清除初次登录信息
 */
@Component
class ClearWeekLoginUser {
    @Autowired
    JedisAdapter jedisAdapter;


    //每个星期天24时触发  @Scheduled(cron = "0 0 24 ? * SUN")
    @Scheduled(cron = "0 0 0 ? * SUN")
    public void clear(){
        jedisAdapter.sdel("LOGIN:");
    }
}
