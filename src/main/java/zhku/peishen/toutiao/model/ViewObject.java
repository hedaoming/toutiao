package zhku.peishen.toutiao.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ipc on 2017/7/9.
 */
//定义一个前台接受数据的对象，所有数据，比如news，user可以作为value形式存储进来
//获取时，就     $!{vo.user.userId}
public class ViewObject {
    private Map<String,Object> objs = new HashMap<String,Object>();

    public void set(String key,Object value){
        objs.put(key,value);
    }

    public Object get(String key){
        return objs.get(key);
    }
}
