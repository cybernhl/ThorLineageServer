package com.lineage.server.command.executor;

import com.add.CustomTaiwanMahjong;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class L1Test implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1Test.class);
    private L1Test() {
    }

    private final Random _random = new Random();

    public static L1CommandExecutor getInstance() {
        return new L1Test();
    }

    public int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        CustomTaiwanMahjong.get().setKillWinSkill();
        pc.sendPackets(new S_HelpMessage("此局通殺"));
    }
}
