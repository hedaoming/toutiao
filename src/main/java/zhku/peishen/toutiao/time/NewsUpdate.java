package zhku.peishen.toutiao.time;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zhku.peishen.toutiao.model.News;
import zhku.peishen.toutiao.service.NewsService;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.RedisKeyUtil;
import zhku.peishen.toutiao.util.ToutiaoUtil;

import java.util.List;

/**
 * Created by ipc on 2017/8/16.
 * 固定时间更新redis中的news
 */
@Component
public class NewsUpdate {

    private static Logger logger = LoggerFactory.getLogger(NewsUpdate.class);

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    NewsService newsService;

    int midCount = 1;

    @Scheduled(cron = "0 * * * * *")
    public void updateNews(){
        try{
            //数据库内容改变，更新redis内容
            if(midCount != ToutiaoUtil.midCount){
                //1. 清除旧缓存
                jedisAdapter.sdel(RedisKeyUtil.getLastNewsKey());
                List<News> list = newsService.getLastNews(0,0,20);
                //2. 设置新缓存
                for(int i = 0;i<list.size();i++){
                    jedisAdapter.zadd(RedisKeyUtil.getLastNewsKey(),i,list.get(i));
                }
                midCount = ToutiaoUtil.midCount;
            }
        }catch(Exception e){
            logger.error("更新redis最新资讯内容出错,updataNews(),"+e.getMessage());
        }
    }
}
