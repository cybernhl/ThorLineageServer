package com.william;

import java.sql.Timestamp;
import java.util.Random;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

public class L1Blend {

	private int _npcid;

	private String _action;

	private String _note;

	private int _checkLevel;

	private int _checkClass;

	private int _rnd;

	private int _hpConsume;

	private int _mpConsume;

	private int[] _materials;

	private int[] _materials_count;

	private int[] _materials_enchants;

	private int _new_item;

	private int _new_item_counts;

	private int _new_Enchantlvl_SW;

	private int _new_item_Enchantlvl;

	private int _new_item_Bless;

	private int _residue_item;

	private int _residue_count;

	private int _replacement_count;

	private boolean _inputamount;

	private boolean _all_in_once;

	private int _bonus_item;

	private int _bonus_item_count;

	private int _bonus_item_enchant;

	private String _sucesshtml;

	private String _failhtml;

	private String _Allmessage;

	private String _runmessage;

	private String _othermessage;

	private int _itemran;
	private int _armorcolor;
	private int _itembroad;

	public int get_npcid() {
		return _npcid;
	}

	public void set_npcid(int npcid) {
		_npcid = npcid;
	}

	public String get_action() {
		return _action;
	}

	public void set_action(String action) {
		_action = action;
	}

	public String get_note() {
		return _note;
	}

	public void set_note(String note) {
		_note = note;
	}

	public int getCheckLevel() {
		return _checkLevel;
	}

	public void setCheckLevel(int checkLevel) {
		_checkLevel = checkLevel;
	}

	public int getCheckClass() {
		return _checkClass;
	}

	public void setCheckClass(int checkClass) {
		_checkClass = checkClass;
	}

	public int getRandom() {
		return _rnd;
	}

	public void setRandom(int rnd) {
		_rnd = rnd;
	}

	public int getHpConsume() {
		return _hpConsume;
	}

	public void setHpConsume(int hpConsume) {
		_hpConsume = hpConsume;
	}

	public int getMpConsume() {
		return _mpConsume;
	}

	public void setMpConsume(int mpConsume) {
		_mpConsume = mpConsume;
	}

	public final int[] getMaterials() {
		return _materials;
	}

	public void setMaterials(final int[] needids) {
		_materials = needids;
	}

	public final int[] getMaterials_count() {
		return _materials_count;
	}

	public final void setMaterials_count(final int[] needcounts) {
		_materials_count = needcounts;
	}

	public final int[] get_materials_enchants() {
		return _materials_enchants;
	}

	public final void set_materials_enchants(final int[] needenchants) {
		_materials_enchants = needenchants;
	}

	public int getNew_item() {
		return _new_item;
	}

	public void setNew_item(int new_item) {
		_new_item = new_item;
	}

	public int getNew_item_counts() {
		return _new_item_counts;
	}

	public void setNew_item_counts(int new_item_counts) {
		_new_item_counts = new_item_counts;
	}

	public int getNew_Enchantlvl_SW() {
		return _new_Enchantlvl_SW;
	}

	public void setNew_Enchantlvl_SW(int new_Enchantlvl_SW) {
		_new_Enchantlvl_SW = new_Enchantlvl_SW;
	}

	public int getNew_item_Enchantlvl() {
		return _new_item_Enchantlvl;
	}

	public void setNew_item_Enchantlvl(int new_item_Enchantlvl) {
		_new_item_Enchantlvl = new_item_Enchantlvl;
	}

	public int getNew_item_Bless() {
		return _new_item_Bless;
	}

	public void setNew_item_Bless(int new_item_bless) {
		_new_item_Bless = new_item_bless;
	}

	public int getResidue_Item() {
		return _residue_item;
	}

	public void setResidue_Item(int residueitem) {
		_residue_item = residueitem;
	}

	public int getResidue_Count() {
		return _residue_count;
	}

	public void setResidue_Count(int residuecount) {
		_residue_count = residuecount;
	}

	public int getReplacement_count() {
		return _replacement_count;
	}

	public void setReplacement_count(int replacement_count) {
		_replacement_count = replacement_count;
	}

	public boolean isInputAmount() {
		return _inputamount;
	}

	public void setInputAmount(boolean flag) {
		_inputamount = flag;
	}

	public boolean isAll_In_Once() {
		return _all_in_once;
	}

	public void setAll_In_Once(boolean flag) {
		_all_in_once = flag;
	}

	public int getBonus_item() {
		return _bonus_item;
	}

	public void setBonus_item(int bonusitem) {
		_bonus_item = bonusitem;
	}

	public int getBonus_item_count() {
		return _bonus_item_count;
	}

	public void setBonus_item_count(int bonusitemcount) {
		_bonus_item_count = bonusitemcount;
	}

	public int getBonus_item_enchant() {
		return _bonus_item_enchant;
	}

	public void setBonus_item_enchant(int bonusitemenchant) {
		_bonus_item_enchant = bonusitemenchant;
	}

	public String get_sucesshtml() {
		return _sucesshtml;
	}

	public void set_sucesshtml(String sucesshtml) {
		_sucesshtml = sucesshtml;
	}

	public String get_runmessage() {
		return _runmessage;
	}

	public void set_runmessage(String runmessage) {
		_runmessage = runmessage;
	}

	public String get_failhtml() {
		return _failhtml;
	}

	public void set_failhtml(String failhtml) {
		_failhtml = failhtml;
	}

	public String get_Allmessage() {
		return _Allmessage;
	}

	public void set_Allmessage(String Allmessage) {
		_Allmessage = Allmessage;
	}

	public int getitembroad() {
		return _itembroad;
	}

	public void setitembroad(int itembroad) {
		_itembroad = itembroad;
	}

	public String get_othermessage() {
		return _othermessage;
	}

	public void set_othermessage(String othermessage) {
		_othermessage = othermessage;
	}

	public int getitemran() {
		return _itemran;
	}

	public void setitemran(int itemran) {
		_itemran = itemran;
	}

	public int getarmorcolor() {
		return _armorcolor;
	}

	public void setarmorcolor(int armorcolor) {
		_armorcolor = armorcolor;
	}

	/**
	 * 顯示製造道具條件清單
	 * 
	 * @param pc
	 * @param npc
	 * @param npcId
	 * @param cmd
	 */
	public void ShowCraftHtml(L1PcInstance pc, L1NpcInstance npc, L1Blend ItemBlend) {
		String msg0 = "";
		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";
		String msg5 = "";
		String msg6 = "";
		String msg7 = "";
		String msg8 = "";
		String msg9 = "";
		String msg10 = "";
		String msg11 = "";
		String msg12 = "";
		String msg13 = "";
		String msg14 = "";
		String msg15 = "";
		String msg16 = "";
		String msg17 = "";
		String msg18 = "";
		String msg19 = "";
		String msg20 = "";

		L1ItemInstance Newitem = ItemTable.get().createItem(ItemBlend.getNew_item());
		if (Newitem != null) {
			Newitem.setCount(ItemBlend.getNew_item_counts());// 設定數量
			Newitem.setEnchantLevel(ItemBlend.getNew_item_Enchantlvl());// 設定強化值
			Newitem.setIdentified(true);// 設定為已鑒定狀態
			msg0 = "" + Newitem.getLogName();
		}

		L1ItemInstance Bonusitem = ItemTable.get().createItem(ItemBlend.getBonus_item());
		if (Bonusitem != null) {
			Bonusitem.setCount(ItemBlend.getBonus_item_count());// 設定數量
			Bonusitem.setEnchantLevel(ItemBlend.getBonus_item_enchant());// 設定強化值
			Bonusitem.setIdentified(true);// 設定為已鑒定狀態
			msg1 = "製造成功時額外獲得: " + Bonusitem.getLogName();
		}

		if (ItemBlend.getCheckLevel() != 0) {
			msg2 = " " + ItemBlend.getCheckLevel() + "級以上。 ";
		} else {
			msg2 = " 無限制 ";
		}

		if (ItemBlend.getCheckClass() == 1) { // 王族
			msg3 = " 王族";
		} else if (ItemBlend.getCheckClass() == 2) { // 騎士
			msg3 = " 騎士";
		} else if (ItemBlend.getCheckClass() == 3) { // 法師
			msg3 = " 法師";
		} else if (ItemBlend.getCheckClass() == 4) { // 妖精
			msg3 = " 妖精";
		} else if (ItemBlend.getCheckClass() == 5) { // 黑妖
			msg3 = " 黑妖";
		} else if (ItemBlend.getCheckClass() == 0) { // 所有職業
			msg3 = " 所有職業";
		}

		if (ItemBlend.getRandom() != -1 && ItemBlend.get_runmessage() != null) {
			msg4 = " " + ItemBlend.get_runmessage() + "";
		} else {

			msg4 = " " + ItemBlend.getRandom() + " %";
		}

		int HammerRnd = CheckForFireHammerCount(pc, npc, ItemBlend);
		if (HammerRnd > 0 && ItemBlend.get_othermessage() != null) {
			msg5 = "[" + ItemBlend.get_othermessage() + "]成功機率+: " + HammerRnd + " %";
		}

		final int[] Materials = ItemBlend.getMaterials();// 所需材料陣列
		final int[] counts = ItemBlend.getMaterials_count();// 所需材料數量陣列
		final int[] enchants = ItemBlend.get_materials_enchants();// 所需強化值陣列

		if (Materials != null) {
			for (int i = 0; i < Materials.length; i++) {
				L1ItemInstance temp = ItemTable.get().createItem(Materials[i]);
				temp.setEnchantLevel(enchants[i]);// 設定強化值
				temp.setIdentified(true);// 設定為已鑒定狀態

				switch (i) {
				case 0:
					msg7 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 1:
					msg8 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 2:
					msg9 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 3:
					msg10 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 4:
					msg11 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 5:
					msg12 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 6:
					msg13 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 7:
					msg14 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 8:
					msg15 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 9:
					msg16 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 10:
					msg17 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 11:
					msg18 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 12:
					msg19 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				case 13:
					msg20 = temp.getLogName() + " (" + counts[i] + ") 個";
					break;
				}
			}
		}

		String msgs[] = { msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11, msg12, msg13, msg14,
				msg15, msg16, msg17, msg18, msg19, msg20 };

		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ItemBlend", msgs));

	}

	/**
	 * 檢查身上火神之槌的數量(最多5支)
	 * 
	 * @param pc
	 * @param npc
	 * @return
	 */
	private int CheckForFireHammerCount(L1PcInstance pc, L1NpcInstance npc, L1Blend ItemBlend) {
		int HammerCount = 0;
		L1ItemInstance firehammer = pc.getInventory().findItemId(ItemBlend.getitemran());// 火神之槌
		if ((firehammer != null)) {// 身上有火神之槌並且NPC是火神煉化工匠
			HammerCount = (int) firehammer.getCount();// 火神之槌數量
			if (HammerCount > 5) {// 最多消耗5只火神之槌
				HammerCount = 5;
			}
		}
		return HammerCount;
	}

	/**
	 * 製造道具條件判斷
	 * 
	 * @param pc
	 * @param npc
	 * @param ItemBlend
	 * @param amount    要求製造的數量
	 * @param checked   是否已確認製造數量
	 */
	public void CheckCraftItem(L1PcInstance pc, L1NpcInstance npc, L1Blend ItemBlend, int amount, boolean checked) {
		final int[] Materials = ItemBlend.getMaterials();// 所需材料陣列
		final int[] Materials_counts = ItemBlend.getMaterials_count();// 所需材料數量陣列
		final int[] enchants = ItemBlend.get_materials_enchants();// 所需強化值陣列

		/** 導引至數量輸入的處理 */
		if (!checked && ItemBlend.isInputAmount()) {// 是否導引至數量輸入
			// 檢查可製造數量並提示材料數量不足
			long xcount = CreateNewItem.checkNewItem(pc, Materials, Materials_counts);

			if (xcount >= 1L) {// 可製造數量大於等於1
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, null));
				return;
			} else {
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}
		}
		/** 導引至數量輸入的處理 END */

		boolean isok = true;// 需求條件是否檢查成功
		int New_itemid = ItemBlend.getNew_item();// 新道具ID
		int New_item_counts = ItemBlend.getNew_item_counts();// 新道具的數量
		L1ItemInstance newitem = ItemTable.get().createItem(New_itemid);

		if (pc.getInventory().checkAddItem(newitem, New_item_counts) != L1Inventory.OK) {// 背包容量、負重量判斷
			isok = false;
		}

		if (ItemBlend.getCheckLevel() != 0) { // 等級判斷
			if (pc.getLevel() < ItemBlend.getCheckLevel()) { // 等級不符
				pc.sendPackets(new S_ServerMessage("等級必須為" + ItemBlend.getCheckLevel() + "以上。"));
				isok = false;
			}
		}

		if (ItemBlend.getCheckClass() != 0) { // 職業判斷
			byte class_id = (byte) 0;
			String Classmsg = "";

			if (pc.isCrown()) { // 王族
				class_id = 1;
			} else if (pc.isKnight()) { // 騎士
				class_id = 2;
			} else if (pc.isWizard()) { // 法師
				class_id = 3;
			} else if (pc.isElf()) { // 妖精
				class_id = 4;
			} else if (pc.isDarkelf()) { // 黑妖
				class_id = 5;	
			}
			switch (ItemBlend.getCheckClass()) {
			case 1:
				Classmsg = "王族";
				break;
			case 2:
				Classmsg = "騎士";
				break;
			case 3:
				Classmsg = "法師";
				break;
			case 4:
				Classmsg = "妖精";
				break;
			case 5:
				Classmsg = "黑暗妖精";
				break;
			}

			if (ItemBlend.getCheckClass() != class_id) { // 職業不符
				pc.sendPackets(new S_ServerMessage(166, "職業必須是", Classmsg, "才能製造此道具"));
				isok = false;
			}
		}

		// 消耗體力 魔力判斷
		if (ItemBlend.getHpConsume() != 0 || ItemBlend.getMpConsume() != 0) {
			if (pc.getCurrentHp() < ItemBlend.getHpConsume()) {

				isok = false;
			}
			if (pc.getCurrentMp() < ItemBlend.getMpConsume()) {

				isok = false;
			}
		}

		boolean enough = false;// 所有材料是否足夠

		if (isok) {// 其他條件都成立
			int num = 0;// 檢查數
			for (int i = 0; i < Materials.length; i++) {// 材料數量判斷
				if (Materials[i] != 0 && Materials_counts[i] != 0) {// 材料與材料數量不為0
					if (!pc.getInventory().checkEnchantItem(Materials[i], enchants[i], Materials_counts[i])) {
						// System.out.println("檢查點 材料不足");
						L1ItemInstance temp = ItemTable.get().createItem(Materials[i]);
						temp.setEnchantLevel(enchants[i]);// 設定強化值
						temp.setIdentified(true);// 設定為已鑒定狀態
						pc.sendPackets(new S_ServerMessage(337, temp.getLogName() + "("
								+ (Materials_counts[i] /*- pc.getInventory().countItems(temp.getItemId())*/) + ")"));
						// System.out.println("不足");
						isok = false;
					} else {
						// System.out.println("檢查點 材料足夠");
						num++;
					}
				}
			}

			if (num == Materials.length) {// 檢查數=材料數
				enough = true;
			}
		}

		if ((isok) && (enough)) { // 所有條件成立且所有材料都足夠

			if (ItemBlend.isAll_In_Once()) {// 一次製造所有道具(只刪除一次材料)
				int[] newcounts = new int[Materials_counts.length];
				for (int i = 0; i < Materials_counts.length; i++) {// 材料數量陣列更新
					newcounts[i] = Materials_counts[i] * amount;
				}

				int Hammercount = CheckForFireHammerCount(pc, npc, ItemBlend);
				if (Hammercount > 0) { // 檢查火神之槌數量
					// 消耗火神之槌
					pc.getInventory().consumeItem(ItemBlend.getitemran(), Hammercount);
				}

				// 刪除材料的處理
				for (int i = 0; i < Materials.length; i++) {

					// System.out.println("檢查點 刪除材料");
					pc.getInventory().consumeEnchantItem(Materials[i], enchants[i], newcounts[i]);
				}

			} else {// 依次刪除材料的情況
				for (int a = 0; a < amount; a++) {// 要求製造的數量
					int Hammercount = CheckForFireHammerCount(pc, npc, ItemBlend);
					if (Hammercount > 0) { // 檢查火神之槌數量
						// 消耗火神之槌
						pc.getInventory().consumeItem(ItemBlend.getitemran(), Hammercount);
					}

					// 刪除材料的處理
					for (int i = 0; i < Materials.length; i++) {

						// System.out.println("檢查點 刪除材料");
						pc.getInventory().consumeEnchantItem(Materials[i], enchants[i], Materials_counts[i]);
					}
				}
			}

			if (ItemBlend.getHpConsume() > 0) {// 消耗體力
				pc.setCurrentHp(pc.getCurrentHp() - ItemBlend.getHpConsume());
			}
			if (ItemBlend.getMpConsume() > 0) {// 消耗魔力
				pc.setCurrentMp(pc.getCurrentMp() - ItemBlend.getMpConsume());
			}

			CraftItem(pc, npc, ItemBlend, amount);// 製造道具製造道具並給予玩家物品

			String sucesshtml = ItemBlend.get_sucesshtml();// 製造成功時顯示的網頁
			int DBrnd = ItemBlend.getRandom();
			if ((sucesshtml != null) && (amount == 1) && (DBrnd == 100)) {// 100%機率成功的道具才顯示網頁
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), sucesshtml));

			}

		} else {// 條件不成立
			String failhtml = ItemBlend.get_failhtml();// 製造失敗時顯示的網頁
			int DBrnd = ItemBlend.getRandom();
			if ((failhtml != null) && (amount == 1) && (DBrnd == 100)) {// 100%機率成功的道具才顯示網頁
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), failhtml));
			} else {
				pc.sendPackets(new S_CloseList(pc.getId()));
			}
		}
	}

	/**
	 * 製造道具並給予玩家物品
	 * 
	 * @param pc
	 * @param npc
	 * @param ItemBlend
	 * @param amount    要求製造的數量
	 */
	private void CraftItem(L1PcInstance pc, L1NpcInstance npc, L1Blend ItemBlend, int amount) {
		Random _random = new Random();
		int DBrnd = ItemBlend.getRandom(); // DB設定基礎成功率
		int HammerRnd = CheckForFireHammerCount(pc, npc, ItemBlend); // 火神之槌增加成功率
		int TotalChance = (DBrnd + HammerRnd) * 10; // 總成功機率 * 10
		int New_itemid = ItemBlend.getNew_item(); // 新道具
		int New_item_counts = ItemBlend.getNew_item_counts(); // 新道具的數量
		int Bonus_itemid = ItemBlend.getBonus_item(); // 製造成功時額外獲得道具
		int Bonusitem_count = ItemBlend.getBonus_item_count(); // 額外道具的數量
		int Bonusitem_enchant = ItemBlend.getBonus_item_enchant(); // 額外道具的強化值

		int ItemLV_SW = ItemBlend.getNew_Enchantlvl_SW(); // 隨機等級 固定:0 ; 隨機:1
		int ItemLV = ItemBlend.getNew_item_Enchantlvl(); // 強化值
		int ItemBless = ItemBlend.getNew_item_Bless(); // 新道具的祝福狀態
		int ResdueItem = ItemBlend.getResidue_Item(); // 失敗後留下的道具
		int ResdueCount = ItemBlend.getResidue_Count(); // 失敗後留下的道具數量
		String npcName = npc.getNpcTemplate().get_name(); // NPC名稱

		int newamount = amount;
		int newcounts = New_item_counts;

		if (ItemBlend.isAll_In_Once()) {// 一次製造所有道具(只判定一次機率)
			newamount = 1;// 要求製造數量設定為1
			newcounts = New_item_counts * amount;// 創造數量更新
		}

		for (int i = 0; i < newamount; i++) {// 要求製造的數量

			if (_random.nextInt(1000) < TotalChance) {// 製造成功(機率亂數1000)
				/** 給予新道具的處理 */
				L1ItemInstance newitem = ItemTable.get().createItem(New_itemid); // 創造新道具並加入世界
				if (newitem != null) {
					if (pc.getInventory().checkAddItem(newitem, newcounts) == L1Inventory.OK) {
						if (newitem.isStackable()) {// 可堆疊物品
							if (ItemLV_SW == 0) {// 固定:0 ; 隨機:1
								newitem.setEnchantLevel(ItemLV);// 設定強化值
								newitem.setBless(ItemBless);// 設定祝福狀態
								newitem.setIdentified(true);// 設定為已鑒定狀態
							} else {
								newitem.setEnchantLevel(_random.nextInt(ItemLV) + 1);// 設定強化值
								newitem.setBless(ItemBless);// 設定祝福狀態
								newitem.setIdentified(true);// 設定為已鑒定狀態
							}
							newitem.setCount(newcounts);// 設定數量

							pc.getInventory().storeItem(newitem);				
							pc.sendPackets(new S_ServerMessage(143, npcName, newitem.getLogName()));

							if ((ItemBlend.getitembroad() > 0) && (ItemBlend.get_Allmessage() != null)) {
								World.get().broadcastPacketToAll(
										new S_SystemMessage(String.format(ItemBlend.get_Allmessage(),
												new Object[] { pc.getName(), newitem.getLogName() })));
							}
							WriteLogTxt.Recording(
									"火神锻造",
									"IP"
											+ "("
											+ pc.getNetConnection().getIp()
											+ ")"
											+ "玩家"
											+ ":【 "
											+ pc.getName()
											+ " 】 "
											+ "成功铸造"
											+ newitem.getLogName()
											+ "("
											+ newcounts
											+ ")"
											+ " 】,时间:"
											+ "("
											+ new Timestamp(System
													.currentTimeMillis()) + ")。");
						} else {// 不可堆疊物品
							for (int c = 0; c < newcounts; c++) {// 創造數量
								L1ItemInstance newitem2 = ItemTable.get().createItem(New_itemid); // 創造新道具並加入世界
								if (ItemLV_SW == 0) {// 固定:0 ; 隨機:1
									newitem2.setEnchantLevel(ItemLV);// 設定強化值
									newitem2.setBless(ItemBless);// 設定祝福狀態
									newitem2.setIdentified(true);// 設定為已鑒定狀態
								} else {
									newitem2.setEnchantLevel(_random.nextInt(ItemLV) + 1);// 設定強化值
									newitem2.setBless(ItemBless);// 設定祝福狀態
									newitem2.setIdentified(true);// 設定為已鑒定狀態
								}
								newitem2.setCount(1);// 設定數量

								pc.getInventory().storeItem(newitem2);
								pc.sendPackets(new S_ServerMessage(143, npcName, newitem2.getLogName()));
								if ((ItemBlend.getitembroad() > 0) && (ItemBlend.get_Allmessage() != null)) {
									World.get().broadcastPacketToAll(
											new S_SystemMessage(String.format(ItemBlend.get_Allmessage(),
													new Object[] { pc.getName(), newitem.getLogName() })));
								}
								WriteLogTxt.Recording(
										"火神鍛造",
										"IP"
												+ "("
												+ pc.getNetConnection().getIp()
												+ ")"
												+ "玩家"
												+ ":【 "
												+ pc.getName()
												+ " 】 "
												+ "成功鑄造"
												+ newitem.getLogName()
												+ "("
												+ newcounts
												+ ")"
												+ " 】,時間:"
												+ "("
												+ new Timestamp(System
														.currentTimeMillis()) + ")。");
							}
						}
					}
				}
				/** 給予新道具的處理 END */

				/** 給予額外道具的處理 */
				L1ItemInstance bonusitem = ItemTable.get().createItem(Bonus_itemid); // 創造額外道具並加入世界
				if (bonusitem != null) {
					if (pc.getInventory().checkAddItem(bonusitem, Bonusitem_count) == L1Inventory.OK) {
						if (bonusitem.isStackable()) {// 可堆疊物品
							bonusitem.setEnchantLevel(Bonusitem_enchant);// 設定強化值
							bonusitem.setIdentified(true);// 設定為已鑒定狀態
							bonusitem.setCount(Bonusitem_count);// 設定數量
							pc.getInventory().storeItem(bonusitem);
							pc.sendPackets(new S_ServerMessage(143, npcName, bonusitem.getLogName()));
						} else {// 不可堆疊物品
							for (int c = 0; c < Bonusitem_count; c++) {// 創造數量
								L1ItemInstance bonusitem2 = ItemTable.get().createItem(Bonus_itemid); // 創造額外道具並加入世界
								bonusitem2.setEnchantLevel(Bonusitem_enchant);// 設定強化值
								bonusitem2.setIdentified(true);// 設定為已鑒定狀態
								bonusitem2.setCount(1);// 設定數量
								pc.getInventory().storeItem(bonusitem2);
								pc.sendPackets(new S_ServerMessage(143, npcName, bonusitem2.getLogName()));
							}
						}
					}
				}
				/** 給予額外道具的處理 END */

			} else {// 製造失敗
				L1ItemInstance residueitem = ItemTable.get().createItem(ResdueItem); // 創造殘留道具並加入世界
				if (residueitem != null && ResdueCount > 0) {// 殘留道具不為空且數量大於0
					if (pc.getInventory().checkAddItem(residueitem, ResdueCount) == L1Inventory.OK) {
						residueitem.setCount(ResdueCount);
						pc.getInventory().storeItem(residueitem);
						pc.sendPackets(new S_ServerMessage(143, npcName, residueitem.getLogName()));
						pc.sendPackets(new S_SystemMessage("道具製造失敗了"));

					}
				} else {
					pc.sendPackets(new S_SystemMessage("道具製造失敗了"));

				}
			}
		}
		// 關閉介面
		pc.sendPackets(new S_CloseList(pc.getId()));
	}
}