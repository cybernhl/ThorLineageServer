package com.lineage.server.serverpackets;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件攻擊(NPC用  - 近距離)
 * @author dexc
 *
 */
public class S_AttackPacketNpc extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件攻擊 - <font color="#ff0000">命中</font>(NPC用  - 近距離)
	 * @param npc 執行者
	 * @param target  目標
	 * @param type 動作編號
	 * @param dmg 傷害值
	 */
	public S_AttackPacketNpc(final L1NpcInstance npc,
			final L1Character target,
			final int type, 
			int dmg
			) {
		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(type);
		this.writeD(npc.getId());// 執行者OBJID
		this.writeD(target.getId());// 被攻擊者OBJID
		//改無動作 by wei512
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
		
		this.writeC(npc.getHeading()); // 執行者面向

		/*this.writeH(0x00); // target x
		this.writeH(0x00); // target y
		
		this.writeC(0x00); // 0x00:none 0x04:Claw 0x08:CounterMirror*/
	}

	/**
	 * 物件攻擊 - <font color="#ff0000">命中</font>(NPC用  - 近距離)
	 * @param npc 執行者
	 * @param target  目標
	 * @param type 動作編號
	 * @param dmg 傷害值
	 */
	public S_AttackPacketNpc(final L1NpcInstance npc,
			final int targetid,
			final int type, 
			final int dmg
			) {
		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(type);
		this.writeD(npc.getId());// 執行者OBJID
		this.writeD(targetid);// 被攻擊者OBJID
		
		if (dmg > 0) {
			this.writeC(0x0a); // 傷害值
			
		} else {
			this.writeC(0x00); // 傷害值
		}
		
		this.writeC(npc.getHeading()); // 執行者面向

		/*this.writeH(0x00); // target x
		this.writeH(0x00); // target y
		
		this.writeC(0x00); // 0x00:none 0x04:Claw 0x08:CounterMirror*/
	}
	
	/**
	 * 物件攻擊 - <font color="#ff0000">未命中</font>(NPC用  - 近距離)
	 * @param npc 執行者
	 * @param target 目標
	 * @param type 動作編號
	 */
	public S_AttackPacketNpc(final L1NpcInstance npc, 
			final L1Character target, 
			final int type
			) {
		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(type);
		this.writeD(npc.getId());// 執行者OBJID
		this.writeD(target.getId());// 被攻擊者OBJID
		this.writeC(0x00); // 傷害值
		this.writeC(npc.getHeading()); // 執行者面向

		this.writeH(0x00); // target x
		this.writeH(0x00); // target y
		
		this.writeC(0x00); // 0x00:none 0x04:Claw 0x08:CounterMirror
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
