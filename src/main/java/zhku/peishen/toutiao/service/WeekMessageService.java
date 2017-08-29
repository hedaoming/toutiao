package zhku.peishen.toutiao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhku.peishen.toutiao.dao.WeekMessageDao;
import zhku.peishen.toutiao.model.WeekMessage;

import java.util.List;

/**
 * Created by ipc on 2017/8/11.
 */
@Service
public class WeekMessageService {

    @Autowired
    private WeekMessageDao weekMessageDao;


    /**
     * 获取该用户每周收到的赞数，评论数，发表的资讯数
     * @return 封装信息
     */
    public List<WeekMessage> getWeekMessages(int userId) {
        return weekMessageDao.getWeekMessages(userId);
    }


}
