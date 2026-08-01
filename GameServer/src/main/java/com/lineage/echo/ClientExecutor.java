package com.lineage.echo;

import java.io.IOException;

import com.custom.clan.ClanSpecialStatFactory;
import com.lineage.mina.LineageProtocolHandler;
import com.lineage.server.model.L1Clan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.ConfigIpCheck;
import com.lineage.echo.encryptions.Encryption;
import com.lineage.list.OnlineUser;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.OpcodesServer;
import com.lineage.server.templates.L1Account;
import com.lineage.server.utils.SystemUtil;

/**
 * 客戶資料處理
 *
 * @author dexc
 */
public class ClientExecutor extends OpcodesClient {

    private static final Log _log = LogFactory.getLog(ClientExecutor.class);

    public static final String CLIENT_KEY = "CLIENT";
    private L1Account _account = null;// 連線帳戶資料

    private L1PcInstance _activeChar = null;// 登入人物資料

    private StringBuilder _ip = null;// 連線IP資料

    private StringBuilder _mac = null;// MAC資料

    private int _kick = 0;

    private Encryption _keys;

    private int _error = -1;// 錯誤次數

    private int _saveInventory = 0;

    private int _savePc = 0;

    public int _xorByte = (byte) 0xf0;

    public long _authdata;

    /**
     * 數據處理使用的套接字對像
     */
    private Channel _csocket = null;
    /**
     * 我所請求的數據包
     */
    private ChannelBuffer buffer;
    /**
     * 我所要求的全部數據包大小
     */
    private Long packet_size;
    private Long packet_send_size;
    private L1PcInstance pc;

    private IoSession _session;

    private final PacketCheck packet_check = new PacketCheck(this);

    public final void setPacketCheck(final int opcode) {
        packet_check.addCheck(opcode);
    }

    public ClientExecutor(final IoSession session) {
        this._session = session;
        packet_size = packet_send_size = 0L;
        buffer = ChannelBuffers.dynamicBuffer();
        pc = new L1PcInstance(this);
    }

    public void setIp(final String ip) {
        this._ip = new StringBuilder(ip);
    }

    public ClientExecutor() {
        buffer = ChannelBuffers.dynamicBuffer();
        pc = new L1PcInstance(this);
    }

    /**
     * 客戶端連接時初始化函數
     *
     * @param socket
     * @param key
     */
    public void update(Channel socket) {
        this._csocket = socket;
        packet_size = packet_send_size = Long.valueOf(0);
        String[] address = socket.getRemoteAddress().toString().substring(1).split(":");
        _ip = new StringBuilder().append(address[0]);
        buffer.clear();
    }

    public ChannelBuffer getBuffer() {
        return buffer;
    }

    /**
     * 傳回要求傳輸數據包大小
     *
     * @return
     */
    public Long getPacketSize() {
        return packet_size;
    }

    /**
     * 設置要求傳輸數據包大小
     *
     * @param size
     */
    public void setPacketSize(Long size) {
        packet_size = size;
    }

    /**
     * 傳回發送數據包大小
     *
     * @return
     */
    public Long getPacketSendSize() {
        return packet_send_size;
    }

    /**
     * 設置發送數據包大小
     *
     * @param size
     */
    public void setPakcetSendSize(Long size) {
        packet_send_size = size;
    }

    public void start() {

    }

    /**
     * 關閉連線線程
     *
     * @throws IOException
     */
    public void close() {
        final L1Clan clan = _activeChar != null ? _activeChar.getClan() : null;
        IoSession temp = null;
        try {
            String mac = null;
            if (_mac != null) {
                mac = _mac.toString();
            }
            if (_session != null) {
                temp = _session;
                _session = null;
            }

            _kick = 0;

            if (_account != null) {
                OnlineUser.get().remove(_account.get_login());
            }

            if (_activeChar != null) {
                quitGame();
            }
            ClanSpecialStatFactory.getInstance().clanStatSet(clan);
            String ipAddr = _ip == null ? "" : _ip.toString();
            String account = null;

            if (_kick < 1) {
                if (_account != null) {
                    account = _account.get_login();
                }
            }

            if (ConfigIpCheck.ISONEIP) {
                LanSecurityManager.ONEIPMAP.remove(ipAddr);
            }
//            if (LineageProtocolHandler.ipcounts.containsKey(ipAddr)) {
//                LineageProtocolHandler.ipcounts.get(ipAddr).decCount();
//            }

            _mac = null;// MAC資料
            _ip = null;// 連線IP資料
            _activeChar = null;// 登入人物資料
            _account = null;// 連線帳戶資料

            //_csocket = null;
            _keys = null;

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n--------------------------------------------------");
            stringBuilder.append("\n       客戶端 離線: (");
            if (account != null) {
                stringBuilder.append(account + " ");
            }
            if (mac != null) {
                stringBuilder.append(" " + mac + " / ");
            }
            stringBuilder.append(ipAddr + ") 完成連線中斷!!");
            stringBuilder.append("\n--------------------------------------------------");
            _log.info(stringBuilder.toString());
            SystemUtil.printMemoryUsage(_log);
            //System.gc();

        } catch (final Exception e) {
            //_log.error(e.getLocalizedMessage(), e);
        } finally {
            if (temp != null && temp.isConnected()) {
                temp.closeNow();
            }
        }
    }

    /**
     * 請求數據包傳輸處理函數
     *
     * @param o
     */
    public void toSender(Object o) {
        if (_session != null) {
            synchronized (_session) {
                if (_session != null && _session.isConnected()) {
                    _session.write(o);
                    return;
                }
            }
        }
        if (o instanceof OpcodesServer) {
            this.close();
            System.out.println("OpcodesServer/T:" + o);
            BasePacketPooling.setPool((OpcodesServer) o);
        }
    }

    /**
     * 傳回帳號暫存資料
     *
     * @return
     */
    public L1Account getAccount() {
        return _account;
    }

    /**
     * 設置登入帳號
     *
     * @param account
     */
    public void setAccount(final L1Account account) {
        _account = account;
    }

    /**
     * 傳回登入帳號
     *
     * @return
     */
    public String getAccountName() {
        if (_account == null) {
            return null;
        }
        return _account.get_login();
    }

    /**
     * 傳回目前登入人物
     *
     * @return
     */
    public L1PcInstance getActiveChar() {
        return _activeChar;
    }

    /**
     * 設置目前登入人物
     *
     * @param pc
     */
    public void setActiveChar(final L1PcInstance pc) {
        _activeChar = pc;
    }

    /**
     * 傳回IP位置
     *
     * @return
     */
    public StringBuilder getIp() {
        return _ip;
    }

    /**
     * 傳回MAC位置
     *
     * @return
     */
    public StringBuilder getMac() {
        return _mac;
    }

    /**
     * 設置MAC位置
     *
     * @param mac
     * @return true:允許登入 false:禁止登入
     */
    public boolean setMac(final StringBuilder mac) {
        _mac = mac;
        return true;
    }

    /**
     * 傳回 Socket
     *
     * @return
     */
    public Channel getSocket() {
        return _csocket;
    }

    public IoSession get_session() {
        return _session;
    }

    /**
     * 中斷連線
     *
     * @return
     */
    public boolean kick() {
        quitGame();

        _kick = 1;
        close();
        return true;
    }

    /**
     * 人物離開遊戲的處理
     *
     * @param pc
     */
    public void quitGame() {
        try {
            if (_activeChar == null) {
                return;
            }
            QuitGame.quitGame(_activeChar);
            _activeChar = null;

        } catch (final Exception e) {
            //_log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 加密與解密金鑰
     */
    public void set_keys(Encryption keys) {
        //System.out.println("加密與解密金鑰:"+keys);
        _keys = keys;
    }

    /**
     * 傳回加密與解密金鑰
     *
     * @return the _keys
     */
    public Encryption get_keys() {
        return _keys;
    }

    /**
     * 傳回錯誤次數
     *
     * @return
     */
    public int get_error() {
        return _error;
    }

    /**
     * 設置錯誤次數
     *
     * @param error
     */
    public void set_error(final int error) {
        _error = error;
        if (error >= 2) {
            kick();
        }
    }

    /**
     * 設置自動存檔背包物件時間
     *
     * @param _saveInventory
     */
    public void set_saveInventory(final int saveInventory) {
        _saveInventory = saveInventory;
    }

    /**
     * 傳回自動存檔背包物件時間
     *
     * @return
     */
    public int get_saveInventory() {
        return _saveInventory;
    }

    /**
     * 設置自動存檔人物資料時間
     *
     * @param _saveInventory
     */
    public void set_savePc(final int savePc) {
        _savePc = savePc;
    }

    /**
     * 傳回自動存檔人物資料時間
     *
     * @return
     */
    public int get_savePc() {
        return _savePc;
    }

    /**
     * 要求使用封包
     *
     * @param data
     */
    public void toPacket(byte[] data) {
        ClientUseOP.execute(data, this);


    }
}
