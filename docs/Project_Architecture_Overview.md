# ThorLineageServer 專案架構概覽 (GameServer)

本文件旨在說明 `ThorLineageServer` 核心模組 `GameServer` 的原始碼結構與功能職責，作為後續處理相依性與維護之參考。

## 1. 核心技術棧
- **開發語言**: Java 1.8
- **網路通訊**: Apache MINA (2.2.2) & Netty (3.6.2.Final)
- **資料庫**: MySQL (5.1.15), C3P0 (0.9.2.1) 連線池
- **日誌系統**: Log4j (1.2.15) 與 SLF4J (1.7.36)
- **硬體綁定**: 具備 HWID 檢測機制 (自訂類別 `ServerHWID`)

## 2. 目錄與包結構說明

### A. 核心啟動與配置 (`com.lineage`)
- `com.lineage.Server`: 伺服器入口點 (Entry Point)，負責環境檢測、HWID 驗證、日誌備份及載入設定。
- `com.lineage.config`: 包含各種設定類別 (如 `Config`, `ConfigRate`, `ConfigSQL` 等)，對應 `./config/*.properties` 檔案。
- `com.lineage.DatabaseFactory`: 管理與 MySQL 的資料庫連線。

### B. 伺服器邏輯核心 (`com.lineage.server`)
- `com.lineage.server.GameServer`: 伺服器主初始化類別，負責依序啟動所有數據表載入、執行緒池、計時器及網路接聽。
- `com.lineage.server.datatables`: **資料存取層 (DAO)**。將資料庫中的道具、NPC、技能、掉落率等靜態資料載入內存快取。
- `com.lineage.server.model`: **領域模型 (Domain Model)**。定義遊戲中的各種實體，如 `L1PcInstance` (玩家角色), `L1NpcInstance` (NPC), `L1ItemInstance` (道具)。
- `com.lineage.server.clientpackets`: 處理**由客戶端傳入**的封包邏輯。
- `com.lineage.server.serverpackets`: 封裝**傳送至客戶端**的封包結構。
- `com.lineage.server.timecontroller`: **伺服器心跳/計時器**。管理遊戲世界的週期性任務（如 NPC AI、玩家回血、定時存檔、活動開啟）。
- `com.lineage.server.world`: 遊戲世界狀態管理，用於檢索在線玩家、對象位置等。

### C. 網路層 (`com.lineage.mina` / `com.lineage.echo`)
- `com.lineage.mina`: 基於 Apache MINA 的底層網路封裝。
- `com.lineage.echo`: 封包處理中介層，包含 `ClientExecutor` (客戶端執行緒) 與加密解密邏輯。

### D. 擴展與自訂功能 (`com.custom`, `com.add`, `com.william`)
- 包含第三方或後期加入的擴展功能，例如：
    - 台灣麻將、百家樂等小遊戲邏輯。
    - 特殊武器/防具能力強化系統。
    - 額外的 NPC 任務系統 (`com.william.NpcQuest`)。

### E. 基礎建設 (`me.aodamiao.pool`, `sun.misc`)
- `me.aodamiao.pool.thread`: 自訂的高效能執行緒池實現。
- `sun.misc.BASE64Encoder`: 為了解決 Java 8+ 環境下 `sun.misc` API 遺失或受限問題，手動建立的相容性 Shim 類別 (實作轉向 `java.util.Base64`)。

## 3. 啟動流程簡述
1. `Server.main()` 被呼叫。
2. 檢查 `ServerHWID`。
3. 載入 `config/*.properties` 設定檔。
4. 初始化 `DatabaseFactory` (C3P0)。
5. `GameServer.initialize()` 開始運作：
    - 載入所有 `datatables` (將遊戲數據常駐內存)。
    - 建立 `World` 實體。
    - 啟動各種 `StartTimer_*` 計時執行緒。
    - 透過 `MinaCoreFactory` 開始監聽連線埠。

## 4. 當前開發注意事項 (相依性處理)
- **SLF4J**: 由於專案混合使用多個庫，SLF4J 的解析策略已在 `build.gradle` 與 `settings.gradle.kts` 中特別設定（強制使用 `@jar`），避免因 POM 損壞導致的構建失敗。
- **JavaFX**: 專案中有引用 `javafx.util.Pair`，若執行環境為 OpenJDK，需確保 `javafx` 模組正確依賴。
- **舊版庫排除**: `log4j` 需排除 `jms`, `jmxtools` 等已從中心庫移除的相依項。
