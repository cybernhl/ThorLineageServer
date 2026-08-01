package com.lineage.server.command.executor;

import com.lineage.mina.CustomSafeConnectIP;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class L1ReloadIPCount implements L1CommandExecutor {

    private L1ReloadIPCount() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1ReloadIPCount();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        CustomSafeConnectIP.getInstance().loadAll(true);
        pc.sendPackets(new S_SystemMessage("重載完成"));
    }
}
