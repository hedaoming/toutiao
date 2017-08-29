package zhku.peishen.toutiao.async.handler;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhku.peishen.toutiao.async.EventHandler;
import zhku.peishen.toutiao.async.EventModel;
import zhku.peishen.toutiao.async.EventType;
import zhku.peishen.toutiao.model.Message;
import zhku.peishen.toutiao.service.MessageService;
import zhku.peishen.toutiao.util.MailSender;

import java.util.*;

/**
 * Created by ipc on 2017/8/6.
 */
@Service
public class LoginExceptionHandler implements EventHandler {

    private static Logger logger = LoggerFactory.getLogger(LoginExceptionHandler.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private MailSender mailSender;
    @Override
    public void doHandle(EventModel eventModel) {
        try{
            //发送站内信通知
            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setToId(eventModel.getActorId());
            message.setFromId(1);
            message.setContent("您上次的登录ip有异常");
            message.setConversationId("1_"+eventModel.getActorId());
            messageService.addMessage(message);
            //发送邮件通知
            Map<String,Object> map = new HashMap<String,Object>();
            String name = eventModel.getExt("name");
            map.put("name",name);
            String to = eventModel.getExt("to");
            mailSender.sendWithHTMLTemplate(to,"登录异常","mail/loginException.html",map);
        }catch(Exception e){
            logger.error("消息队列：处理登录异常"+e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
