package com.lineage.server.serverpackets;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.list.PcLvSkillList;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 魔法購買清單(材料)<br>
 * 類名稱：S_SkillBuyItem<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月10日 下午11:59:21<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_SkillBuyItem extends ServerBasePacket {

	private static final Log _log = LogFactory.getLog(S_SkillBuyItem.class);

	private byte[] _byte = null;

	/**
	 * 魔法購買清單(材料)
	 * @param pc
	 */
	public S_SkillBuyItem(final L1PcInstance pc, final L1NpcInstance npc) {
		
		final ArrayList<Integer> skillList = PcLvSkillList.scount(pc);
		
		final ArrayList<Integer> newSkillList = new ArrayList<Integer>();
		
		for (final Integer integer : skillList) {
			// 檢查是否已學習該法術
			if (!CharSkillReading.get().spellCheck(pc.getId(), (integer + 1))) {
				newSkillList.add(integer);
			}
		}

		if (newSkillList.size() <= 0) {
			this.writeC(S_OPCODE_SKILLBUY);
			this.writeH(0x0000);
			
		} else {
			try {
				this.writeC(S_OPCODE_SKILLBUY);
				this.writeH(newSkillList.size());
				for (final Integer integer : newSkillList) {
					this.writeD(integer);
				}
				
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
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
