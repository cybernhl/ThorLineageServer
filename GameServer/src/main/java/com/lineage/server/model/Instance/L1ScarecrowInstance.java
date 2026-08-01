package com.lineage.server.model.Instance;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.CalcExp;

/**
 * 木人控制項
 * @author dexc
 *
 */
public class L1ScarecrowInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1ScarecrowInstance.class);

	public L1ScarecrowInstance(final L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(final L1PcInstance player) {
		try {
			final L1AttackMode attack = new L1AttackPc(player, this);
			if (attack.calcHit()) {
				if (player.getLevel() < 5) { // ＬＶ制限場合變更
					final ArrayList<L1PcInstance> targetList = new ArrayList<L1PcInstance>();

					targetList.add(player);
					final ArrayList<Integer> hateList = new ArrayList<Integer>();
					hateList.add(1);
					final int exp = (int) this.getExp();
					CalcExp.calcExp(player, this.getId(), targetList, hateList, exp);
				}
				if (this.getHeading() < 7) { // 今向取得
					this.setHeading(this.getHeading() + 1); // 今向設定
				} else {
					this.setHeading(0); // 今向7 以上今向0戾
				}
				this.broadcastPacketAll(new S_ChangeHeading(this)); // 向變更
			}
			attack.action();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance l1pcinstance) {

	}

	public void onFinalAction() {

	}

	public void doFinalAction() {
	}
}
