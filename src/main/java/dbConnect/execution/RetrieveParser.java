package dbConnect.execution;

import dbConnect.DBMapper;
import dbConnect.DBQuery;
import dbConnect.models.enums.Table;

import java.sql.SQLException;
import java.util.List;

public class RetrieveParser {
    private final DBQuery dbQuery;

    public RetrieveParser(DBQuery dbQuery) {
        this.dbQuery = dbQuery;
    }

    public <T> List<T> retrieve(Class<T> modelClass, String whereTerm, Object... params) throws IllegalAccessException, SQLException {
        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getTable() method that return a Table enum.");
        }

        DBMapper<T> mapper;

        try {
            mapper = (DBMapper<T>) modelClass.getMethod("getMap").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getMap() method that return a new instant of mapping method.");
        }

        String query = "select * from " + table.getName();
        if (whereTerm != null && !whereTerm.trim().isEmpty()) {
            query += " where " + whereTerm;
        }

        return dbQuery.loadData(query, mapper, params);
    }

    // select all method
    public <T> List<T> retrieveAll(Class<T> modelClass) throws IllegalAccessException, SQLException {
        return retrieve(modelClass, null);

    }
}
