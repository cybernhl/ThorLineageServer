package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.BaseResetSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharReset;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.utils.CalcInitHpMp;
import com.lineage.server.utils.CalcStat;

/**
 * 要求人物重設
 *
 * @author daien
 *
 */
public class C_CharReset extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CharReset.class);

	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			final int stage = this.readC();
			switch (stage) {
			case 0x01: // 0x01:初始化人物數質
				final int str = this.readC();
				final int intel = this.readC();
				final int wis = this.readC();
				final int dex = this.readC();
				final int con = this.readC();
				final int cha = this.readC();
				
				int hp = 0;
				int mp = 0;
				if (BaseResetSet.RETAIN != 0) {
					hp = ((pc.getMaxHp() * BaseResetSet.RETAIN) / 100);
					mp = ((pc.getMaxMp() * BaseResetSet.RETAIN) / 100);
					
				} else {
					hp = CalcInitHpMp.calcInitHp(pc);
					mp = CalcInitHpMp.calcInitMp(pc);
				}
				
				pc.sendPackets(new S_CharReset(pc, 1, hp, mp, 10, str, intel, wis, dex, con, cha));
				this.initCharStatus(pc, hp, mp, str, intel, wis, dex, con, cha);
				CharacterTable.get();
				CharacterTable.saveCharStatus(pc);
				break;

			case 0x02: // 0x02:等級分配
				final int type2 = this.readC();
				switch (type2) {
				case 0x00: // 提升1級
					this.setLevelUp(pc, 1);
					break;
					
				case 0x07: // 提升10級
					if (pc.getTempMaxLevel() - pc.getTempLevel() < 10) {
						return;
					}
					this.setLevelUp(pc, 10);
					break;

				case 0x01:// 提升1級(力量)
					pc.addBaseStr((byte) 1);
					this.setLevelUp(pc, 1);
					break;

				case 0x02:// 提升1級(智力)
					pc.addBaseInt((byte) 1);
					this.setLevelUp(pc, 1);
					break;

				case 0x03:// 提升1級(精神)
					pc.addBaseWis((byte) 1);
					this.setLevelUp(pc, 1);
					break;

				case 0x04:// 提升1級(敏捷)
					pc.addBaseDex((byte) 1);
					this.setLevelUp(pc, 1);
					break;

				case 0x05:// 提升1級(體質)
					pc.addBaseCon((byte) 1);
					this.setLevelUp(pc, 1);
					break;

				case 0x06:// 提升1級(魅力)
					pc.addBaseCha((byte) 1);
					this.setLevelUp(pc, 1);
					break;

				case 0x08:// 完成
					switch (this.readC()) {
					case 1:
						pc.addBaseStr((byte) 1);
						break;
					case 2:
						pc.addBaseInt((byte) 1);
						break;
					case 3:
						pc.addBaseWis((byte) 1);
						break;
					case 4:
						pc.addBaseDex((byte) 1);
						break;
					case 5:
						pc.addBaseCon((byte) 1);
						break;
					case 6:
						pc.addBaseCha((byte) 1);
						break;
					}
					if (pc.getElixirStats() > 0) {
						pc.sendPackets(new S_CharReset(pc.getElixirStats()));
						return;
					}
					this.saveNewCharStatus(pc);
					break;
				}
				break;

			case 0x03:
				int read1 = this.readC();
				int read2 = this.readC();
				int read3 = this.readC();
				int read4 = this.readC();
				int read5 = this.readC();
				int read6 = this.readC();
				pc.addBaseStr((byte) (read1 - pc.getBaseStr()));
				pc.addBaseInt((byte) (read2 - pc.getBaseInt()));
				pc.addBaseWis((byte) (read3 - pc.getBaseWis()));
				pc.addBaseDex((byte) (read4 - pc.getBaseDex()));
				pc.addBaseCon((byte) (read5 - pc.getBaseCon()));
				pc.addBaseCha((byte) (read6 - pc.getBaseCha()));
				this.saveNewCharStatus(pc);
				break;
			}
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	private void saveNewCharStatus(final L1PcInstance pc) {
		pc.setInCharReset(false);
		if (pc.getOriginalAc() > 0) {
			pc.addAc(pc.getOriginalAc());
		}

		if (pc.getOriginalMr() > 0) {
			pc.addMr(0 - pc.getOriginalMr());
		}

		pc.refresh();
		pc.setCurrentHp(pc.getMaxHp());
		pc.setCurrentMp(pc.getMaxMp());
		if (pc.getTempMaxLevel() != pc.getLevel()) {
			pc.setLevel(pc.getTempMaxLevel());
			pc.setExp(ExpTable.getExpByLevel(pc.getTempMaxLevel()));
		}

		if (pc.getLevel() > 50) {
			pc.setBonusStats(pc.getLevel() - 50);
			
		} else {
			pc.setBonusStats(0);
		}
		pc.sendPackets(new S_OwnCharStatus(pc));

		final L1ItemInstance item = pc.getInventory().findItemId(49142); // 回憶蠟燭
		if (item != null) {
			try {
				pc.getInventory().removeItem(item, 1);
				pc.save(); // 資料存檔

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
		L1Teleport.teleport(pc, 32628, 32772, (short) 4, 4, false);
	}

	private void initCharStatus(final L1PcInstance pc, final int hp, final int mp, final int str,
			final int intel, final int wis, final int dex, final int con, final int cha) {
		pc.addBaseMaxHp((short) (hp - pc.getBaseMaxHp()));
		pc.addBaseMaxMp((short) (mp - pc.getBaseMaxMp()));
		pc.addBaseStr((byte) (str - pc.getBaseStr()));
		pc.addBaseInt((byte) (intel - pc.getBaseInt()));
		pc.addBaseWis((byte) (wis - pc.getBaseWis()));
		pc.addBaseDex((byte) (dex - pc.getBaseDex()));
		pc.addBaseCon((byte) (con - pc.getBaseCon()));
		pc.addBaseCha((byte) (cha - pc.getBaseCha()));
		pc.addMr(0 - pc.getMr());
		pc.addDmgup(0 - pc.getDmgup());
		pc.addHitup(0 - pc.getHitup());
	}

	private void setLevelUp(final L1PcInstance pc, final int addLv) {
		pc.setTempLevel(pc.getTempLevel() + addLv);
		for (int i = 0; i < addLv; i++) {
			final short randomHp = CalcStat.calcStatHp(pc.getType(), pc.getBaseMaxHp(), pc.getBaseCon(), pc.getOriginalHpup());
			final short randomMp = CalcStat.calcStatMp(pc.getType(), pc.getBaseMaxMp(), pc.getBaseWis(), pc.getOriginalMpup());
			pc.addBaseMaxHp(randomHp);
			pc.addBaseMaxMp(randomMp);
		}
		final int newAc = CalcStat.calcAc(pc.getTempLevel(), pc.getBaseDex(), pc);
		pc.sendPackets(new S_CharReset(pc, pc.getTempLevel(),
				pc.getBaseMaxHp(), pc.getBaseMaxMp(), newAc, pc.getBaseStr(),
				pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
