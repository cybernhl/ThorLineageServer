# MySQL Connector 升級與資料庫連線優化指南

本文件指導如何將專案的 MySQL 驅動從 5.1.x 升級至 8.x 系列，並確保與 C3P0 的連動正常。

## 1. 版本選擇與安全性評估

### 建議升級版本：`com.mysql:mysql-connector-j:8.4.0` (LTS)
- **穩定性**：這是 MySQL 官方推出的 **Long-Term Support (LTS)** 版本，目前支援 Java 1.8 且效能最優。
- **安全性 (CVE 評估)**：
    - **5.1.15 (舊版)**：存在 **CVE-2023-21971 (Critical)**，允許透過屬性轉換進行 RCE。
    - **8.0.33**：修復了 5.1 的 RCE，但仍存在 **CVE-2023-22102 (High)**。
    - **8.4.0 (本案選擇)**：作為最新的 LTS 版本，它完整修復了上述所有已知的高危險漏洞，是目前最安全的選擇。

### 關於 CVE-2023-22102 的詳細解析
- **漏洞本質**：該漏洞涉及 MySQL Connector 中的 **LDAP SASL 身份驗證** 實作。
- **評分原因**：評分為 **HIGH (CVSS 8.3)** 是因為若攻擊者成功利用 LDAP 驗證路徑，可能導致連線被完全接管。
- **實務環境分析**：雖然 Lineage 伺服器通常使用原生密碼驗證而非 LDAP，使得此漏洞在實務上較難被觸發，但為了符合最高的安全合規標準，我們選擇直接跳轉至修復後的 **8.4.0 LTS** 版本。

## 2. 驅動類別與連線配置 (重要變動)

### 2.1 驅動類別名稱 (Driver Class)
- **舊版 (5.1.x)**：`com.mysql.jdbc.Driver`
- **新版 (8.x)**：**`com.mysql.cj.jdbc.Driver`**
- **注意**：必須在伺服器的 `Properties` 檔案中同步修改此項。

### 2.2 JDBC 連線字串 (URL) 必填參數
- `serverTimezone=Asia/Taipei`：避免時區錯誤。
- `useSSL=false`：若無 SSL 憑證則需明確關閉。
- `allowPublicKeyRetrieval=true`：相容新版加密驗證方式。

**範例 URL：**
`jdbc:mysql://localhost:3306/l1jdb?serverTimezone=Asia/Taipei&useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8`

## 3. 未來 Java 版本相容性 (11/17/22)
- **Java 11 / 17 / 22**：`8.4.0 LTS` 均能完美支援，無需再次更動版本，具備極佳的向前相容性。

## 4. 執行清單
1. [x] **修改依賴**：在 `build.gradle` 中更新為 `8.4.0`。
2. [ ] **更新設定檔**：在伺服器的 Properties 檔案中修改 `Driver` 路徑與 `URL` 參數。
3. [ ] **驗證連線**：啟動伺服器並測試資料庫讀取。

---
*最後修正日期：2026/08/02*
