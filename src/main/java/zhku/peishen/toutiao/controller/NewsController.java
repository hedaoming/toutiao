package zhku.peishen.toutiao.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zhku.peishen.toutiao.async.EventModel;
import zhku.peishen.toutiao.async.EventProducer;
import zhku.peishen.toutiao.async.EventType;
import zhku.peishen.toutiao.model.*;
import zhku.peishen.toutiao.service.*;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.RedisKeyUtil;
import zhku.peishen.toutiao.util.ToutiaoUtil;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

/**
 * Created by ipc on 2017/7/29.
 */
@Controller
public class NewsController {

    private static Logger logger = LoggerFactory.getLogger(NewsController.class);
    @Autowired
    private NewsService newsService;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private JedisAdapter jedisAdapter;

    /**
     * 添加评论
     * @param newsId 对应资讯id
     * @param content 评论内容
     * @return 刷新页面（重定向）
     */
    @RequestMapping(path = {"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setUserId(hostHolder.getUser().getId());
            comment.setEntityId(newsId);
            comment.setStatus(0);
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            commentService.addComment(comment);
            //更新news的评论数量
            int count = commentService.getCommentCount(newsId,EntityType.ENTITY_NEWS);
            newsService.updateCommentCount(newsId,count);
            //发送站内信:消息队列
            News news = newsService.getNewsById(newsId);
            eventProducer.fireEvent(new EventModel(EventType.COMMENT)
                    .setActorId(hostHolder.getUser().getId())
                    .setEntityOwnerId(news.getUserId())
                    .setExt("newsName",news.getTitle())
                    .setExt("userName",hostHolder.getUser().getName())
                    .setExt("commentTime",comment.getCreatedDate().toString()));

        }catch(Exception e){
            logger.error("添加评论失败"+e.getMessage());
        }
        return "redirect:/news/"+String.valueOf(newsId);
    }

    /**
     * 资讯详情
     * @param newId 资讯的id
     * @param model 设置数据，前台访问
     * @return 跳转到detail.html
     */
    @RequestMapping(path = {"/news/{newsId}"},method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newId,Model model){
        try{
            News news = newsService.getNewsById(newId);
            User owner = userService.getUser(news.getUserId());
            //评论信息
            if(news !=null ){
                List<Comment> comments =  commentService.getCommentByEntityIdLimit(news.getId(), EntityType.ENTITY_NEWS,0,10);
                List<ViewObject> commentVos = new ArrayList<ViewObject>();
                for(Comment comment:comments){
                    ViewObject commentvo = new ViewObject();
                    commentvo.set("comment",comment);
                    commentvo.set("user",userService.getUser(comment.getUserId()));
                    commentVos.add(commentvo);
                }
                model.addAttribute("comments",commentVos);
                //用户是否登录，若登录，则查询对该资讯的赞踩状态
                int localUserId = hostHolder.getUser().getId();
                if(localUserId!=0){
                    model.addAttribute("like",likeService.getLikeStatus(news.getId(), EntityType.ENTITY_NEWS,localUserId));
                }else{
                    model.addAttribute("like",0);
                }
            }
            //设置资讯信息
            model.addAttribute("news",news);
            //设置资讯发布者
            model.addAttribute("owner",owner);
            model.addAttribute("localUser",hostHolder.getUser());
        }catch(Exception e){
            logger.error("查找咨询详情错误"+e.getMessage());

        }
        return "detail";
    }

    /**
     * 添加资讯
     * @param image 图片连接
     * @param title 标题
     * @param link 分享连接
     * @return json格式的结果
     */
    @RequestMapping(path = {"/user/addNews/"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link){
        try{
            News news = new News();
            if(image == null){
                return ToutiaoUtil.getJSONString(1,"请上传图片");
            }
            if(title == null){
                return ToutiaoUtil.getJSONString(1,"标题不能为空");
            }

            if(link == null){
                return ToutiaoUtil.getJSONString(1,"链接不能为空");
            }
            news.setImage(image);
            news.setTitle(title);
            news.setLink(link);
            news.setCreatedDate(new Date());
            //是否是登录用户新增的分享
            if(hostHolder.getUser()!=null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                //匿名用户
                news.setUserId(3);
            }
            newsService.addNews(news);
            //修改状态：表明资讯表内容已经改变
            ToutiaoUtil.midCount++;
            return ToutiaoUtil.getJSONString(0);
        }catch(Exception e){
            logger.error("添加咨询失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发布失败");
        }

    }

    /**
     * 图片下载
     * @param fileName 图片名
     * @param response 输出图片到浏览器
     */
    @RequestMapping(path = {"/image"},method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("fileName") String fileName,
                         HttpServletResponse response){
        try{
            response.setContentType("image/jpeg");
            String filePath = ToutiaoUtil.IMAGE_PATH+fileName;
            File file = new File(filePath);
            StreamUtils.copy(new FileInputStream(file),response.getOutputStream());
        }catch(Exception e){
            logger.error("下载图片失败"+fileName+e.getMessage());
        }
    }

    /**
     * 图片上传
     * @param file 图片
     * @return 图片上传成功后的链接
     */
    @RequestMapping(path={"/uploadImage/"},method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){

        try{
            String fileUrl = qiniuService.uploadImage(file);
            if(fileUrl==null){
                return ToutiaoUtil.getJSONString(1,"图片上传失败");
            }
            return ToutiaoUtil.getJSONString(0,fileUrl);
        }catch(Exception e){
            logger.error("图片上传失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"上传失败");
        }
    }

    /**
     * 删除评论
     * @param commentId
     * @param newsId
     * @param localUserId
     * @return
     */
    @RequestMapping(path = {"/news/delComment"},method = {RequestMethod.GET,RequestMethod.POST})
    public String delComment(@RequestParam("commentId") int commentId,
                             @RequestParam("newsId") int newsId,
                             @RequestParam("localUserId") int localUserId){

        try{
            Comment comment = commentService.getCommentById(commentId);
            if(comment.getUserId()==hostHolder.getUser().getId()){
                commentService.deleteComment(commentId,EntityType.ENTITY_NEWS);
                //更新评论数
                int count = commentService.getCommentCount(newsId,EntityType.ENTITY_NEWS);
                newsService.updateCommentCount(newsId,count);
            }else{
                logger.error("不是自己发表的评论，不能删除");
            }

        }catch(Exception e){
            logger.error("删除评论失败:"+e.getMessage());
        }
        return "redirect:/news/"+newsId;
    }

    /**
     * 跳转到热门资讯
     * @param model
     * @return
     */
    @RequestMapping(path = "/news/hot",method = {RequestMethod.GET,RequestMethod.POST})
    public String newsHot(Model model){
        try{
            List<News> list = jedisAdapter.zrevrange(RedisKeyUtil.getNewsRankKey());
            List<ViewObject> vos = new ArrayList<ViewObject>();
            for(News news:list){
                ViewObject vo = new ViewObject();
                vo.set("news",news);
                vo.set("user",userService.getUser(news.getUserId()));
                //用户是否登录，若登录，则查询对该资讯的赞踩状态
                int localUserId = hostHolder.getUser()==null?0:hostHolder.getUser().getId();
                if(localUserId!=0){
                    int likeStatus = likeService.getLikeStatus(news.getId(), EntityType.ENTITY_NEWS,localUserId);
                    vo.set("like",likeStatus);
                }else{
                    vo.set("like",0);
                }
                vos.add(vo);
            }
            model.addAttribute("vos",vos);
        }catch(Exception e){
            logger.error("获取热门信息失败，"+e.getMessage());
        }
        return "home";
    }
}
