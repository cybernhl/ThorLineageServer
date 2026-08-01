package com.lineage.server.command.executor;

import com.add.CustomBaccarat;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1BaccaratPlayer implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1BaccaratPlayer.class);
    private L1BaccaratPlayer() {

    }
    public static L1CommandExecutor getInstance() {
        return new L1BaccaratPlayer();
    }
    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        CustomBaccarat.getInstance().setPlayerWinner();
        pc.sendPackets(new S_ServerMessage("此局閒贏"));
    }
}
