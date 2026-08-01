# JavaFX 相依性分析報告

本專案在從本地 JAR 遷移至 Maven 過程中，發現對 JavaFX 仍有少量相依。經全面掃描原始碼，目前的相依點僅集中於一個特定類別。

## 1. 相依類別
- **`javafx.util.Pair`**
    - 用途：簡單的鍵值對（Key-Value Pair）容器。
    - 影響：雖然只是一個工具類別，但它屬於 JavaFX 模組。在 Oracle JDK 8 中通常內建，但在現代的 OpenJDK 8 或更高版本（Java 11+）中，JavaFX 已被分離，必須額外安裝 OpenJFX 才能編譯與執行。

## 2. 影響檔案列表 (共 4 檔)

| 檔案路徑 | 主要用途 |
| :--- | :--- |
| `com.lineage.data.npc.shop.CustomPlayerShopByNpc.java` | 處理掛賣商店的道具清單（`List<Pair<Integer, Integer>>`）。 |
| `com.lineage.server.clientpackets.C_Result.java` | 解析客戶端傳入的購買請求，暫存選取道具。 |
| `com.lineage.data.npc.custom.Clan_Level.java` | 處理血盟升級所需的道具與數量需求。 |
| `com.custom.clan.ClanStatData.java` | 儲存血盟各等級屬性與升級材料數據。 |

## 3. 現狀問題
- **編譯風險**：如果開發環境使用 OpenJDK 8 且未配置 OpenJFX，編譯會報錯。
- **運行風險**：若伺服器部署環境缺乏 JavaFX 庫，啟動或執行到相關功能時會拋出 `NoClassDefFoundError`。
- **Maven 化阻礙**：目前 Maven 中央倉庫 of JavaFX (OpenJFX) 主要針對 Java 11+。雖然有 8u 版本，但配置較為複雜，且為了這一個簡單的 `Pair` 類別而引入整個 JavaFX 庫並不划算。

## 4. 詳細對比分析：`javafx.util.Pair` vs `AbstractMap.SimpleImmutableEntry`

如果使用 `java.util.AbstractMap.SimpleImmutableEntry` 來仿造 `javafx.util.Pair`，效能幾乎完全一樣，操作方法也高度相容。

### 1. 效能對比 (Performance)
- **記憶體佔用**：兩者都是物件，內部都只持有兩個引用（Key 和 Value）。在 Java 堆積記憶體中，它們的結構是相同的（物件頭 + 2個欄位引用），效能開銷無異。
- **運算速度**：`hashCode()`、`equals()` 和 `toString()` 的實作邏輯幾乎一模一樣，都是對兩個欄位進行操作。在編譯器的優化下，執行速度沒有差別。

### 2. 操作方法對比 (API Compatibility)
兩者的 API 是高度一致的：

| 特性 | `javafx.util.Pair` | `java.util.AbstractMap.SimpleImmutableEntry` |
| :--- | :--- | :--- |
| **獲取 Key** | `getKey()` | `getKey()` |
| **獲取 Value** | `getValue()` | `getValue()` |
| **不可變性** | 是 (Immutable) | 是 (Immutable) |
| **序列化** | 支援 `Serializable` | 支援 `Serializable` |
| **介面實作** | 無 | 實作了 `Map.Entry<K, V>` |

最大的不同點在於 `SimpleImmutableEntry` 實作了 `Map.Entry` 介面，這意味著它甚至比原本的 `Pair` 更有用，可以直接與 JDK 的集合框架 (Collections) 進行互動。

## 5. Java 後續版本 (11/17/22) 的標準替代方案

### 1. Java Records (Java 14 引入，16 正式化) —— 最推薦
這是 Java 17 和 22 中最正統的寫法。Record 是一種特殊的類別，專門用來當作「純數據載體」。
- **優點**：程式碼極簡，自動生成 `equals()`, `hashCode()`, `toString()`。
- **範例**：
  ```java
  public record L1Pair<K, V>(K key, V value) {}
  
  // 使用方式
  L1Pair<Integer, Integer> pair = new L1Pair<>(40308, 100);
  int id = pair.key();    // 注意：Record 使用 key() 而非 getKey()
  ```

### 2. `Map.entry()` 工廠方法 (Java 9 引入)
- **範例**：
  ```java
  import java.util.Map;
  Map.Entry<Integer, Integer> pair = Map.entry(40308, 100);
  ```
- **缺點**：不允許 `null` 鍵或 `null` 值。

### 3. `AbstractMap.SimpleImmutableEntry` (Java 1.6 就有)
這是 JDK 中最老牌的替代品，在 Java 11/17/22 依然可用。
- **範例**：
  ```java
  import java.util.AbstractMap;
  AbstractMap.SimpleImmutableEntry<Integer, Integer> pair = 
      new AbstractMap.SimpleImmutableEntry<>(40308, 100);
  ```
- **缺點**：類別名稱很長，語法較為笨重。

## 6. 建議處理方案 (預備調整)

**方案 A：建立 Shim Class (仿冒 Package 法)**
- 在 `src/main/java` 下建立 `javafx.util.Pair`，繼承 `java.util.AbstractMap.SimpleImmutableEntry`。
- **優點**：
    - **零改動量**：全專案 `import` 不需修改。
    - **完全解耦**：不再依賴外部 JavaFX 庫。
    - **向前相容**：相容於 Java 1.8 至未來所有版本。

## 7. 決策請求
目前採用 **方案 A**。建立仿製類別後，即可移除 `build.gradle` 中的 `:javafx` 專案相依。
