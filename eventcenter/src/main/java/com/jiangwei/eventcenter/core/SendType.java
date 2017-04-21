package com.jiangwei.eventcenter.core;

/**
 * author: jiangwei18 on 17/3/30 22:18
 */

public class SendType {

    // EventCenter发送全局消息,正序发送
    public static final int TYPE_ALL = 0;
    // EventCenter发送指定消息,按照正序
    public static final int TYPE_ONE = 1;
    // EventCenter发送全局消息,倒序发送
    public static final int TYPE_TAIL_ALL = 2;
    // EventCenter发送指定消息,按照倒序
    public static final int TYPE_TAIL_ONE = 3;
    // EventCenter发送正序消息,除了这个（做拦截用）
    public static final int TYPE_BUT_ONE = 4;
    // EventCenter发送倒序消息,除了这个（做拦截用）
    public static final int TYPE_TAIL_BUT_ONE = 5;

//    // 判断是否有接收对象
//    // 正序唯一
//    public static boolean hasTypeOneTarget = false;
//    // 正序所有
//    public static boolean hasTypeAllTarget = false;
//    // 倒序唯一
//    public static boolean hasTypeTailOneTarget = false;
//    // 倒序所有
//    public static boolean hasTypeTailAllTarget = false;
}
