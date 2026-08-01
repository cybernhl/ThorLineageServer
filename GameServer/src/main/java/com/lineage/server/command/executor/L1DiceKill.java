package com.lineage.server.command.executor;

import com.add.CustomTaiwanMahjong;
import com.lineage.data.npc.CustomDiceGame;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HelpMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1DiceKill implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1DiceKill.class);
    private L1DiceKill() {
    }
    public static L1CommandExecutor getInstance() {
        return new L1DiceKill();
    }
    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        CustomDiceGame.getInstance().setKill();
        pc.sendPackets(new S_HelpMessage("此局開豹子"));
    }
}
