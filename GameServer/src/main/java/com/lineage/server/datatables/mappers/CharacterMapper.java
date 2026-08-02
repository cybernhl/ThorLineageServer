package com.lineage.server.datatables.mappers;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.datatables.lock.CharOtherReading;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1PcInstance 模型轉換器
 */
public class CharacterMapper {

    private static final CharacterMapper _instance = new CharacterMapper();

    public static CharacterMapper get() {
        return _instance;
    }

    /**
     * 從 ResultSet 映射人物資料
     * @param rs 結果集
     * @param client 客戶端執行器
     * @return 封裝後的 L1PcInstance
     * @throws SQLException
     */
    public L1PcInstance mapRow(ResultSet rs, ClientExecutor client) throws SQLException {
        final L1PcInstance pc = new L1PcInstance(client);
        
        final String loginName = rs.getString("account_name").toLowerCase();
        pc.setAccountName(loginName);
        
        final int objid = rs.getInt("objid");
        pc.setId(objid);
        pc.set_showId(-1);
        
        // 處理其它數據 (L1PcOther)
        L1PcOther other = CharOtherReading.get().getOther(pc);
        if (other == null) {
            other = new L1PcOther();
            other.set_objid(objid);
        }
        pc.set_other(other);
        
        pc.setName(rs.getString("char_name"));
        pc.setHighLevel(rs.getInt("HighLevel"));
        pc.setExp(rs.getLong("Exp"));
        pc.addBaseMaxHp(rs.getInt("MaxHp"));
        
        int currentHp = rs.getInt("CurHp");
        if (currentHp < 1) {
            currentHp = 1;
        }
        pc.setDead(false);
        pc.setCurrentHpDirect(currentHp);
        pc.setStatus(0);
        
        pc.addBaseMaxMp(rs.getShort("MaxMp"));
        pc.setCurrentMpDirect(rs.getShort("CurMp"));
        pc.addBaseStr(rs.getInt("Str"));
        pc.addBaseCon(rs.getInt("Con"));
        pc.addBaseDex(rs.getInt("Dex"));
        pc.addBaseCha(rs.getInt("Cha"));
        pc.addBaseInt(rs.getInt("Intel"));
        pc.addBaseWis(rs.getInt("Wis"));
        
        final int status = rs.getInt("Status");
        pc.setCurrentWeapon(status);
        
        final int classId = rs.getInt("Class");
        pc.setClassId(classId);
        pc.setTempCharGfx(classId);
        pc.setGfxId(classId);
        
        pc.set_sex(rs.getInt("Sex"));
        pc.setType(rs.getInt("Type"));
        
        int head = rs.getInt("Heading");
        if (head > 7) {
            head = 0;
        }
        pc.setHeading(head);
        
        pc.setX(rs.getInt("locX"));
        pc.setY(rs.getInt("locY"));
        pc.setMap(rs.getShort("MapID"));
        pc.set_food(rs.getInt("Food"));
        pc.setLawful(rs.getInt("Lawful"));
        pc.setTitle(rs.getString("Title"));
        pc.setClanid(rs.getInt("ClanID"));
        pc.setClanname(rs.getString("Clanname"));
        pc.setClanRank(rs.getInt("ClanRank"));
        pc.setBonusStats(rs.getInt("BonusStatus"));
        pc.setElixirStats(rs.getInt("ElixirStatus"));
        pc.setElfAttr(rs.getInt("ElfAttr"));
        pc.set_PKcount(rs.getInt("PKcount"));
        pc.setPkCountForElf(rs.getInt("PkCountForElf"));
        pc.setExpRes(rs.getInt("ExpRes"));
        pc.setPartnerId(rs.getInt("PartnerID"));
        pc.setAccessLevel(rs.getShort("AccessLevel"));
        
        // 權限判定
        if (pc.getAccessLevel() >= 200) {
            pc.setGm(true);
            pc.setMonitor(false);
        } else if (pc.getAccessLevel() == 100) {
            pc.setGm(false);
            pc.setMonitor(true);
        } else {
            pc.setGm(false);
            pc.setMonitor(false);
        }
        
        pc.setOnlineStatus(rs.getInt("OnlineStatus"));
        pc.setHomeTownId(rs.getInt("HomeTownID"));
        pc.setContribution(rs.getInt("Contribution"));
        pc.setHellTime(rs.getInt("HellTime"));
        pc.setBanned(rs.getBoolean("Banned"));
        pc.setKarma(rs.getInt("Karma"));
        pc.setLastPk(rs.getTimestamp("LastPk"));
        pc.setLastPkForElf(rs.getTimestamp("LastPkForElf"));
        pc.setOriginalStr(rs.getInt("OriginalStr"));
        pc.setOriginalCon(rs.getInt("OriginalCon"));
        pc.setOriginalDex(rs.getInt("OriginalDex"));
        pc.setOriginalCha(rs.getInt("OriginalCha"));
        pc.setOriginalInt(rs.getInt("OriginalInt"));
        pc.setOriginalWis(rs.getInt("OriginalWis"));

        pc.refresh();
        pc.setMoveSpeed(0);
        pc.setBraveSpeed(0);
        pc.setGmInvis(false);

        return pc;
    }
}
