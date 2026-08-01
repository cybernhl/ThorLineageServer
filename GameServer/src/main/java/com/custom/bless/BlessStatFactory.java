package com.custom.bless;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.utils.SQLUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlessStatFactory {
    public static final BlessStatFactory instance = new BlessStatFactory();
    public static BlessStatFactory getInstance() {
        return instance;
    }
    private static Logger _log = Logger.getLogger(BlessStatFactory.class
            .getName());
    private final Map<Integer, BlessStat> data = new HashMap<>();
    public void loadAll() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("select * from 系統_祝福屬性設置");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int id = rs.getInt("流水號");
                final int type = rs.getInt("武器0/防具1/飾品2");
                final int dmgup = rs.getInt("近距離攻擊力");
                final int bow_dmgup = rs.getInt("遠距離攻擊力");
                final int pvp_dmg = rs.getInt("PVP傷害");
                final int hit = rs.getInt("近距離命中率");
                final int bow_hit = rs.getInt("遠距離命中率");
                final int ac = rs.getInt("防禦");
                final int mr = rs.getInt("抗魔");
                final int hp = rs.getInt("血量");
                final int mp = rs.getInt("魔力");
                final int hpR = rs.getInt("回血");
                final int mpR = rs.getInt("回魔");
                final int sp = rs.getInt("魔法攻擊");
                final int str = rs.getInt("力量");
                final int dex = rs.getInt("敏捷");
                final int _con = rs.getInt("體力");
                final int wis = rs.getInt("精神");
                final int _int = rs.getInt("智力");
                final int cha = rs.getInt("魅力");
                final int dmgR = rs.getInt("傷害減免");
                final BlessStat stat = new BlessStat(
                        id, type, dmgup, bow_dmgup, pvp_dmg, hit, bow_hit, ac, mr,
                        hp, mp, hpR, mpR, sp, str, dex, _con, wis, _int, cha, dmgR
                );
                this.data.put(type, stat);
            }
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, "error while creating 系統_武器祝福屬性 table", e);

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
    public final BlessStat getData(final L1ItemInstance item) {
        if (item.getBless() != 0) {
            return null;
        }
        switch (item.getItem().getUseType()) {
            case 1:
                return this.data.get(0); // 取得武器資料
            case 2: // 盔甲
            case 18: // T恤
            case 19: // 斗篷
            case 20: // 手套
            case 21: // 靴
            case 22: // 頭盔
            case 25: // 盾牌
                return this.data.get(1);
            case 40: // 耳環
            case 23: // 戒指
            case 24: // 項鏈
            case 37: // 腰帶
                return this.data.get(2);
        }
        return null;
    }
    public static class BlessStat {
        private final int id, type, dmgu, bow_dmgup, pvp_dmg, hit, bow_hit, ac, mr, hp, mp, hpR, mpR, sp, str, dex, _con, wis, _int, cha, dmgR;

        public BlessStat(int id, int type, int dmgu, int bow_dmgup, int pvp_dmg, int hit, int bow_hit, int ac, int mr, int hp, int mp, int hpR, int mpR, int sp, int str, int dex, int _con, int wis, int _int, int cha, int dmgR) {
            this.id = id;
            this.type = type;
            this.dmgu = dmgu;
            this.bow_dmgup = bow_dmgup;
            this.pvp_dmg = pvp_dmg;
            this.hit = hit;
            this.bow_hit = bow_hit;
            this.ac = ac;
            this.mr = mr;
            this.hp = hp;
            this.mp = mp;
            this.hpR = hpR;
            this.mpR = mpR;
            this.sp = sp;
            this.str = str;
            this.dex = dex;
            this._con = _con;
            this.wis = wis;
            this._int = _int;
            this.cha = cha;
            this.dmgR = dmgR;
        }

        // Getters and Setters
        public int getId() { return id; }

        public int getType() { return type; }

        public int getDmgu() { return dmgu; }

        public int getBow_dmgup() { return bow_dmgup; }

        public int getPvp_dmg() { return pvp_dmg; }

        public int getHit() { return hit; }

        public int getBow_hit() { return bow_hit; }

        public int getAc() { return ac; }
        public int getMr() { return mr; }

        public int getHp() { return hp; }

        public int getMp() { return mp; }

        public int getHpR() { return hpR; }

        public int getMpR() { return mpR; }

        public int getSp() { return sp; }

        public int getStr() { return str; }

        public int getDex() { return dex; }

        public int get_con() { return _con; }

        public int getWis() { return wis; }

        public int get_int() { return _int; }

        public int getCha() { return cha; }

        public int getDmgR() { return dmgR; }
    }
}
