package com.lineage.server.clientpackets;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;

import com.lineage.config.ConfigOther;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;
import com.lineage.list.BadNamesList;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.BeginnerTable;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharCreateStatus;
import com.lineage.server.serverpackets.S_NewCharPacket;
import com.lineage.server.templates.L1Account;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.CalcInitHpMp;

/**
 * 要求創造角色
 *
 * @author daien
 *
 */
public class C_CreateChar extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CreateChar.class);

	// 各職業初始化屬性(王族, 騎士, 精靈, 法師, 黑妖)
	public static final int[] ORIGINAL_STR = new int[] { 13, 16, 11, 8, 12};
	public static final int[] ORIGINAL_DEX = new int[] { 10, 12, 12, 7, 15};
	public static final int[] ORIGINAL_CON = new int[] { 10, 14, 12, 12, 8};
	public static final int[] ORIGINAL_WIS = new int[] { 11, 9, 12, 12, 10};
	public static final int[] ORIGINAL_CHA = new int[] { 13, 12, 9, 8, 9};
	public static final int[] ORIGINAL_INT = new int[] { 10, 8, 12, 12, 11};
	// 各職業初始化可分配點數(王族, 騎士, 精靈, 法師, 黑妖)
	public static final int[] ORIGINAL_AMOUNT = new int[] { 8, 4, 7, 16, 10};

	// 人物外型決定
	private static final int[][] CLASS_LIST = new int[][] {
		new int[] { 0, 61, 138, 734, 2786},// 男性
		new int[] { 1, 48, 37, 1186, 2796}// 女性
	};

	// 出生地點座標
	private static final int[][] LOC_LIST = new int[][] { 
		new int[]{32714, 32877, 69},
		new int[]{32714, 32877, 69},
		new int[]{32714, 32877, 69},
		new int[]{32714, 32877, 69},
		new int[]{32714, 32877, 69}
		/*new int[]{32780, 32781, 68},
		new int[]{32714, 32877, 69},
		new int[]{32714, 32877, 69},
		new int[]{32780, 32781, 68},
		new int[]{32714, 32877, 69},
		new int[]{32714, 32877, 69},
		new int[]{32714, 32877, 69}*/
	};

	public C_CreateChar(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			final L1PcInstance pc = new L1PcInstance(client);
			String name = Matcher.quoteReplacement(this.readS());

			final L1Account account = client.getAccount();
			final int characterSlot = account.get_character_slot();
			final int maxAmount = ConfigAlt.DEFAULT_CHARACTER_SLOT + characterSlot;

			name = name.replaceAll("\\s", "");
			name = name.replaceAll("　", "");
			
			if (name.length() == 0) {
				client.toSender(new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME));
				return;
			}

			// 名稱是否包含禁止字元
			if (!isInvalidName(name)) {
				client.toSender(new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME));
				return;
			}

			// 檢查名稱是否以被使用
			if (CharObjidTable.get().charObjid(name) != 0) {
				client.toSender(new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS));
				return;
			}

			// 已創人物數量
			int countCharacters = client.getAccount().get_countCharacters();
			if (countCharacters >= maxAmount) {
				client.toSender(new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT));
				return;
			}

			pc.setName(name);
			pc.setType(this.readC());
			pc.set_sex(this.readC());
			pc.addBaseStr((byte) this.readC());
			pc.addBaseDex((byte) this.readC());
			pc.addBaseCon((byte) this.readC());
			pc.addBaseWis((byte) this.readC());
			pc.addBaseCha((byte) this.readC());
			pc.addBaseInt((byte) this.readC());

			boolean isStatusError = false;
			final int originalStr = ORIGINAL_STR[pc.getType()];
			final int originalDex = ORIGINAL_DEX[pc.getType()];
			final int originalCon = ORIGINAL_CON[pc.getType()];
			final int originalWis = ORIGINAL_WIS[pc.getType()];
			final int originalCha = ORIGINAL_CHA[pc.getType()];
			final int originalInt = ORIGINAL_INT[pc.getType()];
			final int originalAmount = ORIGINAL_AMOUNT[pc.getType()];

			if (((pc.getBaseStr() < originalStr) || (pc.getBaseDex() < originalDex)
					|| (pc.getBaseCon() < originalCon)
					|| (pc.getBaseWis() < originalWis)
					|| (pc.getBaseCha() < originalCha) || (pc.getBaseInt() < originalInt))
					|| ((pc.getBaseStr() > originalStr + originalAmount)
							|| (pc.getBaseDex() > originalDex + originalAmount)
							|| (pc.getBaseCon() > originalCon + originalAmount)
							|| (pc.getBaseWis() > originalWis + originalAmount)
							|| (pc.getBaseCha() > originalCha + originalAmount) 
							|| (pc.getBaseInt() > originalInt + originalAmount))) {
				isStatusError = true;
			}

			final int statusAmount = pc.getDex() + pc.getCha() + pc.getCon() + pc.getInt() + pc.getStr() + pc.getWis();

			if ((statusAmount != 75) || isStatusError) {
				client.toSender(new S_CharCreateStatus(S_CharCreateStatus.REASON_WRONG_AMOUNT));
				return;
			}

			client.getAccount().set_countCharacters(countCharacters + 1);
			client.toSender(new S_CharCreateStatus(S_CharCreateStatus.REASON_OK));
			initNewChar(client, pc);
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	/**
	 * 創造角色
	 * @param client
	 * @param pc
	 */
	private static void initNewChar(final ClientExecutor client, final L1PcInstance pc) {
		try {
			L1Account account = client.getAccount();
			pc.setId(IdFactory.get().nextId());
			int classid = CLASS_LIST[pc.get_sex()][pc.getType()];

			pc.setClassId(classid);
			pc.setTempCharGfx(classid);
			pc.setGfxId(classid);

			int[] loc = LOC_LIST[pc.getType()];
			pc.setX(loc[0]);
			pc.setY(loc[1]);
			pc.setMap((short) loc[2]);
			
			pc.setHeading(0);
			pc.setLawful(0);

			final int initHp = CalcInitHpMp.calcInitHp(pc);
			final int initMp = CalcInitHpMp.calcInitMp(pc);
			pc.addBaseMaxHp((short) initHp);
			pc.setCurrentHp((short) initHp);
			pc.addBaseMaxMp((short) initMp);
			pc.setCurrentMp((short) initMp);
			pc.resetBaseAc();
			pc.setTitle("");
			pc.setClanid(0);
			pc.setClanRank(0);
			pc.set_food(40);
			
			if (account.get_access_level() >= 200) {
				pc.setAccessLevel((short) account.get_access_level());
				pc.setGm(true);
				pc.setMonitor(false);
				
			} else {
				pc.setAccessLevel((short) 0);
				pc.setGm(false);
				pc.setMonitor(false);
			}
			
			pc.setGmInvis(false);
			pc.setExp(0);
			pc.setHighLevel(0);
			pc.setStatus(0);
			pc.setClanname("");
			pc.setBonusStats(0);
			pc.setElixirStats(0);
			pc.resetBaseMr();
			pc.setElfAttr(0);
			pc.set_PKcount(0);
			pc.setPkCountForElf(0);
			pc.setExpRes(0);
			pc.setPartnerId(0);
			pc.setOnlineStatus(0);
			pc.setHomeTownId(0);
			pc.setContribution(0);
			pc.setBanned(false);
			pc.setKarma(0);
			if (pc.isWizard()) {// 法師技能
				final int object_id = pc.getId();
				final L1Skills l1skills = SkillsTable.get().getTemplate(4); // EB
				final String skill_name = l1skills.getName();
				final int skill_id = l1skills.getSkillId();

				CharSkillReading.get().spellMastery(object_id, skill_id, skill_name, 0, 0); // 資料庫紀錄
			}

			// 紀錄人物帳號
			pc.setAccountName(client.getAccountName());
			// 初始化數值
			pc.refresh();
			
			client.toSender(new S_NewCharPacket(pc));

			// 建立人物資料
			CharacterTable.get().storeNewCharacter(pc);
			// 紀錄人物初始化資料
			CharacterTable.saveCharStatus(pc);
			// 給予新手道具
			BeginnerTable.get().giveItem(pc);
			// 加入建立PC OBJID資料
			CharObjidTable.get().addChar(pc.getId(), pc.getName());

			if (ConfigOther.NewCreate) {
				World.get().broadcastPacketToAll(new S_SystemMessage("\\fR歡迎新手玩家【" + pc.getName() + "】進入遊戲"));
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	public static final String[] BANLIST = new String[]{
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"","",
		"、","",
		"\ue6c1","-",
		"/","+",
		"*","?",
		"!","@",
		"#","$",
		"%","^",
		"&","(",
		")","[",
		"]","<",
		">","{",
		"}",";",
		":","'",
		"\"",",",
		".","~",
		"`",
	};
	
	public static boolean isInvalidName(final String name) {
		//int numOfNameBytes = 0;

		try {
			for (String ban : BANLIST) {
				if (name.indexOf(ban) != -1) {
					return false;
				}
			}
			/*if (name.indexOf("、") != -1) {
				return false;
			}

			if (name.indexOf("") != -1) {
				return false;
			}

			if (name.indexOf("＞") != -1) {
				return false;
			}

			if (name.indexOf("＜") != -1) {
				return false;
			}

			if (name.indexOf("＼") != -1) {
				return false;
			}

			if (name.indexOf("／") != -1) {
				return false;
			}

			if (name.indexOf("\ue6c1") != -1) {
				return false;
			}*/

			if (BadNamesList.get().isBadName(name)) {
				return false;
			}
			
			// 將字串轉為BYTE組 並取回BYTE長度
			final int numOfNameBytes = 
				name.getBytes(Config.CLIENT_LANGUAGE_CODE).length;
			// 全形字服 5字 半形12字
			if ((5 < (numOfNameBytes - name.length())) || (12 < numOfNameBytes)) {
				return false;
			}
			return true;
			
		} catch (final UnsupportedEncodingException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			return false;
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
