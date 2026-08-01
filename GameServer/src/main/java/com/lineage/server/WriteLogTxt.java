package com.lineage.server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WriteLogTxt {

	private static final Log _log = LogFactory.getLog(WriteLogTxt.class);

	public static void NormalLog(final String name, final String info) {
		try {
			SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
			Date d = Calendar.getInstance().getTime();
			String date = " " + sdfmt.format(d);
			final String path = "紀錄/一般道具/Log" + date;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			final FileOutputStream fos = new FileOutputStream(path + "/" + name + date + ".txt", true);
			fos.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos.close();

			final String path2 = "C:\\miscord\\紀錄\\一般道具\\Log" + date;
			newFolder(path2);
			File file2 = new File(path2);
			if (!file2.exists()) {
				file2.mkdir();
			}
			final FileOutputStream fos2 = new FileOutputStream(path2 + "/" + name + date + ".txt", true);
			fos2.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos2.close();
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static void YanBoLog(final String name, final String info) {
		try {
			SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
			Date d = Calendar.getInstance().getTime();
			String date = " " + sdfmt.format(d);
			final String path = "紀錄/元寶類/Log" + date;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			final FileOutputStream fos = new FileOutputStream(path + "/" + name + date + ".txt", true);
			fos.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos.close();

			final String path2 = "C:\\miscord\\紀錄\\元寶類\\Log" + date;
			newFolder(path2);
			File file2 = new File(path2);
			if (!file2.exists()) {
				file2.mkdir();
			}
			final FileOutputStream fos2 = new FileOutputStream(path2 + "/" + name + date + ".txt", true);
			fos2.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos2.close();
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static void GmLog(final String name, final String info) {
		try {
			SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
			Date d = Calendar.getInstance().getTime();
			String date = " " + sdfmt.format(d);
			final String path = "紀錄/GM相關/Log" + date;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			final FileOutputStream fos = new FileOutputStream(path + "/" + name + date + ".txt", true);
			fos.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos.close();

			final String path2 = "C:\\miscord\\紀錄\\GM相關\\Log" + date;
			newFolder(path2);
			File file2 = new File(path2);
			if (!file2.exists()) {
				file2.mkdir();
			}
			final FileOutputStream fos2 = new FileOutputStream(path2 + "/" + name + date + ".txt", true);
			fos2.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos2.close();
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static void EvnetLog(final String name, final String info) {
		try {
			SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
			Date d = Calendar.getInstance().getTime();
			String date = " " + sdfmt.format(d);
			final String path = "紀錄/強化/Log" + date;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			final FileOutputStream fos = new FileOutputStream(path + "/" + name + date + ".txt", true);
			fos.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos.close();

			final String path2 = "C:\\miscord\\紀錄\\強化\\Log" + date;
			newFolder(path2);
			File file2 = new File(path2);
			if (!file2.exists()) {
				file2.mkdir();
			}
			final FileOutputStream fos2 = new FileOutputStream(path2 + "/" + name + date + ".txt", true);
			fos2.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos2.close();
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static void PcLoginLog(final String name, final String info) {
		try {
			SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
			Date d = Calendar.getInstance().getTime();
			String date = " " + sdfmt.format(d);
			final String path = "紀錄/玩家異常/Log" + date;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			final FileOutputStream fos = new FileOutputStream(path + "/" + name + date + ".txt", true);
			fos.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos.close();

			final String path2 = "C:\\miscord\\紀錄\\玩家異常\\Log" + date;
			newFolder(path2);
			File file2 = new File(path2);
			if (!file2.exists()) {
				file2.mkdir();
			}
			final FileOutputStream fos2 = new FileOutputStream(path2 + "/" + name + date + ".txt", true);
			fos2.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos2.close();
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static void OtherLog(final String name, final String info) {
		try {
			SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
			Date d = Calendar.getInstance().getTime();
			String date = " " + sdfmt.format(d);
			final String path = "紀錄/雜項/Log" + date;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			final FileOutputStream fos = new FileOutputStream(path + "/" + name + date + ".txt", true);
			fos.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos.close();

			final String path2 = "C:\\miscord\\紀錄\\雜項\\Log" + date;
			newFolder(path2);
			File file2 = new File(path2);
			if (!file2.exists()) {
				file2.mkdir();
			}
			final FileOutputStream fos2 = new FileOutputStream(path2 + "/" + name + date + ".txt", true);
			fos2.write((info + " 時間：(" + new Timestamp(System.currentTimeMillis()) + ")。\r\n").getBytes());
			fos2.close();
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static void Recording(final String name, final String info) {
		try {

			SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
			Date d = Calendar.getInstance().getTime();
			String date = " " + sdfmt.format(d);
			final String path = "紀錄/清單表" + date;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			final FileOutputStream fos = new FileOutputStream(path + "/" + name + date + ".txt", true);
			fos.write((info + " 時間：" + new Timestamp(System.currentTimeMillis()) + "\r\n").getBytes());
			fos.close();

			final String path2 = "C:\\miscord\\紀錄\\清單表" + date;
			newFolder(path2);
			File file2 = new File(path2);
			if (!file2.exists()) {
				file2.mkdir();
			}
			final FileOutputStream fos2 = new FileOutputStream(path2 + "/" + name + date + ".txt", true);
			fos2.write((info + " 時間：" + new Timestamp(System.currentTimeMillis()) + "\r\n").getBytes());
			fos2.close();
			/*
			 * BufferedWriter out = new BufferedWriter(new FileWriter(
			 * "AllLog/"+name+date+".txt", true)); out.write(info+" 時間："+ new
			 * Timestamp(System.currentTimeMillis()) + "\r\n"); out.close();
			 */
		} catch (IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}
			// File myFilePathcopy = new File("C:\\Users\\Administrator\\Desktop\\1688\\" + filePath);
			// if (!myFilePathcopy.exists()) {
			// myFilePathcopy.mkdirs();
			// }
		} catch (Exception e) {
			_log.error("創建目錄操作出錯" + e.getLocalizedMessage(), e);
		}
	}

}