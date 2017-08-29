package zhku.peishen.toutiao.async.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhku.peishen.toutiao.async.EventHandler;
import zhku.peishen.toutiao.async.EventModel;
import zhku.peishen.toutiao.async.EventType;
import zhku.peishen.toutiao.model.HostHolder;
import zhku.peishen.toutiao.model.Message;
import zhku.peishen.toutiao.model.User;
import zhku.peishen.toutiao.service.MessageService;
import zhku.peishen.toutiao.service.UserService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by ipc on 2017/8/5.
 */
@Component
public class LikeHandler implements EventHandler{

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService  messageService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        User user = userService.getUser(eventModel.getActorId());
        message.setCreatedDate(new Date());
        message.setFromId(eventModel.getActorId());
        //message.setToId(eventModel.getActorId());
        message.setToId(eventModel.getEntityOwnerId());
        message.setContent("用户："+
                user.getName()+
                " 赞了你的资讯，http://localhost:8080/news/"+
                String.valueOf(eventModel.getEntityId()));
        int fromId = eventModel.getActorId();
        int toId = eventModel.getEntityOwnerId();
        message.setConversationId(fromId>toId?
                String.format("%d_%d",toId,fromId):String.format("%d_%d",fromId,toId));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
