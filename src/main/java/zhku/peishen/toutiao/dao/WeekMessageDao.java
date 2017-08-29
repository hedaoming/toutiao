package zhku.peishen.toutiao.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import zhku.peishen.toutiao.model.WeekMessage;

import java.util.List;

/**
 * Created by ipc on 2017/8/11.
 */
@Mapper
public interface WeekMessageDao {

    String TABLE_NAME = "news";

    /**
     * 获取该用户每周收到的赞数，评论数，发表的资讯数
     * @return 封装信息
     */
    @Select({"SELECT COUNT(id) AS news_count,SUM(like_count) AS all_like,SUM(comment_count) AS all_comment FROM " +
            "(SELECT * FROM ",TABLE_NAME," WHERE TIMESTAMPDIFF(DAY,created_date,NOW())<7 AND user_id=#{userId}) tt"})
    List<WeekMessage> getWeekMessages(@Param(("userId")) int userId);

}
