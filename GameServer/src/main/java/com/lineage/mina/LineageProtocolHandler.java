package com.lineage.mina;


import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.sql.IpTable;
import com.lineage.server.serverpackets.S_ServerVersion;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.timecontroller.server.ServerRestartTimer;
import com.lineage.server.utils.SystemUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class LineageProtocolHandler extends IoHandlerAdapter {

    private static final Log _log = LogFactory
            .getLog(LineageProtocolHandler.class);
    public static final Map<String, IPCount> ipcounts = new ConcurrentHashMap<>();

    public static class IPCount {
        private int count = 0;
        private long last_date = 0;
        public IPCount() {
            last_date = System.currentTimeMillis();
        }
        public void addCount() {
            if ((System.currentTimeMillis() - last_date) < 10000) {
                this.count++;
            } else {
                this.count = 0;
            }
            last_date = System.currentTimeMillis();
        }
        public final int getCount() {
            return this.count;
        }
    }

    public LineageProtocolHandler() {
        super();
    }

    /**
     * 這個方法當一個 Session 對像被創建的時候被調用。對於 TCP 連接來說，連接被接受的時候 調用，但要注意此時 TCP
     * 連接並未建立，此方法僅代表字面含義，也就是連接的對象 IoSession 被創建完畢的時候，回調這個方法。 對於 UDP
     * 來說，當有數據包收到的時候回調這個方法，因為 UDP 是無連接的。
     */
    // @Override
    public void sessionCreated(IoSession session) {
        try {
            // 檢測連線是否合法
            StringTokenizer st = new StringTokenizer(session.getRemoteAddress()
                    .toString().substring(1), ":");
            String ip = st.nextToken();
            if (ServerRestartTimer.isRtartTime()) {
                // 關閉該連接
                session.closeNow();
                _log.info("因為重啟拒絕IP:" + ip);
                return;
            }
            if (IpTable.check(ip)) {
                _log.info("拒絕IP:" + ip);
                // 關閉該連接
                session.closeNow();
                return;
            }
            _log.info("IP: (" + ip + ") 開始連線");
            if (st.nextToken().startsWith("0")) {
                _log.info("拒絕IP (Token) :" + ip);
                // 關閉該連接
                session.closeNow();
                return;
            }
            if (!CustomSafeConnectIP.getInstance().containsKey(ip)) {
                if (!ipcounts.containsKey(ip)) {
                    ipcounts.put(ip, new IPCount());
                }
                final IPCount ipCount = ipcounts.get(ip);
                ipCount.addCount();
                if (ipCount.getCount() >= 5) {
                    session.closeNow();
                    System.out.println("連線數攻擊 IP: " + ip + " 以阻擋");
                    return;
                }
            }
            // 這個方法設置關聯在通道上的讀、寫或者是讀寫事件在指定時間內未發生，該通道就進入空
            // 閒狀態。一旦調用這個方法，則每隔idleTime都會回調過濾器、IoHandler中的sessionIdle()
            session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 600);
        } catch (Exception e) {
            // 關閉該連接
            session.closeNow();
        }
    }

    /**
     * 這個方法在連接被打開時調用，它總是在 sessionCreated()方法之後被調用。對於 TCP 來
     * 說，它是在連接被建立之後調用，你可以在這裡執行一些認證操作、發送數據等。 對於 UDP 來說，這個方法與
     * sessionCreated()沒什麼區別，但是緊跟其後執行。如果你每 隔一段時間，發送一些數據，那麼
     * sessionCreated()方法只會在第一次調用，但是 sessionOpened()方法每次都會調用。
     */
    // @Override
    public void sessionOpened(IoSession session) {
        if (session.isClosing()) {
            return;
        }
        try {
            StringTokenizer st = new StringTokenizer(session.getRemoteAddress().toString().substring(1), ":");
            String ip = st.nextToken();
            ClientExecutor lc = new ClientExecutor(session);
            lc.setIp(ip);
            /*
             * 這個方法用於給我們向會話中添加一些屬性，這樣可以在會話過程中都可以使用，類似於 HttpSession 的
             * setAttrbute()方法。IoSession 內部使用同步的 HashMap 存儲你添加的自 定義屬性。
             */
            session.setAttribute(ClientExecutor.CLIENT_KEY, lc);
            // FIXME
//            lc.Initialization();
            session.write(new S_ServerVersion());
//            if (ipcounts.containsKey(ip)) {
//                ipcounts.get(ip).addCount();
//            }
        } catch (Exception e) {
            // 關閉該連接
            session.closeNow();
        }
    }

    /**
     * 當發送消息成功時調用這個方法，注意這裡的措辭，發送成功之後，也就是說發送消息是不 能用這個方法的。
     */
    // @Override
    public void messageSent(IoSession session, Object message) {
        if (message instanceof ServerBasePacket) {
            ((ServerBasePacket) message).close();
        }
    }

    /**
     * 接收到消息時調用的方法，也就是用於接收消息的方法，一般情況下，message 是一個 IoBuffer
     * 類，如果你使用了協議編解碼器，那麼可以強制轉換為你需要的類型。通常我們 都 是 會 使 用 協 議 編 解 碼 器 的 ， 就 像 上 面 的 例
     * 子 ， 因 為 協 議 編 解 碼 器 是 TextLineCodecFactory，所以我們可以強制轉 message 為 String 類型。
     */
    // @Override
    public void messageReceived(IoSession session, Object message) {
        if (message instanceof byte[]) {
            ClientExecutor client = (ClientExecutor) session.getAttribute(ClientExecutor.CLIENT_KEY);
            try {
                client.toPacket((byte[]) message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 對於 TCP 來說，連接被關閉時，調用這個方法。 對於 UDP 來說，IoSession 的 close()方法被調用時才會毀掉這個方法。
     */
    // @Override
    public void sessionClosed(IoSession session) throws Exception {
        ClientExecutor lc = (ClientExecutor) session
                .getAttribute(ClientExecutor.CLIENT_KEY);
        if (lc != null) {
            lc.close();
        } else {
            session.closeNow();
        }
        _log.info("記憶體使用: " + SystemUtil.getUsedMemoryMB() + "MB");
        // _log.info("當前在線玩家" +
        // LoginController.getInstance().getOnlinePlayerCount() +
        // "位，等待玩家連線中...");
    }

    /**
     * 過濾器閒置600秒強制端口連接
     */
    // @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        StringTokenizer st = new StringTokenizer(session.getRemoteAddress()
                .toString().substring(1), ":");
        String ip = st.nextToken();
        ClientExecutor lc = (ClientExecutor) session
                .getAttribute(ClientExecutor.CLIENT_KEY);
        if (lc != null) {
            lc.close();
        } else {
            session.closeNow();
        }
        _log.info("ip:" + ip + "閒置時間太長切斷");
    }

    /**
     * 異常斷線觸發 客戶端斷開連接檢測
     */
    // @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        try {
            ClientExecutor lc = (ClientExecutor) session
                    .getAttribute(ClientExecutor.CLIENT_KEY);
            if (lc != null) {
                lc.close();
            } else {
                session.closeNow();
            }
            _log.error(cause.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}