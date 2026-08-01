package com.lineage.server.serverpackets;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件封包 - 其他人物
 * @author dexc
 *
 */
public class S_OtherCharPacks extends ServerBasePacket {

	private static final int STATUS_POISON = 1;
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	//private static final int STATUS_FREEZE = 8;
	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;
	private static final int STATUS_FASTMOVABLE = 64;
	//private static final int STATUS_GHOST = 128;

	private byte[] _byte = null;

	/**
	 * 物件封包 - 其他人物
	 * @param pc
	 */
	public S_OtherCharPacks(final L1PcInstance pc) {
		int status = STATUS_PC;

		if (pc.getPoison() != null) { // 毒狀態
			if (pc.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		if (pc.isInvisble()) {
			status |= STATUS_INVISIBLE;
		}
		if (pc.isBrave()) {
			status |= STATUS_BRAVE;
		}
		if (pc.isElfBrave()) {
			// 場合、STATUS_BRAVESTATUS_ELFBRAVE立。
			// STATUS_ELFBRAVE效果無？
			status |= STATUS_BRAVE;
			status |= STATUS_ELFBRAVE;
		}
		if (pc.isFastMovable()) {
			status |= STATUS_FASTMOVABLE;
		}

		this.writeC(S_OPCODE_CHARPACK);
		this.writeH(pc.getX());
		this.writeH(pc.getY());
		this.writeD(pc.getId());

		if (pc.isDead()) {
			this.writeH(pc.getTempCharGfxAtDead());
			
		} else {
			this.writeH(pc.getTempCharGfx());
		}

		if (pc.isDead()) {
			this.writeC(pc.getStatus());
		} else {
			this.writeC(pc.getCurrentWeapon());
		}

		this.writeC(pc.getHeading());
		// writeC(0); // makes char invis (0x01), cannot move. spells display
		this.writeC(pc.getChaLightSize());
		this.writeC(pc.getMoveSpeed());
		this.writeD(0x00000000); // exp
		this.writeH(pc.getLawful());

		final StringBuilder stringBuilder = new StringBuilder();
		if (pc.get_other().get_color() != 0) {
			stringBuilder.append(pc.get_other().color());
		}
		stringBuilder.append(pc.getName());

		this.writeS(stringBuilder.toString());
		this.writeS(pc.getTitle());
		this.writeC(status); // 狀態
		this.writeD(pc.getClanid());
		this.writeS(pc.getClanname()); // 血盟名稱
		this.writeS(null); // 主人名稱

		// 0:NPC,道具 
		// 1:中毒 ,
		// 2:隱身 
		// 4:人物
		// 8:詛咒 
		// 16:勇水
		// 32:??
		// 64:??(??)
		// 128:invisible but name
		this.writeC(pc.getClanRank() << 4);// 血盟階級
		
		this.writeC(0xff); // HP顯示
		if (pc.hasSkillEffect(STATUS_BRAVE3)) {
//			this.writeC(0x08); // 巧克力蛋糕
			this.writeC(0x00);
		} else {
			this.writeC(0x00); // 距離(通)
		}
		this.writeC(0x00); // LV
		this.writeC(0x00);
		this.writeC(0xff);
		this.writeC(0xff);
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