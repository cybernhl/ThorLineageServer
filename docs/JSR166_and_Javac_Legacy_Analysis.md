# JSR166 與 Javac 遺留組件分析報告

本專案在重構過程中，對 `:jsr` 與 `:javac` 兩個本地子專案進行了評估。這兩個組件均為 Java 早期版本（1.5/1.6）開發環境下的產物。

## 1. `:jsr` (jsr166.jar) - 併發工具相容庫

### 背景
JSR 166 是 Java 併發公用程式（`java.util.concurrent`）的規範。在 Java 5/6 時代，許多先進的併發功能（如 `Deque`, `NavigableMap`, `ForkJoinPool`）尚未完全成熟或存在於標準庫中。`jsr166.jar` 作為 Backport 庫，讓舊版 Java 能使用這些功能。

### 現狀與評估
- **冗餘性**：本專案已升級至 **Java 1.8**。Java 8 的標準庫（JDK）已完整包含並優化了 JSR 166 的所有功能。
- **原始碼掃描**：掃描結果顯示專案已直接引用 `java.util.concurrent`，未發現對 `edu.emory.mathcs.backport` 等舊路徑的引用。
- **結論**：該組件已完全冗餘。

### 建議操作
- 繼續維持 `build.gradle` 中的註解狀態。
- 確認伺服器長時間運行穩定後，可從 `settings.gradle.kts` 中移除 `include(":jsr")` 並刪除實體檔案。

---

## 2. `:javac` (javac.jar) - 編譯期工具

### 背景
此 JAR 通常是從 JDK 的 `tools.jar` 中提取的 `com.sun.tools.javac` 核心。
- **用途一：動態編譯**：允許伺服器在不重啟的情況下編譯 `./scripts` 或 `./data` 中的 `.java` 原始碼。
- **用途二：環境獨立**：為了讓伺服器能在僅安裝 JRE（缺少 JDK）的機器上執行編譯功能。

### 現狀與評估
- **靜態編譯**：目前的 Gradle 構建流程不依賴此 JAR。
- **運行時依賴**：若伺服器具備 `reload` 腳本的功能，移除此 JAR 可能導致運行時錯誤。
- **結論**：它是功能性組件，而非構建期依賴。

### 建議操作
- **方案 A (推薦)**：將 `javac.jar` 移至 `GameServer/libs` 並以 `compileOnly` 或動態加載方式處理，移除單獨的 `:javac` 子專案以簡化目錄結構。
- **方案 B (現代化)**：參考《動態腳本編譯指南》，改用 JDK 內建的 `JavaCompiler` API。

## 3. 總結
這兩個子專案的存在是為了相容極舊的開發環境。在 Gradle 管理的 Java 1.8 專案中，應盡可能將其轉換為標準的 Maven 依賴或直接利用 JDK 內建功能，以降低維護成本。
