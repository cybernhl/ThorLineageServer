package com.lineage.server.thread;

import me.aodamiao.pool.thread.DefaultThreadPool;

public class NpcOnlyThreadPool extends DefaultThreadPool {
    private static final int CORE_POOL_SIZE = 100;
    private static final long KEEP_ALIVE_TIME = 60L;

    private static final NpcOnlyThreadPool INSTANCE = new NpcOnlyThreadPool();

    public static NpcOnlyThreadPool get() {
        return INSTANCE;
    }

    public NpcOnlyThreadPool() {
        super("NpcOnly", CORE_POOL_SIZE, Integer.MAX_VALUE, KEEP_ALIVE_TIME, Thread.NORM_PRIORITY);
    }
}
