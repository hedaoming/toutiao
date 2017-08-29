package zhku.peishen.toutiao.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import zhku.peishen.toutiao.model.News;
import zhku.peishen.toutiao.model.ViewObject;
import zhku.peishen.toutiao.service.CommentService;
import zhku.peishen.toutiao.service.NewsService;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.RedisKeyUtil;
import zhku.peishen.toutiao.util.ToutiaoUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ipc on 2017/8/15.
 * 固定时间间隔：
 *      ① 对资讯进行排序
 *      ② 更新进redis（原先数据要清除）
 */
@Component
public class NewsRank {

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    NewsService newsService;

    @Autowired
    CommentService commentService;

    private static Logger logger = LoggerFactory.getLogger(NewsRank.class);

    int midCount = 1;
        @Scheduled(cron = "30 * * * * *")
        public void rankNews(){
            try{
                //最新内容改变了，则更新缓存
                if(midCount!= ToutiaoUtil.midCount){
                    //1. 清除已有缓存中的资讯内容
                    jedisAdapter.sdel(RedisKeyUtil.getNewsRankKey());
                    //List<News> newsList = newsService.getLastNews(0,0,20);
                    //1.1 从缓存中获取最新数据
                    List<News> newsList  = newsService.getLastNewsFromRedis();
                    //2. 取出每条新闻：计算score，并设置入redis缓存
                    for(News news:newsList){
                        int likeCount = news.getLikeCount();
                        //排除自己后的评论数
                        int commentCount = commentService.getCommentCountNoUser(news.getUserId(),news.getId());
                        Date date = news.getCreatedDate();
                        double h = (double)(System.currentTimeMillis()-date.getTime())/(1000*60*60);
                        double p = Math.pow(likeCount+500,0.12)-1.1;
                        double x = Math.pow(h+2,1.8);
                        double score = (commentCount+1)*p/x;
                        jedisAdapter.zadd(RedisKeyUtil.getNewsRankKey(),score,news);
                    }
                    midCount = ToutiaoUtil.midCount;
                }

            }catch(Exception e){
                logger.error("排序错误：rankNews（）"+e.getMessage());
            }
        }
}
