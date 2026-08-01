package com.lineage.data.npc.other;

import com.lineage.server.thread.NpcOnlyThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;

/**
 * 文森<BR>
 * 70034
 * @author dexc
 *
 */
public class Npc_Vincent extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Vincent.class);

	/**
	 *
	 */
	private Npc_Vincent() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Vincent();
	}

	@Override
	public int type() {
		return 19;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		if (pc.getLawful() < 0) {// 邪惡
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "vincent2"));
			
		} else {// 一般
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "vincent1"));
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		long xcount = -1;
		
		if (cmd.equalsIgnoreCase("sell")) {// 販賣皮革
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ladar2"));
				
		} else if (cmd.equalsIgnoreCase("request adena2")) {// 販賣皮革
			int[] items = new int[]{40405};
			int[] counts = new int[]{1};
			xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount > 0) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "A1"));
				
			} else {
				isCloseList = true;
			}
			
		} else if (cmd.equalsIgnoreCase("A1")) {// 販賣皮革
			int[] items = new int[]{40405};
			int[] counts = new int[]{1};
			int[] gitems = new int[]{40308};
			int[] gcounts = new int[]{2};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, amount);
		
		} else if (cmd.equalsIgnoreCase("request adena30")) {// 販賣高級皮革
			int[] items = new int[]{40406};
			int[] counts = new int[]{1};
			xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount > 0) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "A2"));
				
			} else {
				isCloseList = true;
			}
			
		} else if (cmd.equalsIgnoreCase("A2")) {// 販賣高級皮革
			int[] items = new int[]{40406};
			int[] counts = new int[]{1};
			int[] gitems = new int[]{40308};
			int[] gcounts = new int[]{30};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, amount);
			
		// 以20張皮革可精製出高級皮革
		} else if (cmd.equalsIgnoreCase("request hard leather")) {// 製造高級皮革
			int[] items = new int[]{40405};
			int[] counts = new int[]{20};
			xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount > 0) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "A3"));
				
			} else {
				isCloseList = true;
			}
			
		} else if (cmd.equalsIgnoreCase("A3")) {// 製造高級皮革
			int[] items = new int[]{40405};
			int[] counts = new int[]{20};
			int[] gitems = new int[]{40406};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, amount);
		
		// 以5張皮革、1塊金屬塊可製造皮帽子。
		} else if (cmd.equalsIgnoreCase("request leather cap")) {// 製造皮帽子
			int[] items = new int[]{40405, 40408};
			int[] counts = new int[]{5, 1};
			int[] gitems = new int[]{20001};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, 1);
		
		// 以6張皮革、2個金屬塊可製造皮涼鞋。
		} else if (cmd.equalsIgnoreCase("request leather sandal")) {// 製造皮涼鞋
			int[] items = new int[]{40405, 40408};
			int[] counts = new int[]{6, 2};
			int[] gitems = new int[]{20193};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, 1);
		
		// 以10張皮革可製造出皮背心。
		} else if (cmd.equalsIgnoreCase("request leather vest")) {// 製造皮背心
			int[] items = new int[]{40405};
			int[] counts = new int[]{10};
			int[] gitems = new int[]{20090};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, 1);
		
		// 以7張皮革就可製造皮盾牌。
		} else if (cmd.equalsIgnoreCase("request leather shield")) {// 製造皮盾牌
			int[] items = new int[]{40405};
			int[] counts = new int[]{7};
			int[] gitems = new int[]{20219};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, 1);
		
		// 以1雙銀釘皮涼鞋，10張高級皮革及10個金屬塊可製造皮長靴。但需要給我手工費300塊金幣。
		} else if (cmd.equalsIgnoreCase("request leather boots")) {// 製造皮長靴
			int[] items = new int[]{20212, 40406, 40408, 40308};
			int[] counts = new int[]{1, 10, 10, 300};
			int[] gitems = new int[]{20192};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, 1);
		
		// 以1頂鋼盔、1頂皮帽子、5張高級皮革和5塊金屬塊可製造出皮頭盔。
		} else if (cmd.equalsIgnoreCase("request leather helmet")) {// 製造皮頭盔
			int[] items = new int[]{20043, 20001, 40406, 40408};
			int[] counts = new int[]{1, 1, 5, 5};
			int[] gitems = new int[]{20002};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, 1);
		
		// 若想製作硬皮背心，需要給我1件銀釘皮背心、15張高級皮革、15塊金屬塊。
		} else if (cmd.equalsIgnoreCase("request hard leather vest")) {// 製作硬皮背心
			int[] items = new int[]{20148, 40406, 40408};
			int[] counts = new int[]{1, 15, 15};
			int[] gitems = new int[]{20145};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, 1);
		
		// 以皮背心和皮帶可製造出皮盔甲。
		} else if (cmd.equalsIgnoreCase("request leather vest with belt")) {// 製造皮盔甲
			int[] items = new int[]{20090, 40778};
			int[] counts = new int[]{1, 1};
			int[] gitems = new int[]{20120};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, 1);
		
		// 以5張高級皮革和2塊金屬塊可製造皮帶。
		} else if (cmd.equalsIgnoreCase("request belt")) {// 製造皮帶
			int[] items = new int[]{40406, 40408};
			int[] counts = new int[]{5, 2};
			xcount = CreateNewItem.checkNewItem(pc, items, counts);
			if (xcount > 0) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, "A4"));
				
			} else {
				isCloseList = true;
			}
			
		} else if (cmd.equalsIgnoreCase("A4")) {// 製造皮帶
			int[] items = new int[]{40406, 40408};
			int[] counts = new int[]{5, 2};
			int[] gitems = new int[]{40778};
			int[] gcounts = new int[]{1};
			isCloseList = getItem(pc, items, counts, gitems, gcounts, amount);
		}
		
		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 交換道具
	 * @param pc
	 * @param items
	 * @param counts
	 * 
	 * @return 是否關閉現有視窗
	 */
	private boolean getItem(final L1PcInstance pc, int[] items, int[] counts, 
			int[] gitems, int[] gcounts, final long amount) {
		try {
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, 
					items, // 需要物件
					counts)
					< 1) {// 傳回可交換道具數小於1(需要物件不足)
				return true;
				
			} else {// 需要物件充足
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, 
						items, counts, // 需要
						gitems, amount, gcounts);// 給予
				return true;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return true;
	}

	@Override
	public int workTime() {
		return 35;
	}

	@Override
	public void work(final L1NpcInstance npc) {
		final Work work = new Work(npc);
		work.getStart();
	}
	
	private class Work implements Runnable {
		
		private L1NpcInstance _npc;
		
		private int _spr;

		private NpcWorkMove _npcMove;
		
		private Point[] _point = new Point[]{
				new Point(33480, 32777),
				new Point(33476, 32777)
		};
		
		private Work(final L1NpcInstance npc) {
			this._npc = npc;
			this._spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
			this._npcMove = new NpcWorkMove(npc);
		}

		/**
		 * 啟動線程
		 */
		public void getStart() {
			NpcOnlyThreadPool.get().schedule(this, 10);
		}
		
		@Override
		public void run() {
			try {
				Point point = null;
				// 前往工作點
				if (!this._npc.getLocation().isSamePoint(this._point[1])) {
					point = this._point[1];
				}
				
				boolean isWork1 = true;
				while (isWork1) {
					Thread.sleep(this._spr);

					if (point != null) {
						isWork1 = this._npcMove.actionStart(point);
					} else {
						isWork1 = false;
					}
				}
				this._npc.setHeading(6);
				this._npc.broadcastPacketX8(new S_ChangeHeading(this._npc));
				
				// 執行工作
				this._npc.broadcastPacketX8(new S_DoActionGFX(this._npc.getId(), 7));
				Thread.sleep(2000);

				// 返回
				if (!this._npc.getLocation().isSamePoint(this._point[0])) {
					point = this._point[0];
				}
				
				boolean isWork2 = true;
				while (isWork2) {
					Thread.sleep(this._spr);

					if (point != null) {
						isWork2 = this._npcMove.actionStart(point);
					} else {
						isWork2 = false;
					}
				}
				this._npc.setHeading(4);
				this._npc.broadcastPacketX8(new S_ChangeHeading(this._npc));
				
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
