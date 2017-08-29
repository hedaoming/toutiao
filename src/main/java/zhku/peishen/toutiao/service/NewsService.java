package zhku.peishen.toutiao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import zhku.peishen.toutiao.dao.NewsDao;
import zhku.peishen.toutiao.model.News;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.RedisKeyUtil;
import zhku.peishen.toutiao.util.ToutiaoUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Created by ipc on 2017/7/9.
 */
@Service
public class NewsService {
    @Autowired
    private NewsDao newsDao;

    @Autowired
    private JedisAdapter jedisAdapter;

    public List<News> getLastNews(int userId,int offset,int limit){
        return newsDao.selectByUserIdAndOffset(userId,offset,limit);
    }

    /**
     *
     *
     * @param file
     * @return 失败时为null；成功时为域名+文件名
     * @throws IOException
     */
    public String uploadImage(MultipartFile file) throws IOException{
        //1. 校验是否是图片
        int pos = file.getOriginalFilename().lastIndexOf(".");
        if(pos<0){
            return null;
        }
        //文件后缀名
        String fileExt = file.getOriginalFilename().substring(pos+1).toLowerCase();
        if(!ToutiaoUtil.isAllowImage(fileExt)){
            return null;
        }

        //2. 保存到服务器
        String fileName = UUID.randomUUID().toString().replace("-","")+"."+fileExt;
        Files.copy(file.getInputStream(),
                new File(ToutiaoUtil.IMAGE_PATH+fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOMAIN+fileName;

    }

    public int addNews(News news) {
        newsDao.addNews(news);
        return news.getId();
    }

    public News getNewsById(int newId) {
        return newsDao.getNewsById(newId);
    }

    public int updateCommentCount(int newsId, int count) {
        return newsDao.updateCommentCount(newsId,count);
    }

    /**
     * 更新赞数
     * @param newsId 资讯id
     * @param likeCount 赞数
     */
    public void updateLikeCount(int newsId, int likeCount) {
        newsDao.updateLikeCount(newsId,likeCount);
    }


    /**
     * 从redis中获取最新的资讯
     * @return
     */
    public List<News> getLastNewsFromRedis(){
        return jedisAdapter.zrange(RedisKeyUtil.getLastNewsKey());
    }
}
