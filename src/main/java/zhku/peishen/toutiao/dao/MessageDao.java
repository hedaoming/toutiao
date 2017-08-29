package zhku.peishen.toutiao.dao;

import org.apache.ibatis.annotations.*;
import zhku.peishen.toutiao.model.Message;

import java.util.List;

/**
 * Created by ipc on 2017/8/1.
 */
@Mapper
public interface MessageDao {
    String TABLE_NAME = "message";
    String INSERT_FILEDS = " from_id,to_id,content,created_date,has_read,conversation_id ";
    String SELECT_FILEDS = " id,"+INSERT_FILEDS;

    /**
     * 添加消息
     * @param message 消息
     * @return
     */
    //insert into message() values()
    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FILEDS,
            ") values(#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);


    /**
     * 通过conversationId来获取双方之间的信息
     * @param conversationId 对话id
     * @param offset 分页起始
     * @param limit 查询多少条
     * @return 消息集合
     */
    //select * from message where conversation_id = ? order by id desc limit ？,?
    @Select({"select ",SELECT_FILEDS," from ",TABLE_NAME,
            " where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    /**
     * 获取与用户有关的所有消息
     * @param userId 用户id
     * @param offset 分页起始参数
     * @param limit 每页显示数据数
     * @return 消息集合
     */
    @Select({"SELECT ",INSERT_FILEDS ,",COUNT(id) AS id FROM ( select ",SELECT_FILEDS,
            " FROM message WHERE to_id=#{userId} ORDER BY id DESC) tt " +
                    " GROUP BY conversation_id ORDER BY created_date DESC LIMIT #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    /**
     * 未读消息id：发送给我的消息
     * @param toId 我的id，也就是别人发送给我的
     * @param conversationId 对话id
     * @return  数量
     */
    //select count(id) from message where to_id=15 and has_read=0 and conversation_id='7_15';
    @Select({"select count(id) from ",TABLE_NAME," where to_id=#{toId} and has_read=0 and conversation_id=#{conversationId}"})
    int getUnreadCount(@Param("toId") int toId,@Param("conversationId") String conversationId);


    /**
     * 标记为已读信息
     * @param conversationId 对话id
     */
    //update message set has_read = 1 where conversation_id = ?
    @Update({"update ",TABLE_NAME," set has_read = 1 where conversation_id=#{conversationId}"})
    void updateHasRead(@Param("conversationId") String conversationId);

    //delect from message where id = ?
    @Delete({"delete from ",TABLE_NAME," where id = #{id}"})
    void delMessage(@Param("id") int id);
}
