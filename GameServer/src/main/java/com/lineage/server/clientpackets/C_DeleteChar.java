package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CommonNews;
import com.lineage.server.serverpackets.S_DeleteCharOK;
import com.lineage.server.world.WorldClan;

/**
 * 要求角色刪除<br>
 * 類名稱：C_DeleteChar<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午2:58:17<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class C_DeleteChar extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_DeleteChar.class);

	public C_DeleteChar(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final String name = this.readS();
			if (name.isEmpty()) {
				return;
			}
			
			try {
				final L1PcInstance pc = CharacterTable.get().restoreCharacter(name, client);
				
				if ((pc != null && pc.getLevel() >= ConfigAlt.NO_DELETE_CHARACTER_LV)) {
					client.toSender(new S_CommonNews("\\f2等級大於" + ConfigAlt.NO_DELETE_CHARACTER_LV + "無法刪除."));
					return;
				}

				if (pc != null) {
					final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
					if (clan != null) {
						clan.delMemberName(name);
					}
				}
				// 已創人物數量
				int countCharacters = client.getAccount().get_countCharacters();
				client.getAccount().set_countCharacters(countCharacters - 1);
				
				// 移出已用名稱清單
				CharObjidTable.get().charRemove(name);
				// 刪除人物
				CharacterTable.get().deleteCharacter(client.getAccountName(), name);
				
			} catch (final Exception e) {
				client.close();
				return;
			}
			client.toSender(new S_DeleteCharOK());
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
