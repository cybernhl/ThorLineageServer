package com.lineage.server.templates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.lineage.server.WriteLogTxt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.GamblingSet;
import com.lineage.data.event.gambling.GamblingNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.datatables.lock.DwarfShopReading;
import com.lineage.server.datatables.lock.GamblingReading;
import com.lineage.server.datatables.lock.ServerCnInfoReading;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.event.GamblingTime;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 人物其他項清單列表
 * @author DaiEn
 *
 */
public class L1PcOtherList {

	private static final Log _log = LogFactory.getLog(L1PcOtherList.class);

	private L1PcInstance _pc;

	public Map<Integer, L1ItemInstance> DELIST;// 虛擬商店買入清單(ORDERID/指定的物品)
	
	private Map<Integer, L1ShopItem> _cnList;// 購買奇怪的商人物品清單(ORDERID/指定的物品數據)
	
	private Map<Integer, L1ItemInstance> _cnSList;// 購買托售商人物品清單(ORDERID/指定的物品數據)

	private Map<Integer, GamblingNpc> _gamList;// 購買食人妖精競賽票清單(ORDERID/指定的參賽者數據)

	private Map<Integer, L1Gambling> _gamSellList;// 賣出食人妖精競賽票清單(物品OBJID/妖精競賽紀錄緩存)

	private Map<Integer, L1IllusoryInstance> _illusoryList;// 召喚分身清單(分身OBJID/分身數據)

	private Map<Integer, L1TeleportLoc> _teleport;// NPC傳送點緩存(傳點排序編號/傳點數據)

	private Map<Integer, Integer> _uplevelList;// 屬性重置清單(模式/增加數值總合)

	private Map<Integer, String[]> _shiftingList;// 裝備轉移人物清單(帳戶中人物排序編號/String[]{OBJID/人物名稱})

	private Map<Integer, L1ItemInstance> _sitemList;// 裝備交換清單(ORDERID/指定的物品)

	private Map<Integer, Integer> _sitemList2;// 裝備交換清單(ORDERID/指定的物品ITEMID)

	public Map<Integer, L1Quest> QUESTMAP;// 暫存任務清單
	
	public Map<Integer, L1ShopS> SHOPXMAP;// 暫存出售紀錄清單
	

	public ArrayList<Integer> ATKNPC;// 暫存需要攻擊的NPCID
	
    public Map<Integer, int[]> SHOPLIST;// 商品领回暂时清单
    
    public Map<Integer, int[]> SHOPLIST1;// 商品领回暂时清单
	
	private int[] _is;// 暫存人物原始素質改變

	public L1PcOtherList(final L1PcInstance pc) {
		this._pc = pc;
		this.DELIST = new HashMap<Integer, L1ItemInstance>();
		
		this._cnList = new HashMap<Integer, L1ShopItem>();
		this._cnSList = new HashMap<Integer, L1ItemInstance>();
		this._gamList = new HashMap<Integer, GamblingNpc>();
		this._gamSellList = new HashMap<Integer, L1Gambling>();
		this._illusoryList = new HashMap<Integer, L1IllusoryInstance>();
		
		this._teleport = new HashMap<Integer, L1TeleportLoc>();
		this._uplevelList = new HashMap<Integer, Integer>();
		this._shiftingList = new HashMap<Integer, String[]>();
		this._sitemList = new HashMap<Integer, L1ItemInstance>();
		this._sitemList2 = new HashMap<Integer, Integer>();

		this.QUESTMAP = new HashMap<Integer, L1Quest>();
		this.SHOPXMAP = new HashMap<Integer, L1ShopS>();

		this.ATKNPC = new ArrayList<Integer>();
        this.SHOPLIST = new HashMap<Integer, int[]>();
        this.SHOPLIST1 = new HashMap<Integer, int[]>();
	}
	
	/**
	 * 清空全部資料
	 */
	public void clearAll() {
		try {
			ListMapUtil.clear(DELIST);
			ListMapUtil.clear(_cnList);
			ListMapUtil.clear(_cnSList);
			ListMapUtil.clear(_gamList);
			ListMapUtil.clear(_gamSellList);
			ListMapUtil.clear(_illusoryList);
			ListMapUtil.clear(_teleport);
			ListMapUtil.clear(_uplevelList);
			ListMapUtil.clear(_shiftingList);
			ListMapUtil.clear(_sitemList);
			ListMapUtil.clear(_sitemList2);
			ListMapUtil.clear(QUESTMAP);
			ListMapUtil.clear(SHOPXMAP);

			ListMapUtil.clear(ATKNPC);
            ListMapUtil.clear(SHOPLIST);
            ListMapUtil.clear(SHOPLIST1);
			
			this.DELIST = null;// 虛擬商店買入清單
			this._cnList = null;
			this._cnSList = null;
			this._gamList = null;
			this._gamSellList = null;
			this._illusoryList = null;
			this._teleport = null;
			this._uplevelList = null;
			this._shiftingList = null;
			this._sitemList = null;
			this._sitemList2 = null;
			this.QUESTMAP = null;
			this.SHOPXMAP = null;

			this.ATKNPC = null;
            this.SHOPLIST = null;
            this.SHOPLIST1 = null;
			this._is = null;
			
			this._pc = null;
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 裝備交換清單
	
	/**
	 * 傳回裝備交換清單(可換)
	 * @return _sitemList2
	 */
	public Map<Integer, Integer> get_sitemList2() {
		return this._sitemList2;
	}

	/**
	 * 加入裝備交換清單(可換)
	 * @param key
	 * @param value
	 */
	public void add_sitemList2(final Integer key, final Integer value) {
		this._sitemList2.put(key, value);
	}

	/**
	 * 清空裝備交換清單(可換)
	 */
	public void clear_sitemList2() {
		this._sitemList2.clear();
	}

	// TODO 裝備交換清單
	
	/**
	 * 傳回裝備交換清單(準備)
	 * @return _sitemList
	 */
	public Map<Integer, L1ItemInstance> get_sitemList() {
		return this._sitemList;
	}

	/**
	 * 加入裝備交換清單(準備)
	 * @param key
	 * @param value
	 */
	public void add_sitemList(final Integer key, final L1ItemInstance value) {
		this._sitemList.put(key, value);
	}

	/**
	 * 清空裝備交換清單(準備)
	 */
	public void clear_sitemList() {
		this._sitemList.clear();
	}

	// TODO 帳戶人物清單
	
	/**
	 * 傳回帳戶人物清單
	 * @return _shiftingList
	 */
	public Map<Integer, String[]> get_shiftingList() {
		return this._shiftingList;
	}

	/**
	 * 加入帳戶人物清單
	 * @param key
	 * @param value
	 */
	public void add_shiftingList(final Integer key, final String[] value) {
		this._shiftingList.put(key, value);
	}

	/**
	 * 移出帳戶人物清單
	 * @param key
	 */
	public void remove_shiftingList(final Integer key) {
		this._shiftingList.remove(key);
	}

	/**
	 * 讀取人物列表<BR>
	 * 將資料置入MAP中
	 */
	public void set_shiftingList() {
		try {
			_shiftingList.clear();
			Connection conn = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {

				conn = DatabaseFactoryLogin.get().getConnection();
				pstm = conn.prepareStatement(
						"SELECT * FROM `characters` WHERE `account_name`=?");
				pstm.setString(1, this._pc.getAccountName());
				rs = pstm.executeQuery();

				int key = 0;
				while (rs.next()) {
					final int objid = rs.getInt("objid");
					final String name = rs.getString("char_name");
					if (!name.equalsIgnoreCase(this._pc.getName())) {
						key++;
						this.add_shiftingList(key, new String[]{String.valueOf(objid), name});
					}
				}
				
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(conn);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 
	
	/**
	 * 傳回分身
	 * @return _illusoryList
	 */
	public Map<Integer, L1IllusoryInstance> get_illusoryList() {
		return this._illusoryList;
	}

	/**
	 * 加入分身清單
	 * @param key
	 * @param value
	 */
	public void addIllusoryList(final Integer key, final L1IllusoryInstance value) {
		this._illusoryList.put(key, value);
	}

	/**
	 * 移出分身清單
	 * @param key
	 */
	public void removeIllusoryList(final Integer key) {
		try {
			if (_illusoryList == null) {
				return;
			}
			if (_illusoryList.get(key) != null) {
				this._illusoryList.remove(key);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 傳送
	
	/**
	 * 傳送點緩存
	 * @param teleportMap
	 */
	public void teleport(final HashMap<Integer, L1TeleportLoc> teleportMap) {
		try {
			ListMapUtil.clear(_teleport);
			_teleport.putAll(teleportMap);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 傳送點緩存
	 * @return _teleport
	 */
	public Map<Integer, L1TeleportLoc> teleportMap() {
		return this._teleport;
	}
	
	/**
	 * 賣出全部物品
	 * @param sellallMap
	 */
	public void sellall(final Map<Integer, Integer> sellallMap) {
		try {
			int getprice = 0;
			for (final Integer integer : sellallMap.keySet()) {
				final L1ItemInstance item = 
					this._pc.getInventory().getItem(integer);
				if (item != null) {
					final int key = item.getItemId();
					final int price = ShopTable.get().getPrice(key);
					final Integer count = sellallMap.get(integer);
					final long remove = this._pc.getInventory().removeItem(integer, count);
					if (remove == count) {
						getprice += (price * count);
					}
				}
			}

			if (getprice > 0) {
				// 物品(金幣)
				final L1ItemInstance item = 
					ItemTable.get().createItem(40308);
				item.setCount(getprice);
				this.createNewItem(item);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 購物清單
	
	/**
	 * 清空全部買入資料
	 */
	public void clear() {
		try {
			ListMapUtil.clear(this._cnList);
			ListMapUtil.clear(this._gamList);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 奇巖賭場

	/**
	 * 複製賣出資料(清空舊資料)
	 * @param sellList
	 */
	public void set_gamSellList(final Map<Integer, L1Gambling> sellList) {
		try {
			ListMapUtil.clear(_gamSellList);
			_gamSellList.putAll(sellList);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 賣出食人妖精競賽票
	 * @param element
	 * @param index
	 */
	public void get_sellGam(final int objid, final int count) {
		try {
			final L1Gambling element = _gamSellList.get(objid);
			if (element == null) {
				return;
			}
			final long countx = (long) (element.get_rate() * GamblingSet.GAMADENA) * count;
			final long remove = this._pc.getInventory().removeItem(objid, count);
			if (remove == count) {
				final int outcount = element.get_outcount() - count;
				if (outcount < 0) {
					return;
				}
				element.set_outcount(outcount);
				GamblingReading.get().updateGambling(element.get_id(), outcount);
				// 奇巖賭場 下注使用物品編號(預設金幣40308)
				final L1ItemInstance item = 
					ItemTable.get().createItem(GamblingSet.ADENAITEM);
				item.setCount(countx);
				this.createNewItem(item);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 加入購買食人妖精競賽票
	 * @param element
	 * @param index
	 */
	public void add_gamList(final GamblingNpc element, final int index) {
		this._gamList.put(new Integer(index), element);
	}

	/**
	 * 購買食人妖精競賽票
	 * @param gamMap
	 */
	public void get_buyGam(final Map<Integer, Integer> gamMap) {
		try {
			for (final Integer integer : gamMap.keySet()) {
				final int index = integer;
				final int count = gamMap.get(integer);
				this.get_gamItem(index, count);
			}
			ListMapUtil.clear(this._gamList);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void get_gamItem(final int index, final int count) {
		try {
			if (count <= 0) {
				return;
			}
			final GamblingNpc element = this._gamList.get(index);
			if (element == null) {
				return;
			}
			
			final int npcid = element.get_npc().getNpcId();// 比賽者NPCID
			final int no = GamblingTime.get_gamblingNo();// 比賽場次編號
			final long adena = GamblingSet.GAMADENA * count;// 需要數量
			final long srcCount = this._pc.getInventory().countItems(GamblingSet.ADENAITEM);// 現有數量
			
			// 奇巖賭場 下注使用物品編號(預設金幣40308)檢查
			if (srcCount >= adena) {
				// 食人妖精競賽票
				final L1ItemInstance item = 
					ItemTable.get().createItem(40309);
				// 容量重量確認
				if (this._pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
					// 扣除奇巖賭場 下注使用物品編號(預設金幣40308)
					this._pc.getInventory().consumeItem(GamblingSet.ADENAITEM, adena);
					
					item.setCount(count);
					item.setGamNo(no + "-" + npcid);
					this.createNewItem(item);
					element.add_adena(adena);

				} else {
					// \f1當你負擔過重時不能交易。
					this._pc.sendPackets(new S_ServerMessage(270));
				}

			} else {
				final L1Item item = 
					ItemTable.get().getTemplate(GamblingSet.ADENAITEM);
				long nc = adena - srcCount;
				// 337：\f1%0不足%s。
				this._pc.sendPackets(new S_ServerMessage(337, item.getNameId() + "(" + nc + ")"));
				// 337：\f1%0不足%s。  
				//this._pc.sendPackets(new S_ServerMessage(189));
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 托售管理員

	/**
	 * 加入購買托售管理員物品
	 */
	public void add_cnSList(final L1ItemInstance shopItem, final int index) {
		this._cnSList.put(new Integer(index), shopItem);
	}

	/**
	 * 買入托售管理員物品
	 */
	public void get_buyCnS(final Map<Integer, Integer> cnMap) {
		try {
			final int itemid_cn = 44070;// 天寶 44070
			for (final Integer integer : cnMap.keySet()) {
				final int count = cnMap.get(integer);
				if (count > 0) {
					// 取回賣出視窗對應排序編號物品
					final L1ItemInstance element = this._cnSList.get(integer.intValue());
					final L1ShopS shopS = 
						DwarfShopReading.get().getShopS(element.getId());
					if (element != null && shopS != null) {
						if (shopS.get_end() != 0) {// 物品非出售中
							continue;
						}
						if (shopS.get_item() == null) {// 物品設置為空
							continue;
						}
						// 取回天寶數量
						final L1ItemInstance itemT = 
							_pc.getInventory().checkItemX(itemid_cn, shopS.get_adena());
						if (itemT == null) {
							// 337：\f1%0不足%s。 0_o"
							_pc.sendPackets(new S_ServerMessage(337, "天寶"));
							continue;
						}
						
						shopS.set_end(1);// 設置資訊為售出
						shopS.set_item(null);
						DwarfShopReading.get().updateShopS(shopS);
						DwarfShopReading.get().deleteItem(element.getId());
						
						this._pc.getInventory().consumeItem(itemid_cn, shopS.get_adena());
						this._pc.getInventory().storeTradeItem(element);
						this._pc.sendPackets(new S_ServerMessage(403, element.getLogName())); // 獲得0%。
						//createNewItem(element);
					}
				}
			}
			ListMapUtil.clear(this._cnList);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	// 奇怪的商人

	/**
	 * 加入購買奇怪的商人物品
	 */
	public void add_cnList(final L1ShopItem shopItem, final int index) {
		this._cnList.put(new Integer(index), shopItem);
	}

	/**
	 * 買入奇怪的商人物品
	 */
	public void get_buyCn(final Map<Integer, Integer> cnMap) {
		try {
			for (final Integer integer : cnMap.keySet()) {
				final int index = integer;
				final int count = cnMap.get(integer);
				if (count > 0) {
					final L1ShopItem element = this._cnList.get(index);
					if (element != null) {
						this.get_cnItem(element, count);
					}
				}
			}
			_pc.set_temp_adena(0);
			ListMapUtil.clear(this._cnList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 賣出奇怪的商人物品
	 */
	public void get_sellCn(final Map<Integer, Integer> cnMap) {
		try {
			long adena_count = 0;
			for (final Integer integer : cnMap.keySet()) {
				final int objid = integer;
				final int count = cnMap.get(integer);
				if (count > 0) {
					final L1ItemInstance findItem = _pc.getInventory().getItem(
							objid);
					if (findItem != null && findItem.getCount() >= count) {
						final L1ShopItem element = this._cnList.get(findItem
								.getId());
						if (element.getPurchasingPrice()<=0) {
							continue;
						}
						if (element != null) {
							long adenaCount = _pc.getInventory().removeItem(
									findItem, count)
									* element.getPurchasingPrice();

							adena_count += adenaCount;

							toGmMsg(findItem.getItem(), adenaCount, false);
						}
					}
				}
			}
			if (adena_count>0) {
				// 獲得天寶
				CreateNewItem.createNewItem(_pc, _pc.get_temp_adena(), adena_count);
			}		
			_pc.set_temp_adena(0);
			ListMapUtil.clear(this._cnList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void get_cnItem(final L1ShopItem element, final int count) {
		try {
			if (_pc.get_temp_adena() == 0) {
				return;
			}
			final int itemid_cn = _pc.get_temp_adena();// 貨幣
														// _pc.get_temp_adena()
			final int itemid = element.getItemId();// 物品編號
			final long getCount = element.getPackCount() * count;// 給予數量
			final long adenaCount = element.getPrice() * count;// 花費
			if (element.getPrice()<=0) {
				return;
			}
			// 物品檢查(貨幣 44070)
			long srcCount = this._pc.getInventory().countItems(itemid_cn);
			// 修復寫法 (避免負數洗法) by terry0412
			if (srcCount >= adenaCount
					&& this._pc.getInventory().consumeItem(itemid_cn,
							adenaCount)) {
				// 找回物品
				final L1Item itemtmp = ItemTable.get().getTemplate(itemid);

				toGmMsg(itemtmp, adenaCount, true);

				if (itemtmp.isStackable()) {
					// 找回物品
					final L1ItemInstance item = ItemTable.get().createItem(
							itemid);
					item.setCount(getCount);
					this.createNewItem(item);
					if (item.getItemId() == 40308) {
						WriteLogTxt.NormalLog("玩家元寶購買金幣紀錄", _pc.getName() + " 花費 " + adenaCount + " 購買 " + item.getName() + " x " + item.getCount() + " 個");
					}
				} else {
					for (int i = 0; i < getCount; i++) {
						// 找回物品
						final L1ItemInstance item = ItemTable.get().createItem(
								itemid);
						if (element.getEnchantLevel() != 0) {
							item.setEnchantLevel(element.getEnchantLevel());
						}
						this.createNewItem(item);
					}
				}

			} else {
				// 原始物件資料
				final L1Item tgItem = ItemTable.get().getTemplate(
						_pc.get_temp_adena());
				long nc = adenaCount - srcCount;
				// 337：\f1%0不足%s。
				this._pc.sendPackets(new S_ServerMessage(337, tgItem
						.getNameId() + "(" + nc + ")"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 後處理

	/**
	 * 通知GM
	 */
	private void toGmMsg(final L1Item itemtmp, final long adenaCount,
			final boolean buy) {
		try {
			ServerCnInfoReading.get().create(this._pc, itemtmp, adenaCount);
			final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
			for (L1PcInstance tgpc : allPc) {
				if (tgpc.isGm()) {
					final StringBuilder topc = new StringBuilder();
					if (buy) {
						topc.append("人物:" + this._pc.getName() + " 買入:"
								+ itemtmp.getNameId() + " 花費:" + adenaCount+ " 貨幣編號:" +_pc.get_temp_adena());
					} else {
						topc.append("人物:" + this._pc.getName() + " 賣出:"
								+ itemtmp.getNameId() + " 獲得:" + adenaCount+ " 貨幣編號:" +_pc.get_temp_adena());
						_log.info(topc.toString());
					}
					tgpc.sendPackets(new S_ServerMessage(166, topc.toString()));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}


    // 後處理

    /**
     * 給予物件的處理
     * 
     * @param pc
     * @param item
     */
    private void createNewItem(final L1ItemInstance item) {
        try {
            this._pc.getInventory().storeItem(item);
            this._pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 獲得0%。

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

	// TODO 屬性重置處理
	
	/**
	 * 屬性重置
	 * @param key 模式<BR>
	 * 0 升級點數/萬能藥點數 可分配數量<BR>
	 * 
	 * 1 力量 (原始)<BR>
	 * 2 敏捷 (原始)<BR>
	 * 3 體質 (原始)<BR>
	 * 4 精神 (原始)<BR>
	 * 5 智力 (原始)<BR>
	 * 6 魅力 (原始)<BR>
	 * 
	 * 7 力量 +-<BR>
	 * 8 敏捷 +-<BR>
	 * 9 體質 +-<BR>
	 * 10 精神 +-<BR>
	 * 11 智力 +-<BR>
	 * 12 魅力 +-<BR>
	 * 
	 * 13 目前分配點數模式 0:升級點數 1:萬能藥點數<BR>
	 * @param value 增加數值總合
	 */
	public void add_levelList(final int key, final int value) {
		_uplevelList.put(key, value);
	}

	/**
	 * 屬性重置清單
	 * @return
	 */
	public Map<Integer, Integer> get_uplevelList() {
		return this._uplevelList;
	}

	/**
	 * 指定數值參數
	 * @param key
	 * @return
	 */
	public Integer get_uplevelList(int key) {
		return this._uplevelList.get(key);
	}

	/**
	 * 清空屬性重置處理清單
	 */
	public void clear_uplevelList() {
		ListMapUtil.clear(this._uplevelList);
	}

	/**
	 * 暫存人物原始素質改變
	 * @param is
	 */
	public void set_newPcOriginal(final int[] is) {
		this._is = is;
	}
	
	/**
	 * 傳回暫存人物原始素質改變
	 * @return
	 */
	public int[] get_newPcOriginal() {
		return this._is;
	}
}
