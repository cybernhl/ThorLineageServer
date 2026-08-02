package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.ItemClass;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.IdFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.datatables.mappers.ItemMapper;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemsArmor;
import com.lineage.server.templates.L1ItemsEtcItem;
import com.lineage.server.templates.L1ItemsWeapon;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 道具,武器,防具資料
 *
 * @author dexc
 *
 */
public class ItemTable {

	private static final Log _log = LogFactory.getLog(ItemTable.class);

	// 防具類型核心分類
	private static final Map<String, Integer> _armorTypes = new HashMap<String, Integer>();

	// 武器類型核心分類
	private static final Map<String, Integer> _weaponTypes = new HashMap<String, Integer>();

	// 武器類型觸發事件
	private static final Map<String, Integer> _weaponId = new HashMap<String, Integer>();

	// 材質類型核心分類
	private static final Map<String, Integer> _materialTypes = new HashMap<String, Integer>();

	// 道具類型核心分類
	private static final Map<String, Integer> _etcItemTypes = new HashMap<String, Integer>();

	// 道具類型觸發事件
	private static final Map<String, Integer> _useTypes = new HashMap<String, Integer>();

	private static ItemTable _instance;

	private L1Item _allTemplates[];
	
	private static Map<Integer, L1ItemsEtcItem> _itemsetcitem;
	
	private static Map<Integer, L1ItemsArmor> _itemsarmor;
	
	private static Map<Integer, L1ItemsWeapon> _itemsweapon;
	
private static Map<Integer, L1ItemsArmor> _itemsarmor1;
	
	private static Map<Integer, L1ItemsWeapon> _itemsweapon1;

	static {
		// 物品類型
		_etcItemTypes.put("arrow", new Integer(0));// 箭
		_etcItemTypes.put("wand", new Integer(1));// 魔杖
		_etcItemTypes.put("light", new Integer(2));// 照明
		_etcItemTypes.put("gem", new Integer(3));// 寶石
		_etcItemTypes.put("totem", new Integer(4));// 圖騰
		_etcItemTypes.put("firecracker", new Integer(5));// 煙火
		_etcItemTypes.put("potion", new Integer(6));// 藥水
		_etcItemTypes.put("food", new Integer(7));// 食物
		_etcItemTypes.put("scroll", new Integer(8));// 卷軸
		_etcItemTypes.put("questitem", new Integer(9));// 任務物品
		_etcItemTypes.put("spellbook", new Integer(10));// 魔法書
		_etcItemTypes.put("petitem", new Integer(11));// 寵物物品
		_etcItemTypes.put("other", new Integer(12));// 其他
		_etcItemTypes.put("material", new Integer(13));// 材料
		_etcItemTypes.put("event", new Integer(14));// 活動物品
		_etcItemTypes.put("sting", new Integer(15));// 飛刀
		_etcItemTypes.put("treasure_box", new Integer(16));// 寶盒

		// 物品使用封包類型
		_useTypes.put("petitem", new Integer(-12)); // 寵物道具
		_useTypes.put("other", new Integer(-11)); // 對讀取方法調用無法分類的物品
		_useTypes.put("power", new Integer(-10)); // 加速藥水
		_useTypes.put("book", new Integer(-9)); // 技術書
		_useTypes.put("makecooking", new Integer(-8));// 料理書
		_useTypes.put("hpr", new Integer(-7));// 增HP道具
		_useTypes.put("mpr", new Integer(-6));// 增MP道具
		_useTypes.put("ticket", new Integer(-5)); // 食人妖精競賽票/死亡競賽票/彩票
		_useTypes.put("petcollar", new Integer(-4)); // 項圈
		_useTypes.put("sting", new Integer(-3)); // 飛刀
		_useTypes.put("arrow", new Integer(-2)); // 箭
		_useTypes.put("none", new Integer(-1)); // 無法使用(材料等)
		_useTypes.put("normal", new Integer(0));// 一般物品
		_useTypes.put("weapon", new Integer(1));// 武器
		_useTypes.put("armor", new Integer(2));// 盔甲
		_useTypes.put("spell_1", new Integer(3)); // 創造怪物魔杖(無須選取目標 - 無數量:沒有任何事情發生)
		_useTypes.put("4", new Integer(4)); // 希望魔杖 XXX
		_useTypes.put("spell_long", new Integer(5)); // 魔杖類型(須選取目標/座標)
		_useTypes.put("ntele", new Integer(6));// 瞬間移動卷軸
		_useTypes.put("identify", new Integer(7));// 鑒定卷軸
		_useTypes.put("res", new Integer(8));// 復活卷軸
		_useTypes.put("home", new Integer(9)); // 傳送回家的卷軸
		_useTypes.put("light", new Integer(10)); // 照明道具
		_useTypes.put("11", new Integer(11)); // 未分類的卷軸 XXX
		_useTypes.put("letter", new Integer(12));// 信紙
		_useTypes.put("letter_card", new Integer(13)); // 信紙(寄出)
		_useTypes.put("choice", new Integer(14));// 請選擇一個物品(道具欄位)
		_useTypes.put("instrument", new Integer(15));// 哨子
		_useTypes.put("sosc", new Integer(16));// 變形卷軸
		_useTypes.put("spell_short", new Integer(17)); // 選取目標 (近距離)
		_useTypes.put("T", new Integer(18));// T恤
		_useTypes.put("cloak", new Integer(19));// 斗篷
		_useTypes.put("glove", new Integer(20)); // 手套
		_useTypes.put("boots", new Integer(21));// 靴
		_useTypes.put("helm", new Integer(22));// 頭盔
		_useTypes.put("ring", new Integer(23));// 戒指
		_useTypes.put("amulet", new Integer(24));// 項鏈
		_useTypes.put("shield", new Integer(25));// 盾牌
		_useTypes.put("guarder", new Integer(25));// 臂甲
		_useTypes.put("dai", new Integer(26));// 對武器施法的卷軸
		_useTypes.put("zel", new Integer(27));// 對盔甲施法的卷軸
		_useTypes.put("blank", new Integer(28));// 空的魔法卷軸
		_useTypes.put("btele", new Integer(29));// 瞬間移動卷軸(祝福)
		_useTypes.put("spell_buff", new Integer(30)); // 魔法卷軸選取目標 (遠距離 無XY座標傳回)
		_useTypes.put("ccard", new Integer(31));// 聖誕卡片
		_useTypes.put("ccard_w", new Integer(32));// 聖誕卡片(寄出)
		_useTypes.put("vcard", new Integer(33));// 情人節卡片
		_useTypes.put("vcard_w", new Integer(34));// 情人節卡片(寄出)
		_useTypes.put("wcard", new Integer(35));// 白色情人節卡片
		_useTypes.put("wcard_w", new Integer(36));// 白色情人節卡片(寄出)
		_useTypes.put("belt", new Integer(37));// 腰帶
		_useTypes.put("food", new Integer(38)); // 食物
		_useTypes.put("spell_long2", new Integer(39)); // 選取目標 (遠距離)
		_useTypes.put("earring", new Integer(40)); // 耳環
		_useTypes.put("fishing_rod", new Integer(42));// 釣魚桿
		//_useTypes.put("aid", new Integer(44)); // 副助道具
		_useTypes.put("enc", new Integer(46)); // 飾品強化卷軸
		
		_useTypes.put("aidr", new Integer(43));//3.5TW輔助右
		_useTypes.put("aidl", new Integer(44));//3.5TW輔助左
		_useTypes.put("aidm", new Integer(45));//3.5TW輔助中  
		_useTypes.put("aidr2", new Integer(48));//3.5TW輔助右下
		_useTypes.put("aidl2", new Integer(47));//3.5TW輔助左下
		
		_useTypes.put("choice_doll", new Integer(55));//請選擇魔法娃娃

		_armorTypes.put("none", new Integer(0));
		_armorTypes.put("helm", new Integer(1));// 頭盔
		_armorTypes.put("armor", new Integer(2));// 盔甲
		_armorTypes.put("T", new Integer(3));// 內衣
		_armorTypes.put("cloak", new Integer(4));// 斗篷
		_armorTypes.put("glove", new Integer(5));// 手套
		_armorTypes.put("boots", new Integer(6));// 長靴
		_armorTypes.put("shield", new Integer(7));// 盾牌
		_armorTypes.put("amulet", new Integer(8));// 項鏈
		_armorTypes.put("ring", new Integer(9));// 戒指
		_armorTypes.put("belt", new Integer(10));// 腰帶
		_armorTypes.put("ring2", new Integer(11));// 戒指2
		_armorTypes.put("earring", new Integer(12));// 耳環
		_armorTypes.put("guarder", new Integer(13));// 臂甲
		
		_armorTypes.put("aidl", new Integer(14)); // 副助道具
		_armorTypes.put("aidr", new Integer(15)); // 副助道具
		_armorTypes.put("aidm", new Integer(16)); // 副助道具
		_armorTypes.put("aidl2", new Integer(17)); // 副助道具
		_armorTypes.put("aidr2", new Integer(18)); // 副助道具

		_weaponTypes.put("none", new Integer(0));// 空手
		_weaponTypes.put("sword", new Integer(1));// 劍(單手)
		_weaponTypes.put("dagger", new Integer(2));// 匕首(單手)
		_weaponTypes.put("tohandsword", new Integer(3));// 雙手劍(雙手)
		_weaponTypes.put("bow", new Integer(4));// 弓(雙手)
		_weaponTypes.put("spear", new Integer(5));// 矛(雙手)
		_weaponTypes.put("blunt", new Integer(6));// 斧(單手)
		_weaponTypes.put("staff", new Integer(7));// 魔杖(單手)
		_weaponTypes.put("throwingknife", new Integer(8));// 飛刀
		_weaponTypes.put("arrow", new Integer(9));// 箭
		_weaponTypes.put("gauntlet", new Integer(10));// 鐵手甲
		_weaponTypes.put("claw", new Integer(11));// 鋼爪(雙手)
		_weaponTypes.put("edoryu", new Integer(12));// 雙刀(雙手)
		_weaponTypes.put("singlebow", new Integer(13));// 弓(單手)
		_weaponTypes.put("singlespear", new Integer(14));// 矛(單手)
		_weaponTypes.put("tohandblunt", new Integer(15));// 雙手斧(雙手)
		_weaponTypes.put("tohandstaff", new Integer(16));// 魔杖(雙手)
		_weaponTypes.put("kiringku", new Integer(17));// 奇古獸(單手)
		_weaponTypes.put("chainsword", new Integer(18));// 鎖鏈劍(單手)

		_weaponId.put("sword", new Integer(4));// 劍
		_weaponId.put("dagger", new Integer(4));// 匕首
		_weaponId.put("tohandsword", new Integer(4));// 雙手劍
		_weaponId.put("bow", new Integer(20));// 弓
		_weaponId.put("blunt", new Integer(11));// 斧(單手)
		_weaponId.put("spear", new Integer(24));// 矛(雙手)
		_weaponId.put("staff", new Integer(40));// 魔杖
		_weaponId.put("throwingknife", new Integer(2922));// 飛刀
		_weaponId.put("arrow", new Integer(66));// 箭
		_weaponId.put("gauntlet", new Integer(62));// 鐵手甲
		_weaponId.put("claw", new Integer(58));// 鋼爪
		_weaponId.put("edoryu", new Integer(54));// 雙刀
		_weaponId.put("singlebow", new Integer(20));// 弓(單手)
		_weaponId.put("singlespear", new Integer(24));// 矛(單手)
		_weaponId.put("tohandblunt", new Integer(11));// 雙手斧
		_weaponId.put("tohandstaff", new Integer(40));// 魔杖(雙手)
		_weaponId.put("kiringku", new Integer(58));// 奇古獸
		_weaponId.put("chainsword", new Integer(24));// 鎖鏈劍

		// 材質
		_materialTypes.put("none", new Integer(0));// 無
		_materialTypes.put("liquid", new Integer(1));// 憶體
		_materialTypes.put("web", new Integer(2));// 蠟
		_materialTypes.put("vegetation", new Integer(3));// 植物
		_materialTypes.put("animalmatter", new Integer(4));// 動物
		_materialTypes.put("paper", new Integer(5));// 紙
		_materialTypes.put("cloth", new Integer(6));// 布
		_materialTypes.put("leather", new Integer(7));// 皮革
		_materialTypes.put("wood", new Integer(8));// 木
		_materialTypes.put("bone", new Integer(9));// 骨頭
		_materialTypes.put("dragonscale", new Integer(10));// 龍鱗
		_materialTypes.put("iron", new Integer(11));// 鐵
		_materialTypes.put("steel", new Integer(12));// 鋼
		_materialTypes.put("copper", new Integer(13));// 銅
		_materialTypes.put("silver", new Integer(14));// 銀
		_materialTypes.put("gold", new Integer(15));// 黃金
		_materialTypes.put("platinum", new Integer(16));// 白金
		_materialTypes.put("mithril", new Integer(17));// 米索莉
		_materialTypes.put("blackmithril", new Integer(18));// 黑色米索莉
		_materialTypes.put("glass", new Integer(19));// 玻璃
		_materialTypes.put("gemstone", new Integer(20));// 寶石
		_materialTypes.put("mineral", new Integer(21));// 礦物
		_materialTypes.put("oriharukon", new Integer(22));// 奧裡哈魯根
	}

	public static ItemTable get() {
		if (_instance == null) {
			_instance = new ItemTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		_itemsetcitem = this.allItemsEtcItem();
		_itemsweapon = this.allItemsWeapon();
		_itemsweapon1 = this.allItemsWeapon1();
		_itemsarmor = this.allItemsArmor();
		_itemsarmor1 = allItemsArmor1();
		this.buildFastLookupTable();
		_log.info("載入道具,武器,防具資料: "
				+ _itemsetcitem.size() + "+" + _itemsweapon.size() + "+" +_itemsarmor.size() + 
				"=" 
				+ (_itemsetcitem.size() + _itemsweapon.size() + _itemsarmor.size())
				+ "(" + timer.get() + "ms)");
	}
	
	/**
	 * 額外新增道具載入
	 * @return
	 */
	private Map<Integer, L1ItemsEtcItem> allItemsEtcItem() {
		final Map<Integer, L1ItemsEtcItem> result = new HashMap<Integer, L1ItemsEtcItem>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1ItemsEtcItem item = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `etcitem`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				item = ItemMapper.get().mapEtcItem(rs);
				final int itemid = item.getItemId();
				
				// 處理映射類型
				item.setType((_etcItemTypes.get(rs.getString("item_type"))).intValue());
				item.setUseType(_useTypes.get(rs.getString("use_type")).intValue());
				item.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());

				ItemClass.get().addList(itemid, item.getclassname(), 0);
				result.put(new Integer(itemid), item);
			}
		} catch (final NullPointerException e) {
			_log.error("加載失敗: " + item.getItemId(), e);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}
	
	/**
	 * 新增武器載入
	 * @return
	 */
	private Map<Integer, L1ItemsWeapon> allItemsWeapon() {
		final Map<Integer, L1ItemsWeapon> result = new HashMap<Integer, L1ItemsWeapon>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1ItemsWeapon weapon = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `weapon`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				weapon = ItemMapper.get().mapWeapon(rs);
				final int itemid = weapon.getItemId();
				
				// 處理映射類型
				weapon.setType((_weaponTypes.get(rs.getString("type"))).intValue());
				weapon.setType1((_weaponId.get(rs.getString("type"))).intValue());
				weapon.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());

				ItemClass.get().addList(itemid, weapon.getclassname(), 1);
				result.put(new Integer(itemid), weapon);
			}
		} catch (final NullPointerException e) {
			_log.error("加載失敗: " + weapon.getItemId(), e);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
		return result;
	}
	private Map<Integer, L1ItemsWeapon> allItemsWeapon1() {
		final Map<Integer, L1ItemsWeapon> result = new HashMap<Integer, L1ItemsWeapon>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1ItemsWeapon weapon = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `weapon_bless`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				weapon = ItemMapper.get().mapWeapon(rs);
				final int itemid = weapon.getItemId();
				
				// 處理映射類型
				weapon.setType((_weaponTypes.get(rs.getString("type"))).intValue());
				weapon.setType1((_weaponId.get(rs.getString("type"))).intValue());
				weapon.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());

				ItemClass.get().addList(itemid, weapon.getclassname(), 1);
				result.put(new Integer(itemid), weapon);
			}
		} catch (final NullPointerException e) {
			_log.error("加載失敗: " + weapon.getItemId(), e);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
		return result;
	}
	/**
	 * 新增防具載入
	 * @return
	 */
	private Map<Integer, L1ItemsArmor> allItemsArmor() {
		final Map<Integer, L1ItemsArmor> result = new HashMap<Integer, L1ItemsArmor>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1ItemsArmor armor = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `armor`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				armor = ItemMapper.get().mapArmor(rs);
				final int itemid = armor.getItemId();
				
				final String typeStr = rs.getString("type");
				// 處理 Type 映射
				if (_armorTypes.containsKey(typeStr)) {
					armor.setType((_armorTypes.get(typeStr))).intValue();
				} else {
					if (typeStr.contains("aid_custom_")) {
						armor.setType(Integer.parseInt(typeStr.replaceAll("aid_custom_", "")) + 0x7FFF);
					}
				}
				
				// 處理 UseType 映射
				if (_useTypes.containsKey(typeStr)) {
					armor.setUseType((_useTypes.get(typeStr))).intValue();
				} else {
					if (typeStr.contains("aid_custom_")) {
						armor.setUseType(Integer.parseInt(typeStr.replaceAll("aid_custom_", "")) + 0x7FFF);
					}
				}
				
				armor.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());

				ItemClass.get().addList(itemid, armor.getclassname(), 2);
				result.put(new Integer(itemid), armor);
			}
		} catch (final NullPointerException e) {
			_log.error("加載失敗: " + armor.getItemId(), e);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
		return result;
	}
	private Map<Integer, L1ItemsArmor> allItemsArmor1() {
		final Map<Integer, L1ItemsArmor> result = new HashMap<Integer, L1ItemsArmor>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1ItemsArmor armor = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `armor_bless`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				armor = ItemMapper.get().mapArmor(rs);
				final int itemid = armor.getItemId();
				
				final String typeStr = rs.getString("type");
				// 處理 Type 映射
				if (_armorTypes.containsKey(typeStr)) {
					armor.setType((_armorTypes.get(typeStr))).intValue();
				} else {
					if (typeStr.contains("aid_custom_")) {
						armor.setType(Integer.parseInt(typeStr.replaceAll("aid_custom_", "")) + 0x7FFF);
					}
				}
				
				// 處理 UseType 映射
				if (_useTypes.containsKey(typeStr)) {
					armor.setUseType((_useTypes.get(typeStr))).intValue();
				} else {
					if (typeStr.contains("aid_custom_")) {
						armor.setUseType(Integer.parseInt(typeStr.replaceAll("aid_custom_", "")) + 0x7FFF);
					}
				}
				
				armor.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());

				ItemClass.get().addList(itemid, armor.getclassname(), 2);
				result.put(new Integer(itemid), armor);
			}
		} catch (final NullPointerException e) {
			_log.error("加載失敗: " + armor.getItemId(), e);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
		return result;
	}
	private void buildFastLookupTable() {
		int highestId = 0;

		final Collection<L1ItemsEtcItem> itemsetcitem = _itemsetcitem.values();
		for (final L1ItemsEtcItem item : itemsetcitem) {
			if (item.getItemId() > highestId) {
				highestId = item.getItemId();
			}
		}

		final Collection<L1ItemsWeapon> itemsweapon = _itemsweapon.values();
		for (final L1ItemsWeapon weapon : itemsweapon) {
			if (weapon.getItemId() > highestId) {
				highestId = weapon.getItemId();
			}
		}
		final Collection<L1ItemsWeapon> itemsweapon1 = _itemsweapon1.values();
		for (final L1ItemsWeapon weapon : itemsweapon1) {
			if (weapon.getItemId() > highestId) {
				highestId = weapon.getItemId();
			}
		}
		final Collection<L1ItemsArmor> itemsarmors = _itemsarmor.values();
		for (final L1ItemsArmor armor : itemsarmors) {
			if (armor.getItemId() > highestId) {
				highestId = armor.getItemId();
			}
		}
		final Collection<L1ItemsArmor> itemsarmors1 = _itemsarmor1.values();
		for (final L1ItemsArmor armor : itemsarmors1) {
			if (armor.getItemId() > highestId) {
				highestId = armor.getItemId();
			}
		}
		this._allTemplates = new L1Item[highestId + 1];

		for (final Iterator<Integer> iter = _itemsetcitem.keySet().iterator(); iter.hasNext();) {
			final Integer id = iter.next();
			final L1ItemsEtcItem item = _itemsetcitem.get(id);
			this._allTemplates[id.intValue()] = item;
		}

		for (final Iterator<Integer> iter = _itemsweapon.keySet().iterator(); iter.hasNext();) {
			final Integer id = iter.next();
			final L1ItemsWeapon item = _itemsweapon.get(id);
			this._allTemplates[id.intValue()] = item;
		}
		for (final Iterator<Integer> iter = _itemsweapon1.keySet().iterator(); iter.hasNext();) {
			final Integer id = iter.next();
			final L1ItemsWeapon item = _itemsweapon1.get(id);
			this._allTemplates[id.intValue()] = item;
		}
		for (final Iterator<Integer> iter = _itemsarmor.keySet().iterator(); iter.hasNext();) {
			final Integer id = iter.next();
			final L1ItemsArmor item = _itemsarmor.get(id);
			this._allTemplates[id.intValue()] = item;
		}
		for (final Iterator<Integer> iter = _itemsarmor1.keySet().iterator(); iter.hasNext();) {
			final Integer id = iter.next();
			final L1ItemsArmor item = _itemsarmor1.get(id);
			this._allTemplates[id.intValue()] = item;
		}
	}
	
	/**
	 * 具有套裝設置的物件 加入效果數字陣列
	 */
	public void se_mode() {
		final PerformanceTimer timer = new PerformanceTimer();
		for (final L1Item item : this._allTemplates) {
			if (item != null) {
				for (final Integer key : ArmorSet.getAllSet().keySet()) {
					// 套裝資料
					final ArmorSet armorSet = ArmorSet.getAllSet().get(key);
					// 套裝中組件
					if (armorSet.isPartOfSet(item.getItemId())) {
						item.set_mode(armorSet.get_mode());
					}
				}
			}
		}
		_log.info("載入套裝效果數字陣列: " + timer.get() + "ms)");
	}

	/**
	 * 傳回指定編號物品資料
	 * @param id
	 * @return
	 */
	public L1Item getTemplate(final int id) {
		try {
			return this._allTemplates[id];
			
		} catch (final Exception e) {
		}
		return null;
	}

	/**
	 * 傳回指定名稱物品資料
	 * @param nameid
	 * @return
	 */
	public L1Item getTemplate(final String nameid) {
		for (final L1Item item : this._allTemplates) {
			if ((item != null) && item.getNameId().equals(nameid)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 產生新物件
	 * @param itemId
	 * @return
	 */
	public L1ItemInstance createItem(final int itemId) {
		final L1Item temp = this.getTemplate(itemId);
		if (temp == null) {
			return null;
		}
		final L1ItemInstance item = new L1ItemInstance();
		item.setId(IdFactory.get().nextId());
		item.setItem(temp);
		item.setBless(temp.getBless());
		item.setCanAbilityType(temp.getAbility());
		
		World.get().storeObject(item);
		return item;
	}

	/**
	 * 依名稱(NameId)找回itemid
	 * @param name
	 * @return
	 */
	public int findItemIdByName(final String name) {
		int itemid = 0;
		for (final L1Item item : this._allTemplates) {
			if ((item != null) && item.getNameId().equals(name)) {
				itemid = item.getItemId();
				break;
			}
		}
		return itemid;
	}

	/**
	 * 依名稱(中文)找回itemid
	 * @param name
	 * @return
	 */
	public int findItemIdByNameWithoutSpace(final String name) {
		int itemid = 0;
		for (final L1Item item : this._allTemplates) {
			if ((item != null) && item.getName().replace(" ", "").equals(name)) {
				itemid = item.getItemId();
				break;
			}
		}
		return itemid;
	}
}
