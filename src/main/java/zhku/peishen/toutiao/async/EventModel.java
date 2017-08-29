package zhku.peishen.toutiao.async;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ipc on 2017/8/5.
 * 每个事件的通用模型：包装事件的数据
 * actorId:操作者id
 * entityId:通用实体id
 * entityType:通用实体类型
 * entityOwnerId:拥有者id，即是需要通知的对象
 */

public class EventModel {
    private EventType eventType;
    private int actorId;
    private int entityId;
    private int entityType;
    private int entityOwnerId;
    private Map<String,String> exts = new HashMap<String,String>();

    public EventModel(){}

    public EventModel(EventType eventType){
        this.eventType = eventType;
    }

    public Map<String,String> getExts(){
        return exts;
    }

    public EventModel setExt(String name,String value){
        exts.put(name,value);
        return this;
    }

    public String getExt(String name){
        return exts.get(name);
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }
}
