# DataTable 代碼精簡化研究分析與實作進度表

本報告旨在針對 `ThorLineageServer` 中所有資料存取類別（DataTables）進行深層掃描，找出代碼冗餘、職責耦合的點，並制定詳細的重構計畫。

---

## 1. 現狀分析與調整原因

### 1.1 為什麼需要調整？
目前專案中的 `DataTable` 類別普遍存在以下問題：
1.  **違反單一職責原則 (Single Responsibility Principle)**：DAO 類別不應同時負責 SQL 執行與詳細的欄位封裝邏輯。
2.  **高代碼重複率**：如 `loadNpcList` 與 `loadNpcMonster` 中，超過 80% 的 `rs.getXXX` 代碼是完全相同的。
3.  **維護性差**：若資料庫 Schema 增加一個欄位，開發者必須在多個 DAO 的多個方法中同步修改重複的賦值邏輯。
4.  **物理拆分阻礙**：目前的 `DataTable` 直接與 `ResultSet` 深度綁定，這使得將「轉換邏輯」移到獨立模組變得很困難。

### 1.2 需要調整的核心文件清單 (部分示例)
經初步分析，以下類別具備極高的重構價值（代碼精簡率預計可達 50% 以上）：

| 檔案名稱 | 臃腫點 (Bloated Points) | 預期精簡內容 |
| :--- | :--- | :--- |
| `NpcTable.java` | `loadNpcList()` 與 `loadNpcMonster()` 合計約 200 行重複封裝代碼。 | 統一調用 `NpcMapper.mapRow()`。 |
| `ItemTable.java` | `allItemsEtcItem`, `allItemsWeapon`, `allItemsArmor` 等 5 個載入方法。 | 使用 `ItemMapper` 提供的專屬方法。 |
| `SkillsTable.java` | `loadSkills()` 中數十行欄位賦值。 | 委託給 `SkillMapper`。 |
| `AccountTable.java` | 包含帳號與金流相關的多個封裝邏輯。 | 統一使用 `AccountMapper`。 |
| `CharacterTable.java` | 玩家角色載入時涉及超大量欄位 (50+)。 | 抽離為 `CharacterMapper`。 |

---

## 2. 重構進度表 (Refactoring Progress)

我將按照業務重要性與複雜度，逐步處理各個 DataTable 的精簡化。

| 階段 | 目標 DataTable | 對應 Mapper | 狀態 | 預計日期 |
| :--- | :--- | :--- | :--- | :--- |
| **Stage 1** | `NpcTable.java` | `NpcMapper` | [x] | 2026/08/02 |
| **Stage 2** | `ItemTable.java` | `ItemMapper` | [x] | 2026/08/02 |
| **Stage 3** | `SkillsTable.java` | `SkillMapper` | [x] | 2026/08/02 |
| **Stage 4** | `AccountTable.java` | `AccountMapper` | [x] | 2026/08/02 |
| **Stage 5** | `SqlTable` 體系 (Clan, Mail, Board) | 多個 Mapper | [x] | 2026/08/02 |
| **Stage 6** | `MySqlCharacterStorage.java` | `CharacterMapper` | [x] | 2026/08/02 |
| **Stage 7** | 其他 Table (Exp, Pet) | 各自 Mapper | [x] | 2026/08/02 |

---

## 3. 實作規範 (Refactoring Standards)

在處理每個 `DataTable` 時，應遵循以下步驟：
1.  **導入 (Import)**：引入對應的 Mapper 類別。
2.  **定位 (Locate)**：找到 `while(rs.next())` 迴圈。
3.  **替換 (Replace)**：將迴圈內手動 `new` 物件與 `set` 欄位的邏輯，替換為 `Mapper.get().mapRow(rs)`。
4.  **清理 (Clean)**：刪除不再使用的本地變數與輔助方法。
5.  **驗證 (Verify)**：確保編譯通過，且 SQL 查詢的欄位別名與 Mapper 中的 key 一致。

---
**下一階段預告**：開始處理 **Stage 1: NpcTable.java** 的代碼精簡。

*文件編撰日期：2026/08/02*
