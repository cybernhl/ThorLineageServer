package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1ItemStatus;

/**
 * 物品增加<br>
 * 類名稱：S_AddItem<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午4:20:03<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_AddItem extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品增加
	 */
	public S_AddItem(final L1ItemInstance item) {
		this.writeC(S_OPCODE_ADDITEM);
		this.writeD(item.getId());
		int type = item.getItem().getUseType();
		if (type < 0) {
			type = 0;
		}
		if (type >= 0x7FFF) { // 新增輔助道具
			type = 0x7FFF;
		}
		this.writeH(type);
		
		/*if (item.getChargeCount() > 0) {
			this.writeC(item.getChargeCount());// 可用次數
			
		} else {
			this.writeC(0x00);// 可用次數
		}*/
		
		this.writeH(item.get_gfxid());
		this.writeC(item.getBless());
		this.writeD((int) Math.min(item.getCount(), 2000000000));
		this.writeC((item.isIdentified()) ? 0x01 : 0x00);
		this.writeS(item.getViewName());
		if (!item.isIdentified()) {
			// 未鑒定 不發送詳細資訊
			this.writeC(0x00);
			
		} else {
			final L1ItemStatus itemInfo = new L1ItemStatus(item);
			final byte[] status = itemInfo.getStatusBytes().getBytes();
			this.writeC(status.length);
			for (final byte b : status) {
				this.writeC(b);
			}
		}
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
