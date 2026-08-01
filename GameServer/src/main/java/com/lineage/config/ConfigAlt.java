package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * 服務器限制設置
 *
 * @author dexc
 *
 */
public final class ConfigAlt {

	/** AltSettings control */
	public static short GLOBAL_CHAT_LEVEL;

	public static short WHISPER_CHAT_LEVEL;

	/**設定自動取得道具的方式0-掉落地上, 1-掉落寵物身上, 2-掉落角色身上*/
	public static byte AUTO_LOOT;

	public static int LOOTING_RANGE;

	/**允許pk*/
	public static boolean ALT_NONPVP;

	/**PK處分*/
	public static boolean ALT_PUNISHMENT;

	/**攻擊資訊*/
	public static boolean ALT_ATKMSG;

	/**聯盟系統開關*/
	public static boolean CLAN_ALLIANCE;

	/**地面物品自動刪除時間(單位:分鐘)*/
	public static int ALT_ITEM_DELETION_TIME;

	//指定範圍內有人物不執行清除  2012-05-07 異動為可見範圍內有玩家不執行清除
	//public static int ALT_ITEM_DELETION_RANGE;

	public static boolean ALT_WHO_COMMANDX;

	public static int ALT_WHO_TYPE;

	public static double ALT_WHO_COUNT;

	/**
	 * 攻城間隔時間
	 */
	public static int ALT_WAR_TIME;

	/**
	 * 攻城持續時間
	 */
	public static int ALT_WAR_TIME_UNIT;

	public static int ALT_WAR_INTERVAL;

	public static int ALT_WAR_INTERVAL_UNIT;

	public static int ALT_RATE_OF_DUTY;

	public static boolean SPAWN_HOME_POINT;

	public static int SPAWN_HOME_POINT_RANGE;

	public static int SPAWN_HOME_POINT_COUNT;

	public static int SPAWN_HOME_POINT_DELAY;

	public static int ELEMENTAL_STONE_AMOUNT;

	public static int HOUSE_TAX_INTERVAL;

	/**最大娃娃攜帶量*/
	public static int MAX_DOLL_COUNT;

	/**NPC(Summon, Pet)背包最大容量*/
	public static int MAX_NPC_ITEM;

	/**個人倉庫容許收容數量*/
	public static int MAX_PERSONAL_WAREHOUSE_ITEM;

	/**血盟倉庫容許收容數量*/
	public static int MAX_CLAN_WAREHOUSE_ITEM;

	/**NPC死亡後刪除時間(單位:秒)*/
	public static int NPC_DELETION_TIME;
	
	/**等級大於多少的人物無法刪除*/
	public static int NO_DELETE_CHARACTER_LV;

	/**初始可建立人物數量*/
	public static int DEFAULT_CHARACTER_SLOT;

	/**萬能藥使用限制(數量)*/
	public static int MEDICINE;

	/**能力質上限*/
	public static int POWER;

	/**萬能藥使用限制(能力質上限)*/
	public static int POWERMEDICINE;

	/**是否允許丟棄道具至地面true允許 false不允許*/
	public static boolean DORP_ITEM;
	
	/** 每日抽獎系統開關 */
	public static boolean LotterySet;
	/** 每日抽獎時間 */
	public static String LotteryTime;
	/** 每日抽獎名額 */
	public static int LotteryQuota;
	/** 每日抽獎禮物編號 */
	public static int LotteryItemId;
	/** 每日抽獎禮物數量 */
	public static int LotteryItemAmount;
	
	

	/**最大拖怪數量*/
	public static final int MAX_NPC = 35;
	
    /** 当金额不等于pallysh01设定那给予多少比值回馈 */
    public static double pallysh01;
    /** 当金额大于或等于pallysh01设定，低于pallysh02设定给予多少比值回馈 */
	public static double pallysh02;
	/** 当金额大于或等于pallysh02设定，低于pallysh03设定给予多少比值回馈 */
	public static double pallysh03;
	/** 当金额大于或等于pallysh03设定，低于pallysh04设定给予多少比值回馈 */
	public static double pallysh04;
	/** 当金额大于或等于pallysh04设定，低于pallysh05设定给予多少比值回馈*/
	public static double pallysh05;
	/** 当金额大于或等于pallysh05设定 */
	public static double pallysh06;
	/** 回馈比列1.1代表百分之10*/
	public static double pally0;
	/** 回馈比列1.1代表百分之10 */
	public static double pally1;
	/** 回馈比列1.1代表百分之10 */
	public static double pally2;
	/** 回馈比列1.1代表百分之10 */
	public static double pally3;
	/** 回馈比列1.1代表百分之10 */
	public static double pally4;
	/** 回馈比列1.1代表百分之10 */
	public static double pally5;
	/** 回馈比列1.1代表百分之10 */
	public static double pally6;
	/** 是否开启赞助红利给予道具 */
	public static boolean Dividend;
	/** 赞助红利给予道具编号 */
	public static int DividendItem;
	/** 赞助多少给予道具1个 */
	public static int DividendQuantity;
		
		
	public static boolean CHANGPASSWORD;

	private static final String ALT_SETTINGS_FILE = "./config/altsettings.properties";

	public static final boolean ShopBulletin = false;

	public static void load() throws ConfigErrorException {
		//_log.info("載入服務器限制設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
			// 指定檔案編碼
			final InputStreamReader isr = new InputStreamReader(is, "utf-8");
			set.load(isr);
			is.close();

			GLOBAL_CHAT_LEVEL = Short.parseShort(set.getProperty("GlobalChatLevel", "30"));
			
			WHISPER_CHAT_LEVEL = Short.parseShort(set.getProperty("WhisperChatLevel", "5"));
			
			AUTO_LOOT = Byte.parseByte(set.getProperty("AutoLoot", "2"));
			
			LOOTING_RANGE = Integer.parseInt(set.getProperty("LootingRange", "3"));
			
			ALT_NONPVP = Boolean.parseBoolean(set.getProperty("NonPvP", "true"));
			
			ALT_PUNISHMENT = Boolean.parseBoolean(set.getProperty("Punishment", "true"));

			CLAN_ALLIANCE = Boolean.parseBoolean(set.getProperty("ClanAlliance", "true"));

			ALT_ITEM_DELETION_TIME = Integer.parseInt(set.getProperty("ItemDeletionTime", "10"));
			if (ALT_ITEM_DELETION_TIME > 60) {
				ALT_ITEM_DELETION_TIME = 60;// 最大設置60分鐘
			}
	     
         	
         	// 每日抽獎系統
         	LotterySet = Boolean.parseBoolean(set.getProperty("LotterySet", "true"));
         	
         	LotteryTime = set.getProperty("LotteryTime", "20:00");
            
         	LotteryQuota = Integer.parseInt(set.getProperty("LotteryQuota", "20"));
         	
         	LotteryItemId = Integer.parseInt(set.getProperty("LotteryItemId", "40308"));
         	
         	LotteryItemAmount = Integer.parseInt(set.getProperty("LotteryItemAmount", "1000000"));
         	
         	//System.out.println(LotterySet + "/" + LotteryTime + "/" + LotteryQuota + "/" + LotteryItemId + "/" + LotteryItemAmount);
					               	
		
			
			//ALT_ITEM_DELETION_RANGE = Integer.parseInt(set.getProperty("ItemDeletionRange", "5"));

			ALT_WHO_COMMANDX = Boolean.parseBoolean(set.getProperty("WhoCommandx", "false"));

			// WHO 顯示 額外設置方式 0:對話視窗顯示 1:視窗顯示
			// 這一項設置必須在WhoCommandx = true才有作用
			ALT_WHO_TYPE = Integer.parseInt(set.getProperty("Who_type", "0"));

			ALT_WHO_COUNT = Double.parseDouble(set.getProperty("WhoCommandcount", "1.0"));
			if (ALT_WHO_COUNT < 1.0) {
				ALT_WHO_COUNT = 1.0;
			}

			String strWar;
			strWar = set.getProperty("WarTime", "2h");
			if (strWar.indexOf("d") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.DATE;
				strWar = strWar.replace("d", "");
				
			} else if (strWar.indexOf("h") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.HOUR_OF_DAY;
				strWar = strWar.replace("h", "");
				
			} else if (strWar.indexOf("m") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.MINUTE;
				strWar = strWar.replace("m", "");
			}
			
			ALT_WAR_TIME = Integer.parseInt(strWar);
			strWar = set.getProperty("WarInterval", "4d");
			if (strWar.indexOf("d") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.DATE;
				strWar = strWar.replace("d", "");
				
			} else if (strWar.indexOf("h") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.HOUR_OF_DAY;
				strWar = strWar.replace("h", "");
				
			} else if (strWar.indexOf("m") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.MINUTE;
				strWar = strWar.replace("m", "");
			}
			
			ALT_WAR_INTERVAL = Integer.parseInt(strWar);

			SPAWN_HOME_POINT = Boolean.parseBoolean(set.getProperty("SpawnHomePoint", "true"));

			SPAWN_HOME_POINT_COUNT = Integer.parseInt(set.getProperty("SpawnHomePointCount", "2"));

			SPAWN_HOME_POINT_DELAY = Integer.parseInt(set.getProperty("SpawnHomePointDelay", "100"));

			SPAWN_HOME_POINT_RANGE = Integer.parseInt(set.getProperty("SpawnHomePointRange", "8"));

			ELEMENTAL_STONE_AMOUNT = Integer.parseInt(set.getProperty("ElementalStoneAmount", "300"));

			HOUSE_TAX_INTERVAL = Integer.parseInt(set.getProperty("HouseTaxInterval", "10"));
			
			MAX_DOLL_COUNT = Integer.parseInt(set.getProperty("MaxDollCount", "1"));

			MAX_NPC_ITEM = Integer.parseInt(set.getProperty("MaxNpcItem", "8"));

			MAX_PERSONAL_WAREHOUSE_ITEM = Integer.parseInt(set.getProperty("MaxPersonalWarehouseItem", "100"));

			MAX_CLAN_WAREHOUSE_ITEM = Integer.parseInt(set.getProperty("MaxClanWarehouseItem", "200"));

			NPC_DELETION_TIME = Integer.parseInt(set.getProperty("NpcDeletionTime", "10"));

			DEFAULT_CHARACTER_SLOT = Integer.parseInt(set.getProperty("DefaultCharacterSlot", "4"));
			
			NO_DELETE_CHARACTER_LV = Integer.parseInt(set.getProperty("NoDeleteCharacterLevel", "4"));

			MEDICINE = Integer.parseInt(set.getProperty("Medicine", "20"));

			POWER = Integer.parseInt(set.getProperty("Power", "35"));

			POWERMEDICINE = Integer.parseInt(set.getProperty("MedicinePower", "45"));

			DORP_ITEM = Boolean.parseBoolean(set.getProperty("dorpitem", "true"));
			
			CHANGPASSWORD = Boolean.parseBoolean(set.getProperty("changpassword", "true"));

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + ALT_SETTINGS_FILE);
		} finally {
			set.clear();
		}
	}
}