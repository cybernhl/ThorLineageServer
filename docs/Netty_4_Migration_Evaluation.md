# Netty 3 to Netty 4 升級評估報告

本報告針對 `GameServer` 網路層核心框架 Netty 的升級進行技術分析。目前專案使用的是 Netty 3.6.2.Final。

## 1. 影響檔案範圍 (共 6 檔)

| 檔案路徑 | 角色 | 核心依賴 |
| :--- | :--- | :--- |
| `com.lineage.netty.login.LineageServer.java` | 啟動器 | `ServerBootstrap`, `NioServerSocketChannelFactory` |
| `com.lineage.netty.CodecFactory.java` | 管線設定 | `ChannelPipelineFactory`, `ChannelPipeline` |
| `com.lineage.netty.Decoder.java` | 解碼器 | `OneToOneDecoder`, `ChannelBuffer` |
| `com.lineage.netty.Encoder.java` | 編碼器 | `OneToOneEncoder`, `ChannelBuffer` |
| `com.lineage.netty.ProtocolHandler.java` | 邏輯處理 | `SimpleChannelUpstreamHandler`, `MessageEvent` |
| `com.lineage.echo.ClientExecutor.java` | 會話管理 | `Channel`, `ChannelBuffer` (用於手動拼接) |

## 2. 核心 API 變更對照 (3.x vs 4.x)

| 特性 | Netty 3.x | Netty 4.x (建議目標) |
| :--- | :--- | :--- |
| **Package** | `org.jboss.netty.*` | **`io.netty.*`** |
| **Buffer** | `ChannelBuffer` | **`ByteBuf`** |
| **Buffer 工廠** | `ChannelBuffers` | `Unpooled` 或 `ByteBufAllocator` |
| **線程模型** | `Executor` (Boss/Worker) | **`EventLoopGroup`** (Boss/Worker) |
| **啟動器配置** | `setOption` (字串) | `option` / `childOption` (強型別) |
| **解碼基類** | `OneToOneDecoder` | `ByteToMessageDecoder` (更適配長度協議) |
| **編碼基類** | `OneToOneEncoder` | `MessageToByteEncoder` |
| **會話附件** | `channel.setAttachment(obj)` | `channel.attr(AttributeKey).set(obj)` |
| **事件處理** | `messageReceived(ctx, e)` | `channelRead(ctx, msg)` |

## 3. 升級難度與風險評估

### 3.1 難度：極高 (High Complexity)
- **非二進位相容**：必須修改所有影響檔案的 `import` 宣告與方法簽名。
- **Buffer 管理變革**：Netty 4 引入了 **引用計數 (Reference Counting)**。如果不正確呼叫 `ReferenceCountUtil.release(msg)`，會導致嚴重的 **記憶體洩漏 (Memory Leak)**。這對高併發的遊戲伺服器是致命的。
- **協定重新實作**：Lineage 的協定格式為「2位元組長度 + 資料」。目前的 `Decoder.java` 採用手動緩衝區管理。遷移後建議改用 `ByteToMessageDecoder` 的原生功能，邏輯需大幅重構。

### 3.2 優點：
- **效能優勢**：`ByteBuf` 池化技術能降低大流量下的 GC 壓力，對伺服器延遲 (Ping) 有正面幫助。
- **安全加固**：修復了 3.x 已知的網路走私與拒絕服務漏洞。
- **現代化**：Netty 3 已停止維護，Netty 4.1.x 則能完美適配 Java 11/17/22 及現代 Linux 的 `epoll`。

## 4. 遷移建議路線 (暫緩執行)

鑑於網路層改動的高風險性，建議採取的策略：

1.  **目前維持現狀**：Netty 3.6.2.Final 目前在 Java 1.8 下仍能運作。在確保 Maven 遷移後的穩定性之前，不建議更動此核心組件。
2.  **分階段實作**：若要升級，應先建立一個對等的 `com.lineage.netty4` 套件進行實驗實作。
3.  **測試要求**：必須進行至少 24 小時的壓力測試，重點觀察 `Direct Memory` 是否有洩漏現象。

---
*報告日期：2026/08/02*
