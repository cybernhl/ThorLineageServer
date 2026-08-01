package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PetInstance;

/**
 * 物品名單(寵物背包)<br>
 * 類名稱：S_PetInventory<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午3:00:20<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_PetInventory extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品名單(寵物背包)
	 * @param pet
	 * @param b 寵物是否剛進入
	 */
	public S_PetInventory(final L1PetInstance pet, boolean b) {
		isTrue(pet);
	}
	
	private void isTrue(final L1PetInstance pet) {
		final List<L1ItemInstance> itemList = pet.getInventory().getItems();

		this.writeC(S_OPCODE_SHOWRETRIEVELIST);
		this.writeD(pet.getId());
		this.writeH(itemList.size());
		this.writeC(0x0b);
		for (final L1ItemInstance item : itemList) {
			if (item != null) {
				this.writeD(item.getId());
				this.writeC(0x16);
				this.writeH(item.get_gfxid());
				this.writeC(item.getBless());
				this.writeD((int) Math.min(item.getCount(), 2000000000));
				this.writeC(item.isIdentified() ? 1 : 0);
				this.writeS(item.getViewName());
			}
		}
		this.writeC(0x0a);
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
