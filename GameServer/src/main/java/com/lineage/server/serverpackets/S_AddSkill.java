package com.lineage.server.serverpackets;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Skills;

/**
 * 增加技能列表<br>
 * 類名稱：S_AddSkill<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:50:33<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_AddSkill extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 增加技能列表(單一技能)裝備指定物品學習魔法、Npc購買魔法
	 * @param pc 執行人物
	 * @param skillid 技能編號
	 */
	public S_AddSkill(final L1PcInstance pc, final int skillid) {
		final byte skill_list_values = 0x20;
		// 定義魔法名單
		final int[] skillList = new int[skill_list_values];
		// 取得魔法資料
		final L1Skills skill = SkillsTable.get().getTemplate(skillid);
		skillList[(skill.getSkillLevel() - 1)] += skill.getId();

		this.writeC(S_OPCODE_ADDSKILL);
		this.writeC(skill_list_values);

		for (final int element : skillList) {
			this.writeC(element);
		}

		if (pc != null) {
			pc.setSkillMastery(skillid);
		}
	}

	/**
	 * 增加技能列表(技能陣列)角色進入遊戲加載已學習技能
	 * @param pc
	 * @param skills
	 */
	public S_AddSkill(L1PcInstance pc, int[] skills) {

		this.writeC(S_OPCODE_ADDSKILL);
		this.writeC(0x20);//32
		
		this.writeC(skills[0]);// 法師技能LV1
		this.writeC(skills[1]);// 法師技能LV2
		this.writeC(skills[2]);// 法師技能LV3
		this.writeC(skills[3]);// 法師技能LV4
		this.writeC(skills[4]);// 法師技能LV5
		this.writeC(skills[5]);// 法師技能LV6
		this.writeC(skills[6]);// 法師技能LV7
		this.writeC(skills[7]);// 法師技能LV8
		this.writeC(skills[8]);// 法師技能LV9
		this.writeC(skills[9]);// 法師技能LV10
		
		this.writeC(skills[10]);// 騎士技能1
		this.writeC(skills[11]);// 騎士技能2
		
		this.writeC(skills[12]);// 黑妖技能1
		this.writeC(skills[13]);// 黑妖技能2
		
		this.writeC(skills[14]);// 王族技能
		
		this.writeC(skills[15]);// un
		
		this.writeC(skills[16]);// 精靈技能 1
		this.writeC(skills[17]);// 精靈技能 2
		this.writeC(skills[18]);// 精靈技能 3
		this.writeC(skills[19]);// 精靈技能 4
		this.writeC(skills[20]);// 精靈技能 5
		this.writeC(skills[21]);// 精靈技能 6
		
		this.writeC(0);
		this.writeC(0);
		this.writeC(0);
		this.writeC(0);

		final int[] ix = new int[] { 
				skills[0], skills[1], skills[2], skills[3], skills[4], // 法師技能 1 ~ 5
				skills[5], skills[6], skills[7], skills[8], skills[9], // 法師技能 6 ~ 10
				
				skills[10], skills[11], // 騎士技能 1 ~ 2
				
				skills[12], skills[13], // 黑妖技能 1 ~ 2
				
				skills[14], // 王族技能
				
				skills[15], 
				
				skills[16], skills[17], skills[18], skills[19], skills[20], skills[21] // 精靈技能 1 ~ 6
				};

		for (int i = 0; i < ix.length; i++) {
			int type = ix[i];
			int rtType = 128;//128
			int rt = 0;
			int skillid = -1;
			while (rt < 8) {// 每組8項技能
				if (type - rtType >= 0) {
					type -= rtType;
					switch (rtType) {
					case 128://128
						skillid = (i << 3) + 8;
						break;
					case 64://64
						skillid = (i << 3) + 7;
						break;
					case 32://32
						skillid = (i << 3) + 6;
						break;
					case 16://16
						skillid = (i << 3) + 5;
						break;
					case 8://8
						skillid = (i << 3) + 4;
						break;
					case 4://4
						skillid = (i << 3) + 3;
						break;
					case 2://2
						skillid = (i << 3) + 2;
						break;
					case 1://1
						skillid = (i << 3) + 1;
						break;
					}
					if (skillid != -1) {
						if (pc != null) {
							pc.setSkillMastery(skillid);
						}
					}
				}
				rt++;
				rtType = rtType >> 1;
			}
		}
	}

	/**
	 * 增加技能列表(技能群)Gm指令學習魔法
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
	 * @param knight1 騎士技能 1
	 * @param knight2 騎士技能 2
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
	public S_AddSkill(final L1PcInstance pc, 
			final int level1, final int level2, final int level3, final int level4, final int level5, 
			final int level6, final int level7, final int level8, final int level9, final int level10, 
			final int knight1, final int knight2, 
			final int de1, final int de2,
			final int royal, 
			final int un, 
			final int elf1, final int elf2, final int elf3, final int elf4, final int elf5, final int elf6
			) {

		this.writeC(S_OPCODE_ADDSKILL);
		this.writeC(0x20);//32
		
		this.writeC(level1);// 法師技能LV1
		this.writeC(level2);// 法師技能LV2
		this.writeC(level3);// 法師技能LV3
		this.writeC(level4);// 法師技能LV4
		this.writeC(level5);// 法師技能LV5
		this.writeC(level6);// 法師技能LV6
		this.writeC(level7);// 法師技能LV7
		this.writeC(level8);// 法師技能LV8
		this.writeC(level9);// 法師技能LV9
		this.writeC(level10);// 法師技能LV10
		
		this.writeC(knight1);// 騎士技能1
		this.writeC(knight2);// 騎士技能2
		
		this.writeC(de1);// 黑妖技能1
		this.writeC(de2);// 黑妖技能2
		
		this.writeC(royal);// 王族技能
		
		this.writeC(un);// un
		
		this.writeC(elf1);
		this.writeC(elf2);
		this.writeC(elf3);
		this.writeC(elf4);
		this.writeC(elf5);
		this.writeC(elf6);
		
		this.writeC(0);
		this.writeC(0);
		this.writeC(0);
		this.writeC(0);

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
			int rtType = 128;//128
			int rt = 0;
			int skillid = -1;
			while (rt < 8) {// 每組8項技能
				if (type - rtType >= 0) {
					type -= rtType;
					switch (rtType) {
					case 128://128
						skillid = (i << 3) + 8;
						break;
					case 64://64
						skillid = (i << 3) + 7;
						break;
					case 32://32
						skillid = (i << 3) + 6;
						break;
					case 16://16
						skillid = (i << 3) + 5;
						break;
					case 8://8
						skillid = (i << 3) + 4;
						break;
					case 4://4
						skillid = (i << 3) + 3;
						break;
					case 2://2
						skillid = (i << 3) + 2;
						break;
					case 1://1
						skillid = (i << 3) + 1;
						break;
					}
					if (skillid != -1) {
						if (pc != null) {
							pc.setSkillMastery(skillid);
						}
					}
				}
				rt++;
				rtType = rtType >> 1;
			}
		}
	}
	public S_AddSkill(final int level1, final int level2, final int level3,
			final int level4, final int level5, final int level6,
			final int level7, final int level8, final int level9,
			final int level10, final int knight, final int l2, final int de1,
			final int de2, final int royal, final int l3, final int elf1,
			final int elf2, final int elf3, final int elf4, final int elf5,
			final int elf6, final int k5, final int l5, final int m5,
			final int n5, final int o5, final int p5) {
		final int i6 = level5 + level6 + level7 + level8;
		final int j6 = level9 + level10;
		writeC(S_OPCODE_ADDSKILL);
		if (i6 > 0 && j6 == 0) {
			writeC(50);
		} else if (j6 > 0) {
			writeC(100);
		} else {
			writeC(32);
		}
		writeC(level1);
		writeC(level2);
		writeC(level3);
		writeC(level4);
		writeC(level5);
		writeC(level6);
		writeC(level7);
		writeC(level8);
		writeC(level9);
		writeC(level10);
		writeC(knight);
		writeC(l2);
		writeC(de1);
		writeC(de2);
		writeC(royal);
		writeC(l3);
		writeC(elf1);
		writeC(elf2);
		writeC(elf3);
		writeC(elf4);
		writeC(elf5);
		writeC(elf6);
		writeC(k5);
		writeC(l5);
		writeC(m5);
		writeC(n5);
		writeC(o5);
		writeC(p5);
		writeD(0);
		writeD(0);
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
