package com.lineage.server.serverpackets;

import com.lineage.server.datatables.sql.ServerTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.utils.RangeInt;

/**
 * 角色資訊<br>
 * 類名稱：S_OwnCharStatus<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月21日 上午10:46:02<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_OwnCharStatus extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * 角色屬性與能力值
	 * @param bp
	 * @param cha
	 * @return
	 */
	public S_OwnCharStatus(L1PcInstance pc) {
		int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
		time = time - (time % 300);
 
		this.writeC(OpcodesServer.S_OPCODE_OWNCHARSTATUS);
		this.writeD(pc.getId());

		this.writeC(pc.getLevel());
		
		this.writeExp(pc.getExp());

		this.writeC(pc.getStr());
		this.writeC(pc.getInt());
		this.writeC(pc.getWis());
		this.writeC(pc.getDex());
		this.writeC(pc.getCon());
		this.writeC(pc.getCha());
		this.writeH(pc.getCurrentHp());
		this.writeH(pc.getMaxHp());
		this.writeH(pc.getCurrentMp());
		this.writeH(pc.getMaxMp());
		int ac = RangeInt.ensure((pc.getAc() + pc.getEquipSlot().getAttachAc()), -211, 44);
		this.writeC(ac);
		this.writeD(time);//時間
		int food = (int) (((double) pc.get_food() / (double) 225) * 29);
		this.writeC(food);// 滿腹度
		this.writeC(pc.getInventory().getWeight182());// 負重
		this.writeH(pc.getLawful());
		this.writeC(pc.getFire());
		this.writeC(pc.getWater());
		this.writeC(pc.getWind());
		this.writeC(pc.getEarth());
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
	}
}