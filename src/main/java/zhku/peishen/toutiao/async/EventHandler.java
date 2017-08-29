package zhku.peishen.toutiao.async;

import java.util.List;

/**
 * Created by ipc on 2017/8/5.
 */
public interface EventHandler {

    void doHandle(EventModel eventModel);

    List<EventType> getSupportEventTypes();
}
