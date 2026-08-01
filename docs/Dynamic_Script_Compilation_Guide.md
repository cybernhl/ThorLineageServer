# 動態腳本編譯與現代化技術指南

動態腳本編譯（Dynamic Script Compilation）是 Lineage 模擬器常見的核心功能，允許管理員在伺服器運行期間修改 `.java` 檔案並立即生效（Hot-swap），而不需重新啟動整個服務。

## 1. 核心原理
1. **讀取原始碼**：監視指定目錄（如 `./scripts`）下的 `.java` 檔案。
2. **呼叫編譯器**：使用 Java Compiler API 將原始碼編譯為 `.class` 字節碼。
3. **動態載入**：透過自訂的 `ClassLoader` 將編譯後的類別載入 JVM 並實例化。

## 2. 實作方案演進

### A. 傳統方案 (Java 1.5 - 1.8)
- **依賴**：`tools.jar` 或 `javac.jar`。
- **做法**：直接調用 `com.sun.tools.javac.Main`。
- **缺點**：強烈依賴特定 JDK 檔案，且在沒有 JDK 的 JRE 環境中會失敗。

### B. 現代化 JDK 標準方案 (Java 8+)
- **API**：`javax.tools.JavaCompiler`。
- **寫法**：
  ```java
  JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
  // 使用 StandardJavaFileManager 進行編譯
  ```
- **優點**：標準 JDK API，不需額外引入 JAR。

### C. Maven 依賴方案 (推薦用於跨環境)
若要確保在任何環境（甚至只有 JRE）都能編譯，建議引入 **ECJ (Eclipse Compiler for Java)**。
- **Maven 座標**：`org.eclipse.jdt:ecj:3.26.0` (或更新版本)
- **優點**：完全獨立，不依賴環境中的 JDK，且編譯速度極快。

## 3. 跨版本升級挑戰 (Java 11 / 17 / 22)

當專案從 Java 1.8 遷移至更高版本時，動態編譯會面臨以下重大變化：

### 1. 模組化系統 (JPMS / Java 9+)
- **限制**：Java 9 以後，`tools.jar` 已不復存在。原本私有的 `com.sun.tools.javac` 包被封裝在 `jdk.compiler` 模組中。
- **對策**：
    - 若需繼續使用，啟動時需加入參數：`--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED`。
    - 建議改用 `javax.tools` 介面以獲得更好的相容性。

### 2. 記憶體中編譯 (In-Memory Compilation)
在現代架構中，建議不再將 `.class` 寫入硬碟，而是直接在記憶體中生成字節碼。這可以透過實作 `SimpleJavaFileObject` 與 `ForwardingJavaFileManager` 來達成，能顯著提升腳本載入速度。

### 3. 安全管理器 (Security Manager) 的棄用
- **變化**：Java 17 標記棄用，Java 21+ 可能進一步限制。
- **影響**：Lineage 伺服器傳統上使用 `Security Manager` 來限制腳本的行為（如禁止執行 `System.exit()`）。
- **對策**：未來需改用容器化（Docker）或更現代的沙箱技術來控制腳本權限。

### 4. ClassLoader 的結構變化
- **挑戰**：Java 11+ 的 ClassLoader 層級結構與 1.8 不同（例如 `AppClassLoader` 不再繼承 `URLClassLoader`）。
- **對策**：動態加載腳本的 `ClassLoader` 需重新設計，以確保能正確訪問到 `GameServer` 核心模組中的類別。

## 4. 總結建議
1. **短期 (Java 1.8)**：移除本地 `:javac` 子專案，改為在 `build.gradle` 中引入 `ecj` 或直接使用 JDK 的 `JavaCompiler`。
2. **中期 (遷移至 11/17)**：全面拋棄 `com.sun.tools.javac` 私有 API，改用 `javax.tools` 標準介面。
3. **長期 (2026+ 技術債清理)**：考慮引入像是 `Groovy` 或 `Kotlin Scripting` 作為腳本引擎，它們天生具備更強大的動態編譯與載入能力，能更好地適配 Java 22+ 的環境。
