package com.lineage.server.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.world.World;

public class L1Clan {

	private static final Log _log = LogFactory.getLog(L1Clan.class);

	private final Lock _lock = new ReentrantLock(true);

	/**[見習]*/
	public static final int CLAN_RANK_PROBATION = 1;

	/**[一般] 無倉庫使用權*/
	public static final int CLAN_RANK_PUBLIC = 2;

	/**[副君主]*/
	public static final int CLAN_RANK_GUARDIAN = 3;

	/**[聯盟君主]*/
	public static final int CLAN_RANK_PRINCE = 4;

	/**聯合血盟[修習騎士]*/
	public static final int ALLIANCE_CLAN_RANK_ATTEND = 5;
	
	/**聯合血盟[守護騎士]*/
	public static final int ALLIANCE_CLAN_RANK_GUARDIAN = 6;
	
	/**一般血盟[一般]*/
	public static final int NORMAL_CLAN_RANK_GENERAL = 7;
	
	/**一般血盟[修習騎士]*/
	public static final int NORMAL_CLAN_RANK_ATTEND = 8;
	
	/**一般血盟[守護騎士]*/
	public static final int NORMAL_CLAN_RANK_GUARDIAN = 9;
	
	/**一般血盟[聯盟君主]*/
	public static final int NORMAL_CLAN_RANK_PRINCE = 10;

	private int _clanId;

	private String _clanName;

	private int _leaderId;

	private String _leaderName;

	private int _castleId;

	private int _houseId;

	private int _warehouse = 0;
	private int level = 0;

	private final L1DwarfForClanInventory _dwarfForClan = new L1DwarfForClanInventory(this);

	private final ArrayList<String> _membersNameList = new ArrayList<String>();

	// 全部血盟成員與階級資料
	//private static final HashMap<String, Integer> _membersNameList = new HashMap<String, Integer>();
	
	public int getClanId() {
		return this._clanId;
	}

	/**
	 * 設置血盟ID
	 * @param clan_id
	 */
	public void setClanId(final int clan_id) {
		this._clanId = clan_id;
	}

	/**
	 * 血盟名稱
	 * @return
	 */
	public String getClanName() {
		return this._clanName;
	}

	/**
	 * 設置血盟名稱
	 * @param clan_name
	 */
	public void setClanName(final String clan_name) {
		this._clanName = clan_name;
	}

	/**
	 * 盟主OBJID
	 * @return
	 */
	public int getLeaderId() {
		return this._leaderId;
	}

	/**
	 * 設置盟主OBJID
	 * @param leader_id
	 */
	public void setLeaderId(final int leader_id) {
		this._leaderId = leader_id;
	}

	/**
	 * 盟主名稱
	 * @return
	 */
	public String getLeaderName() {
		return this._leaderName;
	}

	/**
	 * 設置盟主名稱
	 * @param leader_name
	 */
	public void setLeaderName(final String leader_name) {
		this._leaderName = leader_name;
	}

	/**
	 * 擁有城堡ID
	 * @return
	 */
	public int getCastleId() {
		return this._castleId;
	}

	/**
	 * 設置擁有城堡ID
	 * @param hasCastle
	 */
	public void setCastleId(final int hasCastle) {
		this._castleId = hasCastle;
	}

	/**
	 * 擁有小屋ID
	 * @return
	 */
	public int getHouseId() {
		return this._houseId;
	}

	/**
	 * 設置擁有小屋ID
	 * @param hasHideout
	 */
	public void setHouseId(final int hasHideout) {
		this._houseId = hasHideout;
	}
	public int getLevel() {
		return this.level;
	}
	public void setLevel(final int level) {
		this.level = level;
	}

	/**
	 * 加入血盟成員清單
	 * @param member_name
	 */
	public void addMemberName(final String member_name) {
		this._lock.lock();
		try {
			if (!this._membersNameList.contains(member_name)) {
				this._membersNameList.add(member_name);
			}
			
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 移出血盟成員清單
	 * @param member_name
	 */
	public void delMemberName(final String member_name) {
		this._lock.lock();
		try {
			if (this._membersNameList.contains(member_name)) {
				this._membersNameList.remove(member_name);
			}
			
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 血盟線上成員數量
	 * @return
	 */
	public int getOnlineClanMemberSize() {
		int count = 0;
		try {
			for (final String name : this._membersNameList) {
				final L1PcInstance pc = World.get().getPlayer(name);
				// 人員在線上
				if (pc != null) {
					count++;
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return count;
	}

	/**
	 * 全部血盟成員數量
	 * @return
	 */
	public int getAllMembersSize() {
		try {
			return this._membersNameList.size();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}
	
	/**
	 * 對血盟線上成員發送封包
	 */
	public void sendPacketsAll(final ServerBasePacket packet) {
		try {
			for (final Object nameobj : _membersNameList.toArray()) {
				final String name = (String) nameobj;
				final L1PcInstance pc = World.get().getPlayer(name);
				if (pc != null) {
					pc.sendPackets(packet);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 血盟線上成員
	 * @return
	 */
	public L1PcInstance[] getOnlineClanMember() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出清單
		final ArrayList<L1PcInstance> onlineMembers = new ArrayList<L1PcInstance>();
		try {
			temp.addAll(this._membersNameList);
			
			for (final String name : temp) {
				final L1PcInstance pc = World.get().getPlayer(name);
				if ((pc != null) && !onlineMembers.contains(pc)) {
					onlineMembers.add(pc);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return onlineMembers.toArray(new L1PcInstance[onlineMembers.size()]);
	}

	/**
	 * 血盟線上成員名單
	 * @return
	 */
	public StringBuilder getOnlineMembersFP() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出名單
		final StringBuilder result = new StringBuilder();
		try {
			temp.addAll(this._membersNameList);
			
			for (final String name : temp) {
				final L1PcInstance pc = World.get().getPlayer(name);
				if (pc != null) {
					result.append(name + " ");
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * 全部血盟成員名單(包含離線)
	 * @return
	 */
	public StringBuilder getAllMembersFP() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出名單
		final StringBuilder result = new StringBuilder();
		try {
			temp.addAll(this._membersNameList);
			
			for (final String name : temp) {
				result.append(name + " ");
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * 血盟線上成員名單(不包含階級)2017年9月9日22:35:06取消階級
	 * @return
	 */
	public StringBuilder getOnlineMembersFPWithRank() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出名單
		final StringBuilder result = new StringBuilder();
		try {
			temp.addAll(this._membersNameList);
			
			for (final String name : temp) {
				final L1PcInstance pc = World.get().getPlayer(name);
				if (pc != null) {
					result.append(name/* + this.getRankString(pc) */+ " ");
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * 全部血盟成員名單(包含離線)
	 * @return
	 */
	public StringBuilder getAllMembersFPWithRank() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出名單
		final StringBuilder result = new StringBuilder();
		try {
			temp.addAll(this._membersNameList);
			
			for (final String name : temp) {
				final L1PcInstance pc = CharacterTable.get().restoreCharacter(name, null);// 可能有錯
				if (pc != null) {
					result.append(name + " ");
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	String[] _rank = new String[]{
			// 2:一般 3:副君主 4:聯盟君主 5:修習騎士 6:守護騎士 7:一般 8:修習騎士 9:守護騎士 10:聯盟君主
			"","","一般","副君主","聯盟君主","修習騎士","守護騎士","一般","修習騎士","守護騎士","聯盟君主",
	};
	/**
	 * 血盟階級
	 * @param pc
	 * @return
	 */
	public String getRankString(final L1PcInstance pc) {
		if (pc != null) {
			if (pc.getClanRank() > 0) {
				return _rank[pc.getClanRank()];
			}
		}
		return "";
	}

	public String[] getAllMembers() {
		return this._membersNameList.toArray(new String[this._membersNameList.size()]);
	}

	/**
	 * 血盟倉庫資料
	 * @return
	 */
	public L1DwarfForClanInventory getDwarfForClanInventory() {
		return this._dwarfForClan;
	}

	public int getWarehouseUsingChar() {// 血盟倉庫目前使用者
		return this._warehouse;
	}

	public void setWarehouseUsingChar(final int objid) {
		this._warehouse = objid;
	}
	
	// 血盟技能
	private boolean _clanskill = false;
	
	/**
	 * 設置是否能啟用血盟技能
	 * @param boolean1
	 */
	public void set_clanskill(boolean boolean1) {
		this._clanskill = boolean1;
	}
	
	/**
	 * 是否能啟用血盟技能
	 * @return true有 false沒有
	 */
	public boolean isClanskill() {
		return this._clanskill;
	}

	// 血盟技能結束時間
	private Timestamp _skilltime = null;
	
	/**
	 * 設置血盟技能結束時間
	 * @param skilltime
	 */
	public void set_skilltime(Timestamp skilltime) {
		this._skilltime = skilltime;
	}
	
	/**
	 * 血盟技能結束時間
	 * @return _skilltime
	 */
	public Timestamp get_skilltime() {
		return this._skilltime;
	}
}
