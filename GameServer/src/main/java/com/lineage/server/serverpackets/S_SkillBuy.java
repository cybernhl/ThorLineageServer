package com.lineage.server.serverpackets;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 魔法購買(金幣)<br>
 * 類名稱：S_SkillBuy<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月10日 下午11:57:11<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_SkillBuy extends ServerBasePacket {

	private static final Log _log = LogFactory.getLog(S_SkillBuy.class);

	private byte[] _byte = null;

	/**
	 * 魔法購買(金幣)
	 * @param pc 學習者
	 * @param newSkillList 學習清單 
	 */
	public S_SkillBuy(final L1PcInstance pc, final ArrayList<Integer> newSkillList) {
		try {
			if (newSkillList.size() <= 0) {
				this.writeC(S_OPCODE_SKILLBUY);
				this.writeH(0x0000);
				
			} else {
				this.writeC(S_OPCODE_SKILLBUY);
				this.writeD(300);
				this.writeH(newSkillList.size());
				for (final Integer integer : newSkillList) {
					this.writeD(integer);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
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
