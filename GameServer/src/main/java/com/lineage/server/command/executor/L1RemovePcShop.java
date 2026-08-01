package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1RemovePcShop implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1RemovePcShop.class);

    private L1RemovePcShop() {}

    public static L1CommandExecutor getInstance() {
        return new L1RemovePcShop();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            if (!pc.isGm()) {
                return;
            }
            if (pc.isRemovePcShop()) {
                pc.sendPackets(new S_HelpMessage("\\fW剔除掛賣商人模式: 關閉"));
            } else {
                pc.sendPackets(new S_HelpMessage("\\fW剔除掛賣商人模式: 開啟"));
            }
            pc.setRemovePcShop(!pc.isRemovePcShop());
        } catch (final Exception e) {
            if (pc == null) {
                _log.error("錯誤的命令格式: " + this.getClass().getSimpleName());

            } else {
                _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
                // 261 \f1指令錯誤。
                pc.sendPackets(new S_ServerMessage(261));
            }

        }
    }
}
