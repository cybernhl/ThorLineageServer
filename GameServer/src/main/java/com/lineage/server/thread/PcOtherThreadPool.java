package com.lineage.server.thread;

import java.util.TimerTask;
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

import com.lineage.config.Config;
import com.lineage.server.model.monitor.L1PcMonitor;

/**
 * 線程管理中心(PC)
 * @author dexc
 *
 */
public class PcOtherThreadPool {

	private static final Log _log = LogFactory.getLog(PcOtherThreadPool.class);

	private static PcOtherThreadPool _instance;

	private Executor _executor;
	
	// 一個 ExecutorService，可安排在給定的延遲後運行或定期執行的命令。 
	private ScheduledExecutorService _scheduler;

	private final int _pcSchedulerPoolSize = 1 + Config.MAX_ONLINE_USERS / 10;

	public static PcOtherThreadPool get() {
		if (_instance == null) {
			_instance = new PcOtherThreadPool();
		}
		return _instance;
	}

	private PcOtherThreadPool() {
		// 創建一個可根據需要創建新線程的線程池，但是在以前構造的線程可用時將重用它們。
		_executor = Executors.newCachedThreadPool();
		
		// AI(創建一個線程池，它可安排在給定延遲後運行命令或者定期地執行。)
		_scheduler = Executors.newScheduledThreadPool(_pcSchedulerPoolSize,
				new PriorityThreadFactory("PcOth", Thread.NORM_PRIORITY));
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
	 * 創建並執行在給定延遲後啟用的一次性操作。 
	 * @param r - 要執行的任務
	 * @param delay - 從現在開始延遲執行的時間
	 * @return
	 */
	public ScheduledFuture<?> pcSchedule(final L1PcMonitor r, final long delay) {
		try {
			if (delay <= 0) {
				// 在未來某個時間執行給定的命令。
				// 該命令可能在新的線程、已入池的線程或者正調用的線程中執行，這由 Executor 實現決定。 
				_executor.execute(r);
				return null;
			}
			// 創建並執行在給定延遲後啟用的一次性操作。 
			return _scheduler.schedule(r, delay, TimeUnit.MILLISECONDS);

		} catch (final RejectedExecutionException e) {
			_log.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	// ScheduledThreadPoolExecutor
	
	/**
	 * 創建並執行一個在給定初始延遲後首次啟用的定期操作，後續操作具有給定的週期；
	 * 也就是將在 initialDelay 後開始執行，然後在 initialDelay+period 後執行，
	 * 接著在 initialDelay + 2 * period 後執行，依此類推。
	 * 如果任務的任何一個執行遇到異常，則後續執行都會被取消。
	 * 
	 * 否則，只能通過執行程序的取消或終止方法來終止該任務。
	 * 如果此任務的任何一個執行要花費比其週期更長的時間，則將推遲後續執行，但不會同時執行。 
	 * 
	 * @param command - 要執行的任務
	 * @param initialDelay - 首次執行的延遲時間
	 * @param period - 連續執行之間的週期
	 * @return
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(final TimerTask command, 
			final long initialDelay, final long period) {
		try {
			return _scheduler.scheduleAtFixedRate(command, 
					initialDelay, period, TimeUnit.MILLISECONDS);

		} catch (final RejectedExecutionException e) {
			_log.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	// 取消任務計時器
	
	/**
	 * 試圖取消對此任務的執行。
	 * 如果任務已完成、或已取消，或者由於某些其他原因而無法取消，則此嘗試將失敗。
	 * 當調用 cancel 時，如果調用成功，而此任務尚未啟動，則此任務將永不運行。
	 * 如果任務已經啟動，則 mayInterruptIfRunning 參數確定是否應該以試圖停止任務的方式來中斷執行此任務的線程。 
	 * 此方法返回後，對 isDone() 的後續調用將始終返回 true。
	 * 如果此方法返回 true，則對 isCancelled() 的後續調用將始終返回 true。 
	 * 
	 * @param future - 一個延遲的、結果可接受的操作，可將其取消。
	 * @param mayInterruptIfRunning - 如果應該中斷執行此任務的線程，則為 true；否則允許正在運行的任務運行完成 
	 */
	public void cancel(final ScheduledFuture<?> future, boolean mayInterruptIfRunning) {
		try {
			future.cancel(mayInterruptIfRunning);

		} catch (final RejectedExecutionException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 取消任務計時器
	 * 取消此計時器任務。如果任務安排為一次執行且還未運行，<BR>
	 * 或者尚未安排，則永遠不會運行。如果任務安排為重複執行，<BR>
	 * 則永遠不會再運行。（如果發生此調用時任務正在運行，則任務將運行完，但永遠不會再運行。） <BR>
	 * <BR>
	 * 注意，從重複的計時器任務的 run 方法中調用此方法絕對保證計時器任務不會再運行。 <BR>
	 * <BR>
	 * 此方法可以反覆調用；第二次和以後的調用無效。<BR>
	 * <BR>
	 * @param task - 所要安排的任務。
	 */
	public void cancel(final TimerTask task) {
		try {
			// 取消此計時器任務。
			task.cancel();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
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
			_prio = prio;
			_name = name;
			_group = new ThreadGroup(_name);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = new Thread(_group, r);
			t.setName(_name + "-" + _threadNumber.getAndIncrement());
			t.setPriority(_prio);
			return t;
		}

		@SuppressWarnings("unused")
		public ThreadGroup getGroup() {
			return _group;
		}
	}
}
