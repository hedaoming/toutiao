package zhku.peishen.toutiao.dao;

import org.apache.ibatis.annotations.*;
import zhku.peishen.toutiao.model.Comment;

import java.util.List;

/**
 * Created by ipc on 2017/7/31.
 */
@Mapper
public interface CommentDao {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " content,entity_type,entity_id,created_date,user_id,status ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    /**
     * 添加评论
     * @param comment 评论
     * @return
     */
    //insert into comment() values()
    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,
            ") values(#{content},#{entityType},#{entityId},#{createdDate},#{userId},#{status})"})
    int addComment(Comment comment);

    /**
     * 得到当前实体id的所有相关评论
     * @param entityId 实体id
     * @param entityType 类型
     * @return
     */
    //select * from comment where entity_id = ? and entity_type = ?
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME,
            " where entity_id=#{entityId} and entity_type=#{entityType} order by id DESC"})
    List<Comment> getCommnetByEntityId(@Param("entityId") int entityId,
                                       @Param("entityType") int entityType);

    /**
     * 获取有效评论
     * @param entityId 实体id
     * @param entityType 类型
     * @param offset 起始数据
     * @param limit 一次获取数据量数目
     * @return 数据
     */
    //SELECT * FROM COMMENT WHERE entity_id = 22 AND STATUS=0  ORDER BY created_date DESC LIMIT 0,10
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME,
            " where entity_id=#{entityId} and entity_type=#{entityType} AND STATUS=0 ORDER BY created_date DESC LIMIT #{offset},#{limit}"})
    List<Comment> getCommentByEntityIdLimit(@Param("entityId") int entityId,
                                            @Param("entityType") int entityType,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);
    /**
     * 获取评论数量
     * @param entityId 实体id
     * @param entityType 类型
     * @return
     */
    //select count(id) from comment where entity_id = ? and entity_type = ? and status=0
    @Select({"select count(id) from ",TABLE_NAME,
            " where entity_id=#{entityId} and entity_type=#{entityType} and status=0"})
    int getCommentCount(@Param("entityId") int entityId,@Param("entityType") int entityType);

    /**
     * 删除评论：实际只是修改状态
     * @param id id
     * @param entityType 类型
     * @param status 要修改成的值
     */
    @Update({"update ",TABLE_NAME," set status = #{status} where id=#{id} and entity_type=#{entityType}"})
    void updateStatus(@Param("id") int id,
                      @Param("entityType") int entityType,
                      @Param("status") int status);

    //select * from comment where id = ?
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{commentId}"})
    Comment getCommentById(@Param("commentId") int commentId);

    /**
     * 通过某实体id获得评论数量（不包括userId的评论）
     * @param userId
     * @param entityId
     * @return
     */
    //SELECT COUNT(id) FROM COMMENT WHERE user_id != 17 AND entity_id = 25
    @Select({"SELECT COUNT(id) FROM ",TABLE_NAME," WHERE user_id != #{userId} AND entity_id = #{entityId}"})
    int getCommentCountNoUser(@Param("userId") int userId,
                              @Param("entityId") int entityId);
}
