# 多資料庫支援 (MySQL / SQLite) 整合策略報告

本文件分析將 `ThorLineageServer` 擴展為支援雙資料庫引擎（MySQL 與 SQLite）的可行性、優勢及具體實作路徑。

## 1. 核心動機與應用場景

| 引擎 | 適合場景 | 優勢 | 缺點 |
| :--- | :--- | :--- | :--- |
| **MySQL** | 正式營運、多人連線 | 高併發處理能力、數據一致性強、支援遠端管理。 | 需要安裝伺服器軟體、配置相對複雜。 |
| **SQLite** | 開發除錯、單機測試、極小型私人服 | 零配置（無需安裝）、單一檔案存儲、啟動速度極快。 | 不適合高併發寫入、功能較為基礎。 |

---

## 2. SQL 語法相容性分析 (MySQL vs SQLite)

經過對 `com.lineage.server.datatables` 下數百個類別的初步掃描，專案使用的 SQL 特性與 SQLite 的相容性如下：

### 2.1 高度相容部分 (無需改動)
*   **標準 CRUD**：`SELECT`, `INSERT`, `UPDATE`, `DELETE` 等基本語句完全一致。
*   **反引號 (Backticks)**：MySQL 習慣使用的 `` `table_name` `` 在 SQLite 的 JDBC 驅動中通常能正確處理（或可透過參數相容）。
*   **JDBC 介面**：專案透過 `PreparedStatement` 傳值，能自動處理不同資料庫間的轉義字元。

### 2.2 潛在不相容與風險
*   **資料型態**：MySQL 的 `TINYINT`, `SMALLINT`, `BIGINT` 在 SQLite 中統一映射為 `INTEGER`。
*   **特殊函數**：如 `NOW()`, `DATE_FORMAT()` 等 MySQL 特有函數需替換為標準 SQL 或 SQLite 對應函數。
*   **鎖定機制**：SQLite 採用檔案鎖，在大併發寫入時可能出現 `Database is locked`。

---

## 3. 架構重構方案：Database 抽象化

要達成無痛切換，核心在於修改 `com.lineage.DatabaseFactory`，將其從「寫死 C3P0」轉變為「資料源策略模式」。

### 3.1 步驟一：擴展配置 (`com.lineage.config.ConfigSQL`)
需要在 `ConfigSQL.java` 中增加資料庫類型開關。

```java
// 建議新增的靜態欄位
public static String DB_TYPE = "mysql"; // 可選值: "mysql", "sqlite"
public static String SQLITE_FILE = "./data/l1jdb.db";
```

### 3.2 步驟二：重構連線管理 (`com.lineage.DatabaseFactory`)
將內部的 `_source` 類別從 `ComboPooledDataSource` 提升為 JDBC 標準的 `javax.sql.DataSource`。

**可能的實作方案範例：**
```java
public class DatabaseFactory {
    // 改用通用介面，這讓底層可以是 C3P0 或 SQLiteDataSource
    private javax.sql.DataSource _source;

    private DatabaseFactory() {
        try {
            if ("sqlite".equalsIgnoreCase(ConfigSQL.DB_TYPE)) {
                // 初始化 SQLite (使用 org.sqlite.SQLiteDataSource)
                org.sqlite.SQLiteDataSource sqliteSource = new org.sqlite.SQLiteDataSource();
                sqliteSource.setUrl("jdbc:sqlite:" + ConfigSQL.SQLITE_FILE);
                this._source = sqliteSource;
            } else {
                // 預設維持 C3P0 + MySQL 
                ComboPooledDataSource mysqlSource = new ComboPooledDataSource();
                mysqlSource.setDriverClass(_driver);
                mysqlSource.setJdbcUrl(_url);
                mysqlSource.setUser(_user);
                mysqlSource.setPassword(_password);
                // 這裡可以保留原本的 C3P0 優化配置
                this._source = mysqlSource;
            }
            // 測試初始連線
            this._source.getConnection().close();
        } catch (Exception e) {
            _log.fatal("資料庫連線初始化失敗!", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return _source.getConnection();
    }
}
```

---

## 4. 環境準備與改動點總結

### 4.1 依賴調整 (`build.gradle`)
必須引入 SQLite 的 JDBC 驅動程式。
```gradle
implementation ("org.xerial:sqlite-jdbc:3.45.3.0")
```

### 4.2 資源清理工具 (`SQLUtil.java`)
現有的 `SQLUtil.close()` 封裝得很好，透過 JDBC 介面操作，**完全不需要修改**。

### 4.3 業務邏輯層 (`datatables`)
由於所有的 `datatables` 都是透過 `DatabaseFactory.get().getConnection()` 獲取連線，這種架構重構對業務代碼是 **100% 透明** 的。

---

## 5. 實作建議流程

1.  **第一階段 (依賴)**：在 Gradle 中引入 SQLite 驅動，不影響現有功能。
2.  **第二階段 (配置)**：在 `ConfigSQL` 中實作 `DB_TYPE` 的讀取邏輯。
3.  **第三階段 (工廠)**：重構 `DatabaseFactory` 的單例初始化邏輯。
4.  **第四階段 (遷移)**：編寫一個簡單的 Java 工具，將 MySQL 的數據導出並寫入 SQLite 檔案。

---
*文件編撰日期：2026/08/02*
