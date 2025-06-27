package dbConnect.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An interface that handles mapping of {@link ResultSet}.
 * @param <T> a {@code DataModel} Object that required parsing.
 */
public interface ResultSetInterface<T>{
    /**
     * Map the query result to an instance of a data model.
     * @param resultSet instance of a ResultSet
     * @return instance of a Data Model with its attributes from the query result.
     * @throws SQLException when error occurred during mapping between MySQL query result and the DataModel.
     */
    T map(ResultSet resultSet) throws SQLException;
}
