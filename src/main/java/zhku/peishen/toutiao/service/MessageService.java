package zhku.peishen.toutiao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhku.peishen.toutiao.dao.MessageDao;
import zhku.peishen.toutiao.model.Message;

import java.util.List;

/**
 * Created by ipc on 2017/8/1.
 */
@Service
public class MessageService {

    @Autowired
    private MessageDao messageDao;

    /**
     * 添加消息
     * @param message 消息
     * @return
     */
    public void addMessage(Message message){
        messageDao.addMessage(message);
    }

    /**
     * 通过conversationId来获取双方之间的信息
     * @param conversationId 对话id
     * @param offset 分页起始
     * @param limit 查询多少条
     * @return 消息集合
     */
    public List<Message> getConversationDetail(String conversationId,int offset,int limit){
        return messageDao.getConversationDetail(conversationId,offset,limit);
    }

    /**
     * 获取与用户有关的所有消息
     * @param userId 用户id
     * @param offset 分页起始参数
     * @param limit 每页显示数据数
     * @return 消息集合
     */
    public List<Message> getConversationList(int userId,int offset,int limit){
        return messageDao.getConversationList(userId, offset, limit);
    }

    /**
     * 未读消息id：发送给我的消息
     * @param toId 我的id，也就是别人发送给我的
     * @param conversationId 对话id
     * @return  数量
     */
    public int getUnreadCount(int toId,String conversationId){
        return messageDao.getUnreadCount(toId,conversationId);
    }

    /**
     * 标记为已读信息
     * @param conversationId 对话id
     */
    public void updateHasRead(String conversationId) {
        messageDao.updateHasRead(conversationId);
    }

    public void delMessage(int id) {
        messageDao.delMessage(id);
    }
}
