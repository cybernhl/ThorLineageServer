package com.lineage.server.model.classes;

import com.lineage.server.model.Instance.L1PcInstance;

public abstract class L1ClassFeature {
	
	public static L1ClassFeature newClassFeature(final int classId) {
		switch (classId) {
		case L1PcInstance.CLASSID_PRINCE:
		case L1PcInstance.CLASSID_PRINCESS:
			return new L1RoyalClassFeature();
			
		case L1PcInstance.CLASSID_ELF_MALE:
		case L1PcInstance.CLASSID_ELF_FEMALE:
			return new L1ElfClassFeature();
			
		case L1PcInstance.CLASSID_KNIGHT_MALE:
		case L1PcInstance.CLASSID_KNIGHT_FEMALE:
			return new L1KnightClassFeature();
			
		case L1PcInstance.CLASSID_WIZARD_MALE:
		case L1PcInstance.CLASSID_WIZARD_FEMALE:
			return new L1WizardClassFeature();
	
		}
		throw new IllegalArgumentException();
	}

	/**
	 * 職業AC補正
	 * @param ac
	 * @return
	 */
	public abstract int getAcDefenseMax(int ac);

	/**
	 * 職業魔法等級
	 * @param playerLevel
	 * @return
	 */
	public abstract int getMagicLevel(int playerLevel);
	
	/**
	 * 職業物理攻擊補正
	 * @param playerLevel
	 * @return
	 */
	public abstract int getAttackLevel(int playerLevel);
	
	/**
	 * 職業物理命中補正
	 * @param playerLevel
	 * @return
	 */
	public abstract int getHitLevel(int playerLevel);
}