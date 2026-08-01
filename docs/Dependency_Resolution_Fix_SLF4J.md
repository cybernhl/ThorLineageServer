# Gradle 依賴解析錯誤修復報告：SLF4J POM 毀損問題

## 1. 問題現象 (Symptoms)
在執行 Gradle Sync 或建置時，出現以下錯誤訊息：
```text
[Fatal Error] slf4j-simple-1.7.36.pom:2:1: 宣告集中不允許內容。
[Fatal Error] slf4j-api-1.7.36.pom:2:1: 宣告集中不允許內容。
> Could not resolve org.slf4j:slf4j-api:1.7.36
> Could not resolve org.slf4j:slf4j-simple:1.7.36
```

## 2. 根本原因 (Root Cause)
錯誤訊息 `宣告集中不允許內容` (Content not allowed in prolog) 是 XML 解析器拋出的經典錯誤。這通常代表 Gradle 下載到的 `.pom` 檔案（XML 格式）內容損毀，常見原因包括：
1. **編碼問題**：檔案開頭包含了隱藏的 **BOM (Byte Order Mark)** 字元。
2. **網路截斷/代理錯誤**：檔案下載不完整，或者被公司防火牆/代理伺服器攔截，導致 `.pom` 檔案內容實際上是 HTML 登入頁面或錯誤訊息頁面。
3. **倉庫同步異常**：遠端鏡像倉庫（如阿里雲等）在同步時產生了毀損的快取檔案。

## 3. 解決方法 (Solutions)

### 核心思路
強制 Gradle 忽略毀損的元數據檔案 (`.pom`)，直接尋找並下載二進位檔案 (`.jar`)。

### 方案 A：在 build.gradle (Groovy) 中設定
修改 `repositories` 區塊，加入 `metadataSources` 設定：

```groovy
repositories {
    mavenCentral {
        metadataSources {
            artifact() // 強制只尋找 JAR 檔案，忽略損毀的 POM 檔案
        }
    }
}

dependencies {
    // 使用 @jar 綴詞明確指定格式
    implementation ("org.slf4j:slf4j-api:1.7.36@jar")
    implementation ("org.slf4j:slf4j-simple:1.7.36@jar")
}
```

### 方案 B：在 build.gradle.kts (Kotlin DSL) 中設定
如果之後切換到 KTS，寫法如下：

```kotlin
repositories {
    mavenCentral {
        metadataSources {
            artifact() // 關鍵：直接獲取二進位檔
        }
    }
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.36@jar")
    implementation("org.slf4j:slf4j-simple:1.7.36@jar")
}
```

## 4. 進階處理 (如果仍有問題)
如果上述設定後依然失敗，代表本地快取已徹底毀損，請執行以下步驟：
1. **刪除本地損毀快取**：
   手動刪除 `E:\Users\Neo\.gradle\caches\modules-2\files-2.1\org.slf4j\` 下的目錄。
2. **重新同步**：
   在 Android Studio 中點擊 `File` -> `Invalidate Caches...` -> 勾選 `Clear file system cache and Local History` -> `Restart`。

---
**文件日期**：2026-08-01
**專案名稱**：ThorLineageServer
**模組**：GameServer
