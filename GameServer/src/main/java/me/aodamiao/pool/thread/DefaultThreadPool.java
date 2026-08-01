package me.aodamiao.pool.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import me.aodamiao.pool.thread.factory.CachedThreadFactory;
import me.aodamiao.pool.thread.factory.PriorityThreadFactory;

/**
 * 描述
 * 
 * @author Dragon Li
 * @created 2016年5月5日 下午4:34:10
 */
public class DefaultThreadPool implements ThreadPool {

    private static final int DEFAULT_CORE_POOL_SIZE = 1;
    private static final long DEFAULT_KEEP_ALIVE_TIME = 60L;

    private ScheduledExecutorService scheduler;

//    private Executor executor;
    private CustomThreadPoolExecutor executor;
//    private CustomScheThreadPoolExecutor scheduler;
    private boolean shutdown;

    /**
     * 單線程池
     * 
     * @author Dragon Li
     * @created 2018年5月20日 下午11:56:34
     * @param name
     */
    public DefaultThreadPool(final String name) {
        this(name, DEFAULT_CORE_POOL_SIZE, Integer.MAX_VALUE, DEFAULT_KEEP_ALIVE_TIME, Thread.NORM_PRIORITY);
    }

    /**
     * 線程池
     * 
     * @author Dragon Li
     * @created 2018年5月20日 下午11:54:09
     * @param name
     *            池名
     * @param corePoolSize
     *            初始線程數
     * @param maximumPoolSize
     *            最大線程數
     * @param keepAliveTime
     *            閒置線程存活時間(秒)
     * @param priority
     *            權限
     */
    public DefaultThreadPool(final String name, final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final int priority) {
        this.executor = new CustomThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new CachedThreadFactory(new StringBuilder().append("Exec-").append(name).toString(), priority));
        this.scheduler = new CustomScheThreadPoolExecutor(corePoolSize, new PriorityThreadFactory(new StringBuilder().append("Sche-").append(name).toString(), priority));
        this.shutdown = false;
    }

    @Override
    public void execute(final Runnable r) {
        if (this.executor == null) {
            final Thread t = new Thread(r);
            t.start();
        } else {
            this.executor.execute(r);
        }
    }

    @Override
    public void execute(final Thread t) {
        t.start();
    }

    @Override
    public ScheduledFuture<?> schedule(final Runnable r, final long delay) {
        if (delay <= 0) {
            this.executor.execute(r);
            return null;
        }
        return this.scheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long initialDelay, final long period) {
        return scheduler.scheduleAtFixedRate(r, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {
        synchronized (this) {
            if (!this.shutdown) {
                this.executor.shutdown();
                this.scheduler.shutdown();
                this.shutdown = true;
            }
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        synchronized (this) {
            if (!this.shutdown) {
                final List<Runnable> remainList = new ArrayList<Runnable>();
                remainList.addAll(this.executor.shutdownNow());
                remainList.addAll(this.scheduler.shutdownNow());
                this.shutdown = true;
                return remainList;
            }
        }
        return null;
    }
}
