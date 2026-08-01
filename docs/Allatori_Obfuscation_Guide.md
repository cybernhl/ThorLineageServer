# Allatori Java Obfuscator 使用指南

本文檔說明本專案中 `allatori.jar` 的用途、配置方式以及如何執行程式碼混淆。

## 1. 什麼是 Allatori?

Allatori 是一款專業級的 Java 程式碼混淆器（Obfuscator）。它的主要目的是保護智慧財產權，防止他人透過反編譯工具輕易獲取伺服器的原始碼。

### 主要功能：
- **名稱混淆**：將類別、方法和變數名稱改為無意義的字符（如 `a`, `b`, `iii`）。
- **流程混淆**：修改程式碼執行路徑，使反編譯後的邏輯極度難以理解。
- **字串加密**：加密程式中的明文字串（如提示訊息、SQL 語句）。
- **移除調試訊息**：刪除行號、變數名等有助於逆向工程的資訊。

## 2. 核心檔案說明

- **`allatori.jar`** (位於 `GameServer/libs/`): 混淆器的主程式。
- **`allatori/config.xml`**: 混淆配置文件，定義了哪些類別需要保留名稱、加密強度等。

## 3. 專案集成與自動化

我們已經在 Gradle 中集成了混淆流程。主要邏輯位於 `GameServer/build.gradle` 的 `runAllatori` 任務中。

### 混淆流程如下：
1. **生成 Fat JAR**: 執行 `shadowJar` 產生包含所有依賴的 `GameServer-all.jar`。
2. **準備輸入**: 將 `shadowJar` 的產出複製到 `GameServer/in/GameServer.jar`。
3. **執行混淆**: 調用 `allatori.jar` 並根據 `config.xml` 的規則處理檔案。
4. **輸出結果**: 混淆後的檔案會輸出到專案根目錄，並由 Gradle 自動移動到 `build/libs/` 下更名為 `GameServer-*-obfuscated.jar`。

## 4. 配置文件分析 (`allatori/config.xml`)

本專案目前的關鍵配置：

- **保留清單 (`<keep-names>`)**:
    - `com.lineage.Server`: 啟動類別必須保留名稱，否則無法啟動。
    - `com.lineage.server.datatables.*`: 數據表讀取相關類別，通常因為反射或序列化需求需保留。
    - `com.lineage.server.model.*`: 核心模型物件。
- **屬性設置 (`<property>`)**:
    - `string-encryption`: 已啟用 (Strong v4)，這會加密所有字串。
    - `control-flow-obfuscation`: 已啟用。
    - `classes-naming`: 設置為 `iii` (會產生如 `iiiii` 類型的名稱)。

## 5. 如何執行混淆

在終端機執行以下 Gradle 任務：

```bash
./gradlew :GameServer:runAllatori
```

成功執行後，你可以在 `GameServer/build/libs/` 目錄下找到標註為 `obfuscated` 的 JAR 檔案。

## 6. 注意事項

- **過期時間**: 目前 `config.xml` 中設置的 `expiry date` 為 `2025/12/30`。過期後混淆過的程式將無法運行或會彈出警告。
- **反射 (Reflection)**: 如果程式碼中使用了反射來調用類別或方法，必須在 `config.xml` 的 `<keep-names>` 中將其排除，否則混淆後會因為找不到名稱而導致 `ClassNotFoundException` 或 `NoSuchMethodError`。
- **排錯**: 若混淆後的伺服器無法啟動，請檢查 `GameServer/log.xml` (Allatori 產生的日誌) 以分析混淆過程中的變更。
