package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 服務器倍率設置
 *
 * @author dexc
 *
 */
public final class ConfigRate {

	/** Rate control */
	public static double RATE_XP;
	public static double RATE_PET_EXP;

	public static double RATE_LA;

	public static double RATE_KARMA;

	public static double RATE_DROP_ADENA;

	public static double RATE_DROP_ITEMS;

	public static int ENCHANT_CHANCE_WEAPON;
	public static int ENCHANT_CHANCE_WEAPON_1;
	public static int ENCHANT_CHANCE_WEAPON_2;
	public static int ENCHANT_CHANCE_WEAPON_3;
	public static int ENCHANT_CHANCE_WEAPON_4;
	public static int ENCHANT_CHANCE_WEAPON_5;
	public static int ENCHANT_CHANCE_WEAPON_6;

	public static int ENCHANT_CHANCE_ARMOR;
	public static int ENCHANT_CHANCE_ARMOR_1;
	public static int ENCHANT_CHANCE_ARMOR_2;
	public static int ENCHANT_CHANCE_ARMOR_3;
	public static int ENCHANT_CHANCE_ARMOR_4;
	public static int ENCHANT_CHANCE_ARMOR_5;
	public static int ENCHANT_CHANCE_ARMOR_6;

	public static int ATTR_ENCHANT_CHANCE;

	/**角色負重倍率 0.0-127.0*/
	public static double RATE_WEIGHT_LIMIT;

	/**寵物負重倍率 0.0-127.0*/
	public static double RATE_WEIGHT_LIMIT_PET;
	public static boolean RATE_SHOP_SWITCH = false;
	public static double RATE_SHOP_SELLING_PRICE;

	public static double RATE_SHOP_PURCHASING_PRICE;

	public static int CREATE_CHANCE_DIARY;

	public static int CREATE_CHANCE_RECOLLECTION;

	public static int CREATE_CHANCE_MYSTERIOUS;

	public static int CREATE_CHANCE_PROCESSING;

	public static int CREATE_CHANCE_PROCESSING_DIAMOND;

	public static int CREATE_CHANCE_DANTES;

	public static int CREATE_CHANCE_ANCIENT_AMULET;

	public static int CREATE_CHANCE_HISTORY_BOOK;

	private static final String RATES_CONFIG_FILE = "./config/rates.properties";

	public static void load() throws ConfigErrorException {
		//_log.info("載入服務器倍率設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(RATES_CONFIG_FILE));
			set.load(is);
			is.close();

			RATE_XP = Double.parseDouble(set.getProperty("RateXp", "1.0"));
			RATE_PET_EXP = Double.parseDouble(set.getProperty("RatePetExp", "2.0"));

			RATE_LA = Double.parseDouble(set.getProperty("RateLawful", "1.0"));

			RATE_KARMA = Double.parseDouble(set.getProperty(
					"RateKarma", "1.0"));
			RATE_DROP_ADENA = Double.parseDouble(set.getProperty(
					"RateDropAdena", "1.0"));
			RATE_DROP_ITEMS = Double.parseDouble(set.getProperty(
					"RateDropItems", "1.0"));
			ENCHANT_CHANCE_WEAPON = Integer.parseInt(set.getProperty("EnchantChanceWeapon", "68"));
			ENCHANT_CHANCE_WEAPON_1 = Integer.parseInt(set.getProperty("EnchantChanceWeapon_1", String.valueOf(ENCHANT_CHANCE_WEAPON)));
			ENCHANT_CHANCE_WEAPON_2 = Integer.parseInt(set.getProperty("EnchantChanceWeapon_2", String.valueOf(ENCHANT_CHANCE_WEAPON)));
			ENCHANT_CHANCE_WEAPON_3 = Integer.parseInt(set.getProperty("EnchantChanceWeapon_3", String.valueOf(ENCHANT_CHANCE_WEAPON)));
			ENCHANT_CHANCE_WEAPON_4 = Integer.parseInt(set.getProperty("EnchantChanceWeapon_4", String.valueOf(ENCHANT_CHANCE_WEAPON)));
			ENCHANT_CHANCE_WEAPON_5 = Integer.parseInt(set.getProperty("EnchantChanceWeapon_5", String.valueOf(ENCHANT_CHANCE_WEAPON)));
			ENCHANT_CHANCE_WEAPON_6 = Integer.parseInt(set.getProperty("EnchantChanceWeapon_6", String.valueOf(ENCHANT_CHANCE_WEAPON)));
			ENCHANT_CHANCE_ARMOR = Integer.parseInt(set.getProperty("EnchantChanceArmor", "52"));
			ENCHANT_CHANCE_ARMOR_1 = Integer.parseInt(set.getProperty("EnchantChanceArmor_1", String.valueOf(ENCHANT_CHANCE_ARMOR)));
			ENCHANT_CHANCE_ARMOR_2 = Integer.parseInt(set.getProperty("EnchantChanceArmor_2", String.valueOf(ENCHANT_CHANCE_ARMOR)));
			ENCHANT_CHANCE_ARMOR_3 = Integer.parseInt(set.getProperty("EnchantChanceArmor_3", String.valueOf(ENCHANT_CHANCE_ARMOR)));
			ENCHANT_CHANCE_ARMOR_4 = Integer.parseInt(set.getProperty("EnchantChanceArmor_4", String.valueOf(ENCHANT_CHANCE_ARMOR)));
			ENCHANT_CHANCE_ARMOR_5 = Integer.parseInt(set.getProperty("EnchantChanceArmor_5", String.valueOf(ENCHANT_CHANCE_ARMOR)));
			ENCHANT_CHANCE_ARMOR_6 = Integer.parseInt(set.getProperty("EnchantChanceArmor_6", String.valueOf(ENCHANT_CHANCE_ARMOR)));
			ATTR_ENCHANT_CHANCE = Integer.parseInt(set.getProperty(
					"AttrEnchantChance", "10"));
			RATE_WEIGHT_LIMIT = Double.parseDouble(set.getProperty(
					"RateWeightLimit", "1"));
			RATE_WEIGHT_LIMIT_PET = Double.parseDouble(set
					.getProperty("RateWeightLimitforPet", "1"));
			RATE_SHOP_SELLING_PRICE = Double.parseDouble(set
					.getProperty("RateShopSellingPrice", "1.0"));
			RATE_SHOP_SWITCH = Boolean.parseBoolean(set.getProperty("rate_shop_switch", "true"));
			RATE_SHOP_PURCHASING_PRICE = Double.parseDouble(set
					.getProperty("RateShopPurchasingPrice", "1.0"));
			CREATE_CHANCE_DIARY = Integer.parseInt(set.getProperty(
					"CreateChanceDiary", "33"));
			CREATE_CHANCE_RECOLLECTION = Integer.parseInt(set
					.getProperty("CreateChanceRecollection", "90"));
			CREATE_CHANCE_MYSTERIOUS = Integer.parseInt(set
					.getProperty("CreateChanceMysterious", "90"));
			CREATE_CHANCE_PROCESSING = Integer.parseInt(set
					.getProperty("CreateChanceProcessing", "90"));
			CREATE_CHANCE_PROCESSING_DIAMOND = Integer.parseInt(set
					.getProperty("CreateChanceProcessingDiamond", "90"));
			CREATE_CHANCE_DANTES = Integer.parseInt(set.getProperty(
					"CreateChanceDantes", "50"));
			CREATE_CHANCE_ANCIENT_AMULET = Integer.parseInt(set
					.getProperty("CreateChanceAncientAmulet", "90"));
			CREATE_CHANCE_HISTORY_BOOK = Integer.parseInt(set
					.getProperty("CreateChanceHistoryBook", "50"));
		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + RATES_CONFIG_FILE);

		} finally {
			set.clear();
		}
	}
}