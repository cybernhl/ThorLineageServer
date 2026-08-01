package com.lineage.server.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 線程管理中心
 * @author dexc
 *
 */
public class NpcAiThreadPool {

	private static final Log _log = LogFactory.getLog(NpcAiThreadPool.class);

	private static NpcAiThreadPool _instance;

	private static final int SCHEDULED_CORE_POOL_SIZE = 100;

	// 執行已提交的 Runnable 任務的對象。
	// 此接口提供一種將任務提交與每個任務將如何運行的機制（包括線程使用的細節、調度等）分離開來的方法。
	// 通常使用 Executor 而不是顯式地創建線程。例如，可能會使用以下方法，
	// 而不是為一組任務中的每個任務調用 new Thread(new(RunnableTask())).start()：
	private Executor _executor;
	
	// 一個 ExecutorService，可安排在給定的延遲後運行或定期執行的命令。 
	private ScheduledExecutorService _scheduler;

	public static NpcAiThreadPool get() {
		if (_instance == null) {
			_instance = new NpcAiThreadPool();
		}
		return _instance;
	}

	private NpcAiThreadPool() {
		// 創建一個可根據需要創建新線程的線程池，但是在以前構造的線程可用時將重用它們。
		_executor = Executors.newCachedThreadPool();
		
		// 常規(創建一個線程池，它可安排在給定延遲後運行命令或者定期地執行。)
		_scheduler = Executors.newScheduledThreadPool(SCHEDULED_CORE_POOL_SIZE,
				new PriorityThreadFactory("NpcAiTPool", Thread.NORM_PRIORITY));
	}

	// Executor
	
	/**
	 * 使該線程開始執行；Java 虛擬機調用該線程的 run 方法。
	 * @param r
	 */
	public void execute(final Runnable r) {
		try {
			if (_executor == null) {
				final Thread t = new Thread(r);
				t.start();
				
			} else {
				_executor.execute(r);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 使該線程開始執行；Java 虛擬機調用該線程的 run 方法。
	 * @param t
	 */
	public void execute(final Thread t) {
		try {
			t.start();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 創建並執行在給定延遲後啟用的一次性操作。
	 * @param r 要執行的任務
	 * @param delay 從現在開始延遲執行的時間
	 * @return
	 */
	public ScheduledFuture<?> schedule(final Runnable r, final long delay) {
		try {
			if (delay <= 0) {
				_executor.execute(r);
				return null;
			}
			return _scheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
			
		} catch (final RejectedExecutionException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 根據需要創建新線程的對象。
	 * 使用線程工廠就無需再手工編寫對 new Thread 的調用了，從而允許應用程序使用特殊的線程子類、屬性等等。
	 * @author daien
	 *
	 */
	private class PriorityThreadFactory implements ThreadFactory {
		
		private final int _prio;

		private final String _name;

		private final AtomicInteger _threadNumber = new AtomicInteger(1); 

		private final ThreadGroup _group;

		/**
		 * PriorityThreadFactory
		 * @param name 線程名稱
		 * @param prio 優先等級
		 */
		public PriorityThreadFactory(final String name, final int prio) {
			this._prio = prio;
			this._name = name;
			this._group = new ThreadGroup(this._name);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = new Thread(this._group, r);
			t.setName(this._name + "-" + this._threadNumber.getAndIncrement());
			t.setPriority(this._prio);
			return t;
		}

		@SuppressWarnings("unused")
		public ThreadGroup getGroup() {
			return this._group;
		}
	}
}
