package com.lineage.mina;

import com.lineage.server.utils.PerformanceTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;


public class MinaCoreFactory {
    private static final Log _log = LogFactory.getLog(MinaCoreFactory.class);

    private final int port;
    public MinaCoreFactory(final int port) {
        this.port = port;
        _log.info(getClass().getSimpleName() + " 開始監聽服務端口:(" + this.port + ")");
    }

    public void run() {
        try {
            PerformanceTimer timer = new PerformanceTimer();
            NioSocketAcceptor acceptor = new NioSocketAcceptor();
            DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
            chain.addLast("codec", new ProtocolCodecFilter(new LineageCodecFactory()));
            acceptor.setReuseAddress(true);
            acceptor.getSessionConfig().setTcpNoDelay(true);
            acceptor.setHandler(new LineageProtocolHandler());
            chain.addLast("ThreadPool", new ExecutorFilter(Integer.MAX_VALUE));
            // 設置監聽端口
            acceptor.bind(new InetSocketAddress(this.port));
            _log.info("-------------------------------------------------------------------");
            _log.info("(" + this.port + ")端口監聽配置完成 " + timer.get() + " ms"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
