package zhku.peishen.toutiao.async.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhku.peishen.toutiao.async.EventHandler;
import zhku.peishen.toutiao.async.EventModel;
import zhku.peishen.toutiao.async.EventType;
import zhku.peishen.toutiao.model.Message;
import zhku.peishen.toutiao.service.MessageService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by ipc on 2017/8/10.
 */
@Component
public class WeekMessageHandler implements EventHandler{

    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel eventModel) {
        int fromId = 1;
        int toId = eventModel.getEntityId();
        String newsCount = eventModel.getExt("newsCount");
        String likeCount = eventModel.getExt("likeCount");
        String commentCount = eventModel.getExt("commentCount");
        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setCreatedDate(new Date());
        message.setContent("系统通知：您过去一周发表的资讯数是："+newsCount+"条，收到的评论数是："+commentCount+"条，收到的赞数是："+likeCount+"个");
        message.setConversationId(fromId+"_"+toId);
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.WEEKMESSAGE);
    }
}
