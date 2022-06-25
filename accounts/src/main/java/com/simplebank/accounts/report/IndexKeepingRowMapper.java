package com.simplebank.accounts.report;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends the standard jdbc RowMapper with an in-memory index of the mapped domain objects.
 * Keep in mind that this implementation keeps the created domain objects in memory, so if you need them garbage collected,
 * you need to destroy the instance of this class as well. Also, any modifications to the domain objects will be reflected
 * in the internal Map of this class that stores them.
 * @param <K> The type of the index field
 * @param <V> The type of the data class being mapped.
 */
public abstract class IndexKeepingRowMapper<K,V> implements RowMapper<V> {
    private Map<K, V> index;

    /**
     * Descendants of this class are forced to implement this method instead of mapRow, which in turn calls this method
     * and caters for the indexing behavior.
     * @param rs the result set as it is passed to RowMapper.mapRow
     * @param rowNum the rowNum set as it is passed to RowMapper.mapRow
     * @return The created entity.
     * @throws SQLException in case of improper use or the resultSet
     */
    abstract V doMapRow(ResultSet rs, int rowNum) throws SQLException;

    /**
     * Implementations should provide the means to obtain the index value for each mapped row by implementing this method
     * @param row the mapped domain object as returned by doMapRow
     * @return the value of the id that uniquely identifies this domain object
     */
    abstract K getIndexValue(V row);

    public IndexKeepingRowMapper() {
        index = new HashMap<>();
    }

    @Override
    public final V mapRow(ResultSet rs, int rowNum) throws SQLException {
        V row = doMapRow(rs, rowNum);
        index.put(getIndexValue(row), row);
        return row;
    }

    /**
     * Returns a domain object by its id provided. Returns null if such id is not found
     * @param id the unique identifier of the domain object, as provided by getIndexValue
     * @return The domain object with the given id, or null if not found in the current resultSet
     */
    V getFromIndex(K id) {
        return index.get(id);
    }

    /**
     * Provides a list of the keys of the domain object mapped through this RowMapper.
     * The list is detached from the RowMapper itself, so adding/removing elements to either the returning List or the RowMapper,
     * doesn't affect the other.
     * @return a list of the keys of the domain object mapped through this RowMapper.
     */
    List<K> getIndexes() {
        return new ArrayList<K>(index.keySet());
    }
}
