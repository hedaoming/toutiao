package zhku.peishen.toutiao.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import zhku.peishen.toutiao.dao.LoginTicketDao;
import zhku.peishen.toutiao.dao.UserDao;
import zhku.peishen.toutiao.model.HostHolder;
import zhku.peishen.toutiao.model.LoginTicket;
import zhku.peishen.toutiao.model.User;
import zhku.peishen.toutiao.util.JedisAdapter;
import zhku.peishen.toutiao.util.WeekMessageUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by ipc on 2017/7/16.
 * 过滤器：得配置过滤器（Configuration），验证用户发送的cookie是否有效，若有效则自动登录
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketDao loginTicketDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    WeekMessageUtil weekMessageUtil;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        /*Cookie[] cookies = httpServletRequest.getCookies();
        LoginTicket ticket = new LoginTicket();
        if(cookies!=null){
            //验证此cookie是否在login_ticket中存在，有则用其userId获取User信息，并设置到当前线程全局可用（ThreadLocal）
            for(Cookie cookie : cookies){
                if(loginTicketDao.selectByTicket(cookie.getValue())!=null){
                    ticket = loginTicketDao.selectByTicket(cookie.getValue());
                    break;
                }
            }
        }
        if(ticket.getTicket()!=null){
            //ticket过期
            if(ticket.getStatus()!=0 || ticket.getExpired().before(new Date())){
                //不会往下执行，postHandle和afterCompletion也不会执行了
                hostHolder.clear();
                return true;
            }
            User user = userDao.selectById(ticket.getUserId());
            hostHolder.setUser(user);
        }
        return true;*/

        String ticket = null;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if (ticket != null) {
            LoginTicket loginTicket = loginTicketDao.selectByTicket(ticket);
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                return true;
            }

            User user = userDao.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
            //是否存在于缓存中,不存在则是第一次登录，发送消息并添加入缓存
            if(!jedisAdapter.sismember("LOGIN:",String.valueOf(user.getId()))){
                weekMessageUtil.sendWeekMeesage(user);
                jedisAdapter.sadd("LOGIN:",String.valueOf(user.getId()));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if(modelAndView!=null && hostHolder.getUser()!=null){
            modelAndView.addObject("user",hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
