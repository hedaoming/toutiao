package zhku.peishen.toutiao.controller;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import zhku.peishen.toutiao.async.EventModel;
import zhku.peishen.toutiao.async.EventProducer;
import zhku.peishen.toutiao.async.EventType;
import zhku.peishen.toutiao.async.handler.LikeHandler;
import zhku.peishen.toutiao.model.EntityType;
import zhku.peishen.toutiao.model.HostHolder;
import zhku.peishen.toutiao.model.News;
import zhku.peishen.toutiao.model.User;
import zhku.peishen.toutiao.service.LikeService;
import zhku.peishen.toutiao.service.NewsService;
import zhku.peishen.toutiao.util.ToutiaoUtil;

/**
 * Created by ipc on 2017/8/3.
 */
@Controller
public class LikeController {

    private static Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;
    /**
     * 点赞
     * @param newsId 用户id
     * @return 成功则返回总赞数目，失败则返回信息
     */
    @RequestMapping(path = {"/like"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newsId") int newsId){
        try{
            User user = hostHolder.getUser();
            if(user!=null){
                long likeCount = likeService.like(newsId, EntityType.ENTITY_NEWS,user.getId());
                //更新数据库数据
                newsService.updateLikeCount(newsId,(int)likeCount);
                News news = newsService.getNewsById(newsId);
                //添加到消息队列：给资讯发布方通知
                eventProducer.fireEvent(new EventModel(EventType.LIKE)
                        .setEntityOwnerId(news.getUserId())
                        .setActorId(hostHolder.getUser().getId())
                        .setEntityId(newsId));
                return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));
            }

        }catch(Exception e){
            logger.error("点赞异常"+e.getMessage());
        }
        return ToutiaoUtil.getJSONString(1,"点赞失败");
    }

    /**
     * 踩功能
     * @param newsId 用户id
     * @return 成功则返回总赞数目，失败则返回失败信息
     */
    @RequestMapping(path = {"/dislike"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("newsId") int newsId){
        try{
            User user = hostHolder.getUser();
            if(user!=null){
                long likeCount = likeService.dislike(newsId,EntityType.ENTITY_NEWS,user.getId());
                newsService.updateLikeCount(newsId,(int)likeCount);
                return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));
            }
        }catch(Exception e){
            logger.error("踩异常"+e.getMessage());
        }
        return ToutiaoUtil.getJSONString(1,"踩失败");
    }
}
