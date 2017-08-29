package zhku.peishen.toutiao.async.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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
public class CommentHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        int toId = eventModel.getEntityOwnerId();
        int fromId = eventModel.getActorId();
        message.setToId(toId);
        message.setFromId(fromId);
        message.setCreatedDate(new Date());
        String title = eventModel.getExt("newsName");
        String userName = eventModel.getExt("userName");
        String commentTime = eventModel.getExt("commentTime");
        message.setContent("用户："+userName+"在"+commentTime+"评论了你的资讯名为"+title+"的资讯");
        message.setConversationId(toId<fromId?
                String.format("%d_%d",toId,fromId):String.format("%d_%d",fromId,toId));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT);
    }
}
