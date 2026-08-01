# 依賴項遷移與管理說明

本專案正處於從「本地 JAR 封裝專案」遷移至「Maven 遠端依賴」的過程。

## 1. 遷移背景
原始專案使用大量本地 `*.jar` 檔案，並透過子專案（如 `:c3p0`, `:mina_core`）重新封裝。這種方式雖然穩定，但難以維護版本且佔用原始碼倉庫空間。

## 2. 當前遷移狀態 (GameServer/build.gradle)

目前 `GameServer` 已嘗試切換以下依賴至 Maven：

| 庫名稱 | Maven 座標 | 備註 |
| :--- | :--- | :--- |
| **C3P0** | `com.mchange:c3p0:0.9.2.1` | 原本地版本為 0.9.1.2 |
| **Commons Logging** | `commons-logging:1.1.1` | |
| **Fastjson** | `com.alibaba:fastjson:1.2.76` | |
| **JNA** | `net.java.dev.jna:jna-jpms:5.8.0` | 包含 platform 部分 |
| **Log4j** | `log4j:1.2.15` | **需排除缺失項**: `jms`, `jmxtools`, `jmxri` |
| **Mina Core** | `org.apache.mina:mina-core:2.2.2` | |
| **MySQL Connector** | `mysql:mysql-connector-java:5.1.15` | |
| **Netty** | `io.netty:netty:3.6.2.Final` | |
| **OSHI** | `com.github.oshi:oshi-core:5.7.5` | |
| **SLF4J** | `org.slf4j:slf4j-api:1.7.36@jar` | **特殊處理**: 僅下載 JAR，跳過損壞的 POM |

## 3. 解決中的技術問題

### A. SLF4J POM 損壞問題
- **現象**: SLF4J 1.7.36 的某些 POM 檔案在某些鏡像站點中格式錯誤（如 `slf4j-parent`）。
- **對策**: 
    1. 在 `settings.gradle.kts` 中設定 `metadataSources { artifact() }` 優先讀取 JAR。
    2. 在 `build.gradle` 中對 SLF4J 使用 `@jar` 綴詞，強制 Gradle 忽略 POM 解析。

### B. JavaFX 相依性 (`javafx.util.Pair`)
- **現狀**: 目前使用 `implementation(project(":javafx"))` 本地專案。
- **後續建議**: 若要完全 Maven 化且維持 Java 8，可能需要額外指定 `org.openjfx:javafx-base` 或維持現狀，因為 Java 8 之後 JavaFX 已從某些 OpenJDK 發行版分離。

### C. javax.servlet-api
- **模式**: `compileOnly`。
- **原因**: 伺服器內部可能使用了某些 servlet API 但運行環境（或混淆過程）不需要將其打包。

## 4. 保留的本地子專案
目前仍有部分子專案未完全切換，建議在確認 Maven 版本運行穩定後，逐步移除 `settings.gradle.kts` 中的 `include` 與對應資料夾。

- `:javac` (編譯期工具)
- `:jsr` (Java 5/6 併發包相容性，Java 8 其實已內建，可能可移除)
- `:javafx` (本地 JFX 庫)
