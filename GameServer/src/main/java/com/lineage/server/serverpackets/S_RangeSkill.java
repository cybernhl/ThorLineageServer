package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.skill.TargetStatus;

/**
 * 範圍魔法<br>
 * 類名稱：S_RangeSkill<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午11:15:19<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_RangeSkill extends ServerBasePacket {

	private static AtomicInteger _sequentialNumber = new AtomicInteger(9000000); 

	private byte[] _byte = null;

	public static final int TYPE_NODIR = 0;

	public static final int TYPE_DIR = 8;

	/**
	 * 範圍魔法
	 * @param cha
	 * @param targetList
	 * @param spellgfx
	 * @param actionId
	 * @param type
	 */
	public S_RangeSkill(final L1Character cha, 
			final ArrayList<TargetStatus> targetList,
			final int spellgfx, final int actionId, final int type) {
		this.writeC(S_OPCODE_RANGESKILLS);
		this.writeC(actionId);
		
		this.writeD(cha.getId());
		this.writeH(cha.getX());
		this.writeH(cha.getY());
		
		switch (type) {
		case TYPE_NODIR:
			this.writeC(cha.getHeading());
			break;
			
		case TYPE_DIR:
			final int newHeading = 
				calcheading(
						cha.getX(),
						cha.getY(), 
						targetList.get(0).getTarget().getX(),
						targetList.get(0).getTarget().getY()
						);
			cha.setHeading(newHeading);
			this.writeC(cha.getHeading());
			break;
		}
		
		this.writeD(_sequentialNumber.incrementAndGet()); // 番號送。
		this.writeH(spellgfx);
		this.writeC(type); // 0:範圍 6:遠距離 8:範圍&遠距離
		this.writeH(0x0000);
		this.writeH(targetList.size());
		
		for (TargetStatus target : targetList) {
			//System.out.println("TG: "+target.getTarget().getName() + "/" + target.isCalc());
			this.writeD(target.getTarget().getId());
			if (ConfigOther.poly_Mlist.contains(target.getTarget().getTempCharGfx())) {
				this.writeC(0x00);
			}
			if (target.isCalc()) {
				this.writeC(0x20);
				
			} else {
				this.writeC(0x00); // 0x00:無傷害 大於0傷害質
			}
		}
	}

	public S_RangeSkill(final L1Character cha,
						final ArrayList<TargetStatus> targetList,
						final int spellgfx, final int actionId, final int type, boolean action) {
		this.writeC(S_OPCODE_RANGESKILLS);
		this.writeC(actionId);

		this.writeD(action ? cha.getId() : 0);
		this.writeH(cha.getX());
		this.writeH(cha.getY());

		switch (type) {
			case TYPE_NODIR:
				this.writeC(cha.getHeading());
				break;

			case TYPE_DIR:
				final int newHeading =
						calcheading(
								cha.getX(),
								cha.getY(),
								targetList.get(0).getTarget().getX(),
								targetList.get(0).getTarget().getY()
						);
				cha.setHeading(newHeading);
				this.writeC(cha.getHeading());
				break;
		}

		this.writeD(_sequentialNumber.incrementAndGet()); // 番號送。
		this.writeH(spellgfx);
		this.writeC(type); // 0:範圍 6:遠距離 8:範圍&遠距離
		this.writeH(0x0000);
		this.writeH(targetList.size());

		for (TargetStatus target : targetList) {
			//System.out.println("TG: "+target.getTarget().getName() + "/" + target.isCalc());
			this.writeD(target.getTarget().getId());
			if (ConfigOther.poly_Mlist.contains(target.getTarget().getTempCharGfx())) {
				this.writeC(0x00);
			}
			if (target.isCalc()) {
				this.writeC(0x20);

			} else {
				this.writeC(0x00); // 0x00:無傷害 大於0傷害質
			}
		}
	}

	private static int calcheading(final int myx, final int myy, final int tx, final int ty) {
		int newheading = 0;
		if ((tx > myx) && (ty > myy)) {
			newheading = 3;
		}
		if ((tx < myx) && (ty < myy)) {
			newheading = 7;
		}
		if ((tx > myx) && (ty == myy)) {
			newheading = 2;
		}
		if ((tx < myx) && (ty == myy)) {
			newheading = 6;
		}
		if ((tx == myx) && (ty < myy)) {
			newheading = 0;
		}
		if ((tx == myx) && (ty > myy)) {
			newheading = 4;
		}
		if ((tx < myx) && (ty > myy)) {
			newheading = 5;
		}
		if ((tx > myx) && (ty < myy)) {
			newheading = 1;
		}
		return newheading;
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