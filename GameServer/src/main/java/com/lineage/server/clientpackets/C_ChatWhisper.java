package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;

import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRecord;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.LogChatReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChatWhisperFrom;
import com.lineage.server.serverpackets.S_ChatWhisperTo;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 要求使用密語聊天頻道
 *
 * @author daien
 *
 */
public class C_ChatWhisper extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ChatWhisper.class);

	public C_ChatWhisper(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			// 來源對像
			final L1PcInstance whisperFrom = client.getActiveChar();

			if (decrypt.length > 108) {
				_log.warn("人物:" + whisperFrom.getName() + "對話(密語)長度超過限制:" + client.getIp().toString());
				client.set_error(client.get_error() + 1);
				return;
			}

			// 你從現在被禁止閒談。
			if (whisperFrom.hasSkillEffect(STATUS_CHAT_PROHIBITED)) {
				whisperFrom.sendPackets(new S_ServerMessage(242));
				return;
			}

			// 等級 %0 以下無法使用密談。
			if ((whisperFrom.getLevel() < ConfigAlt.WHISPER_CHAT_LEVEL) && !whisperFrom.isGm()) {
				whisperFrom.sendPackets(new S_ServerMessage(404, String.valueOf(ConfigAlt.WHISPER_CHAT_LEVEL)));
				return;
			}

			// 取回對話內容
			final String targetName = this.readS();
			final String text = this.readS();

			// 目標對像
			final L1PcInstance whisperTo = World.get().getPlayer(targetName);

			// \f1%0%d 不在線上。
			if (whisperTo == null) {
				whisperFrom.sendPackets(new S_ServerMessage(73, targetName));
				return;
			}

			// 對象是自己
			if (whisperTo.equals(whisperFrom)) {
				return;
			}

			// %0%s 斷絕你的密語。
			if (whisperTo.getExcludingList().contains(whisperFrom.getName())) {
				whisperFrom.sendPackets(new S_ServerMessage(117, whisperTo.getName()));
				return;
			}

			// \f1%0%d 目前關閉悄悄話。
			if (!whisperTo.isCanWhisper()) {
				whisperFrom.sendPackets(new S_ServerMessage(205, whisperTo.getName()));
				return;
			}

			if (ConfigRecord.GM_OVERHEARD) {
				for (L1Object visible : World.get().getAllPlayers()) {
					if ((visible instanceof L1PcInstance)) {
						L1PcInstance GM = (L1PcInstance) visible;
						if (!whisperFrom.isFV() && !whisperTo.isFV()) {
							if ((GM.isGm()) && (whisperFrom.getId() != GM.getId())) {
								GM.sendPackets(new S_SystemMessage(
										"\\fV【密】" + whisperFrom.getName() + " -> " + targetName + ":" + text));
							}
						}
					}
				}
			}

			whisperFrom.sendPackets(new S_ChatWhisperTo(whisperTo, text));
			whisperTo.sendPackets(new S_ChatWhisperFrom(whisperFrom, text));

			if (ConfigRecord.LOGGING_CHAT_WHISPER) {
				LogChatReading.get().isTarget(whisperFrom, whisperTo, text, 9);
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
