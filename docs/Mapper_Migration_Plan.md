# Mapper 模式遷移與實作計畫 (2026/08/02)

本計畫旨在將 `ThorLineageServer` 的資料轉換邏輯（ResultSet to POJO）從 DAO 中抽離，為未來的模組化拆分與 SQLite 支援打下基礎。

## 1. 遷移目標
- **消除代碼重複**：同一個 POJO 的賦值邏輯不再散落在多個 Table 類別中。
- **支援多資料庫**：轉換邏輯（Mapper）與資料存取（SQL）解耦。
- **提升測試性**：Mapper 可以獨立進行單元測試。

## 2. 實作順序 (Priority)

| 優先序 | 目標 POJO | 對應 Mapper 類別 | 涉及 DataTables | 狀態 |
| :--- | :--- | :--- | :--- | :--- |
| **1** | `L1Npc` | `NpcMapper` | `NpcTable`, `NpcSpawnTable` | [x] |
| **2** | `L1Item`, `L1ItemsWeapon`, `L1ItemsArmor` | `ItemMapper` | `ItemTable`, `ItemBoxTable` | [ ] |
| **3** | `L1Skills` | `SkillMapper` | `SkillsTable`, `MobSkillTable` | [ ] |
| **4** | `L1Account` | `AccountMapper` | `AccountTable` | [ ] |
| **5** | `L1PcInstance` | `CharacterMapper` | `CharacterTable` | [ ] |
| **6** | `L1Clan` | `ClanMapper` | `ClanTable` | [ ] |
| **7** | `MapData` | `MapDataMapper` | `MapsTable` | [ ] |
| **8** | `L1Quest` | `QuestMapper` | `QuestTable` | [ ] |
| **9** | `L1ShopS` | `ShopMapper` | `ShopTable` | [ ] |
| **10** | `L1Pet` | `PetMapper` | `PetTable` | [ ] |
| **11** | `L1Event` | `EventMapper` | `EventTable` | [ ] |
| **12** | `L1Board` | `BoardMapper` | `BoardTable` | [ ] |
| **13** | `L1Castle` | `CastleMapper` | `CastleTable` | [ ] |
| **14** | `L1House` | `HouseMapper` | `HouseTable` | [ ] |
| **15** | `L1Town` | `TownMapper` | `TownTable` | [ ] |
| **16** | `L1Mail` | `MailMapper` | `MailTable` | [ ] |
| **17** | `L1Bank` | `BankMapper` | `AccountBankTable` | [ ] |
| **18** | `L1Drop` | `DropMapper` | `DropTable` | [ ] |
| **19** | `L1Trap` | `TrapMapper` | `TrapTable` | [ ] |
| **20** | `L1Doll` | `DollMapper` | `DollPowerTable` | [ ] |

## 3. 實作規範 (Convention)
1. **目錄位置**：所有 Mapper 置於 `com.lineage.server.datatables.mappers` 套件下。
2. **介面遵循**：必須實作 `RowMapper<T>` 介面。
3. **無狀態性**：Mapper 必須是無狀態的（Stateless），建議使用單例模式或靜態工廠。
4. **欄位寬容度**：針對非核心欄位，應進行 null 檢查或 try-catch，以相容不同版本的 SQL Schema。

## 4. 進行中的實作紀錄
- [x] **基礎建設**：建立 `RowMapper<T>` 介面。
- [ ] **NpcMapper實作**：開發中。
- [ ] **ItemMapper實作**：待辦。
