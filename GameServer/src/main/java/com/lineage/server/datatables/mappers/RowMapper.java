package com.lineage.server.datatables.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 通用資料列轉換介面
 * @param <T> 目標 POJO 類型
 */
public interface RowMapper<T> {
    /**
     * 將 ResultSet 的目前列轉換為 Java 物件
     * @param rs 結果集
     * @return 封裝後的物件
     * @throws SQLException
     */
    T mapRow(ResultSet rs) throws SQLException;
}
