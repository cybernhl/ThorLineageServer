package com.lineage.server.command.executor;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1ReloadConfig implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1ReloadConfig.class);

    public static L1CommandExecutor getInstance() {
        return new L1ReloadConfig();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            ConfigAlt.load();
            ConfigOther.load();
            ConfigRate.load();
        } catch (final Exception e) {
            System.out.println("CONFIG 資料加載異常!" + e);
        }
    }
}
