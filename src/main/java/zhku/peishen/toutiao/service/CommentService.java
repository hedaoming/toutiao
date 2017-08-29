package zhku.peishen.toutiao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhku.peishen.toutiao.dao.CommentDao;
import zhku.peishen.toutiao.model.Comment;

import java.util.List;

/**
 * Created by ipc on 2017/7/31.
 */
@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    public int addComment(Comment comment){
        return commentDao.addComment(comment);
    }

    public List<Comment> getCommentByEntityId(int entityId,int entityType){
        return commentDao.getCommnetByEntityId(entityId,entityType);
    }

    /**
     * 按时间降序并分页
     * @param entityId 实体id
     * @param entityType 类型
     * @param offset 起始数据
     * @param limit 一次获取数据量数目
     * @return 数据
     */
    public List<Comment> getCommentByEntityIdLimit(int entityId,int entityType,int offset,int limit){
        return commentDao.getCommentByEntityIdLimit(entityId,entityType,offset,limit);
    }

    public int getCommentCount(int entityId,int entityType){
        return commentDao.getCommentCount(entityId,entityType);
    }

    /**
     * 删除评论
     * @param id id
     * @param entityType 类型
     */
    public void deleteComment(int id,int entityType){
        commentDao.updateStatus(id,entityType,1);
    }

    public Comment getCommentById(int commentId) {
        return commentDao.getCommentById(commentId);
    }

    /**
     * 通过某实体id获得评论数量（不包括userId的评论）
     * @param userId
     * @param entityId
     * @return
     */
    public int getCommentCountNoUser(int userId,int entityId){
        return commentDao.getCommentCountNoUser(userId,entityId);
    }
}

