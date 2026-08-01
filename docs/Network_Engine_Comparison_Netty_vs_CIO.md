# 網路引擎對比報告：Netty vs. Kotlin CIO

針對 `ThorLineageServer` 未來可能引入 Kotlin 與進行網路層重構，本文件對比了業界霸主 **Netty** 與 Kotlin 原生 **CIO (Coroutine-based I/O)** 的深度差異與應用場景。

## 1. 核心技術對比

| 特性 | Netty (業界標準) | CIO (Ktor / Kotlin 原生) |
| :--- | :--- | :--- |
| **並發模型** | **事件驅動 (Event-driven)**：基於 Callbacks 與 Handler 鏈。 | **協程 (Coroutines)**：基於 `suspend` 非阻塞掛起。 |
| **開發語言** | Java (對 Kotlin 極度友善) | Kotlin 專用 |
| **底層技術** | Java NIO，支援 **Native (epoll/kqueue)** 優化。 | Kotlin 協程與 Java NIO 的封裝。 |
| **緩衝區管理** | 極致優化的 **`ByteBuf` (零拷貝)** | 標準 `ByteBuffer` |
| **程式碼風格** | 較為繁瑣 (大量的 Listeners 與 Handlers)。 | **極簡**：異步代碼寫起來像同步代碼一樣直觀。 |
| **效能表現** | **極致**：針對高吞吐量、低延遲進行了 20 年的優化。 | **優良**：適合輕量級，在極限吞吐量下略遜 Netty。 |
| **成熟度** | 極高 (Minecraft, Apple, FB 都在用)。 | 中高 (隨 Ktor 框架普及，主要用於 Web/API)。 |

## 2. 為什麼在「MMO 遊戲核心」中 Netty 依然是首選？

雖然 Kotlin CIO 的語法非常優美，但針對 Lineage 這種需要處理大量長連接、複雜自訂協定的遊戲，Netty 有不可取代的優勢：

### 2.1 強大的 Pipeline (管線) 機制
Lineage 的協定通常是 `[2位元組長度標頭] + [加密資料]`。
- **Netty 做法**：可以非常優雅地串接內建的 `LengthFieldBasedFrameDecoder` -> `DecryptHandler` -> `LogicHandler`。
- **CIO 做法**：通常需要手動在協程中讀取位元組並拆解，代碼雖然直觀，但對於「流式處理」的封裝不如 Netty 完備。

### 2.2 極致的零拷貝 (Zero-copy) 與記憶體池
- **Netty 的 `ByteBuf`**：支援池化（Pooled），在高頻率發送封包（如：移動更新、戰動廣播）時，能顯著降低 GC (垃圾回收) 的壓力。
- **CIO**：主要依賴 JVM 原生的 `ByteBuffer`，在處理每秒數萬次的加解密與拼接時，產生的臨時物件較多，可能導致 Ping 值跳動。

### 2.3 作業系統層級優化 (Native Transports)
在 Linux 環境下，Netty 可以直接呼叫作業系統的核心 `epoll`。這對於處理 2000 人以上同時在線的 MMO 伺服器來說，是確保「低延遲（Low Latency）」的技術基石。

## 3. CIO 的最佳應用場景：Ktor 框架

CIO 的真正優勢在於**開發效率**與**代碼可讀性**，非常適合開發：
- **Web 管理後台 (REST API)**：例如玩家帳號管理、點數商城介面。
- **外部通訊橋接**：與 Discord / Telegram 的 Webhook 通訊。
- **跨服數據交換**：輕量級的跨服聊天或排名數據同步。

**範例對比：**
- **Netty**：需要寫一個 `Handler` 類別，覆寫 `channelRead`，處理異步回調。
- **CIO (Ktor)**：只需在一個 `launch` 區塊中寫下 `val data = socket.read()`，完全像寫單機程式一樣簡單。

## 4. 針對 `ThorLineageServer` 的技術路線建議

為了兼顧「核心穩定」與「開發效率」，建議採取**混血方案**：

1.  **GameServer 核心 (TCP/加密封包)**：
    - **繼續使用 Netty 4.x**。
    - 遷移至 Kotlin 後，利用 Kotlin 的 `Extension Functions` (擴充函數) 來簡化 Netty 的 API 呼叫，讓代碼更美觀。
2.  **外部擴充功能 (Web/API/Discord)**：
    - **使用 Kotlin + Ktor + CIO**。
    - 這樣可以讓主程式保持專注於遊戲邏輯，周邊工具則能快速開發、疊代。

---
*最後修正日期：2026/08/02*
