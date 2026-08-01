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

public class CustomSpecialStatTable {
    private static final Log _log = LogFactory.getLog(CustomSpecialStatTable.class);
    private static CustomSpecialStatTable _instance;
    public static CustomSpecialStatTable get() {
        if (_instance == null) {
            _instance = new CustomSpecialStatTable();
        }
        return _instance;
    }
    private Map<Integer, CustomAttachStat> specialData = new HashMap<>();
    public void load() {
        this.specialData.clear();
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `custom_special_stat_datas`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                specialData.put(rs.getInt("物品編號"), new CustomAttachStat(rs.getInt("小怪傷害"), rs.getInt("大怪傷害"), rs.getInt("魔法攻擊"), rs.getInt("吸血"), rs.getInt("吸魔"), rs.getInt("命中"), rs.getInt("額外攻擊"), rs.getInt("PVP增傷"), rs.getInt("防禦"), rs.getInt("血量"), rs.getInt("魔力"), rs.getInt("回血"), rs.getInt("回魔"), rs.getInt("抗魔"), rs.getInt("物理減傷")));
            }
        }  catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("載入特殊能力資料 (" + timer.get() + "ms)");
    }

    public final Map<Integer, CustomAttachStat> getSpecialDatas() {
        return this.specialData;
    }

    public static class CustomAttachStat {
        private int 小怪傷害,大怪傷害,魔法攻擊,吸血,吸魔,命中,額外攻擊,PVP增傷,防禦,血量,魔力,回血,回魔,抗魔,物理減傷;
        public CustomAttachStat(final int 小怪傷害,final int 大怪傷害,final int 魔法攻擊,final int 吸血,final int 吸魔,final int 命中,final int 額外攻擊,final int PVP增傷,final int 防禦,final int 血量,final int 魔力,final int 回血,final int 回魔,final int 抗魔,final int 物理減傷) {
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
        }
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
    }
}
