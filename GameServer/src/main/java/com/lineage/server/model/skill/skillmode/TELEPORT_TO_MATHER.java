package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 世界樹的呼喚131
 * @author dexc
 *
 */
public class TELEPORT_TO_MATHER extends SkillMode {

	public TELEPORT_TO_MATHER() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
		final int dmg = 0;// magic.calcMagicDamage(L1SkillId.CURE_POISON);
		final L1PcInstance pc = (L1PcInstance) cha;
		int _locX = 33051, _locY = 32337, _mapid = 4;
		final L1Map map = L1WorldMap.get().getMap((short) _mapid);
		int r = 10;
		int tryCount = 0;
		int newX = _locX;
		int newY = _locY;
		do {
			tryCount++;
			newX = _locX + (int) (Math.random() * r) - (int) (Math.random() * r);
			newY = _locY + (int) (Math.random() * r) - (int) (Math.random() * r);
			if (map.isPassable(newX, newY, pc)) {
				break;
			}
			Thread.sleep(1);
		} while (tryCount < 5);
		if (pc.getMap().isEscapable() || pc.isGm()) {
			if (tryCount >= 5) {
				L1Teleport.teleport(pc, 33051, 32337, (short) 4, 5, true);

			} else {
				L1Teleport.teleport(pc, newX, newY, (short) _mapid, pc.getHeading(), true);
			}
		} else {
			// 276 \f1在此無法使用傳送。
			pc.sendPackets(new S_ServerMessage(276));
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		}
		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic,
			final int integer) throws Exception {
		final int dmg = 0;
		
		return dmg;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		// TODO Auto-generated method stub
	}
}
