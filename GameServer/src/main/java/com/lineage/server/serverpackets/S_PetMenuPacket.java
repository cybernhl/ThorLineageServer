package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;

/**
 * 召喚獸、寵物控制清單<br>
 * 類名稱：S_PetMenuPacket<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月6日 下午12:45:05<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_PetMenuPacket extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC對話視窗
	 * @param npc
	 * @param exppercet
	 */
	public S_PetMenuPacket(final L1NpcInstance npc, final int exppercet) {
		this.buildpacket(npc, exppercet);
	}

	private void buildpacket(final L1NpcInstance npc, final int exppercet) {
		this.writeC(S_OPCODE_SHOWHTML);

		if (npc instanceof L1PetInstance) { // 寵物
			final L1PetInstance pet = (L1PetInstance) npc;
			this.writeD(pet.getId());
			this.writeS("anicom");
			this.writeC(0x00);
			this.writeH(0x0a);
			switch (pet.getCurrentPetStatus()) {
			case 1:
				this.writeS("$469"); // 攻擊態勢
				break;
			case 2:
				this.writeS("$470"); // 防禦態勢
				break;
			case 3:
				this.writeS("$471"); // 休憩
				break;
			case 5:
				this.writeS("$472"); // 警戒
				break;
			default:
				this.writeS("$471"); // 休憩
			break;
			}
			this.writeS(Integer.toString(pet.getCurrentHp())); // 目前HP
			this.writeS(Integer.toString(pet.getMaxHp())); // 最大HP
			this.writeS(Integer.toString(pet.getCurrentMp())); // 目前MP
			this.writeS(Integer.toString(pet.getMaxMp())); // 最大MP
			this.writeS(Integer.toString(pet.getLevel())); // 等級
			this.writeS(pet.getName());
			this.writeS("$611"); // 飽食度
			this.writeS(Integer.toString(exppercet)); // 經驗值%
			this.writeS(Integer.toString(pet.getLawful())); // 正義值
			
		} else if (npc instanceof L1SummonInstance) { // 召喚獸
			final L1SummonInstance summon = (L1SummonInstance) npc;
			this.writeD(summon.getId());
			this.writeS("moncom");
			this.writeC(0x00);
			this.writeH(0x06); // 渡引數文字數模樣
			switch (summon.get_currentPetStatus()) {
			case 1:
				this.writeS("$469"); // 攻擊態勢
				break;
			case 2:
				this.writeS("$470"); // 防禦態勢
				break;
			case 3:
				this.writeS("$471"); // 休憩
				break;
			case 5:
				this.writeS("$472"); // 警戒
				break;
			default:
				this.writeS("$471"); // 休憩
			break;
			}
			this.writeS(Integer.toString(summon.getCurrentHp())); // 目前HP
			this.writeS(Integer.toString(summon.getMaxHp())); // 最大HP
			this.writeS(Integer.toString(summon.getCurrentMp())); // 目前MP
			this.writeS(Integer.toString(summon.getMaxMp())); // 最大MP
			this.writeS(Integer.toString(summon.getLevel())); // 等級
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
