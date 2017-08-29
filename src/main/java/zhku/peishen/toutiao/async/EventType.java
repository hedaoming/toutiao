package zhku.peishen.toutiao.async;

/**
 * Created by ipc on 2017/8/5.
 * 事件的类型
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    WEEKMESSAGE(4);

    private int value;

    EventType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
