package com.lineage;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigSQL;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 資料庫連接設置管理
 */
public class DatabaseFactory {
    private static final Log _log = LogFactory.getLog(DatabaseFactory.class);
    // 連接池
    private ComboPooledDataSource _source;
    // 驅動程式
    private static String _driver;
    // 資料庫位置
    private static String _url;
    // 使用者名稱
    private static String _user;
    // 使用者密碼
    private static String _password;

    /**
     * 靜態初始化器，由JVM來保證線程安全.
     */
    private static class Holder {
        static DatabaseFactory instance = new DatabaseFactory();
    }

    /**
     * 取得該類的實例.
     */
    public static DatabaseFactory get() {
        return Holder.instance;
    }

    /**
     * 設置資料載入
     * 
     * @throws SQLException
     */
    private DatabaseFactory() {
        try {
            this._source = new ComboPooledDataSource();
            this._source.setDriverClass(_driver);
            this._source.setJdbcUrl(_url);
            this._source.setUser(_user);
            this._source.setPassword(_password);

            this._source.getConnection().close();

        } catch (final SQLException e) {
            _log.fatal("資料庫讀取錯誤!", e);

        } catch (final Exception e) {
            _log.fatal("資料庫讀取錯誤!", e);

        }
    }

    /**
     * 初始化設置
     */
    public static void setDatabaseSettings() {
        _driver = ConfigSQL.DB_DRIVER;
        _url = ConfigSQL.DB_URL1 + ConfigSQL.DB_URL2 + ConfigSQL.DB_URL3;
        _user = ConfigSQL.DB_LOGIN;
        _password = ConfigSQL.DB_PASSWORD;
    }

    /**
     * 資料庫連線關閉
     */
    public void shutdown() {
        try {
            this._source.close();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            this._source = null;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 傳回資料庫連接.
     * 
     * @return Connection
     */
    public Connection getConnection() {
        Connection con = null;

        while (con == null) {
            try {
                con = this._source.getConnection();
            } catch (final SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
        return con;
    }
}