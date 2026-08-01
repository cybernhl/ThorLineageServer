package com.lineage.server.serverpackets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.timecontroller.server.ServerRestartTimer;

/**
 * 伺服器訊息(行數/行數,附加字串)
 * 
 * @author hjx1000
 * 
 */
public class S_WhoStationery extends ServerBasePacket {
	
	private static final String _S_WhoStationery = "[S] _S_WhoStationery";

    private byte[] _byte = null;

    /**
     * 伺服器訊息(行數/行數,附加字串)
     * 
     * @param cha
     */
    /**
     * 玩家輸入WHO顯示信息為佈告欄(訊息閱讀)模式
     * 
     * @param pc
     *            查詢的玩家
     */
    public S_WhoStationery(final L1PcInstance pc) {

        final String nowDate = new SimpleDateFormat(
                "MM月dd號 kk:mm").format(new Date());
        final double EXP = ConfigRate.RATE_XP * ConfigOther.RATE_XP_WHO;
        final double RWL = ConfigRate.RATE_WEIGHT_LIMIT;
        final double RDI = ConfigRate.RATE_DROP_ITEMS;
        final double RDA = ConfigRate.RATE_DROP_ADENA;
        final double RLA = ConfigRate.RATE_LA;
        final double RKA = ConfigRate.RATE_KARMA;
        final int RKC = pc.get_PKcount();
        //final int time = L1GameReStart.getWillRestartTime();
        /*final int P_HP = ConfigCharSetting.PRINCE_MAX_HP;
        final int P_MP = ConfigCharSetting.PRINCE_MAX_MP;
        final int K_HP = ConfigCharSetting.KNIGHT_MAX_HP;
        final int K_MP = ConfigCharSetting.KNIGHT_MAX_MP;
        final int E_HP = ConfigCharSetting.ELF_MAX_HP;
        final int E_MP = ConfigCharSetting.ELF_MAX_MP;
        final int W_HP = ConfigCharSetting.WIZARD_MAX_HP;
        final int W_MP = ConfigCharSetting.WIZARD_MAX_MP;
        final int D_HP = ConfigCharSetting.DARKELF_MAX_HP;
        final int D_MP = ConfigCharSetting.DARKELF_MAX_MP;
        final int R_HP = ConfigCharSetting.DRAGONKNIGHT_MAX_HP;
        final int R_MP = ConfigCharSetting.DRAGONKNIGHT_MAX_MP;
        final int I_HP = ConfigCharSetting.ILLUSIONIST_MAX_HP;
        final int I_MP = ConfigCharSetting.ILLUSIONIST_MAX_MP;*/

        final String S_WhoCharinfo = "經驗倍率:" + EXP + " 倍\r\n" + "負重倍率:" + RWL
                + " 倍\r\n" + "掉寶倍率:" + RDI + " 倍\r\n" + "金幣倍率:" + RDA
                + " 倍\r\n" + "正義倍率:" + RLA + " 倍\r\n" + "友好倍率:" + RKA
                + " 倍\r\n" + "總PK次數:" + RKC + " 次\r\n" + "重啟時間:"
                + ServerRestartTimer.get_restartTime() + "\r\n" + 
//                "王族maxHP:" + P_HP + "王族maxMP:" + P_MP + "\r\n" + 
//                "騎士maxHP:" + K_HP + "騎士maxMP:" + K_MP + "\r\n" + 
//                "妖精maxHP:" + E_HP + "妖精maxMP:" + E_MP + "\r\n" + 
//                "法師maxHP:" + W_HP + "法師maxMP:" + W_MP + "\r\n" + 
//                "黑妖maxHP:" + D_HP + "黑妖maxMP:" + D_MP + "\r\n" + 
//                "龍騎maxHP:" + R_HP + "龍騎maxMP:" + R_MP + "\r\n" + 
//                "幻術maxHP:" + I_HP + "幻術maxMP:" + I_MP + "\r\n" +
                "萬能藥最多可使用10瓶\r\n" + "角色屬性最大值包括萬能藥: 35" + "\r\n" /*+
                "點卡剩餘時間:" + pc.getNetConnection().getAccount().get_card_fee()
                + "分鐘" + "\r\n"*/ + "當前時間:" + nowDate + "\r\n" + "攻擊傷害顯示命令：\r\n/who 顯示傷害\r\n"
                + "查詢掉落命令：\r\n" + "例：查詢 力量手套\r\n";

        // 當前的 年、月、日 (範例:12/01/10)
        final SimpleDateFormat setDateFormat = new SimpleDateFormat("yy/MM/dd");
        this.writeC(S_OPCODE_BOARDREAD);
        this.writeD(0x00);
        this.writeS("天堂"); // 作者
        this.writeS("天堂"); // 標題
        this.writeS(setDateFormat.format(Calendar.getInstance().getTime())); // 討論編號
        this.writeS(S_WhoCharinfo); // 顯示查詢信息
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
        	this._byte = this._bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
    	return _S_WhoStationery;
    }
}
