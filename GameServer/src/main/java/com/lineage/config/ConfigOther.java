package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 服務器活動設置
 *
 * @author dexc
 *
 */
public final class ConfigOther {
	
	private static final Log _log = LogFactory.getLog(ConfigOther.class);

	/**啟用加速檢測*/
	public static boolean SPEED = false;

	/**允許速率範圍質*/
	public static double SPEED_TIME = 1.2D;

	/**怪物是否主動攻擊紅人*/
	public static boolean KILLRED = true;
	
	public static int RATE_XP_WHO = 1;

	/**允許盟組解散血盟*/
	public static boolean CLANDEL;

	/**允許盟員自行建立封號*/
	public static boolean CLANTITLE;

	/**自行建立血盟人數上限*/
	public static int CLANCOUNT;

	/**啟用人物全時光照(true啟用 false關閉)*/
	public static boolean LIGHT;

	/**顯示怪物血條*/
	public static boolean HPBAR;

	/**一般商店是否顯示詳細資訊*/
	public static boolean SHOPINFO;

	/**血盟小屋HP恢復增加*/
	public static int HOMEHPR;

	/**血盟小屋MP恢復增加*/
	public static int HOMEMPR;
	

	/**攻城旗幟內是否允許攜帶娃娃 true:允許 false:禁止*/
	public static boolean WAR_DOLL;
	
	/**廣播扣除金幣或是飽食度(0:飽食度    1:金幣)*/
	public static int SET_GLOBAL;
	
	/**廣播扣除質(set_global設置0:扣除飽食度量    set_global設置1:扣除金幣量)*/
	public static int SET_GLOBAL_COUNT;

	/**廣播扣除金幣或是飽食度(0:飽食度    1:金幣)*/
	public static int SET_BIG_CHAT;

	/**廣播扣除質(set_global設置0:扣除飽食度量    set_global設置1:扣除金幣量)*/
	public static int SET_BIG_CHAT_COUNT;

	/**廣播/買賣頻道間隔秒數*/
	public static int SET_GLOBAL_TIME;

	public static boolean AutoAddSkill; // 自动学习技能
	public static int CLAN_STAT_LEVEL;
	public static int CLAN_STAT_LEVEL_COUNT;

	/** 天堂M變身列表 */
	public static List<Integer> poly_Mlist = new ArrayList<Integer>();
	
	private static final String LIANG = "./config/other.properties";
	
    public static long HTML_ONLINE_TIME;

	public static int wgjc_sj = 1;

	public static boolean wgjc_kg;
	public static boolean party_exp_add;
	public static double party_exp_member_size_rate_2;
	public static double party_exp_member_size_rate_3;
	public static double party_exp_member_size_rate_4;
	public static double party_exp_member_size_rate_5;
	public static double party_exp_member_size_rate_6;
	public static double party_exp_member_size_rate_7;
	public static double party_exp_member_size_rate_8;
	public static int Weapon_Lv;
	public static int Armor_Lv;

	public static int weaponSuccessLvl;
	public static int armorSuccessLvl;

	public static boolean 特殊額外攻擊;

	public static int CUSTOM_TAIWAN_MAHJONG_ITEM_ID;
	public static String CUSTOM_TAIWAN_MAHJONG_ITEM_NAME;
	public static String CUSTOM_TAIWAN_MAHJONG_TEXT;
	public static int CUSTOM_TAIWAN_MAHJONG_MIN_AMOUNT;
	public static boolean MR_DMG_HALVED_ENABLE = false;
	public static int MR_DMG_HALVED_VALUE = 100;
	public static boolean PLAYER_SHOP_MAP_96 = true;
	public static boolean NewCreate = true;

	public static boolean ATTACK_PET = false;
	public static double ATTACK_DEF = 0.0;

	public static boolean packet_crypt = false;
	public static boolean game123 = false;

	public static int WIND_SHOT_BOW_DMG_VALUE;
	public static int WIND_SHOT_BOW_HIT_VALUE;
	public static int STORM_EYE_BOW_DMG_VALUE;
	public static int STORM_EYE_BOW_HIT_VALUE;
	public static int STORM_SHOT_BOW_DMG_VALUE;
	public static int STORM_SHOT_BOW_HIT_VALUE;

	public static int check_plugin_delay;
	public static int MAX_PARTY_SIZE;
	public static int CLAN_HOUSE_TAXES;
	public static int ABILITY_3_CHANCE;
	public static int ABILITY_2_CHANCE;


    
	/**NpcId 显示*/
	//public static boolean NPCID = false;

	public static void load() throws ConfigErrorException {
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(LIANG));
			set.load(new InputStreamReader(is, Charset.forName("UTF-8")));
			is.close();

			SPEED = Boolean.parseBoolean(set.getProperty("speed", "false"));
			
			SPEED_TIME = Double.parseDouble(set.getProperty("speed_time", "1.2"));


			KILLRED = Boolean.parseBoolean(set.getProperty("kill_red", "false"));

			RATE_XP_WHO = Integer.parseInt(set.getProperty("rate_xp_who", "1"));

			CLANDEL = Boolean.parseBoolean(set.getProperty("clanadel", "false"));

			CLANTITLE = Boolean.parseBoolean(set.getProperty("clanatitle", "false"));

			CLANCOUNT = Integer.parseInt(set.getProperty("clancount", "100"));

			// 啟用人物全時光照(true啟用 false關閉)
			LIGHT = Boolean.parseBoolean(set.getProperty("light", "false"));
			特殊額外攻擊 = Boolean.parseBoolean(set.getProperty("dmg_modifier", "true"));

			// 顯示怪物血條(true啟用 false關閉)
			HPBAR = Boolean.parseBoolean(set.getProperty("hpbar", "false"));
			
			SHOPINFO = Boolean.parseBoolean(set.getProperty("shopinfo", "false"));

			HOMEHPR = Integer.parseInt(set.getProperty("homehpr", "100"));

			HOMEMPR = Integer.parseInt(set.getProperty("homempr", "100"));

			SET_GLOBAL = Integer.parseInt(set.getProperty("set_global", "100"));
			SET_BIG_CHAT = Integer.parseInt(set.getProperty("set_big_chat", "0"));

			// 自动学习技能
			AutoAddSkill = Boolean.parseBoolean(set.getProperty(
					"AutoAddSkill", "true"));    
       
			if (set.getProperty("poly_Mlist") != null) {
				for (final String str : set.getProperty("poly_Mlist")
						.split(",")) {
					poly_Mlist.add(Integer.parseInt(str));
				}
			}
			SET_GLOBAL_COUNT = Integer.parseInt(set.getProperty("set_global_count", "100"));
			SET_BIG_CHAT_COUNT = Integer.parseInt(set.getProperty("set_big_chat_count", "8"));

			SET_GLOBAL_TIME = Integer.parseInt(set.getProperty("set_global_time", "5"));
			CLAN_STAT_LEVEL = Integer.parseInt(set.getProperty("clan_stat_level", "40"));
			CLAN_STAT_LEVEL_COUNT = Integer.parseInt(set.getProperty("clan_stat_level_count", "15"));

			WAR_DOLL = Boolean.parseBoolean(set.getProperty("war_doll", "true"));

			wgjc_sj = Integer.parseInt(set.getProperty("jcsj", "30"));

			wgjc_kg = Boolean.parseBoolean(set.getProperty("wgkg", "true"));
			party_exp_add = Boolean.parseBoolean(set.getProperty("party_exp_add", "true"));
			party_exp_member_size_rate_2 = Double.parseDouble(set.getProperty("party_exp_member_size_rate_2", "1.05"));
			party_exp_member_size_rate_3 = Double.parseDouble(set.getProperty("party_exp_member_size_rate_3", "1.075"));
			party_exp_member_size_rate_4 = Double.parseDouble(set.getProperty("party_exp_member_size_rate_4", "1.10"));
			party_exp_member_size_rate_5 = Double.parseDouble(set.getProperty("party_exp_member_size_rate_5", "1.125"));
			party_exp_member_size_rate_6 = Double.parseDouble(set.getProperty("party_exp_member_size_rate_6", "1.15"));
			party_exp_member_size_rate_7 = Double.parseDouble(set.getProperty("party_exp_member_size_rate_7", "1.175"));
			party_exp_member_size_rate_8 = Double.parseDouble(set.getProperty("party_exp_member_size_rate_8", "1.20"));
			Weapon_Lv = Integer.parseInt(set.getProperty("Weapon_Lv", "100"));
			MAX_PARTY_SIZE = Integer.parseInt(set.getProperty("max_party_size", "8"));
			CLAN_HOUSE_TAXES = Integer.parseInt(set.getProperty("clan_house_taxes", "150000"));
			Armor_Lv = Integer.parseInt(set.getProperty("Armor_Lv", "100"));

			weaponSuccessLvl = Integer.parseInt(set.getProperty("Weapon_Success_Lvl", "100"));
			armorSuccessLvl = Integer.parseInt(set.getProperty("Armor_Success_Lvl", "100"));

			CUSTOM_TAIWAN_MAHJONG_ITEM_ID = Integer.parseInt(set.getProperty("custom_taiwan_mahjong_item_id", "40308"));
			CUSTOM_TAIWAN_MAHJONG_MIN_AMOUNT = Integer.parseInt(set.getProperty("custom_taiwan_mahjong_min_amount", "500"));
			CUSTOM_TAIWAN_MAHJONG_ITEM_NAME = set.getProperty("custom_taiwan_mahjong_item_name", "金幣");
			CUSTOM_TAIWAN_MAHJONG_TEXT = set.getProperty("custom_taiwan_mahjong_text", "規則記得打");
			MR_DMG_HALVED_ENABLE = Boolean.parseBoolean(set.getProperty("mr_dmg_halved_enable", "false"));
			PLAYER_SHOP_MAP_96 = Boolean.parseBoolean(set.getProperty("player_shop_map_96", "true"));
			ATTACK_PET = Boolean.parseBoolean(set.getProperty("attack_pet", "false"));
			NewCreate = Boolean.parseBoolean(set.getProperty("NewCreate", "true"));
			packet_crypt = Boolean.parseBoolean(set.getProperty("packet_crypt", "false"));
			MR_DMG_HALVED_VALUE = Integer.parseInt(set.getProperty("mr_dmg_halved_value", "100"));
			WIND_SHOT_BOW_DMG_VALUE = Integer.parseInt(set.getProperty("wind_shot_bow_dmg_value", "0"));
			WIND_SHOT_BOW_HIT_VALUE = Integer.parseInt(set.getProperty("wind_shot_bow_hit_value", "6"));
			STORM_EYE_BOW_DMG_VALUE = Integer.parseInt(set.getProperty("storm_eye_bow_dmg_value", "3"));
			STORM_EYE_BOW_HIT_VALUE = Integer.parseInt(set.getProperty("storm_eye_bow_hit_value", "2"));
			STORM_SHOT_BOW_HIT_VALUE = Integer.parseInt(set.getProperty("storm_shot_bow_hit_value", "5"));
			STORM_SHOT_BOW_DMG_VALUE = Integer.parseInt(set.getProperty("storm_shot_bow_dmg_value", "-1"));
			ABILITY_3_CHANCE = Integer.parseInt(set.getProperty("ability_3_chance", "10000"));
			ABILITY_2_CHANCE = Integer.parseInt(set.getProperty("ability_2_chance", "200000"));
			check_plugin_delay = Integer.parseInt(set.getProperty("check_plugin_delay", "2000"));
			ATTACK_DEF = Double.parseDouble(set.getProperty("attack_def", "0.8"));
			game123 = Boolean.parseBoolean(set.getProperty("game123", "false"));
		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + LIANG);

		} finally {
			set.clear();
		}
	}
}