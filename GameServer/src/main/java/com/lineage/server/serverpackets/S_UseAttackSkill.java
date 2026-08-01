package com.lineage.server.serverpackets;

import static com.lineage.server.model.skill.L1SkillId.SHAPE_CHANGE;

import java.util.concurrent.atomic.AtomicInteger;

import com.lineage.config.ConfigOther;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;

/**
 * 物件攻擊(技能使用)
 * @author dexc
 *
 */
public class S_UseAttackSkill extends ServerBasePacket {

	private static AtomicInteger _sequentialNumber = new AtomicInteger(4500000); 

	private byte[] _byte = null;

	/**
	 * 物件攻擊(武器 技能使用-不需動作代號-不送出傷害)
	 * @param cha 執行者
	 * @param targetobj 目標OBJID
	 * @param spellgfx 遠程動畫編號
	 * @param x X點
	 * @param y Y點
	 * @param actionId 動作代號
	 * @param motion 具有執行者
	 */
	public S_UseAttackSkill(final L1Character cha, final int targetobj, final int spellgfx,
			final int x, final int y, final int actionId, final boolean motion) {
		this.buildPacket(cha, targetobj, spellgfx, x, y, actionId, 0, motion);
	}

	/**
	 * 物件攻擊(NPC / PC 技能使用)
	 * @param cha 執行者
	 * @param targetobj 目標OBJID
	 * @param spellgfx 遠程動畫編號
	 * @param x X點
	 * @param y Y點
	 * @param actionId 動作代號
	 * @param dmg 傷害力
	 */
	public S_UseAttackSkill(final L1Character cha, final int targetobj, final int spellgfx,
			final int x, final int y, final int actionId, final int dmg) {
		this.buildPacket(cha, targetobj, spellgfx, x, y, 18, dmg, true);
	}


	public S_UseAttackSkill(final L1Character cha, final int targetobj, final int spellgfx,
							final int x, final int y, final int actionId, final int dmg, boolean action) {
		this.buildPacket(cha, targetobj, spellgfx, x, y, 18, dmg, action);
	}

	/**
	 * 物件攻擊(技能使用 - PC/NPC共用)
	 * @param cha 執行者
	 * @param targetobj 目標OBJID
	 * @param spellgfx 遠程動畫編號
	 * @param x X點
	 * @param y Y點
	 * @param actionId 動作代號
	 * @param dmg 傷害力
	 * @param withCastMotion 具有執行者
	 */
	private void buildPacket(final L1Character cha, final int targetobj, 
			final int spellgfx,
			final int x, final int y, 
			int actionId, 
			int dmg, 
			final boolean withCastMotion) {
		if (cha instanceof L1PcInstance) {
			// 變身中變動作代號異動
			if (cha.hasSkillEffect(SHAPE_CHANGE)
					&& (actionId == ActionCodes.ACTION_SkillAttack)) {
				
				final int tempchargfx = cha.getTempCharGfx();
				if ((tempchargfx == 5727) || (tempchargfx == 5730)) {
					// 物件具有變身 改變動作代號
					actionId = ActionCodes.ACTION_SkillBuff;
					
				} else if ((tempchargfx == 5733) || (tempchargfx == 5736)) {
					// 物件具有變身 改變動作代號
					actionId = ActionCodes.ACTION_Attack;
				}
			}
		}
		// 火靈之主動作代號強制變更
		if (cha.getTempCharGfx() == 4013) {
			actionId = ActionCodes.ACTION_Attack;
		}

		// 設置新面向
		final int newheading = calcheading(cha.getX(), cha.getY(), x, y);
		cha.setHeading(newheading);

		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(actionId);// 動作代號
		this.writeD(withCastMotion ? cha.getId() : 0x00000000);// 執行者OBJID
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
			this.writeC(0x000a); // 傷害值
			
		} else {
			this.writeC(0x0000); // 傷害值
		}
		
		this.writeC(newheading);// 新面向
		
		// 以原子方式將當前值加 1。
		this.writeD(_sequentialNumber.incrementAndGet());
		
		this.writeH(spellgfx);// 遠程動畫編號
		this.writeC(0x00); // 具備飛行動畫:6, 不具備飛行動畫:0
		this.writeH(cha.getX());// 執行者X點
		this.writeH(cha.getY());// 執行者Y點
		this.writeH(x);// 目標X點
		this.writeH(y);// 目標Y點

		this.writeH(0x00000000);
		this.writeC(0x00);
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
	
	/**
	 * 群裡距離魔法(例如火龍流星雨)
	 * @param cha
	 * @param targetobj
	 * @param x
	 * @param y
	 * @param data
	 * @param withCastMotion
	 */
	public S_UseAttackSkill(L1Character cha, int targetobj, int x, int y, int[] data, boolean withCastMotion) {
		buildPacket(cha, targetobj, x, y, data, withCastMotion);
	}
	
	/**
	 * 群裡距離魔法(例如火龍流星雨)
	 * @param cha
	 * @param targetobj
	 * @param x
	 * @param y
	 * @param data
	 * @param withCastMotion
	 */
	private void buildPacket(L1Character cha, int targetobj, int x, int y, int[] data, boolean withCastMotion) {
		if (cha instanceof L1PcInstance) {
			// 變身中變動作代號異動
			if (cha.hasSkillEffect(SHAPE_CHANGE)
					&& (data[0] == ActionCodes.ACTION_SkillAttack)) {
				
				final int tempchargfx = cha.getTempCharGfx();
				if ((tempchargfx == 5727) || (tempchargfx == 5730)) {
					// 物件具有變身 改變動作代號
					data[0] = ActionCodes.ACTION_SkillBuff;
					
				} else if ((tempchargfx == 5733) || (tempchargfx == 5736)) {
					// 物件具有變身 改變動作代號
					data[0] = ActionCodes.ACTION_Attack;
				}
			}
		}
		// 火靈之主動作代號強制變更
		if (cha.getTempCharGfx() == 4013) {
			data[0] = ActionCodes.ACTION_Attack;
		}

		// 設置新面向
		final int newheading = calcheading(cha.getX(), cha.getY(), x, y);
		cha.setHeading(newheading);

		this.writeC(S_OPCODE_ATTACKPACKET);
		this.writeC(data[0]);// 動作代號
		this.writeD(withCastMotion ? cha.getId() : 0x00000000);// 執行者OBJID
		this.writeD(targetobj);// 目標OBJID
		
		if (data[1] > 0) {
			this.writeC(0x000a); // 傷害值
			
		} else {
			this.writeC(0x0000); // 傷害值
		}
		
		this.writeC(newheading);// 新面向
		
		// 以原子方式將當前值加 1。
		this.writeD(_sequentialNumber.incrementAndGet());
		
		this.writeH(data[2]);// 遠程動畫編號
		this.writeC(data[3]); // 具備飛行動畫:6, 不具備飛行動畫:0
		this.writeH(cha.getX());// 執行者X點
		this.writeH(cha.getY());// 執行者Y點
		this.writeH(x);// 目標X點
		this.writeH(y);// 目標Y點

		this.writeH(0x00000000);
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