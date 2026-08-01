package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CustomAttachStatTable {
    private static final Log _log = LogFactory.getLog(CustomAttachStatTable.class);
    private static CustomAttachStatTable _instance;

    public static CustomAttachStatTable get() {
        if (_instance == null) {
            _instance = new CustomAttachStatTable();
        }
        return _instance;
    }
    private final Map<Integer, CustomAttachStat> weaponDatas = new HashMap<>();
    private final Map<Integer, CustomAttachStat> armorDatas = new HashMap<>();

    public void load() {
        this.weaponDatas.clear();
        this.armorDatas.clear();
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `custom_attach_stat_datas`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int equipType = rs.getInt("武器 0 / 防具 1");
                final CustomAttachStat attach = new CustomAttachStat(rs.getInt("附魔編號"), rs.getString("附魔名稱"), rs.getInt("附魔等級"), rs.getInt("小怪傷害"), rs.getInt("大怪傷害"), rs.getInt("魔法攻擊"), rs.getInt("吸血"), rs.getInt("吸魔"), rs.getInt("命中"), rs.getInt("額外攻擊"), rs.getInt("PVP增傷"), rs.getInt("防禦"), rs.getInt("血量"), rs.getInt("魔力"), rs.getInt("回血"), rs.getInt("回魔"), rs.getInt("抗魔"), rs.getInt("物理減傷"), rs.getInt("成功機率"), rs.getInt("經驗"));
                if (equipType == 1) {
                    this.armorDatas.put(rs.getInt("附魔編號"), attach);
                } else {
                    this.weaponDatas.put(rs.getInt("附魔編號"), attach);
                }
            }
        }  catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("載入附魔武器資料數量: " + weaponDatas.size() + " 附魔裝備資料數量: " + armorDatas.size() + " (" + timer.get() + "ms)");
    }

    public final Map<Integer, CustomAttachStat> getWeaponDatas() {
        return this.weaponDatas;
    }

    public final Map<Integer, CustomAttachStat> getArmorDatas() {
        return this.armorDatas;
    }

    public static class CustomAttachStat {
        private int 附魔編號,附魔等級,小怪傷害,大怪傷害,魔法攻擊,吸血,吸魔,命中,額外攻擊,PVP增傷,防禦,血量,魔力,回血,回魔,抗魔,物理減傷,機率,經驗;
        private final String 附魔名稱;
        public CustomAttachStat(final int 附魔編號, final String 附魔名稱, final int 附魔等級,final int 小怪傷害,final int 大怪傷害,final int 魔法攻擊,final int 吸血,final int 吸魔,final int 命中,final int 額外攻擊,final int PVP增傷,final int 防禦,final int 血量,final int 魔力,final int 回血,final int 回魔,final int 抗魔,final int 物理減傷,final int 機率,final int 經驗) {
            this.附魔編號 = 附魔編號;
            this.附魔名稱 = 附魔名稱;
            this.附魔等級 = 附魔等級;
            this.小怪傷害 = 小怪傷害;
            this.大怪傷害 = 大怪傷害;
            this.魔法攻擊 = 魔法攻擊;
            this.吸血 = 吸血;
            this.吸魔 = 吸魔;
            this.命中 = 命中;
            this.額外攻擊 = 額外攻擊;
            this.PVP增傷 = PVP增傷;
            this.防禦 = 防禦;
            this.血量 = 血量;
            this.魔力 = 魔力;
            this.回血 = 回血;
            this.回魔 = 回魔;
            this.抗魔 = 抗魔;
            this.物理減傷 = 物理減傷;
            this.機率 = 機率;
            this.經驗 = 經驗;
        }

        public final String get附魔名稱() {
            return this.附魔名稱;
        }
        public final int get附魔編號() { return this.附魔編號; }
        public final int get附魔等級() { return this.附魔等級; }
        public final int get小怪傷害() { return this.小怪傷害; }
        public final int get大怪傷害() { return this.大怪傷害; }
        public final int get魔法攻擊() { return this.魔法攻擊; }
        public final int get吸血() { return this.吸血; }
        public final int get吸魔() { return this.吸魔; }
        public final int get命中() { return this.命中; }
        public final int get額外攻擊() { return this.額外攻擊; }
        public final int getPVP增傷() { return this.PVP增傷; }
        public final int get防禦() { return this.防禦; }
        public final int get血量() { return this.血量; }
        public final int get魔力() { return this.魔力; }
        public final int get回血() { return this.回血; }
        public final int get回魔() { return this.回魔; }
        public final int get抗魔() { return this.抗魔; }
        public final int get物理減傷() { return this.物理減傷; }
        public final int get機率() { return this.機率; }
        public final int get經驗() { return this.經驗; }
    }
}
