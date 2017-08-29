package zhku.peishen.toutiao.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhku.peishen.toutiao.dao.LoginTicketDao;
import zhku.peishen.toutiao.dao.UserDao;
import zhku.peishen.toutiao.model.LoginTicket;
import zhku.peishen.toutiao.model.User;
import zhku.peishen.toutiao.util.ToutiaoUtil;

import java.util.*;

/**
 * Created by ipc on 2017/7/9.
 */
//加入IoC容器中
@Service
public class UserService {

    //从IoC中获取对象
    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginTicketDao loginTicketDao;

    public User getUser(int id){
        return userDao.selectById(id);
    }

    //注册方法：验证数据以及加强密码
    public Map<String,Object> reg(String username, String password) {
        //存储信息
        Map<String,Object> map = new HashMap<String,Object>();
        //验证用户名和密码的合法性
        if(StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }
        //用户名存在
        if(userDao.selectByName(username)!=null){
            map.put("msgUserExist","用户名存在");
            return map;
        }else{
            Random random = new Random();
            User user = new User();
            user.setName(username);
            String salt = UUID.randomUUID().toString().substring(0,5);
            user.setSalt(salt);
            //MD５加密（有password和salt组成的密码）
            user.setPassword(ToutiaoUtil.MD5(password+salt));
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            userDao.addUser(user);

            String ticket = addLoginTicket(user.getId());
            map.put("ticket",ticket);

            return map;
        }
    }

    //登录方法：添加ticket
    public Map<String,Object> login(String username,String password){
        Map<String,Object> map = new HashMap<String,Object>();
        if(StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空!!!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user = userDao.selectByName(username);

        if(user==null){
            map.put("msgname","用户名不存在");
            return map;
        }

        //验证密码是否与用户名匹配
        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd","密码错误");
            return map;
        }

        //为登录用户生成一个ticket标志
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        //为登录异步队列准备数据
        map.put("user",user);
        return map;

    }

    //增加ticket
    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-",""));
        Date date = new Date();
        //1000天
        date.setTime(date.getTime() + 1000*3600*24);
        loginTicket.setExpired(date);
        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    //登出功能
    public void logout(String ticket) {
        loginTicketDao.updateStatus(ticket,1);
    }
}
