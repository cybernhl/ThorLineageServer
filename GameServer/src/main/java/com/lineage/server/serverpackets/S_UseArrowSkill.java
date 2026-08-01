package com.lineage.server.serverpackets;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;

/**
 * 物件攻擊(遠程-物理攻擊 PC/NPC共用)
 * @author dexc
 *
 */
public class S_UseArrowSkill extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件攻擊 - <font color="#ff0000">命中</font>(遠程-物理攻擊 PC/NPC共用)
	 * @param cha 執行者
	 * @param targetobj 目標OBJID
	 * @param spellgfx 遠程動畫編號
	 * @param x 目標X
	 * @param y 目標Y
	 * @param dmg 傷害力
	 */
	public S_UseArrowSkill(final L1Character cha, final int targetobj, 
			final int spellgfx, 
			final int x,
			final int y, 
			int dmg) {

		int aid = 1;
		// 外型編號改變動作
		switch (cha.getTempCharGfx()) {
		case 3860:// 妖魔弓箭手
			aid = 21;
			break;
			
		case 2716:// 古代亡靈
			aid = 19;
			break;
		}

		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(aid);// 動作代號
		this.writeD(cha.getId());// 執行者OBJID
		this.writeD(targetobj);// 目標OBJID
		//改無動作 by wei512
		final L1Object target = World.get().findObject(targetobj);
		if (target instanceof L1PcInstance) {
			L1PcInstance tg = (L1PcInstance) target;
			if (ConfigOther.poly_Mlist.contains(tg.getTempCharGfx())) {
				dmg = 0;
			}
		}
		if (dmg > 0) {
			this.writeC(0x0a); // 傷害值
			
		} else {
			this.writeC(0x00); // 傷害值
		}
		
		this.writeC(cha.getHeading());// 新面向
		
		// 以原子方式將當前值加 1。
		this.writeD(0x00000152);
		
		this.writeH(spellgfx);// 遠程動畫編號
		this.writeC(0x7f);// ??
		this.writeH(cha.getX());// 執行者X點
		this.writeH(cha.getY());// 執行者Y點
		this.writeH(x);// 目標X點
		this.writeH(y);// 目標Y點

		this.writeD(0x00000000);
		this.writeC(0x00);
	}

	/**
	 * 物件攻擊 - <font color="#ff0000">未命中</font>(遠程-物理攻擊 PC/NPC共用)
	 * 空攻擊使用
	 * @param cha 執行者
	 * @param spellgfx 遠程動畫編號
	 * @param x 目標X
	 * @param y 目標Y
	 */
	public S_UseArrowSkill(final L1Character cha,
			final int spellgfx, 
			final int x,
			final int y
			) {

		int aid = 1;
		// 外型編號改變動作
		if (cha.getTempCharGfx() == 3860) {
			aid = 21;
		}
		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(aid);// 動作代號
		this.writeD(cha.getId());// 執行者OBJID
		this.writeD(0x00);// 目標OBJID
		this.writeC(0x00);// 傷害力
		this.writeC(cha.getHeading());// 新面向
		
		// 以原子方式將當前值加 1。
		this.writeD(0x00000152);
		
		this.writeH(spellgfx);// 遠程動畫編號
		this.writeC(0x7f);// ??
		this.writeH(cha.getX());// 執行者X點
		this.writeH(cha.getY());// 執行者Y點
		this.writeH(x);// 目標X點
		this.writeH(y);// 目標Y點

		this.writeD(0x00000000);
		this.writeC(0x00);
	}

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
