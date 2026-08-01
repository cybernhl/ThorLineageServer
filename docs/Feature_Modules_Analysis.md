# 自訂功能模組分析 (Add-on Features)

本專案除了標準的 Lineage 模擬器邏輯外，還包含了多個自訂功能包，這些包通常用於實現私服特有的玩法或修正。

## 1. `com.add` - 額外小遊戲模組
- **CustomBaccarat.java**: 百家樂遊戲邏輯。
- **CustomTaiwanMahjong.java**: 台灣麻將遊戲邏輯。
- **CustomTaiwanMahjongLog.java**: 麻將遊戲紀錄，用於防作弊或審計。

## 2. `com.custom` - 核心功能增強
- **Ability 系統**:
    - `CustomArmorAbility.java`: 自訂防具特殊能力。
    - `CustomWeaponAbility.java`: 自訂武器特殊能力。
    - `AbilityData.java`: 能力數值載體。
- **Stat 工廠**:
    - `BlessStatFactory.java`: 祝福屬性工廠。
    - `ClanSpecialStatFactory.java`: 血盟特殊屬性加成。
- **管理與工具**:
    - `LookPlayerInstance.java`: 可能用於檢視玩家資訊的功能。
    - `WeaponAttributes.java`: 武器屬性擴充。
    - `AutoPlayGame.java`: 自動掛機/內掛邏輯。

## 3. `com.william` - William 系列擴充
這是一套常見的 Lineage 私服擴充組件：
- **Enchant 強化系統**:
    - `EnchantOrginal.java`, `EnchantOrginal1.java`: 強化邏輯修正或替代方案。
    - `L1WilliamEnchantOrginal.java`: William 版強化演算法。
- **Drop & Reward 掉落與獎勵**:
    - `Drop_limit.java`: 掉落數量限制。
    - `Reward.java`, `Reward1.java`: 獎勵領取系統。
    - `PayBonus.java`: 儲值/贊助紅利系統。
- **NPC 擴充**:
    - `NpcQuest.java`: 擴充的 NPC 任務腳本引擎。
    - `NpcTalkTable.java`: W 系列自訂對話管理。
- **戰鬥修正**:
    - `PcStrDmg.java`, `PcDexDmg.java`: 根據力量(STR)與敏捷(DEX)修正玩家傷害的公式。

## 4. `custom` (Root Package)
- **ServerHWID.java**: 伺服器硬體指紋鎖。這是伺服器保護的核心，防止未經授權的環境執行伺服器端程序。

## 5. `me.aodamiao` - 貓貓執行緒池
- 提供了一套高效能的執行緒調度方案，取代或輔助 Java 內建的 `ScheduledThreadPoolExecutor`，針對遊戲高併發場景優化。
