package zhku.peishen.toutiao.model;

/**
 * 封装一周站内信的信息
 * Created by ipc on 2017/8/11.
 */
public class WeekMessage {
    private int newsCount;
    private int allLike;
    private int allComment;

    public int getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(int newsCount) {
        this.newsCount = newsCount;
    }

    public int getAllLike() {
        return allLike;
    }

    public void setAllLike(int allLike) {
        this.allLike = allLike;
    }

    public int getAllComment() {
        return allComment;
    }

    public void setAllComment(int allComment) {
        this.allComment = allComment;
    }
}
