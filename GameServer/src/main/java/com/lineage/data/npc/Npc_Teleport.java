package com.lineage.data.npc;

import java.util.Calendar;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTeleportActionTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1TeleportAction;
import com.lineage.server.timecontroller.server.NpcTeleportUseMapTimer;

/**
 * NPC傳送系統
 * 
 * @author juonena
 * 
 */
public class Npc_Teleport extends NpcExecutor {
	private static final Log _log = LogFactory.getLog(Npc_Teleport.class);
	/**
	 */
	private Npc_Teleport() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Teleport();
	}

	@Override
	public int type() {
		return 2;
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		int npcid = npc.getNpcId();
		L1TeleportAction teleport = NpcTeleportActionTable.get().get_loc(npcid, cmd);
		
		if (teleport == null) {
			_log.error("npc自定傳系設置錯誤(npc_teleport) npcid: " + npcid);
			return;
		}
		
		
		/**
		 * 檢查職業 (0=不檢查 1=王 2=騎 4=妖 8=法 16=黑 32=龍 64=幻, 如需檢查2種以上請相加即可 例如 檢查王跟法1+8 輸入9
		 */
		int checkClass = teleport.getCheckClass();
		if (checkClass != 0) {
			boolean check = false;
			if (checkClass >= 8) {
				if (pc.isElf()) {
					check = true;
				}
				checkClass -= 8;
			}
			if (checkClass >= 4) {
				if (pc.isWizard()) {
					check = true;
				}
				checkClass -= 4;
			}
			if (checkClass >= 2) {
				if (pc.isKnight()) {
					check = true;
				}
				checkClass -= 2;
			}
			if (checkClass >= 1) {
				if (pc.isCrown()) {
					check = true;
				}
				checkClass--;
			}
			if (checkClass > 0) {
				_log.error("npc_teleport檢查職業設置錯誤:餘數大於0 npcid: " + npcid);
			}
			
			if (!check) {
				pc.sendPackets(new S_ServerMessage(166, "無法接受您的職業"));
				return;
			}
		}
		
		/**
		 * 檢查正義屬性 0=不檢查 1=中立 2=正義 3=邪惡
		 */
		final int checkLawful = teleport.getCheckLawful();
		if (checkLawful != 0) {
			//boolean check = false;
			final int lawful = pc.getLawful();
			if (checkLawful == 3 && lawful >= 0) {
				//check = true;
				pc.sendPackets(new S_ServerMessage(166, "你的正義屬性必須為:邪惡"));
				return;
			}
			
			if (checkLawful == 1 && (lawful < 0 || lawful >= 500)) {
				//check = true;
				pc.sendPackets(new S_ServerMessage(166, "你的正義屬性必須為:中立"));
				return;
			}
			
			if (checkLawful == 2 && lawful < 500) {
				//check = true;
				pc.sendPackets(new S_ServerMessage(166, "你的正義屬性必須為:正義"));
				return;
			}
		}
		
		/**
		 * 檢查等級
		 */
		final int min = teleport.getMin();
		if (min > pc.getLevel()) {
			pc.sendPackets(new S_ServerMessage(166, "您的等級必須:" + min + "級以上"));
			return;
		}
		final int max = teleport.getMax();
		if (max < pc.getLevel()) {
			pc.sendPackets(new S_ServerMessage(166, "您已超過最高:" + max + "級限制"));
			return;
		}
		
		/**
		 * 檢查時間限制
		 */
		final int startWeek = teleport.getStartWeek();
		final int startHour = teleport.getStartHour();
		final int endHour = teleport.getEndHour();
		
		final Calendar date = Calendar.getInstance();
		final int nowWeek = (date.get(Calendar.DAY_OF_WEEK) - 1);
		final int nowHour = date.get(Calendar.HOUR_OF_DAY);
		
		// 只限制星期
		if (startWeek > 0 && startHour < 0 && endHour < 0) {
			if (startWeek != nowWeek) {
				pc.sendPackets(new S_ServerMessage(166, "使用時間限制:星期【" + startWeek +" 】"));
				return;
			}
		}
		
		// 限制星期.限制時間
		else if (startWeek > 0 && startHour >= 0 && endHour >= 0) {
			if (startWeek != nowWeek || nowHour < startHour || nowHour >= endHour) {
				pc.sendPackets(new S_ServerMessage(166, "使用時間限制:星期【 " + startWeek + " 】時間為【 " + startHour + " 】點，至【 " + endHour + "】點"));
				return;
			}
		}
		
		// 限制時間
		else if (startWeek < 0 && startHour >= 0 && endHour >= 0) {
			if (nowHour < startHour || nowHour >= endHour) {
				pc.sendPackets(new S_ServerMessage(166, "使用時間限制:時間為【 " + startHour + " 】點，至【 " + endHour + "】點"));
				return;
			}
		}
		
		/**
		 * 檢查扣除指定道具/數量
		 */
		final int itemid = teleport.getItemId();
		final int count = teleport.getCount();
		if (itemid != 0 && count != 0) {
			final L1ItemInstance item = pc.getInventory().checkItemX(itemid, count);
			if (item != null) {
				pc.getInventory().removeItem(item, count);// 刪除道具

			} else {
				final L1Item itemtmp = ItemTable.get().getTemplate(itemid);
				// \f1%0不足%s。
				pc.sendPackets(new S_ServerMessage(337, itemtmp.getNameId()));
				return;
			}
		}
		
		/**
		 * 檢查限制停留秒數
		 */
		int time = teleport.getTime();
		final int m = teleport.getMapid();
		
		if (time != 0) {
			final Random _random = new Random();
			final int rtime = _random.nextInt(60) + 60;
			time += rtime;
			NpcTeleportUseMapTimer.put(pc, m, time);
		}
		
		/**
		 * 開始傳送
		 */
		final int x = teleport.getLocx();
		final int y = teleport.getLocy();
		L1Teleport.teleport(pc, x, y, (short) m, 5, true);
	}
}
