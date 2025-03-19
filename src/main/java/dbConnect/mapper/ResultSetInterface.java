package dbConnect.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An interface that handles mapping of {@link ResultSet}.
 * Please use {@link SQLMapper} instead.
 * @param <T> a {@code DataModel} Object that required parsing.
 */

public interface ResultSetInterface<T>{
    T map(ResultSet resultSet) throws SQLException;
}
