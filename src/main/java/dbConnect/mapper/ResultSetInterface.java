package dbConnect.mapper;

import dbConnect.map.SQLMap;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An interface that handles mapping of {@link ResultSet}.
 * @param <T> a {@code DataModel} Object that required parsing.
 */

public interface ResultSetInterface<T>{
    T map(ResultSet resultSet) throws SQLException;
}
