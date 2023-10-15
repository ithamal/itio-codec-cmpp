package io.github.ithmal.itio.codec.cmpp.store;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * @author: ken.lin
 * @since: 2023-10-14 16:27
 */
public class MemoryLongSmsAssembler<T> implements LongSmsAssembler<T> {

    private static final ScheduledExecutorService cleanExecutorService = Executors.newScheduledThreadPool(1);

    private final ConcurrentHashMap<String, MessageEntry<T>> concurrentHashMap = new ConcurrentHashMap<>();

    private ScheduledFuture<?> cleanTaskFuture;

    private BiConsumer<String, List<T>> timeoutCallback;

    public MemoryLongSmsAssembler(int timeout) {
        cleanTaskFuture = cleanExecutorService.scheduleAtFixedRate(() -> {
            int timeoutMills = timeout * 1000;
            if (timeoutCallback == null) {
                return;
            }
            List<String> removeKeys = new ArrayList<>();
            // 遍历缓存
            concurrentHashMap.forEach((key, item) -> {
                // 计算超时
                long timeDiffMills = System.currentTimeMillis() - item.getAfterAccessAt();
                // 通知超时
                if (timeDiffMills > timeoutMills) {
                    timeoutCallback.accept(key, item.getList());
                }
                // 延迟5秒删除
                else if (timeDiffMills > timeoutMills + 5000) {
                    removeKeys.add(key);
                }
            });
            for (String removeKey : removeKeys) {
                concurrentHashMap.remove(removeKey);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public List<T> put(String key, short pkTotal, short pkNumber, T msg) {
        MessageEntry<T> entry = concurrentHashMap.computeIfAbsent(key, k -> new MessageEntry<>(k, pkTotal));
        if (entry.appendAndIsFull(pkNumber, msg)) {
            List<T> list = entry.getList();
            return list;
        } else {
            return null;
        }
    }

    @Override
    public void remove(String key) {
        concurrentHashMap.remove(key);
    }

    @Override
    public void onTimeout(BiConsumer<String, List<T>> consumer) {
        this.timeoutCallback = consumer;
    }

    @Override
    public void release() {
        cleanTaskFuture.cancel(true);
    }

    private static class MessageEntry<T> {

        private final String key;

        private final Object[] items;

        private long afterAccessAt;

        public MessageEntry(String key, int total) {
            this.key = key;
            this.items = new Object[total];
            this.afterAccessAt = System.currentTimeMillis();
        }

        public synchronized boolean appendAndIsFull(short pkNumber, T msg) {
            items[pkNumber - 1] = msg;
            afterAccessAt = System.currentTimeMillis();
            for (int i = 0; i < items.length; i++) {
                if (items[i] == null) {
                    return false;
                }
            }
            return true;
        }

        public String getKey() {
            return key;
        }

        public long getAfterAccessAt() {
            return afterAccessAt;
        }

        public List<T> getList() {
            List<T> list = new ArrayList<>(items.length);
            for (Object item : items) {
                if (item != null) {
                    list.add((T) item);
                }
            }
            return list;
        }
    }
}
