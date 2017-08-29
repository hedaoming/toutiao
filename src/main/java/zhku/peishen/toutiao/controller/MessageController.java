package zhku.peishen.toutiao.controller;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import zhku.peishen.toutiao.model.HostHolder;
import zhku.peishen.toutiao.model.Message;
import zhku.peishen.toutiao.model.User;
import zhku.peishen.toutiao.model.ViewObject;
import zhku.peishen.toutiao.service.MessageService;
import zhku.peishen.toutiao.service.UserService;
import zhku.peishen.toutiao.util.ToutiaoUtil;

import javax.swing.text.html.ObjectView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ipc on 2017/8/1.
 */
@Controller
public class MessageController {

    private static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 消息列表
     * @param model 设置数据
     * @return 消息页面
     */
    @RequestMapping(path = {"/msg/list"},method = {RequestMethod.GET,RequestMethod.POST})
    public String conversationList(Model model){
        try{
            //已登录用户信息
            User user = hostHolder.getUser();
            //与用户关联的消息
            List<Message> messageList = messageService.getConversationList(user.getId(),0,10);
            List<ViewObject> messageVOs = new ArrayList<ViewObject>();
            for(Message message:messageList){
                ViewObject vo = new ViewObject();
                vo.set("conversation",message);
                User targetUser =
                        message.getToId() ==
                                user.getId()?userService.getUser(message.getFromId()):
                                userService.getUser(message.getToId());
                vo.set("userId",targetUser.getId());
                vo.set("headUrl",targetUser.getHeadUrl());
                vo.set("userName",targetUser.getName());
                vo.set("unreadCount",messageService.getUnreadCount(user.getId(),message.getConversationId()));
                messageVOs.add(vo);

            }
            model.addAttribute("conversations",messageVOs);
        }catch(Exception e){
            logger.error("获取站内信异常"+e.getMessage());
        }
        return "letter";
    }
    /**
     * 消息详情
     * @param conversationId 对话id
     * @param model 数据放置地，前端获取
     * @return detail页面
     */
    @RequestMapping(path = {"/msg/detail"},method = {RequestMethod.GET,RequestMethod.POST})
    public String conversationDetail(@RequestParam("conversationId") String conversationId,Model model){
        try{
            List<Message> messages= messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messageVOs = new ArrayList<>();
            for(Message message:messages){
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                vo.set("userId",message.getFromId());
                vo.set("headUrl",userService.getUser(message.getFromId()).getHeadUrl());
                messageVOs.add(vo);
            }
            model.addAttribute("messages",messageVOs);
            messageService.updateHasRead(conversationId);
        }catch(Exception e){
            logger.error("获取消息详情失败"+e.getMessage());
        }
        return "letterDetail";
    }

    /**
     * 添加消息
     * @param content 消息内容
     * @param fromId 哪个用户发出
     * @param toId  发往哪个用户
     * @return 成功则返回消息id
     */
    @RequestMapping(path = {"/msg/addMessage"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String addMessage(@Param("content") String content,
                             @Param("fromId") int fromId,
                             @Param("toId") int toId){
        try{
            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setContent(content);
            message.setFromId(fromId);
            message.setToId(toId);
            message.setConversationId(fromId>toId?
                    String.format("%d_%d",toId,fromId):String.format("%d_%d",fromId,toId));
            messageService.addMessage(message);
            return ToutiaoUtil.getJSONString(message.getId());
        }catch(Exception e){
            logger.error("添加消息失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"添加失败");
        }
    }

    /**
     * 删除站内信
     * @param id 站内信id
     * @param conversationId 对话id，以便重定向
     * @return
     */
    @RequestMapping(path = {"/msg/detail/del"},method = {RequestMethod.POST,RequestMethod.GET})
    public String delMessage(@RequestParam("id") int id,
                             @RequestParam("conversationId") String conversationId){
        try{
            messageService.delMessage(id);
        }catch(Exception e){
            logger.error("删除消息失败："+e.getMessage());
        }
        return "redirect:/msg/detail?conversationId="+conversationId;
    }
}
