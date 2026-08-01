package me.aodamiao.pool.thread.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述
 * 
 * @author Dragon Li
 * @created 2016年5月5日 下午5:24:09
 */
public class PriorityThreadFactory implements ThreadFactory {

    private String groupName;
    private ThreadGroup group;
    private AtomicInteger threadNumber;
    private int threadPriority;

    public PriorityThreadFactory(final String groupName, final int threadPriority) {
        this.groupName = groupName;
        this.group = new ThreadGroup(groupName);
        this.threadNumber = new AtomicInteger(1);
        this.threadPriority = threadPriority;
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread t = new Thread(this.group, r);
        t.setName(new StringBuilder().append(this.groupName).append("-").append(this.threadNumber.getAndIncrement()).toString());
        t.setPriority(this.threadPriority);
        return t;
    }
}
