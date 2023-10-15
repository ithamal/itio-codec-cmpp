package io.github.ithmal.itio.codec.cmpp.sequence;

import lombok.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息流水号管理器
 * @author: ken.lin
 * @since: 2023-10-12 23:09
 */
public class SequenceManager {

    private AtomicInteger offset ;

    public SequenceManager(){
        this(0);
    }

    public SequenceManager(int initialValue){
        offset = new AtomicInteger(initialValue);
    }

    public int nextValue() {
        return offset.accumulateAndGet(1, (prev, x) -> {
            if (prev == Integer.MAX_VALUE) {
                return 1;
            } else {
                return prev + x;
            }
        });
    }

    public static void main(String[] args) {
        SequenceManager manager = new SequenceManager();
        for (int i = 0; i < 10; i++) {
            System.out.println(manager.nextValue());
        }
    }
}
