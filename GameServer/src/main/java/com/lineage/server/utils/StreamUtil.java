package com.lineage.server.utils;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StreamUtil {

	private static final Log _log = LogFactory.getLog(StreamUtil.class);

	/**
	 * 關閉此流並釋放與此流關聯的所有系統資源。如果已經關閉該流，則調用此方法無效。
	 * @param closeables
	 */
	public static void close(final Closeable... closeables) {
		for (final Closeable c : closeables) {
			try {
				if (c != null) {
					c.close();
				}

			} catch (final IOException e) {
				_log.error("關閉Closeable發生異常", e);
			}
		}
	}

	/**
	 * 請求取消此鍵的通道到其選擇器的註冊
	 * @param keys
	 */
	public static void close(final SelectionKey... keys) {
		for (final SelectionKey key : keys) {
			if (key != null) {
				key.cancel();
			}
		}
	}

	/**
	 * 關閉此選擇器
	 * @param keys
	 */
	public static void close(final Selector... selectors) {
		for (final Selector selector : selectors) {
			try {
				if (selector != null) {
					selector.close();
				}

			} catch (final IOException e) {
				_log.error("關閉Selector發生異常", e);
			}
		}
	}

	/**
	 * 關閉此套接字。 
	 * 所有當前阻塞於此套接字上的 I/O 操作中的線程都將拋出 SocketException。 
	 * 
	 * 套接字被關閉後，便不可在以後的網絡連接中使用（即無法重新連接或重新綁定）。需要創建新的套接字。
	 * 
	 * 關閉此套接字也將會關閉該套接字的 InputStream 和 OutputStream。 
	 * 
	 * 如果此套接字有一個與之關聯的通道，則關閉該通道。
	 * 
	 * @param csocket
	 */
	public static void close(final Socket csocket) {
		try {
			if (!csocket.isClosed()) {
				csocket.shutdownInput();
				csocket.shutdownOutput();
				csocket.close();
			}
			
		} catch (final IOException e) {
			_log.error("關閉Socket發生異常", e);
		}
	}

	/**
	 * 關閉此套接字。 在 accept() 中所有當前阻塞的線程都將會拋出 SocketException。 
	 * 如果此套接字有一個與之關聯的通道，則關閉該通道。
	 * 
	 * @param server
	 */
	public static void close(final ServerSocket server) {
		try {
			if (!server.isClosed()) {
				server.close();
			}
			
		} catch (final IOException e) {
			_log.error("關閉ServerSocket發生異常", e);
		}
	}

	/**
	 * 中斷線程。 
	 * 如果當前線程沒有中斷它自己（這在任何情況下都是允許的），
	 * 則該線程的 checkAccess 方法就會被調用，這可能拋出 SecurityException。 
	 * 
	 * 如果線程在調用 Object 類的 wait()、wait(long) 或 wait(long, int) 方法，
	 * 或者該類的 join()、join(long)、join(long, int)、sleep(long) 或 
	 * sleep(long, int) 方法過程中受阻，則其中斷狀態將被清除，
	 * 它還將收到一個 InterruptedException。 
	 * 
	 * 如果該線程在可中斷的通道上的 I/O 操作中受阻，則該通道將被關閉，
	 * 該線程的中斷狀態將被設置並且該線程將收到一個 ClosedByInterruptException。 
	 * 
	 * 如果該線程在一個 Selector 中受阻，則該線程的中斷狀態將被設置，它將立即從選擇操作返回，
	 * 並可能帶有一個非零值，就好像調用了選擇器的 wakeup 方法一樣。 
	 * 
	 * 如果以前的條件都沒有保存，則該線程的中斷狀態將被設置。
	 * 
	 * 中斷一個不處於活動狀態的線程不需要任何作用。
	 * 
	 * @param serverExecutor
	 */
	public static void interrupt(final Thread thread) {
		try {
			if (thread.isAlive()) {
				thread.interrupt();
			}
			
		} catch (final Exception e) {
			_log.error("關閉Thread發生異常", e);
		}
	}
}
