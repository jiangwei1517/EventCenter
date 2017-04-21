package core;

import static core.SendType.TYPE_ALL;
import static core.SendType.TYPE_BUT_ONE;
import static core.SendType.TYPE_ONE;
import static core.SendType.TYPE_TAIL_ALL;
import static core.SendType.TYPE_TAIL_BUT_ONE;
import static core.SendType.TYPE_TAIL_ONE;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.support.annotation.NonNull;

/**
 * author: jiangwei18 on 17/3/30 20:03 email: jiangwei18@baidu.com Hi: jwill金牛
 */

public class EventCenter {
    private static String TAG = "EventCenter";
    // 全局的EventCenter,单例模式返回
    private static volatile EventCenter eventCenter = new EventCenter();
    // 一个Class类对应一个EventEntity实体
    private ConcurrentHashMap<Class<? extends Event>, EventEntity> eventCache = new ConcurrentHashMap<>();
    // ids 分发各类id 1:EventEntity id 2:handler id
    private AtomicInteger ids = new AtomicInteger(0);

    private static class EventEntity {
        // EventEntity的唯一标识
        private int classId;
        // classHandlers 对应Class所有的对象
        private CopyOnWriteArrayList<EventHandler> classHandlers;
    }

    private EventCenter() {

    }

    public void register(@NonNull Set<Class<? extends Event>> events, @NonNull EventHandler handler) {
        if (events.size() == 0) {
            return;
        }
        for (Class<? extends Event> event : events) {
            EventEntity eventEntity = eventCache.get(event);
            if (eventEntity == null) {
                continue;
            }
            int newHandlerId = ids.getAndIncrement();
            handler.setHandlerId(newHandlerId);
            eventEntity.classHandlers.add(handler);
        }
    }

    public void unRegister(@NonNull Set<Class<? extends Event>> events, @NonNull EventHandler handler) {
        if (events.size() == 0) {
            return;
        }
        for (Class<? extends Event> event : events) {
            EventEntity eventEntity = eventCache.get(event);
            if (eventEntity == null) {
                continue;
            }
            boolean remove = eventEntity.classHandlers.remove(handler);
            if (!remove) {
                throw new IllegalStateException("register must before unRegister");
            }
        }
    }

    private void initEntity(Class<? extends Event> event, EventEntity eventEntity) {
        eventEntity.classId = ids.getAndIncrement();
        eventEntity.classHandlers = new CopyOnWriteArrayList<>();
        eventCache.put(event, eventEntity);
    }

    /**
     *
     * @param event
     * @param targetHandlerId TYPE_ALL或TYPE_TAIL_ALL通知所有时候,此参数无关
     * @param sendType
     * @param objs
     */
    public void send(@NonNull Class<? extends Event> event, int targetHandlerId, int sendType, Object...objs) {
        EventEntity eventEntity = eventCache.get(event);
        if (!event.getSimpleName().startsWith("EventOn")) {
            throw new IllegalStateException("interface 's name needs starts with EventOn");
        }
        if (eventEntity == null) {
            eventEntity = new EventEntity();
            initEntity(event, eventEntity);
        }
        int size = eventEntity.classHandlers.size();
        if (size == 0) {
            throw new IllegalStateException("not found any handlers of the event,please register first");
        }
        if (sendType == TYPE_ONE) {
            // 定向传递
            for (EventHandler handler : eventEntity.classHandlers) {
                if (handler.getHandlerId() == targetHandlerId) {
                    android.os.Message message = handler.obtainMessage();
                    message.what = eventEntity.classId;
                    message.arg1 = sendType;
                    // obj 可以为空
                    message.obj = objs;
                    handler.sendMessage(message);
                }
            }
        } else if (sendType == TYPE_ALL) {
            for (EventHandler handler : eventEntity.classHandlers) {
                android.os.Message message = handler.obtainMessage();
                message.what = eventEntity.classId;
                message.arg1 = TYPE_ALL;
                // obj 可以为空
                message.obj = objs;
                handler.sendMessage(message);
            }
        } else if (sendType == TYPE_TAIL_ALL) {
            for (int index = size - 1; index >= 0; index--) {
                EventHandler handler = eventEntity.classHandlers.get(index);
                android.os.Message message = handler.obtainMessage();
                message.what = eventEntity.classId;
                message.arg1 = TYPE_ALL;
                // obj 可以为空
                message.obj = objs;
                handler.sendMessage(message);
            }
        } else if (sendType == TYPE_TAIL_ONE) {
            for (int index = size - 1; index >= 0; index--) {
                EventHandler handler = eventEntity.classHandlers.get(index);
                if (handler.getHandlerId() == targetHandlerId) {
                    android.os.Message message = handler.obtainMessage();
                    message.what = eventEntity.classId;
                    message.arg1 = sendType;
                    // obj 可以为空
                    message.obj = objs;
                    handler.sendMessage(message);
                }
            }
        } else if (sendType == TYPE_BUT_ONE) {
            for (EventHandler handler : eventEntity.classHandlers) {
                if (handler.getHandlerId() > targetHandlerId) {
                    android.os.Message message = handler.obtainMessage();
                    message.what = eventEntity.classId;
                    message.arg1 = TYPE_BUT_ONE;
                    // obj 可以为空
                    message.obj = objs;
                    handler.sendMessage(message);
                    break;
                }
            }
        } else if (sendType == TYPE_TAIL_BUT_ONE) {
            for (EventHandler handler : eventEntity.classHandlers) {
                if (handler.getHandlerId() < targetHandlerId) {
                    android.os.Message message = handler.obtainMessage();
                    message.what = eventEntity.classId;
                    message.arg1 = TYPE_TAIL_BUT_ONE;
                    // obj 可以为空
                    message.obj = objs;
                    handler.sendMessage(message);
                    break;
                }
            }
        }
    }

    public int requestMessageQueue(@NonNull Class<? extends Event> event) {
        EventEntity eventEntity = eventCache.get(event);
        if (eventEntity == null) {
            eventEntity = new EventEntity();
            initEntity(event, eventEntity);
        }
        return eventEntity.classId;
    }

    public static EventCenter getInstance() {
        return eventCenter;
    }
}
