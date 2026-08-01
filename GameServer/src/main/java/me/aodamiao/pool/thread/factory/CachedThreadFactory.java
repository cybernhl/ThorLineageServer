package me.aodamiao.pool.thread.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述
 * 
 * @author Dragon Li
 * @created 2016年5月5日 下午5:24:56
 */
public class CachedThreadFactory implements ThreadFactory {

    private AtomicInteger poolNumber;
    private AtomicInteger threadNumber;
    private ThreadGroup group;
    private String namePrefix;
    private int threadPriority;

    public CachedThreadFactory(final String poolName, final int priority) {
        this.poolNumber = new AtomicInteger(1);
        this.threadNumber = new AtomicInteger(1);
        final SecurityManager securMgr = System.getSecurityManager();
        this.group = (securMgr != null) ? securMgr.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = new StringBuilder().append(poolName).append("-").append(this.poolNumber.getAndIncrement()).append("-Thread-").toString();
        this.threadPriority = priority;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        final String tName = new StringBuilder().append(this.namePrefix).append(this.threadNumber.getAndIncrement()).append("(").append(runnable.toString()).append(")").toString();
        final Thread thread = new Thread(this.group, runnable, tName, 0);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != this.threadPriority) {
            thread.setPriority(this.threadPriority);
        }
        return thread;
    }
}
