package com.simplebank.accounts.report;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class IndexKeepingRowMapper<K,V> implements RowMapper<V> {
    private Map<K, V> index;

    abstract V doMapRow(ResultSet rs, int rowNum) throws SQLException;
    abstract K getIndexValue(V row);

    public IndexKeepingRowMapper() {
        index = new HashMap<>();
    }

    @Override
    public V mapRow(ResultSet rs, int rowNum) throws SQLException {
        V row = doMapRow(rs, rowNum);
        index.put(getIndexValue(row), row);
        return row;
    }

    V getFromIndex(K id) {
        return index.get(id);
    }
}
