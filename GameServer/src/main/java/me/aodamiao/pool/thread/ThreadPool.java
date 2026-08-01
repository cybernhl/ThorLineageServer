package me.aodamiao.pool.thread;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * 線程池
 * 
 * @author Dragon Li
 * @created 2016年5月5日 下午3:03:25
 */
public interface ThreadPool {

    /**
     * 當緩衝線程池正常時使用緩衝線程池執行Runnable實例，否則開啟新線程執行
     * 
     * @author Dragon Li
     * @created 2016年5月5日 下午3:13:27
     * @param r
     */
    public void execute(Runnable r);

    /**
     * 運行線程(調用傳入線程實例的start方法)<BR>
     * 如非重要的需要單獨使用新線程的作業，推薦使用{@link #execute(Runnable)}
     * 
     * @author Dragon Li
     * @created 2016年5月5日 下午3:11:25
     * @param t
     */
    public void execute(Thread t);

    /**
     * 在指定的延遲後一次性執行Runnable實例
     * 
     * @author Dragon Li
     * @created 2016年5月5日 下午3:14:07
     * @param r
     * @param delay
     *            從現在開始延遲執行的時間(毫秒,小等於0的時間會被立即執行並返回空值)
     * @return
     */
    public ScheduledFuture<?> schedule(Runnable r, long delay);

    /**
     * 在指定的延遲後週期性執行Runnable實例<BR>
     * 也就是將在delay後開始執行，然後在delay + period後執行<BR>
     * 接著在delay + 2 * period後再次執行，類推<BR>
     * 如果任務的任何一個執行遇到異常，則後續執行都會被取消<BR>
     * 否則，只能通過執行程序的取消或終止方法來終止該任務<BR>
     * 如果此任務的任何一個執行要花費比其週期更長的時間，則將推遲後續執行，但不會同時執行
     * 
     * @author Dragon Li
     * @created 2016年5月5日 下午3:14:09
     * @param r
     * @param initialDelay
     *            首次執行的延遲時間
     * @param period
     *            連續執行之間的週期
     * @return
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initialDelay, long period);

    /**
     * 關閉線程池
     * 
     * @author Dragon Li
     * @created 2016年5月5日 下午4:13:41
     */
    public void shutdown();

    /**
     * 立即關閉線程池
     * 
     * @author Dragon Li
     * @created 2016年5月5日 下午4:13:44
     * @return 未開始執行的任務列表
     */
    public List<Runnable> shutdownNow();
}
