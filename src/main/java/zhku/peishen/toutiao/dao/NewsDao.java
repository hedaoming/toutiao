package zhku.peishen.toutiao.dao;

import org.apache.ibatis.annotations.*;
import zhku.peishen.toutiao.model.News;

import java.util.List;

/**
 * Created by ipc on 2017/7/9.
 */
@Mapper
public interface NewsDao {
    String TABLE_NAME = "news";
    String SELECT_FIELDS = " id,title,link,image,like_count,comment_count,created_date,user_id ";
    String INSERT_FIELDS = " title,link,image,like_count,comment_count,created_date,user_id ";

    //insert into news(title,...) values(title,...)
    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS, ") " +
            "values(#{title},#{link},#{image},#{likeCount},#{commentCount},#{createdDate},#{userId})"})
    int addNews(News news);

    List<News> selectByUserIdAndOffset(@Param("userId") int userId,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    //select * from news where id = ?
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id = #{id}"})
    News getNewsById(@Param("id") int id);

    //update comment set count=? where news_id = ?
    @Update({"update ",TABLE_NAME," set comment_count=#{commentCount} where id = #{id}"})
    int updateCommentCount(@Param("id") int id,@Param("commentCount") int commentCount);

    /**
     * 更新赞数
     * @param newsId 资讯id
     * @param likeCount 赞数
     */
    //update news set like_count = ? where id = ?
    @Update({"update ",TABLE_NAME," set like_count=#{likeCount} where id = #{newId}"})
    void updateLikeCount(@Param("newId") int newsId,
                         @Param("likeCount") int likeCount);
}
