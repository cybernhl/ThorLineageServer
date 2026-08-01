package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.BLESS_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.HOLY_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.SHADOW_FANG;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.lineage.data.item_etcitem.hole.CustomHoleGemData;
import com.lineage.data.item_etcitem.hole.CustomHoleGemDataByArmor;
import com.lineage.server.datatables.CustomAttachStatTable;
import com.lineage.server.datatables.CustomSpecialStatTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.GemItemSet;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.L1EquipmentTimer;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPower_name;
import com.lineage.server.templates.L1ItemGem;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.utils.RangeLong;

/**
 * 物品類控制項
 * @author dexc
 *
 */
public class L1ItemInstance extends L1Object {

	private static final Log _log = LogFactory.getLog(L1ItemInstance.class);

	private static final long serialVersionUID = 1L;

	private long _count;

	private int _itemId;

	private boolean _isEquipped = false;

	private boolean _isEquippedTemp = false;

	private int _enchantLevel;// 物品強化質

	private boolean _isIdentified = false;

	private int _durability;

	private int _chargeCount;

	private int _remainingTime;

	private int _lastWeight;

	private boolean _isRunning = false;// 裝備強化時間軸

	private int _bless;

	private int _attrEnchantKind;

	private int _attrEnchantLevel;

	private String _gamno = null;

	private L1Item _item;

	private Timestamp _lastUsed = null;
	
	private final LastStatus _lastStatus = new LastStatus();

	private L1PcInstance _pc;

	private EnchantTimer _timer;

	private CustomAttachStatTable.CustomAttachStat custom_attach_stat;

	private int attach_index = 0;
	private int specialStat = 0;
	private int gem_hole = 0;
	private int gem_hole_index = 0;
	private int can_ability_type = 0;
	private int ability_pos_1 = 0;
	private int ability_pos_2 = 0;
	private int ability_pos_3 = 0;
	/** [原碼] 裝備保護卷軸 */
	private boolean proctect = false;

	/**
	 * [原碼] 裝備保護卷軸
	 *
	 * @return
	 */
	public boolean getproctect() {
		return proctect;
	}

	/**
	 * [原碼] 裝備保護卷軸
	 *
	 * @param i
	 */
	public void setproctect(boolean i) {
		proctect = i;
	}

	/** [原碼] 裝備保護卷軸 */
	private int _ProctectRom = 0;

	/**
	 * [原碼] 裝備保護卷軸
	 *
	 * @return
	 */
	public int getProctectRom() {
		return _ProctectRom;
	}

	/**
	 * [原碼] 裝備保護卷軸
	 *
	 * @param i
	 */
	public void setProctectRom(int i) {
		_ProctectRom = i;
	}

	private int _ProctectType = 0;

	public int getProctectType() {
		return _ProctectType;
	}

	public void setProctectType(int i) {
		_ProctectType = i;
	}
	public L1ItemInstance() {
		_count = 1;
		_enchantLevel = 0;
	}

	public L1ItemInstance(final L1Item item, final long count) {
		this();
		setItem(item);
		setCount(count);
	}

	public final void setAttachStat(final CustomAttachStatTable.CustomAttachStat stat) {
		this.custom_attach_stat = stat;
	}

	public final CustomAttachStatTable.CustomAttachStat getAttachStat() {
		return this.custom_attach_stat;
	}
	
	

	/**
	 * 傳回鑒定狀態
	 *
	 * @return 確認濟true、未確認false。
	 */
	public boolean isIdentified() {
		return _isIdentified;
	}

	/**
	 * 設置鑒定狀態
	 *
	 * @param identified
	 *            確認濟true、未確認false。
	 */
	public void setIdentified(final boolean identified) {
		_isIdentified = identified;
	}

	/**
	 * 傳回NAMEID
	 * @return
	 */
	public String getName() {
		//_item.getName();
		return _item.getNameId();
	}

	/**
	 * 傳回數量
	 *
	 * @return
	 */
	public long getCount() {
		return _count;
	}

	/**
	 * 數量設置
	 *
	 * @param count
	 */
	public void setCount(final long count) {
		//System.out.println("數量設置:" + count);
		_count = count;
	}

	/**
	 * 場次代號
	 * @return
	 */
	public String getGamNo() {
		return _gamno;
	}

	/**
	 * 設定場次代號
	 * @param gamno
	 */
	public void setGamNo(final String gamno) {
		_gamno = gamno;
	}

	/**
	 * 物品裝備狀態
	 *
	 * @return 已裝備true、未裝備false。
	 */
	public boolean isEquipped() {
		return _isEquipped;
	}

	/**
	 * 設置物品裝備狀態
	 *
	 * @param equipped 已裝備true、未裝備false。
	 */
	public void setEquipped(final boolean equipped) {
		_isEquipped = equipped;
	}

	public L1Item getItem() {
		return _item;
	}

	public void setItem(final L1Item item) {
		_item = item;
		_itemId = item.getItemId();
	}

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(final int itemId) {
		_itemId = itemId;
	}

	/**
	 * 物品是否可以堆疊
	 * @return true:可以 false:不可以
	 */
	public boolean isStackable() {
		return _item.isStackable();
	}

	@Override
	public void onAction(final L1PcInstance player) {
	}

	/**
	 * 物品強化質
	 * @return
	 */
	public int getEnchantLevel() {
		return _enchantLevel;
	}

	/**
	 * 設定物品強化質
	 * @param enchantLevel
	 */
	public void setEnchantLevel(final int enchantLevel) {
		_enchantLevel = enchantLevel;
	}

	public final int getAttachIndex() {
		return this.attach_index;
	}

	public final void setAttachIndex(final int attach_index) {
		this.attach_index = attach_index;
		if (attach_index > 0) {
			if (CustomAttachStatTable.get().getWeaponDatas().containsKey(attach_index)) {
				setAttachStat(CustomAttachStatTable.get().getWeaponDatas().get(attach_index));
			} else if (CustomAttachStatTable.get().getArmorDatas().containsKey(attach_index)) {
				setAttachStat(CustomAttachStatTable.get().getArmorDatas().get(attach_index));
			}
		} else {
			setAttachStat(null);
		}
	}
	public final int getSpecialStat() {
		return this.specialStat;
	}
	public final void setSpecialStat(final int specialStat) {
		this.specialStat = specialStat;
	}

	public final int getGemHole() {
		return this.gem_hole;
	}

	public final void setGemHole(final int gem_hole) {
		this.gem_hole = gem_hole;
	}

	public final int getGemHoleIndex() {
		return this.gem_hole_index;
	}

	public final void setGemHoleIndex(final int gem_hole_index) {
		this.gem_hole_index = gem_hole_index;
	}
	public int getAbilityPos1ID() {
		return this.ability_pos_1;
	}
	public int getAbilityPos2ID() {
		return this.ability_pos_2;
	}
	public int getAbilityPos3ID() {
		return this.ability_pos_3;
	}
	public void setAbilityPos1(int set) {
		this.ability_pos_1 = set;
	}
	public void setAbilityPos2(int set) {
		this.ability_pos_2 = set;
	}
	public void setAbilityPos3(int set) {
		this.ability_pos_3 = set;
	}
	public int getCanAbilityType() {
		return this.can_ability_type;
	}
	public void setCanAbilityType(int set) {
		this.can_ability_type = set;
	}
	public int get_gfxid() {
		return _item.getGfxId();
	}

	/**
	 * 傳回武器損壞度
	 * @return
	 */
	public int get_durability() {
		return _durability;
	}

	/**
	 * 傳回可用次數
	 * @return
	 */
	public int getChargeCount() {
		return _chargeCount;
	}

	/**
	 * 設置可用次數
	 * @param i
	 */
	public void setChargeCount(final int i) {
		_chargeCount = i;
	}

	/**
	 * 剩餘時間
	 * @return
	 */
	public int getRemainingTime() {
		return _remainingTime;
	}

	/**
	 * 剩餘時間
	 * @param i
	 */
	public void setRemainingTime(final int i) {
		_remainingTime = i;
	}

	public void setLastUsed(final Timestamp t) {
		_lastUsed = t;
	}

	public Timestamp getLastUsed() {
		return _lastUsed;
	}

	public int getLastWeight() {
		return _lastWeight;
	}

	public void setLastWeight(final int weight) {
		_lastWeight = weight;
	}

	/**
	 * 祝福 0/128
	 * 一般 1/129
	 * 詛咒 2/130
	 * ?? 3/131
	 * @param i
	 */
	public void setBless(final int i) {
		_bless = i;
	}

	/**
	 * 祝福 0/128
	 * 一般 1/129
	 * 詛咒 2/130
	 * ?? 3/131
	 * @return
	 */
	public int getBless() {
		return _bless;
	}

	/**
	 * 屬性強化類型
	 * @param i
	 */
	public void setAttrEnchantKind(final int i) {
		_attrEnchantKind = i;
	}

	/**
	 * 屬性強化類型
	 * @return
	 */
	public int getAttrEnchantKind() {
		return _attrEnchantKind;
	}

	/**
	 * 屬性強化質
	 * @param i
	 */
	public void setAttrEnchantLevel(final int i) {
		_attrEnchantLevel = i;
	}
	
	private int _card_use = 0;// 0:未使用 1:使用中 2:到期

	/**
	 * 0:未使用 1:使用中 2:到期
	 * @return
	 */
	public int get_card_use() {
		return _card_use;
	}
	
	/**
	 * 0:未使用 1:使用中 2:到期
	 * @param card_use
	 */
	public void set_card_use(int card_use) {
		_card_use = card_use;
	}

	/**
	 * 屬性強化質
	 * @return
	 */
	public int getAttrEnchantLevel() {
		return _attrEnchantLevel;
	}

	/*
	 * 耐久性、0~127 -值許可。
	 */
	public void set_durability(int i) {
		if (i < 0) {
			i = 0;
		}

		if (i > 127) {
			i = 127;
		}
		_durability = i;
	}

	public int getWeight() {
		if (getItem().getWeight() == 0) {
			return 0;
			
		} else {
			return (int) Math.max(getCount() * getItem().getWeight() / 1000, 1);
		}
	}

	/**
	 * 前回DB保存際格納
	 */
	public class LastStatus {

		public long count;

		public int itemId;

		public boolean isEquipped = false;

		public int enchantLevel;

		public boolean isIdentified = true;

		public int durability;

		public int chargeCount;

		public int remainingTime;

		public Timestamp lastUsed = null;

		public int bless;

		public int attrEnchantKind;

		public int attrEnchantLevel;

		//private String gamno;

		public void updateAll() {
			count = getCount();
			itemId = getItemId();
			isEquipped = isEquipped();
			isIdentified = isIdentified();
			enchantLevel = getEnchantLevel();
			durability = get_durability();
			chargeCount = getChargeCount();
			remainingTime = getRemainingTime();
			lastUsed = getLastUsed();
			bless = getBless();
			attrEnchantKind = getAttrEnchantKind();
			attrEnchantLevel = getAttrEnchantLevel();
			//this.gamno = L1ItemInstance.this.getGamNo();
		}

		public void updateCount() {
			count = getCount();
		}

		public void updateItemId() {
			itemId = getItemId();
		}

		public void updateEquipped() {
			isEquipped = isEquipped();
		}

		public void updateIdentified() {
			isIdentified = isIdentified();
		}

		public void updateEnchantLevel() {
			enchantLevel = getEnchantLevel();
		}

		/**
		 * 更新武器損壞度
		 */
		public void updateDuraility() {
			durability = get_durability();
		}

		public void updateChargeCount() {
			chargeCount = getChargeCount();
		}

		public void updateRemainingTime() {
			remainingTime = getRemainingTime();
		}

		public void updateLastUsed() {
			lastUsed = getLastUsed();
		}

		public void updateBless() {
			bless = getBless();
		}

		public void updateAttrEnchantKind() {
			attrEnchantKind = getAttrEnchantKind();
		}

		public void updateAttrEnchantLevel() {
			attrEnchantLevel = getAttrEnchantLevel();
		}

		/*public void updateGamno() {
			this.gamno = L1ItemInstance.this.getGamNo();
		}*/
	}

	public LastStatus getLastStatus() {
		return _lastStatus;
	}

	/**
	 * 前回DB保存時變化集合返。
	 */
	public int getRecordingColumns() {
		int column = 0;

		if (getCount() != _lastStatus.count) {
			column += L1PcInventory.COL_COUNT;
		}
		if (getItemId() != _lastStatus.itemId) {
			column += L1PcInventory.COL_ITEMID;
		}
		if (isEquipped() != _lastStatus.isEquipped) {
			column += L1PcInventory.COL_EQUIPPED;
		}
		if (getEnchantLevel() != _lastStatus.enchantLevel) {
			column += L1PcInventory.COL_ENCHANTLVL;
		}
		if (get_durability() != _lastStatus.durability) {
			column += L1PcInventory.COL_DURABILITY;
		}
		if (getChargeCount() != _lastStatus.chargeCount) {
			column += L1PcInventory.COL_CHARGE_COUNT;
		}
		if (getLastUsed() != _lastStatus.lastUsed) {
			column += L1PcInventory.COL_DELAY_EFFECT;
		}
		if (isIdentified() != _lastStatus.isIdentified) {
			column += L1PcInventory.COL_IS_ID;
		}
		if (getRemainingTime() != _lastStatus.remainingTime) {
			column += L1PcInventory.COL_REMAINING_TIME;
		}
		if (getBless() != _lastStatus.bless) {
			column += L1PcInventory.COL_BLESS;
		}
		if (getAttrEnchantKind() != _lastStatus.attrEnchantKind) {
			column += L1PcInventory.COL_ATTR_ENCHANT_KIND;
		}
		if (getAttrEnchantLevel() != _lastStatus.attrEnchantLevel) {
			column += L1PcInventory.COL_ATTR_ENCHANT_LEVEL;
		}

		return column;
	}

	/**
	 * 背包/倉庫 物件完整名稱取回<br>
	 */
	public String getNumberedViewName(final long count) {
		final StringBuilder name = new StringBuilder(getNumberedName(count, true));
        int itemType2 = getItem().getType2();
		int itemId = getItem().getItemId();
		if (this.getAttachIndex() > 0) { // 暫時使用
			if (getItem().getUseType() == 1) {
				if (CustomAttachStatTable.get().getWeaponDatas().containsKey(this.getAttachIndex())) {
					final CustomAttachStatTable.CustomAttachStat data = CustomAttachStatTable.get().getWeaponDatas().get(this.getAttachIndex());
					name.append(data.get附魔名稱());
				}
			} else {
				if (CustomAttachStatTable.get().getArmorDatas().containsKey(this.getAttachIndex())) {
					final CustomAttachStatTable.CustomAttachStat data = CustomAttachStatTable.get().getArmorDatas().get(this.getAttachIndex());
					name.append(data.get附魔名稱());
				}
			}
		}
		if (_time != null) {
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			name.append("[" + sdf.format(_time) + "]"); // 使用期限
		}
		if (getCanAbilityType() == 1) {
			name.append("[潛力:未鑑定]");
		} else if (getCanAbilityType() == 2) {
			name.append("[一排潛]");
		} else if (getCanAbilityType() == 3) {
			name.append("[兩排潛]");
		} else if (getCanAbilityType() == 4) {
			name.append("[三排潛]");
		}
		//武器寶石鑲嵌系統
		if (get_Gem_name() != null && _item.getUseType() == 1) {
			int dmgcount = 0;
			dmgcount += get_Gem_name().get_punchcount();
			name.append("[已鑲嵌:");
			name.append(dmgcount);
			name.append("|可鑲嵌:");
			name.append(GemItemSet.ARMORHOLE);
			name.append("]");
		}
		String string = "";


		if (_power_name != null) {
			name.append(" \\fT");
			for (int i = 0 ; i < _power_name.get_hole_count() ; i++) {
				switch (i) {
				case 0:
					name.append(set_hole_name(_power_name.get_hole_1()));
					break;
				case 1:
					name.append(set_hole_name(_power_name.get_hole_2()));
					break;
				case 2:
					name.append(set_hole_name(_power_name.get_hole_3()));
					break;
				case 3:
					name.append(set_hole_name(_power_name.get_hole_4()));
					break;
				case 4:
					name.append(set_hole_name(_power_name.get_hole_5()));
					break;
				}
			}
		}
		
	     // 自动内挂
 		/*if (isIdentified()
 				&& (getItemId() >= 70429 && getItemId() <= 70440 || getItemId() >= 70534
 						&& getItemId() <= 70535 || getItemId() == 81273 || getItemId() == 270719|| getItemId() == 61001)) {
 			if (!isNowAuto()) {
 				name.append(" (OFF)");
 			} else {
 				name.append(" (ON)");
 			}
 		}*/
		
		switch (_item.getUseType()) {
		default:
			if (isEquippedTemp()) {
				// 防具/武器/道具 類型物件送出使用中物件上方會出現E
				name.append(" ($117)"); // 使用中
			}
			break;
			
		case -12: // 寵物用具
			if (isEquipped()) {
				name.append(" ($117)"); // 使用中(Worn)
			}
			break;

		case -4: // 項圈
			final L1Pet pet = PetReading.get().getTemplate(getId());
			if (pet != null) {
				final L1Npc npc = NpcTable.get().getTemplate(pet.get_npcid());
				name.append("[Lv." + pet.get_level() + "]" + pet.get_name() + " HP" + pet.get_hp() + " " + npc.get_nameid());
			}
			break;
			
		case 1: // 武器
			if (isEquipped()) {
				// 武器 類型物件送出使用中物件上方會出現E
				name.append(" ($9)"); // 揮舞(Armed)
			}
			break;
		case 0: // 普通道具
			if (get_card_use() == 1) {
				name.append(" ($117)"); // 使用中(Worn)
			}
		case 10: // 照明道具
			if (isNowLighting()) {
				name.append(" ($10)");// 打開
			}
			switch (_item.getItemId()) {
			case 40001: // 燈
			case 40002: // 燈籠
				if (getRemainingTime() <= 0) {
					name.append(" ($11)");// 無油
				}
				break;
			}
			break;

		case 2: // 盔甲
		case 18: // T恤
		case 19: // 斗篷
		case 20: // 手套
		case 21: // 靴
		case 22: // 頭盔
		case 23: // 戒指
		case 24: // 項鏈
		case 25: // 盾牌
		case 37: // 腰帶
		case 40: // 耳環
		case 43:// 副助道具
		case 44:// 副助道具
		case 45:// 副助道具
		case 48:// 副助道具
		case 47:// 副助道具
		case 0x7FFF:
		case 0x8000:
		case 0x8001:
		case 0x8002:
		case 0x8003:
		case 0x8004:
		case 0x8005:
		case 0x8006:
		case 0x8007:
		case 0x8008:
			if (isEquipped()) {
				// 防具/道具 類型物件送出使用中物件上方會出現E
				name.append(" ($117)"); // 使用中(Worn)
			}
			break;
		}
		//name.append(":" + _item.getItemId());
		if (getGemHoleIndex() > 0) {
			if (getItem().getUseType() == 1) {
				if (CustomHoleGemData.getInstance().containsKeyByIndex(getGemHoleIndex())) {
					name.append("[").append(CustomHoleGemData.getInstance().getGemDataByIndex(getGemHoleIndex()).getName()).append("]");
				}
			} else {
				if (CustomHoleGemDataByArmor.getInstance().containsKeyByIndex(getGemHoleIndex())) {
					name.append("[").append(CustomHoleGemDataByArmor.getInstance().getGemDataByIndex(getGemHoleIndex()).getName()).append("]");
				}
			}
		} else if (getGemHole() > 0) {
			name.append("[").append("未鑲崁").append("]");
		}
		return name.toString();
	}

	private String set_hole_name(int hole) {
		String string = "";
		switch (hole) {
		case 0:
			string = "◎";
			break;
		case 1:
			string = "力";
			break;
		case 2:
			string = "敏";
			break;
		case 3:
			string = "體";
			break;
		case 4:
			string = "精";
			break;
		case 5:
			string = "智";
			break;
		case 6:
			string = "魅";
			break;
		case 7:
			string = "血";
			break;
		case 8:
			string = "魔";
			break;
		case 9:
			if (this.getItem().getType2() == 1) {
				string = "攻";
			}
			break;
		case 10:
			if (this.getItem().getType2() == 2) {
				string = "防";
			}
			break;
		case 11:
			if (this.getItem().getType2() == 2) {
				string = "抗";
			}
			break;
		}
		return string;
	}

	/**
	 * 背包會倉庫名稱顯示。<br>
	 * 範例: +6 匕首 (揮舞)
	 */
	public String getViewName() {
		return getNumberedViewName(_count);
	}

	/**
	 * 返回顯示名稱。<br>
	 * 範例:強力治癒藥水(50) / +6 匕首
	 */
	public String getLogName() {
		return getNumberedName(_count, true);
	}
	
	/**
	 * 物件完整名稱取回
	 * @param count 數量
	 * @param mode 模式 true:使用NAMEID false:使用中文註解名稱
	 * @return
	 */
	public String getNumberedName(final long count, final boolean mode) {
		final StringBuilder name = new StringBuilder();
		if (this.getSpecialStat() > 0) {
			name.append("強化 ");
		}
		if (getproctect()) {
			name.append("(保護中) ");
		}
		// 顯示特殊屬性物品額外名稱
	
		if (this.isIdentified()) {
			switch (_item.getUseType()) {
			case 1:// 武器
				// 追加值
				if (getEnchantLevel() >= 0) {
					name.append("+" + getEnchantLevel() + " ");
					
				} else if (getEnchantLevel() < 0) {
					name.append(String.valueOf(getEnchantLevel()) + " ");
				}
				// 附加屬性
				final int attrEnchantLevel = getAttrEnchantLevel();
				if (attrEnchantLevel > 0) {
					name.append(attrEnchantLevel());
				}
				break;

			case 2: // 盔甲
			case 20: // 手套
			case 21: // 靴
			case 22: // 頭盔
				
			case 18: // T恤
			case 19: // 斗篷
			case 25: // 盾牌
			case 23:// 戒指
			case 24:// 項鏈
			case 37:// 腰帶
			case 40:// 耳環
				// 追加值
				if (getEnchantLevel() >= 0) {
					name.append("+" + getEnchantLevel() + " ");
					
				} else if (getEnchantLevel() < 0) {
					name.append(String.valueOf(getEnchantLevel()) + " ");
				}
				break;
			}
		}

		if (mode) {
			name.append(_item.getNameId());
			
		} else {
			name.append(_item.getName());
		}
		if (this.getBless() == 0) {
			switch (getItem().getUseType()) {
				case 1: // 武器
				case 2: // 盔甲
				case 18: // T恤
				case 19: // 斗篷
				case 20: // 手套
				case 21: // 靴
				case 22: // 頭盔
				case 25: // 盾牌
				case 40: // 耳環
				case 23: // 戒指
				case 24: // 項鏈
				case 37: // 腰帶
					name.append("[祝福]");
					break;
			}
		}

		if (_item.getUseType() == -5) { // 食人妖精競賽票
			name.append("\\f_[" + getGamNo() + "]");
		}

		if (this.isIdentified()) {
			// 資料庫原始最大可用次數大於0
			if (getItem().getMaxChargeCount() > 0) {
				name.append(" (" + getChargeCount() + ")");
				
			} else {
				switch (_item.getItemId()) {
				case 20383: // 軍馬頭盔
					name.append(" (" + getChargeCount() + ")");
					break;

				default:
					break;
				}
			}

			// 武器/防具 具有使用時間
			if ((getItem().getMaxUseTime() > 0) && (getItem().getType2() != 0)) {
				name.append(" (" + getRemainingTime() + ")");
			}
		}

		if (count > 1) {
			if (count < 1000000000) {
				name.append(" (" + count + ")");

			} else {
				name.append(" (" + RangeLong.scount(count) + ")");
			}
		}

		return name.toString();
	}
	
	// 屬性武器
	private static final String[][] _attrEnchant = new String[][]{
			new String[]{"土靈『一階』", "土靈『二階』", "土靈『三階』", "土靈『四階』", "土靈『五階』"},// 地之, 崩裂, 地靈
			new String[]{"火靈『一階』", "火靈『二階』", "火靈『三階』", "火靈『四階』", "火靈『五階』"},// 火之, 烈焰, 火靈
			new String[]{"水靈『一階』", "水靈『二階』", "水靈『三階』", "水靈『四階』", "水靈『五階』"},// 水之, 海嘯, 水靈
			new String[]{"風之 ", "暴風 ", "風靈 "},// 風之, 暴風, 風靈
			// ADD LOLI
			new String[]{"光之 ", "閃耀 ", "光靈 "},
			new String[]{"暗之 ", "陰影 ", "暗靈 "},
			new String[]{"聖之 ", "神聖 ", "聖靈 "},
			new String[]{"邪之 ", "邪惡 ", "邪靈 "},
			new String[]{"金靈『一階』", "金靈『二階』", "金靈『三階』", "金靈『四階』", "金靈『五階』"},
			new String[]{"木靈『一階』", "木靈『二階』", "木靈『三階』", "木靈『四階』", "木靈『五階』"},
	};
	
	/**
	 * 屬性武器
	 * @return
	 */
	private StringBuilder attrEnchantLevel() {
		final StringBuilder attrEnchant = new StringBuilder();

		final int attrEnchantLevel = this.getAttrEnchantLevel();
		int type = 0;
		switch (this.getAttrEnchantKind()) {
		case 1: // 地
			type = 0;
			break;

		case 2: // 火
			type = 1;
			break;

		case 4: // 水
			type = 2;
			break;
			
		case 8: // 風
			type = 3;
			break;
			
		case 16: // 光
			type = 4;
			break;
			
		case 32: // 暗
			type = 5;
			break;
			
		case 64: // 聖
			type = 6;
			break;
			
		case 128: // 邪
			type = 7;
			break;
		case 256:
			type = 8;
			break;
		case 512:
			type = 9;
			break;
		default:
			break;
		}
		attrEnchant.append(_attrEnchant[type][attrEnchantLevel-1]);
		return attrEnchant;
	}

	
	/**
	 * 物品詳細資料
	 */
	public byte[] getStatusBytes() {
		final L1ItemStatus itemInfo = new L1ItemStatus(this);
		return itemInfo.getStatusBytes().getBytes();
	}
	
	/**
	 * 抗魔
	 * @return
	 */
	public int getMr() {
		final L1ItemPower itemPower = new L1ItemPower(this);
		return itemPower.getMr();
	}

	class EnchantTimer extends TimerTask {

		public EnchantTimer() {
		}

		@Override
		public void run() {
			try {
				final int type = getItem().getType();
				final int type2 = getItem().getType2();
				final int objid = getId();
				if ((_pc != null) && _pc.getInventory().getItem(objid) != null) {
					if ((type == 2) && (type2 == 2) && isEquipped()) {
						_pc.addAc(3);
						_pc.sendPackets(new S_OwnCharStatus(_pc));
					}
				}
				setAcByMagic(0);
				setDmgByMagic(0);
				setHolyDmgByMagic(0);
				setHitByMagic(0);
				// 308 你的 %0%o 失去了光芒。
				_pc.sendPackets(new S_ServerMessage(308, getLogName()));
				_isRunning = false;
				_timer = null;

			} catch (final Exception e) {
				_log.warn("EnchantTimer: " + getItemId());
			}
		}
	}

	private int _acByMagic = 0;

	/**
	 * 魔法增加額外防禦力
	 * @return
	 */
	public int getAcByMagic() {
		return _acByMagic;
	}

	/**
	 * 魔法增加額外防禦力
	 * @param i
	 */
	public void setAcByMagic(final int i) {
		_acByMagic = i;
	}

	private int _dmgByMagic = 0;

	/**
	 * 魔法增加額外攻擊
	 * @return
	 */
	public int getDmgByMagic() {
		int adddmg = 0;
		if (_power_name != null && this.getItem().getType2() == 1) {
			switch (_power_name.get_hole_1()) {
			case 9:// 攻  額外攻擊+3
				adddmg += 3;
				break;
			}
			switch (_power_name.get_hole_2()) {
			case 9:// 攻  額外攻擊+3
				adddmg += 3;
				break;
			}
			switch (_power_name.get_hole_3()) {
			case 9:// 攻  額外攻擊+3
				adddmg += 3;
				break;
			}
			switch (_power_name.get_hole_4()) {
			case 9:// 攻  額外攻擊+3
				adddmg += 3;
				break;
			}
			switch (_power_name.get_hole_5()) {
			case 9:// 攻  額外攻擊+3
				adddmg += 3;
				break;
            }
        }
		//武器寶石鑲嵌系統 同步魔法命中
		if (get_Gem_name() != null) {
			final L1ItemGem Gem = get_Gem_name();
			adddmg += Gem.get_dmgew();
		}
		//end
        return _dmgByMagic + adddmg;
    }

	/**
	 * 魔法增加額外攻擊
	 * @param i
	 */
	public void setDmgByMagic(final int i) {
		_dmgByMagic = i;
	}

	private int _holyDmgByMagic = 0;

	public int getHolyDmgByMagic() {
		return _holyDmgByMagic;
	}

	public void setHolyDmgByMagic(final int i) {
		_holyDmgByMagic = i;
	}

	private int _hitByMagic = 0;
	
	

    /**
     * 魔法增加額外命中
     * 
     * @return
     */
	public int getHitByMagic() {
		//武器寶石鑲嵌系統 同步命中
		int adddhit = 0;
		if (get_Gem_name() != null) {
			final L1ItemGem Gem = get_Gem_name();
			adddhit += Gem.get_hit();
		}
		return _hitByMagic + adddhit;
		//end
		//return _hitByMagic;
	}

	/**
	 * 魔法增加額外命中
	 * @param i
	 */
	public void setHitByMagic(final int i) {
		_hitByMagic = i;
	}

	/**
	 * 盔甲強化時間軸
	 * @param pc
	 * @param skillId
	 * @param skillTime
	 */
	public void setSkillArmorEnchant(final L1PcInstance pc, final int skillId, final int skillTime) {
		final int type = getItem().getType();
		final int type2 = getItem().getType2();
		if (_isRunning) {
			_timer.cancel();
			final int objid = this.getId();
			if ((pc != null) && pc.getInventory().getItem(objid) != null) {
				if ((type == 2) && (type2 == 2) && isEquipped()) {
					pc.addAc(getAcByMagic());
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			}
			setAcByMagic(0);
			_isRunning = false;
			_timer = null;
		}

		if ((type == 2) && (type2 == 2) && isEquipped()) {
			pc.addAc(-3);
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
		setAcByMagic(3);
		_pc = pc;
		_char_objid = _pc.getId();

		_timer = new EnchantTimer();
		(new Timer()).schedule(_timer, skillTime);
		_isRunning = true;
	}

	/**
	 * 武器強化時間軸
	 * @param pc
	 * @param skillId
	 * @param skillTime
	 */
	public void setSkillWeaponEnchant(final L1PcInstance pc, final int skillId, final int skillTime) {
		if (getItem().getType2() != 1) {
			return;
		}
		if (_isRunning) {
			_timer.cancel();
			setDmgByMagic(0);
			setHolyDmgByMagic(0);
			setHitByMagic(0);
			_isRunning = false;
			_timer = null;
		}

		switch (skillId) {
		case HOLY_WEAPON:
			setHolyDmgByMagic(1);
			setHitByMagic(1);
			break;

		case ENCHANT_WEAPON:
			setDmgByMagic(2);
			break;

		case BLESS_WEAPON:
			setDmgByMagic(2);
			setHitByMagic(2);
			break;

		case SHADOW_FANG:
			setDmgByMagic(5);
			break;

		default:
			break;
		}

		_pc = pc;
		_char_objid = _pc.getId();
		
		_timer = new EnchantTimer();
		(new Timer()).schedule(_timer, skillTime);
		_isRunning = true;
	}

	private int _itemOwnerId = 0;

	public int getItemOwnerId() {
		return _itemOwnerId;
	}

	public void setItemOwnerId(final int i) {
		_itemOwnerId = i;
	}

	private L1EquipmentTimer _equipmentTimer;

	/**
	 * 計時物件啟用
	 * @param pc
	 */
	public void startEquipmentTimer(final L1PcInstance pc) {
		if (getRemainingTime() > 0) {
			_equipmentTimer = new L1EquipmentTimer(pc, this);
			final Timer timer = new Timer(true);
			timer.scheduleAtFixedRate(_equipmentTimer, 1000, 1000);
		}
	}

	/**
	 * 計時物件停止計時
	 * @param pc
	 */
	public void stopEquipmentTimer(final L1PcInstance pc) {
		if (getRemainingTime() > 0) {
			_equipmentTimer.cancel();
			_equipmentTimer = null;
		}
	}

	private boolean _isNowLighting = false;

	public boolean isNowLighting() {
		return _isNowLighting;
	}

	public void setNowLighting(final boolean flag) {
		_isNowLighting = flag;
	}
	
	/**
	 * 傳回物件使用中
	 * @return
	 */
	public boolean isEquippedTemp() {
		return _isEquippedTemp;
	}

	/**
	 * 設置物件使用中
	 * @param isEquippedTemp
	 */
	public void set_isEquippedTemp(final boolean isEquippedTemp) {
		_isEquippedTemp = isEquippedTemp;
	}

	private boolean _isMatch = false;
	
	/**
	 * 完成套裝
	 * @param isMatch
	 */
	public void setIsMatch(final boolean isMatch) {
		_isMatch = isMatch;
	}
	
	/**
	 * 完成套裝
	 * @return true:完成套裝 false:未完成套裝
	 */
	public boolean isMatch() {
		return _isMatch;
	}

	// 物品使用者OBJID
	private int _char_objid = -1;
	
	/**
	 * 設置物品使用者OBJID
	 * @param char_objid
	 */
	public void set_char_objid(int char_objid) {
		_char_objid = char_objid;
	}
	
	/**
	 * 物品使用者OBJID
	 * @return _skilltime
	 */
	public int get_char_objid() {
		return _char_objid;
	}

	// 物品使用期限結束時間
	private Timestamp _time = null;
	
	/**
	 * 設置物品使用期限結束時間
	 * @param time
	 */
	public void set_time(Timestamp time) {
		_time = time;
	}
	
	/**
	 * 物品使用期限結束時間
	 * @return _skilltime
	 */
	public Timestamp get_time() {
		return _time;
	}

	public boolean isRunning() {
		return _timer != null;
	}

	// 凹槽
	private L1ItemPower_name _power_name = null;

	public void set_power_name(L1ItemPower_name power_name) {
		_power_name = power_name;
	}
	
	public L1ItemPower_name get_power_name() {
		return _power_name;
	}
	
	// 屬性武器
	private static final String[][] _attrEnchantString = new String[][]{
			new String[]{"土靈『一階』", "土靈『二階』", "土靈『三階』", "土靈『四階』", "土靈『五階』"},// 地之, 崩裂, 地靈
			new String[]{"火靈『一階』", "火靈『二階』", "火靈『三階』", "火靈『四階』", "火靈『五階』"},// 火之, 烈焰, 火靈
			new String[]{"水靈『一階』", "水靈『二階』", "水靈『三階』", "水靈『四階』", "水靈『五階』"},// 水之, 海嘯, 水靈
			new String[]{"風之 ", "暴風 ", "風靈 "},// 風之, 暴風, 風靈
			// ADD LOLI
			new String[]{"光之 ", "閃耀 ", "光靈 "},
			new String[]{"暗之 ", "陰影 ", "暗靈 "},
			new String[]{"聖之 ", "神聖 ", "聖靈 "},
			new String[]{"邪之 ", "邪惡 ", "邪靈 "},
			new String[]{"金靈『一階』", "金靈『二階』", "金靈『三階』", "金靈『四階』", "金靈『五階』"},
			new String[]{"木靈『一階』", "木靈『二階』", "木靈『三階』", "木靈『四階』", "木靈『五階』"},
	};

	public String getNumberedName_to_String() {
		final StringBuilder name = new StringBuilder();
		// 追加值
		if (getEnchantLevel() >= 0) {
			name.append("+" + getEnchantLevel() + " ");
			
		} else if (getEnchantLevel() < 0) {
			name.append(String.valueOf(getEnchantLevel()) + " ");
		}
		
		switch (_item.getUseType()) {
		case 1:// 武器
			// 附加屬性
			final int attrEnchantLevel = getAttrEnchantLevel();
			if (attrEnchantLevel > 0) {
				int type = 0;
				switch (this.getAttrEnchantKind()) {
				case 1: // 地
					type = 0;
					break;

				case 2: // 火
					type = 1;
					break;

				case 4: // 水
					type = 2;
					break;
					
				case 8: // 風
					type = 3;
					break;
					
				case 16: // 光
					type = 4;
					break;
					
				case 32: // 暗
					type = 5;
					break;
					
				case 64: // 聖
					type = 6;
					break;
					
				case 128: // 邪
					type = 7;
					break;
				case 256: // 邪
					type = 8;
					break;
				case 512: // 邪
					type = 9;
					break;
				}
				name.append(_attrEnchantString[type][attrEnchantLevel-1]);
			}
			break;

		case 2: // 盔甲
		case 20: // 手套
		case 21: // 靴
		case 22: // 頭盔
		case 18: // T恤
		case 19: // 斗篷
		case 25: // 盾牌
		case 23:// 戒指
		case 24:// 項鏈
		case 37:// 腰帶
		case 40:// 耳環
			break;
		}
		
		name.append(_item.getName());
		// 資料庫原始最大可用次數大於0
		if (getItem().getMaxChargeCount() > 0) {
			name.append(" (" + getChargeCount() + ")");
			
		} else {
			switch (_item.getItemId()) {
			case 20383: // 軍馬頭盔
				name.append(" (" + getChargeCount() + ")");
				break;

			default:
				break;
			}
		}

		if (_power_name != null) {
			for (int i = 0 ; i < _power_name.get_hole_count() ; i++) {
				switch (i) {
				case 0:
					name.append(set_hole_name(_power_name.get_hole_1()));
					break;
				case 1:
					name.append(set_hole_name(_power_name.get_hole_2()));
					break;
				case 2:
					name.append(set_hole_name(_power_name.get_hole_3()));
					break;
				case 3:
					name.append(set_hole_name(_power_name.get_hole_4()));
					break;
				case 4:
					name.append(set_hole_name(_power_name.get_hole_5()));
					break;
				}
			}
		}

		long count = this.getCount();
		if (count > 1) {
			if (count < 1000000000) {
				name.append(" (" + count + ")");

			} else {
				name.append(" (" + RangeLong.scount(count) + ")");
			}
		}
		return name.toString();
	}
	
	
	//武器寶石鑲嵌系統
	private L1ItemGem _Gem_name = null;
	public void set_Gem_name(L1ItemGem Gem_name) {
		_Gem_name = Gem_name;
	}
	public L1ItemGem get_Gem_name() {
		return _Gem_name;
	}
	//end

	/**
	 * 取得附魔小怪傷害
	 * @return
	 */
	public final int getAttachDmgSmall() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get小怪傷害();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get小怪傷害();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}

	/**
	 * 取得附魔小怪傷害
	 * @return
	 */
	public final int getAttachDmgLarge() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get大怪傷害();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get大怪傷害();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}
	/**
	 * 取得附魔魔法攻擊
	 * @return
	 */
	public final int getAttachMagicDamage() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get魔法攻擊();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get魔法攻擊();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}

	/**
	 * 取得附魔吸血
	 * @return
	 */
	public final int getAttachDrainHp() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get吸血();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get吸血();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}

	/**
	 * 取得附魔吸魔
	 * @return
	 */
	public final int getAttachDrainMp() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get吸魔();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get吸魔();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}

	/**
	 * 取得附魔命中
	 * @return
	 */
	public final int getAttachHit() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get命中();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get命中();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}

	/**
	 * 取得附魔額外攻擊
	 * @return
	 */
	public final int getAttachOtherDamage() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get額外攻擊();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get額外攻擊();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}

	/**
	 * 取得附魔PVP增傷
	 * @return
	 */
	public final int getAttachPVPDamage() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.getPVP增傷();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).getPVP增傷();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}
	/**
	 * 取得附魔防禦
	 * @return
	 */
	public final int getAttachAc() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get防禦();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get防禦();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}
	/**
	 * 取得附魔血量
	 * @return
	 */
	public final int getAttachHp() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get血量();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get血量();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}

	/**
	 * 取得附魔魔力
	 * @return
	 */
	public final int getAttachMp() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get魔力();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get魔力();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}

	/**
	 * 取得附魔回血
	 * @return
	 */
	public final int getAttachHpR() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get回血();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get回血();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}

	/**
	 * 取得附魔回魔
	 * @return
	 */
	public final int getAttachMpR() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get回魔();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get回魔();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}
	public final int getAttachExpAdd() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get經驗();
		}
		return ret;
	}

	/**
	 * 取得附魔抗魔
	 * @return
	 */
	public final int getAttachMr() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get抗魔();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get抗魔();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}
	/**
	 * 取得附魔物理減傷
	 * @return
	 */
	public final int getAttachReductionDamage() {
		int ret = 0;
		if (this.custom_attach_stat != null) {
			ret += this.custom_attach_stat.get物理減傷();
		}
		if (specialStat > 0) {
			final int itemId = this.getItemId();
			if (CustomSpecialStatTable.get().getSpecialDatas().containsKey(itemId)) {
				ret += CustomSpecialStatTable.get().getSpecialDatas().get(itemId).get物理減傷();
			} else {
				// TODO 裝備/武器/飾品
			}
		}
		return ret;
	}
	/* 旅館資料 */
	private int _keyId = 0;
	private int _innNpcId = 0;
	private Timestamp _dueTime;
	private boolean _isHall;

	public int getKeyId() {
		return this._keyId;
	}

	public void setKeyId(int i) {
		this._keyId = i;
	}

	public int getInnNpcId() {
		return this._innNpcId;
	}

	public void setInnNpcId(int i) {
		this._innNpcId = i;
	}

	public boolean checkRoomOrHall() {
		return this._isHall;
	}

	public void setHall(boolean i) {
		this._isHall = i;
	}

	public Timestamp getDueTime() {
		return this._dueTime;
	}

	public void setDueTime(Timestamp i) {
		this._dueTime = i;
	}
}

