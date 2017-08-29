package zhku.peishen.toutiao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.RedisKeyUtil;

/**
 * Created by ipc on 2017/8/3.
 */
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 获取用户对该实体的喜欢状态
     * @param entityId 实体id
     * @param entityType 实体类型
     * @param userId 用户id
     * @return 1则喜欢，-1则不喜欢，0则没有设置状态
     */
    public int getLikeStatus(int entityId,int entityType,int userId){
        boolean like = jedisAdapter.sismember(RedisKeyUtil.getLikeKey(entityId,entityType),String.valueOf(userId));
        if(like){
            return 1;
        }
        boolean disLike = jedisAdapter.sismember(RedisKeyUtil.getDisLikeKey(entityId,entityType),String.valueOf(userId));
        if(disLike){
            return -1;
        }else{
            return 0;
        }
    }

    /**
     * 将userId加入到喜欢的集合中
     * @param entityId
     * @param entityType
     * @param userId
     * @return 喜欢的人数
     */
    public long like(int entityId,int entityType,int userId){
        //添加到喜欢集合
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        //从反对集合中删除
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityId,entityType);
        jedisAdapter.srem(dislikeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    /**
     * 将userId加入到不喜欢的集合中
     * @param entityId
     * @param entityType
     * @param userId
     * @return 喜欢的人数
     */
    public long dislike(int entityId,int entityType,int userId){
        //添加到反对集合
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityId,entityType);
        jedisAdapter.sadd(dislikeKey,String.valueOf(userId));
        //从喜欢集合中删除
        String likeKey = RedisKeyUtil.getLikeKey(entityId,entityType);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }
}
