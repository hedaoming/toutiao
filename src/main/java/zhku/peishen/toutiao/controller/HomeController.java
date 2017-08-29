package zhku.peishen.toutiao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import zhku.peishen.toutiao.model.*;
import zhku.peishen.toutiao.service.LikeService;
import zhku.peishen.toutiao.service.NewsService;
import zhku.peishen.toutiao.service.UserService;
import zhku.peishen.toutiao.service.WeekMessageService;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.RedisKeyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ipc on 2017/7/9.
 */
@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    JedisAdapter jedisAdapter;


    /**
     * 获取资讯信息并包装在ViewObject中
     * @param userId 用户id
     * @param offset 分页起始
     * @param limit 每页显示数据数
     * @return 包装了ViewObject的List
     */
    private List<ViewObject> getViewObject(int userId,int offset,int limit){
        //List<News> listNews = newsService.getLastNews(userId,offset,limit);
        //从缓存中获取热门资讯
        //List<News> listNews = jedisAdapter.zrevrange(RedisKeyUtil.getNewsRankKey());
        //从缓存中获取最新资讯
        List<News> listNews = newsService.getLastNewsFromRedis();
        int localUserId = hostHolder.getUser()==null?0:hostHolder.getUser().getId();
        List<ViewObject> vos = new ArrayList<ViewObject>();
        for(News news : listNews){
            ViewObject vo = new ViewObject();
            vo.set("news",news);
            vo.set("user", userService.getUser(news.getUserId()));
            //用户是否登录，若登录，则查询对该资讯的赞踩状态
            if(localUserId!=0){
                int likeStatus = likeService.getLikeStatus(news.getId(), EntityType.ENTITY_NEWS,localUserId);
                vo.set("like",likeStatus);
            }else{
                vo.set("like",0);
            }
            vos.add(vo);//存储引用
        }
        return vos;
    }

    /**
     * 首页加载资讯
     * @param model 设置数据
     * @param pop
     * @return 首页
     */
    @RequestMapping(value = {"/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop",defaultValue = "0") int pop){
        List<ViewObject> vos = getViewObject(0,0,10);
        model.addAttribute("vos",vos);
        model.addAttribute("pop",pop);
        return "home";
    }

    /*@RequestMapping("/testDate")
    public String test(Model model){
        List<ViewObject> vos = getViewObject(0,0,10);
        model.addAttribute("vos",vos);
        return "news";
    }*/
}
