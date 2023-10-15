package io.github.ithmal.itio.codec.cmpp.store;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author: ken.lin
 * @since: 2023-10-14 15:44
 */
public interface LongSmsAssembler<T> {

    /**
     * 加入一条消息，如何满返回集合, 否则返回null
     * @param key 消息key
     * @param pkTotal 总条目
     * @param pkNumber 当前序号
     * @param msg 消息
     * @return 消息集合
     */
    List<T> put(String key, short pkTotal, short pkNumber, T msg);

    /**
     * 移除
     * @param key
     */
    void remove(String key);

    /**
     * 注册超时回调
     * @param consumer
     */
    void onTimeout(BiConsumer<String, List<T>> consumer);

    /**
     * 释放
     */
    void release();
}
