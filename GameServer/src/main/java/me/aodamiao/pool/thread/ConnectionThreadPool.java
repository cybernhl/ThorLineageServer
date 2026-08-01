package me.aodamiao.pool.thread;

public class ConnectionThreadPool extends DefaultThreadPool {

	private static final int CORE_POOL_SIZE = 1;
	private static final long KEEP_ALIVE_TIME = 30L;
	private static final ConnectionThreadPool INSTANCE = new ConnectionThreadPool();

	public static ConnectionThreadPool getInstance() {
		return INSTANCE;
	}

	private ConnectionThreadPool() {
		super("Connection", CORE_POOL_SIZE, Integer.MAX_VALUE, KEEP_ALIVE_TIME, Thread.NORM_PRIORITY);
	}
}
