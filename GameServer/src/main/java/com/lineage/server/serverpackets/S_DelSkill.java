package com.lineage.server.serverpackets;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Skills;

/**
 * 移除技能列表
 * @author dexc
 *
 */
public class S_DelSkill extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 移除技能列表(單一技能)
	 * @param pc 執行人物
	 * @param skillid 技能編號
	 */
	public S_DelSkill(final L1PcInstance pc, final int skillid) {
		final byte skill_list_values = 0x20;
		// 定義魔法名單
		final int[] skillList = new int[skill_list_values];
		// 取得魔法資料
		final L1Skills skill = SkillsTable.get().getTemplate(skillid);
		if (skill.getSkillLevel() > 0) {
			skillList[(skill.getSkillLevel() - 1)] += skill.getId();

			this.writeC(S_OPCODE_DELSKILL);
			this.writeC(skill_list_values);

			for (final int element : skillList) {
				this.writeC(element);
			}

			if (pc != null) {
				pc.removeSkillMastery(skillid);
			}
		}
	}

	/**
	 * 移除技能列表(技能群)
	 * @param pc 執行人物
	 * <BR>
	 * @param level1 法師技能LV1
	 * @param level2 法師技能LV2
	 * @param level3 法師技能LV3
	 * @param level4 法師技能LV4
	 * @param level5 法師技能LV5
	 * @param level6 法師技能LV6
	 * @param level7 法師技能LV7
	 * @param level8 法師技能LV8
	 * @param level9 法師技能LV9
	 * @param level10 法師技能LV10
	 * <BR>
	 * @param knight1 騎士技能1
	 * @param knight2 騎士技能2
	 * <BR>
	 * @param de1 黑妖技能1
	 * @param de2 黑妖技能2
	 * <BR>
	 * @param royal 王族技能
	 * <BR>
	 * @param un2
	 * <BR>
	 * @param elf1 精靈技能1
	 * @param elf2 精靈技能2
	 * @param elf3 精靈技能3
	 * @param elf4 精靈技能4
	 * @param elf5 精靈技能5
	 * @param elf6 精靈技能6
	 */
	public S_DelSkill(final L1PcInstance pc, 
			final int level1, final int level2, final int level3, final int level4, final int level5, 
			final int level6, final int level7, final int level8, final int level9, final int level10, 
			final int knight1, final int knight2, 
			final int de1, final int de2, 
			final int royal,
			final int un, 
			final int elf1, final int elf2, final int elf3, final int elf4, final int elf5, final int elf6
			) {
		
		final int i6 = level5 + level6 + level7 + level8;
		final int j6 = level9 + level10;
		
		this.writeC(S_OPCODE_DELSKILL);
		
		if ((i6 > 0x00) && (j6 == 0x00)) {
			this.writeC(0x32);// 50
			
		} else if (j6 > 0x00) {
			this.writeC(0x64);// 100
			
		} else {
			this.writeC(0x20);// 32
		}
		
		this.writeC(level1);
		this.writeC(level2);
		this.writeC(level3);
		this.writeC(level4);
		this.writeC(level5);
		this.writeC(level6);
		this.writeC(level7);
		this.writeC(level8);
		this.writeC(level9);
		this.writeC(level10);
		
		this.writeC(knight1);
		this.writeC(knight2);
		
		this.writeC(de1);
		this.writeC(de2);
		
		this.writeC(royal);
		
		this.writeC(un);
		
		this.writeC(elf1);
		this.writeC(elf2);
		this.writeC(elf3);
		this.writeC(elf4);
		this.writeC(elf5);
		this.writeC(elf6);
		
		//this.writeD(0x00000000);
		//this.writeD(0x00000000);
		
		final int[] ix = new int[] { 
				level1, level2, level3, level4, level5, 
				level6, level7, level8, level9, level10, 
				knight1, knight2, 
				de1, de2, 
				royal,
				un, 
				elf1, elf2, elf3, elf4, elf5, elf6
				};

		for (int i = 0; i < ix.length; i++) {
			int type = ix[i];
			int rtType = 128;
			int rt = 0;
			int skillid = -1;
			while (rt < 8) {// 每組8項技能
				if (type - rtType >= 0) {
					type -= rtType;
					switch (rtType) {
					case 128:
						skillid = (i << 3) + 8;
						break;
					case 64:
						skillid = (i << 3) + 7;
						break;
					case 32:
						skillid = (i << 3) + 6;
						break;
					case 16:
						skillid = (i << 3) + 5;
						break;
					case 8:
						skillid = (i << 3) + 4;
						break;
					case 4:
						skillid = (i << 3) + 3;
						break;
					case 2:
						skillid = (i << 3) + 2;
						break;
					case 1:
						skillid = (i << 3) + 1;
						break;
					}
					if (skillid != -1) {
						if (pc != null) {
							pc.removeSkillMastery(skillid);
						}
					}
				}
				rt++;
				rtType = rtType >> 1;
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
