package zhku.peishen.toutiao.controller;

import com.sun.deploy.net.HttpResponse;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zhku.peishen.toutiao.async.EventModel;
import zhku.peishen.toutiao.async.EventProducer;
import zhku.peishen.toutiao.async.EventType;
import zhku.peishen.toutiao.model.User;
import zhku.peishen.toutiao.service.UserService;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.ToutiaoUtil;
import zhku.peishen.toutiao.util.WeekMessageUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by ipc on 2017/7/12.
 */
@Controller
public class LoginController {

    private final static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;



    //注册功能
    @RequestMapping(path = "/reg",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String register(@RequestParam(value="username") String username,
                           @RequestParam(value="password") String password,
                           @RequestParam(value = "rember",defaultValue = "0") int rememberme,
                           HttpServletResponse response){
        try{
            Map<String,Object> map = userService.reg(username,password);
            //增加成功
            if(map.containsKey("ticket")){
                //将ticket加入到cookie中，发到客户端
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                if(rememberme > 0){
                    //设置5天
                    cookie.setMaxAge(3600*24*5);
                }
                //设置cookie有效路径为整个项目
                cookie.setPath("/");
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0,"注册成功");
            }
            return ToutiaoUtil.getJSONString(1,map);
        }catch(Exception e){
            log.error("注册异常"+e.getMessage());
            e.printStackTrace();
            return ToutiaoUtil.getJSONString(1,"注册异常");
        }

    }

    /**
     * 登录
     * @param username  用户名
     * @param password 密码
     * @param rememberme 是否记住我：ticket有效时间增大
     * @param response 设置ticket（加入Cookie中）
     * @return
     */
    @RequestMapping(path = "/login",method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember",defaultValue="0") int rememberme,
                        HttpServletResponse response){
        try{
            Map<String ,Object> map = userService.login(username,password);
            if(map.containsKey("ticket")){
                //设置cookie
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme>0){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                User user = (User) map.get("user");

                //登录异常：通过异步队列发送信息
                /*
                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setActorId(user.getId())
                        .setExt("name",user.getName())
                        .setExt("to","peishenzhao@gmail.com"));
                 */
                 return ToutiaoUtil.getJSONString(0,"登录成功");
            }else{
                return ToutiaoUtil.getJSONString(1,map);
            }
        }catch(Exception e){
            log.error("登录异常"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"登录异常");
        }

    }
    //登出功能
    @RequestMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }

}
