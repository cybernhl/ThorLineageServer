package com.lineage;

import com.lineage.config.ConfigSQL;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.zip.GZIPOutputStream;

/**
 * 魔法娃娃處理時間軸(娃娃效果:輔助技能)
 * @author dexc
 *
 */
public class DatabaseAutoBackTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(DatabaseAutoBackTimer.class);

	private ScheduledFuture<?> _timer;
	private boolean isok = true;

	public void start() {
		if (!ConfigSQL.isAutoBack) {
			return;
		}
		final int timeMillis = 5 * 60 * 1000;// 10秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			if (!isok) {
				System.out.println("備份作業進行中 ::");
				return;
			}
			System.out.println("正在備份數據庫作業 ::");
			isok = false;
			final File dir = new File("./dbback");
			if (!dir.exists()) {
				dir.mkdir();
			}
			final StringBuffer sb = new StringBuffer();
			sb.append("\"");
			sb.append(ConfigSQL.mysql_path + "mysqldump.exe");
			sb.append("\"");
			sb.append(" --default-character-set=utf8");
			sb.append(" --user=").append(ConfigSQL.DB_LOGIN_LOGIN);
			if (!ConfigSQL.DB_PASSWORD_LOGIN.isEmpty()) {
				sb.append(" --password=").append(ConfigSQL.DB_PASSWORD_LOGIN);
			}
			sb.append(" ").append(ConfigSQL.DB_URL2_LOGIN);
			System.out.println(sb.toString());
			final SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm");
			final String retStrFormatNowDate = sdFormatter.format(System.currentTimeMillis());
			Process process = Runtime.getRuntime().exec(sb.toString());
			File file = new File(dir.getPath() + "\\" + retStrFormatNowDate + ".sql.gz");
			file.getParentFile().mkdirs();
			try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
				try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream)) {
					try (InputStream inputStream = process.getInputStream()) {
						byte[] buf = new byte[1024];
						int len;
						while ((len = inputStream.read(buf)) != -1) {
							gzipOutputStream.write(buf, 0, len);
						}
						gzipOutputStream.finish();
						gzipOutputStream.flush();
					} catch (Exception ex) {
						System.out.println(ex);
					}
				}
			}
		} catch (final Exception e) {
			_log.error("自動備份時間軸 異常重啟", e);
			_timer.cancel(false);
			// GeneralThreadPool.get().cancel(_timer, false);
			final DatabaseAutoBackTimer dollTimer = new DatabaseAutoBackTimer();
			dollTimer.start();
		} finally {
			isok = true;
			System.out.println("~備份數據庫作業完成 ::");
		}
	}
}
