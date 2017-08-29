package zhku.peishen.toutiao.model;

import org.springframework.stereotype.Component;

/**
 * Created by ipc on 2017/7/16.
 * 保存登录拦截器中的User：存入当前线程中
 */

@Component
public class HostHolder {
    //每个线程只能操作自己的变量
    private static ThreadLocal<User> users  = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}

