package com.lineage.server.clientpackets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_WhoCharinfo;
import com.lineage.server.timecontroller.server.ServerRestartTimer;
import com.lineage.server.world.World;

/**
 * 要求查詢玩家
 *
 * @author daien
 *
 */
public class C_Who extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Who.class);

	public C_Who(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 资料载入
			this.read(decrypt);
			
			//final String s = this.readS();
			//L1Character find = World.get().getPlayer(s);
			final L1PcInstance pc = client.getActiveChar();
			// 20171129
            final int count = World.get().getAllPlayers().size();
            int people = (int) (count * ConfigAlt.ALT_WHO_COUNT);

            if (ConfigAlt.ALT_WHO_COMMANDX) {
                final String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                switch (ConfigAlt.ALT_WHO_TYPE) {
					case 0:// 對話視窗顯示
						pc.sendPackets(new S_ServerMessage("\\fV線上人數: " + people));
						pc.sendPackets(new S_ServerMessage("\\fV啟動時間: " + String.valueOf(ServerRestartTimer.get_startTime())));
						pc.sendPackets(new S_ServerMessage("\\fV經驗倍率: " + (ConfigRate.RATE_XP * ConfigOther.RATE_XP_WHO)));
						pc.sendPackets(new S_ServerMessage("\\fV金錢倍率: " + ConfigRate.RATE_DROP_ADENA));
						if (pc.isGm()) {
							pc.sendPackets(new S_ServerMessage("\\fV沖武倍率: " + ConfigRate.ENCHANT_CHANCE_WEAPON));
							pc.sendPackets(new S_ServerMessage("\\fV沖防倍率: " + ConfigRate.ENCHANT_CHANCE_ARMOR));
						} else {
							pc.sendPackets(new S_ServerMessage("\\fV沖武倍率: 1"));
							pc.sendPackets(new S_ServerMessage("\\fV沖防倍率: 1"));
						}
						pc.sendPackets(new S_ServerMessage("\\fV現實時間: " + nowDate));
						pc.sendPackets(new S_ServerMessage("\\fV重啟時間: " + ServerRestartTimer.get_restartTime()));
						break;
						
					case 1:// 視窗顯示
						int clanOnlineCount = 0;
						if (pc.getClan() != null) {
							for (final String name : pc.getClan().getAllMembers()) {
								final L1PcInstance member_pc = World.get().getPlayer(name);
								if (member_pc != null && member_pc.getLevel() >= ConfigOther.CLAN_STAT_LEVEL) {
									clanOnlineCount++;
								}
							}
						}
						final String[] info = new String[]{
								Config.SERVERNAME,// 伺服器資訊:
	                    		String.valueOf(people), // 線上人數
								String.valueOf((ConfigRate.RATE_XP * ConfigOther.RATE_XP_WHO)),// 經驗
								String.valueOf(ConfigRate.RATE_DROP_ITEMS),// 掉寶
								String.valueOf(ConfigRate.RATE_DROP_ADENA),// 金幣
//								String.valueOf(ConfigRate.RATE_LA),// 正義
//								String.valueOf(ConfigRate.RATE_WEIGHT_LIMIT),// 負重
								String.valueOf(pc.isGm() ? ConfigRate.ENCHANT_CHANCE_WEAPON : 1),// 武器
								String.valueOf(pc.isGm() ? ConfigRate.ENCHANT_CHANCE_ARMOR : 1),// 防具
								String.valueOf(ConfigAlt.POWER),// 手點上限
								String.valueOf(ConfigAlt.POWERMEDICINE),// 單項萬能藥上限
								String.valueOf(ConfigAlt.MEDICINE),// 總和萬能藥瓶數
								nowDate,// 目前時間
								ServerRestartTimer.get_restartTime(),// 重啟時間
								pc.getClan() == null ? "無" : pc.getClan().getClanName(),
								"血盟狀態需要角色上線數量:",
								String.valueOf(ConfigOther.CLAN_STAT_LEVEL_COUNT),
								"血盟線上滿 " + ConfigOther.CLAN_STAT_LEVEL + "級 角色數量:",
								String.valueOf(clanOnlineCount)
						};
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_who", info));
						break;
					}
				}
			
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
