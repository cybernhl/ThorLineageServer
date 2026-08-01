package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.SHAPE_CHANGE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;

/**
 * 要求角色表情動作
 *
 * @author daien
 *
 */
public class C_ExtraCommand extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ExtraCommand.class);

	public C_ExtraCommand(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}
			
			if (pc.isDead()) { // 死亡
				return;
			}
			
			if (pc.isTeleport()) { // 傳送中
				return;
			}

			if (pc.isInvisble()) { // 隱身狀態
				return;
			}

			final int actionId = this.readC();

			if (pc.hasSkillEffect(SHAPE_CHANGE)) { // 念為、變身中他送信
				final int gfxId = pc.getTempCharGfx();
				if ((gfxId != 6080) && (gfxId != 6094)) { // 騎馬用變身例外
					return;
				}
			}
			
			// 10格範圍封包發送
			pc.broadcastPacketX10(new S_DoActionGFX(pc.getId(), actionId));
			pc.set_actionId(actionId);
			
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
