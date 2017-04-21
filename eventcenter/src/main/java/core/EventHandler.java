package core;

import static core.SendType.TYPE_BUT_ONE;
import static core.SendType.TYPE_ONE;
import static core.SendType.TYPE_TAIL_BUT_ONE;
import static core.SendType.TYPE_TAIL_ONE;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

/**
 * author: jiangwei18 on 17/3/30 20:12 email: jiangwei18@baidu.com Hi: jwill金牛
 */

public class EventHandler extends Handler {
    private static String TAG = "EventCenter";

    private WeakReference<Context> weakContext;

    private ArrayList<Class<? extends Event>> messages = new ArrayList<>();
    // 封装目标handler下所有回调方法
    private SparseArray<Method> callBacksMethod = new SparseArray<>();
    // 注册的事件
    private Set<Class<? extends Event>> mEvents = new LinkedHashSet<>();

    private SparseArray<Class<? extends Event>> eventCache = new SparseArray<>();

    private int handlerId;

    private boolean isEventInterface = false;

    public int getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(int handlerId) {
        this.handlerId = handlerId;
    }

    public EventHandler(Context context) {
        weakContext = new WeakReference<>(context);
        Class clazz = getClass();
        while (clazz != null && clazz != EventHandler.class) {
            clazz = clazz.getSuperclass();
        }
        if (clazz != EventHandler.class) {
            // 所有handler都要直接或间接继承EventHandler
            throw new IllegalStateException("class must extends EventHandler");
        }
        Class clazz2 = getClass();
        while (clazz2 != EventHandler.class) {
            Class[] judgeInterfaces = clazz2.getInterfaces();
            if (judgeInterfaces == null || judgeInterfaces.length == 0) {
                throw new IllegalStateException("class must implements Interface");
            }
            for (int i = 0; i < judgeInterfaces.length; i++) {
                Class[] interfaces1 = judgeInterfaces[i].getInterfaces();
                // EventOn接口必须继承Event接口
                if (interfaces1 == null) {
                    throw new IllegalStateException("EventInterface can't be null");
                }
                if (interfaces1.length != 1) {
                    // EventOn接口只能有继承Event接口,不允许其他
                    throw new IllegalStateException("EventInterface only can extends Event Interface");
                }
                if (interfaces1[0] == Event.class) {
                    isEventInterface = true;
                }
            }
            // handler实现的所有接口都有event
            if (!isEventInterface) {
                throw new IllegalStateException("class must implements at least Event Interface");
            }
            for (Class event : judgeInterfaces) {
                if (!event.getSimpleName().startsWith("EventOn")) {
                    throw new IllegalStateException("interface 's name needs starts with EventOn");
                }
                messages.add(event);
            }

            clazz2 = clazz2.getSuperclass();
        }
        System.out.println("1111"+messages.size());
        if (messages.size() != 0) {
            for (Class<? extends Event> event : messages) {
                Method[] methods = event.getMethods();
                mEvents.add(event);
                int id = EventCenter.getInstance().requestMessageQueue(event);
                eventCache.put(id, event);
                if (methods != null && methods.length != 0) {
                    Method method = methods[0];
                    if (!method.getName().startsWith("on")) {
                        throw new IllegalStateException("void must start with on: onLoad....");
                    }
                    callBacksMethod.put(id, method);
                } else {
                    throw new IllegalStateException("can't find method to callBack");
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        Method method = callBacksMethod.get(msg.what);
        Object result = null;
        if (method != null) {
            try {
                result = method.invoke(this, (Object[]) msg.obj);
                Log.d(TAG, "Class:" + getClass().getSimpleName() + "---Method:" + method.getName() + "---"
                        + "handlerId:" + getHandlerId() + "---success");
            } catch (Exception e) {
                if (e.getMessage().contains("Wrong number of arguments")) {
                    throw new IllegalStateException("args:Wrong number of arguments");
                } else {
                    e.printStackTrace();
                }
            }
            // 如果方法返回boolean值,可以判断被不被拦截
            if (result != null && result instanceof Boolean && (msg.arg1 == TYPE_ONE) || msg.arg1 == TYPE_TAIL_ONE
                    || msg.arg1 == TYPE_BUT_ONE || msg.arg1 == TYPE_TAIL_BUT_ONE) {
                boolean isInterrupt = (boolean) result;
                // FALSE 不拦截
                Object[] a = (Object[]) msg.obj;
                if (!isInterrupt && (msg.arg1 == TYPE_ONE || msg.arg1 == TYPE_BUT_ONE)) {
                    // 按照正序下发
                    EventCenter.getInstance().send(eventCache.get(msg.what), getHandlerId(), TYPE_BUT_ONE, a);
                } else if (msg.arg1 == TYPE_TAIL_ONE || msg.arg1 == TYPE_TAIL_BUT_ONE) {
                    // 按照倒序下发
                    EventCenter.getInstance().send(eventCache.get(msg.what), getHandlerId(), TYPE_TAIL_BUT_ONE, a);
                }
            }
        } else {
            throw new IllegalStateException("can't find method,have you declared a right id??");
        }
    }

    public void register() {
        if (mEvents.size() != 0) {
            EventCenter.getInstance().register(mEvents, this);
        } else {
            throw new IllegalStateException("event null");
        }
    }

    public void unRegister() {
        if (mEvents.size() != 0) {
            EventCenter.getInstance().unRegister(mEvents, this);
        } else {
            throw new IllegalStateException("event null");
        }
    }

    public Context getContext() {
        if (weakContext != null) {
            Context context = weakContext.get();
            return context;
        }
        return null;
    }

}
