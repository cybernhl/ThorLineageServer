package com.lineage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.LogManager;

import custom.ServerHWID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigBad;
import com.lineage.config.ConfigBoxMsg;
import com.lineage.config.ConfigCharSetting;
import com.lineage.config.ConfigDescs;
import com.lineage.config.ConfigGuaji;
import com.lineage.config.ConfigIpCheck;
import com.lineage.config.ConfigKill;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.config.ConfigRecord;
import com.lineage.config.ConfigSQL;
import com.lineage.server.GameServer;

/**
 * 服務器啟動
 */
public class Server {

	private static final String LOG_PROP = "./config/logging.properties";

	private static final String LOG_4J = "./config/log4j.properties";

	private static final String _loginfo = "./loginfo";

	private static final String _back = "./back";

	/**
	 * MAIN
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final String hwid = ServerHWID.getHWID();
		if (hwid.isEmpty()) {
			return;
		}
		if (!ServerHWID.checkHwid(ServerHWID.getHWID())) { // 機碼綁定
			System.out.println("HWID: " + ServerHWID.getHWID());
			try {
				System.in.read();
			} catch (Exception ex) {

			}
			return;
		}
		Calendar date = Calendar.getInstance();
		// 1=週日 7=週六
		System.out.println(">>>>" + date.get(Calendar.DAY_OF_WEEK));
		System.out.println(">>>>" + date.get(Calendar.HOUR_OF_DAY));
		
		// 測試用核心
		try {
			// 在核心啟動命令後面加上 test 可以用來作顯示測試
			if (args[0].equalsIgnoreCase("test")) {
				Config.DEBUG = true;
			}
			
		} catch (final Exception e) {
			//e.printStackTrace();
		}
		
		// 壓縮舊檔案
		final CompressFile bean = new CompressFile();
		try {
			// 建立備份用資料夾
			final File file = new File(_back);
			if (!file.exists()) {
				file.mkdir();
			}

			final String nowDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
			bean.zip(_loginfo, "./back/"+nowDate+".zip");

			final File loginfofile = new File(_loginfo);
			final String[] loginfofileList = loginfofile.list();
			for (final String fileName : loginfofileList) {
				final File readfile = new File(_loginfo + "/" + fileName);
				if (readfile.exists() && !readfile.isDirectory()) {
					readfile.delete();
				}
			}

		} catch (final IOException e) {
			System.out.println("資料夾不存在: " + _back + " 已經自動建立!");
		}

		boolean error = false;

		try {
			final InputStream is = new BufferedInputStream(new FileInputStream(LOG_PROP));
			LogManager.getLogManager().readConfiguration(is);
			is.close();

		} catch (final IOException e) {
			System.out.println("檔案遺失: " + LOG_PROP);
			error = true;
		}

		try {
			PropertyConfigurator.configure(LOG_4J);

		} catch (final Exception e) {
			System.out.println("檔案遺失: " + LOG_4J);
			System.exit(0);
		}

		try {
			Config.load();
			ConfigAlt.load();
			ConfigCharSetting.load();
			ConfigOther.load();
			ConfigRate.load();
			ConfigSQL.load();
			ConfigRecord.load();
			
			ConfigDescs.load();
			ConfigBad.load();
			ConfigKill.load();
			ConfigIpCheck.load();
			ConfigBoxMsg.load();
			ConfigGuaji.load();
			
		} catch (final Exception e) {
			System.out.println("CONFIG 資料加載異常!" + e);
			error = true;
		}

		final Log log = LogFactory.getLog(Server.class);

		final String infoX = "\n\r##################################################"
			+ "\n\r       核心版本:" + Config.VER + "/" + Config.SRCVER + ")"
			+ "\n\r##################################################";

		log.info(infoX);

		final File file = new File("./jar");
		final String[] fileNameList = file.list();

		for (final String fileName : fileNameList) {
			final File readfile = new File(fileName);
			if (!readfile.isDirectory()) {
			//	log.info("加載引用JAR: " + fileName);
			}
		}

		if (error) {
			System.exit(0);
		}

		//log.info("訊息辨識(色彩涵義): [INFO]資訊");
		//log.debug("訊息辨識(色彩涵義): [DEBUG]除錯");
		//log.warn("訊息辨識(色彩涵義): [WARN]警告");
		//log.error("訊息辨識(色彩涵義): [ERROR]錯誤");
		//log.fatal("訊息辨識(色彩涵義): [FATAL]嚴重錯誤");

		// SQL讀取初始化
		DatabaseFactoryLogin.setDatabaseSettings();
		DatabaseFactory.setDatabaseSettings();

		DatabaseFactoryLogin.get();
		DatabaseFactory.get();
		
		// 安全管理器
		final LanSecurityManager securityManager = new LanSecurityManager();
		System.setSecurityManager(securityManager);
		//log.info("加載 安全管理器: LanSecurityManager");
		
		final String osname = System.getProperties().getProperty("os.name");
		if (osname.lastIndexOf("Linux") != -1) {
			Config.ISUBUNTU = true;
		}

		GameServer.getInstance().initialize();
	}
}
