package com.lineage.data.item_etcitem;

import static com.lineage.server.model.skill.L1SkillId.SHAPE_CHANGE;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.PolyTable;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Sosc_PolyReel extends ItemExecutor {
	public static ItemExecutor get() {
		return new Sosc_PolyReel();
	}

	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		String text = pc.getText();

		if (text == null) {
			return;
		}

		pc.setText(null);

		int time = 1800;

		if (item.getBless() == 0) {
			time = 2100;
		}

		if (item.getBless() == 128) {
			time = 2100;
		}

		if (item.getItemId() == 42029) {
			time = 5150;		
			return;
		}

		L1PolyMorph poly = PolyTable.get().getTemplate(text);

		if ((poly != null) || (text.equals("none"))) {
			if (text.equals("none")) {
				pc.removeSkillEffect(SHAPE_CHANGE);
			}
			if (text.equals("none")) {
				L1PolyMorph.undoPoly(pc);
				// System.out.println("变形卷轴取消变身");
				pc.getInventory().removeItem(item, 1L);
			} else if ((poly.getMinLevel() <= pc.getLevel()) || (pc.isGm())) {
				L1PolyMorph.doPoly(pc, poly.getPolyId(), time, 1);
				pc.getInventory().removeItem(item, 1L);
			}
		} else {
			pc.sendPackets(new S_ServerMessage(181));
		}
	}
}