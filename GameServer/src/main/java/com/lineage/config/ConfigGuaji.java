package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;

/**
 * 法師_技能設定表
 *
 * @author Shinogame
 *
 */
public final class ConfigGuaji {
	


	public static String guaji;//中文設置

	
	
	 
	 public static boolean guajiexp2;
	 
	// public static String guajimsg;//中文設置
	 
	 public static boolean checktimeguaji;
	 public static int timestart;
	public static int timeend;
	
	 public static boolean Guaji_action;
	 

	 /*public static int guajiWeight;
	 public static int guajiadenavip;
	 public static double guajiexp = 1.0D;*/
	//---------------------------------------------------
	private static Log _log;
	
	private static final String OTHER_SETTINGS_FILE = "./config/其他控制端/掛機相關設置表.properties";

	public static void load() throws ConfigErrorException {
		//_log.info("載入服務器限制設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(OTHER_SETTINGS_FILE));
			// 指定檔案編碼
			final InputStreamReader isr = new InputStreamReader(is, "utf-8");
			set.load(isr);
			is.close();
			guaji = set.getProperty("guaji", "");
			
			
		
			guajiexp2 = Boolean.parseBoolean(set.getProperty("guajiexp2", "true"));
			
			//guajimsg = set.getProperty("guajimsg", "");
			
			checktimeguaji = Boolean.parseBoolean(set.getProperty("checktimeguaji", "false"));
			timestart = Integer.parseInt(set.getProperty("timestart", "100"));
			timeend = Integer.parseInt(set.getProperty("timeend", "100"));
			
		
			
			Guaji_action = Boolean.parseBoolean(set.getProperty("Guaji_action", "true"));
			
		//	guajiexp = Double.parseDouble(set.getProperty("guajiexp", "1.0"));
			//   guajiWeight = Integer.parseInt(set.getProperty("guajiWeight", "0"));
			//   guajiadenavip = Integer.parseInt(set.getProperty("guajiadenavip", "0"));
			
		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + OTHER_SETTINGS_FILE);

		} finally {
			set.clear();
		}
	}
}