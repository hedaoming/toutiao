package zhku.peishen.toutiao.util;

import org.springframework.stereotype.Component;

/**
 * Created by ipc on 2017/8/3.
 * 赞的key名称
 */
public class RedisKeyUtil {
    private static String SPILT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENT = "EVENT";
    private static String NEWS_RANK = "NEWSRANK";
    private static String NEWS_LAST = "NEWSLAST";

    public static String getLastNewsKey(){
        return NEWS_LAST;
    }

    public static String getNewsRankKey(){
        return NEWS_RANK;
    }

    public static String getLikeKey(int entityId,int entityType){
        return BIZ_LIKE+SPILT+String.valueOf(entityType)+SPILT+String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityId,int entityType){
        return BIZ_DISLIKE+SPILT+String.valueOf(entityType)+SPILT+String.valueOf(entityId);
    }

    public static String getEventQueueKey(){
        return BIZ_EVENT;
    }
}
