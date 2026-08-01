package com.lineage.server.serverpackets;

import com.lineage.config.ConfigOther;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件攻擊(PC 用)
 * @author dexc
 *
 */
public class S_AttackPacketPc extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件攻擊 - <font color="#ff0000">命中</font>(PC 用 - 近距離)
	 * @param pc 執行者
	 * @param target 目標
	 * @param type 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
	 * @param dmg 傷害力
	 */
	public S_AttackPacketPc(final L1PcInstance pc, 
			final L1Character target, 
			final int type, 
			int dmg) {
		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(ActionCodes.ACTION_Attack);// ACTION_AltAttack
		this.writeD(pc.getId());
		this.writeD(target.getId());
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
		
		this.writeC(pc.getHeading());
		
		this.writeH(0x00); // target x
		this.writeH(0x00); // target y
		this.writeC(type); // 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
	}

	/**
	 * 物件攻擊 - <font color="#ff0000">未命中</font>(PC 用 - 近距離)
	 * @param pc
	 * @param target
	 */
	public S_AttackPacketPc(final L1PcInstance pc, 
			final L1Character target) {
		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(ActionCodes.ACTION_Attack);// ACTION_AltAttack
		this.writeD(pc.getId());
		this.writeD(target.getId());
		this.writeC(0x00); // damage
		this.writeC(pc.getHeading());
		
		this.writeH(0x00); // target x
		this.writeH(0x00); // target y
		this.writeC(0x00); // 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
	}
	
	/**
	 * 物件攻擊 - <font color="#ff0000">空擊</font>(PC 用 - 近距離)
	 * @param pc
	 */
	public S_AttackPacketPc(final L1PcInstance pc) {
		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(ActionCodes.ACTION_Attack);// ACTION_AltAttack
		this.writeD(pc.getId());
		this.writeD(0x00);
		this.writeC(0x00); // damage
		this.writeC(pc.getHeading());
		
		this.writeH(0x00); // target x
		this.writeH(0x00); // target y
		this.writeC(0x00); // 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
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
