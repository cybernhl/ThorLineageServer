# 日誌系統分析與無痛遷移策略報告

本報告針對 `GameServer` 原始碼中的日誌系統使用現況進行全面評估，並提出基於「官方橋接器 (Shim Classes)」的無痛升級方案。

## 1. 使用分佈統計 (2026/08/02 掃描結果)

| 日誌框架 | 搜尋關鍵字 | 使用檔案數量 | 分佈範圍 |
| :--- | :--- | :--- | :--- |
| **Commons-Logging (JCL)** | `LogFactory.getLog` | **數百個檔案** | 專案核心、資料模組、伺服器邏輯（絕對多數）。 |
| **Log4j (1.2.x)** | `Logger.getLogger` | **約 12-20 檔** | 主要於 `com.william.*`、`com.custom.*` 及部分 `datatables`。 |
| **SLF4J** | `LoggerFactory.getLogger` | **0** | 原始碼中目前尚未直接使用。 |

## 2. 日誌框架關係解析 (The Logging Trio)

專案目前同時存在三種日誌相關依賴，其歷史與邏輯關係如下：
- **SLF4J (現代前端介面)**：目前業界標準，負責統一 API 語法。
- **Commons-Logging (舊前端介面)**：早期 Java 專案標準，目前已被 SLF4J 取代。
- **Log4j 1.2.x (舊後端引擎)**：負責實際的日誌寫入，但存在嚴重的安全漏洞 (CVE)。

## 3. 建議升級方案：官方 Shim Class (橋接遷移)

為了在不修改數百個 `.java` 檔案的前提下提升安全性與效能，建議採用以下策略：

### 3.1 核心機制：包名仿冒 (Shimming)
利用 SLF4J 官方提供的橋接 JAR 包（`jcl-over-slf4j` 與 `log4j-over-slf4j`）。這些 JAR 包內建了與舊框架完全相同的 Package 名稱（`org.apache.log4j` 等），但其內部邏輯已改為將日誌導向 SLF4J。

### 3.2 針對 `Server.java` 強制配置的處理
原始碼中 `com.lineage.Server` 會調用 `PropertyConfigurator.configure(LOG_4J)`。
- **橋接器行為**：`log4j-over-slf4j` 中的 `PropertyConfigurator` 是空操作 (No-op)。它會接收呼叫但不會執行任何邏輯，也不會拋出錯誤。
- **結果**：即使原始碼不改動，日誌系統也會順利切換至 Logback，由 `logback.xml` 統一接管。

### 3.3 優缺點評估
**優點：**
- **零代碼改動**：全專案原始碼不需做任何 `import` 修改。
- **安全性**：徹底移除有漏洞的 `log4j:log4j:1.2.15`。
- **配置統一**：由單一的 `logback.xml` 替代零散的 `.properties` 檔案。

## 4. 深層掃描結果 (Deep Scan Results)
- **自定義擴展**：**無**。專案未繼承 Log4j 內部類別，相容性 100%。
- **配置調用**：`com.lineage.Server.java` 第 121 行。

## 5. 舊配置轉譯說明 (Log4j to Logback)
根據 `log4j.properties` 的定義，我們已將以下邏輯轉譯至 `logback.xml`：
- **A1 (Console)**：對應 `ConsoleAppender`，格式為 `%p - %m%n`。
- **A2 (File)**：對應 `RollingFileAppender`，路徑為 `./loginfo/log4j-Message.log`，具備滾動策略。

## 6. 待執行清單 (Phased Plan)
1. [x] **深層掃描**：確認原始碼中不包含複雜的 Log4j 擴展。
2. [x] **設定檔轉譯**：完成從 `log4j.properties` 到 `logback.xml` 的邏輯對應。
3. [ ] **依賴替換**：在 `build.gradle` 中執行橋接。
4. [ ] **啟動驗證**：測試伺服器日誌輸出。

---
*最後修正日期：2026/08/02*
