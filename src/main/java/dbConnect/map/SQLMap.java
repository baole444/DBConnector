package dbConnect.map;

import dbConnect.mapper.ResultSetInterface;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An implementation of {@link DataMap} for SQL mapping using {@link ResultSet}
 * @param <T> a {@code DataModel} Object that required parsing.
 * @see ResultSetInterface
 */
public class SQLMap<T> implements DataMap<T, ResultSet> {
    private final ResultSetInterface<T> mapper;

    public SQLMap(ResultSetInterface<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T map(ResultSet resultSet) throws SQLException {
        return mapper.map(resultSet);
    }
}
