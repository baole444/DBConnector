package dbConnect.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An implementation of {@link DataMapper} for SQL mapping using {@link ResultSet}
 * @param <T> a {@code DataModel} Object that required parsing.
 * @see ResultSetInterface
 */
public class SQLMapper<T> implements DataMapper<T, ResultSet> {
    private final ResultSetInterface<T> mapper;

    public SQLMapper(ResultSetInterface<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T map(ResultSet resultSet) throws SQLException {
        return mapper.map(resultSet);
    }
}
