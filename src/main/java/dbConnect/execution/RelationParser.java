package dbConnect.execution;

import dbConnect.models.enums.CascadeType;
import dbConnect.models.enums.FetchMethod;
import dbConnect.models.relationship.ForeignKey;
import dbConnect.models.relationship.ManyToOne;
import dbConnect.models.relationship.OneToMany;
import dbConnect.models.relationship.OneToOne;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

/**
 * Handle relationship related query using reflection.
 */
public class RelationParser {
    private final SqlDBQuery sqlDBQuery;
    private final MongoDBQuery mongoDBQuery;
    private final RetrieveParser retrieveParser;
    private final InsertParser insertParser;
    private final FieldReflector fieldReflector;

    /**
     * Constructor of {@link RelationParser}.
     * For noSQL query, see {@link RelationParser#RelationParser(MongoDBQuery)}
     * @param sqlDBQuery an instance of {@link SqlDBQuery#SqlDBQuery(String, String, String)}
     */
    public RelationParser(SqlDBQuery sqlDBQuery) {
        this.sqlDBQuery = sqlDBQuery;
        this.mongoDBQuery = null;
        this.retrieveParser = new RetrieveParser(sqlDBQuery);
        this.insertParser = new InsertParser(sqlDBQuery);
        this.fieldReflector = new FieldReflector(sqlDBQuery);
    }

    /**
     * Constructor of {@link RelationParser}.
     * For SQL query, see {@link RelationParser#RelationParser(SqlDBQuery)}
     * @param mongoDBQuery an instance of {@link MongoDBQuery#MongoDBQuery(String, String)}
     */
    public RelationParser(MongoDBQuery mongoDBQuery) {
        this.mongoDBQuery = mongoDBQuery;
        this.sqlDBQuery = null;
        this.retrieveParser = new RetrieveParser(mongoDBQuery);
        this.insertParser = new InsertParser(mongoDBQuery);
        this.fieldReflector = new FieldReflector(mongoDBQuery);
    }

    /**
     * Load all relationships of a model based on fetch type.
     * @param model an instance of a data model.
     * @param fetchMethod the type of fetch strategy to use.
     * @param <T> type of the data model.
     * @throws IllegalAccessException if failed to access a field.
     * @throws SQLException if SQL operation failed.
     */
    public <T> void loadRelationships(T model, FetchMethod fetchMethod) throws IllegalAccessException,SQLException {
        if (model == null) return;

        Class<?> modelClass = model.getClass();
        Field[] fields = modelClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            // idk why it does not pick up the present check and give a warning, can be ignored.
            if (field.isAnnotationPresent(OneToMany.class)) {
                OneToMany oneToMany = field.getAnnotation(OneToMany.class);

                if (oneToMany.fetch() == fetchMethod) {
                    loadOneToManyRelation(model, field, oneToMany);
                }

                continue;
            }

            if (field.isAnnotationPresent(ManyToOne.class)) {
                ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);

                if (manyToOne.fetch() == fetchMethod) {
                    loadManyToOneRelation(model, field, manyToOne);
                }

                continue;
            }

            if (field.isAnnotationPresent(OneToOne.class)) {
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);

                if (oneToOne.fetch() == fetchMethod) {
                    loadOneToOneRelation(model, field, oneToOne);
                }
            }
        }
    }

    /**
     * Save data of a model and its related models.
     * @param model an instance of a data model.
     * @return true, if successfully saved.
     * @param <T> type of the data model.
     * @throws SQLException if SQL operation failed.
     * @throws IllegalAccessException if failed to access a field.
     */
    public <T> boolean saveRelationships(T model) throws SQLException, IllegalAccessException {
        if (model == null) return false;

        executeCascadeOperations(model, CascadeType.PERSIST);

        return insertParser.insert(model) > 0;
    }

    /**
     * Find and load related Data Model instances into designated Data Model's relationship field.
     * @param model the designated data model.
     * @param foreignKeyName the targeted relationship field.
     * @return List of related model instances.
     * @param <T> type of the designated data model.
     * @param <R> type of the related data model.
     * @throws NoSuchFieldException if the relationship field in the designated data model doesn't exist.
     * @throws SQLException if SQL operation failed.
     * @throws IllegalAccessException if the targeted field is not a relationship field.
     */
    @SuppressWarnings("unchecked")
    public <T, R> List<R> getRelatedRelation(T model, String foreignKeyName) throws NoSuchFieldException, SQLException, IllegalAccessException {
        if (model == null) {
            throw new IllegalArgumentException("Model instance cannot be null");
        }

        if (foreignKeyName == null || foreignKeyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Foreign key name cannot be null or empty");
        }

        Class<?> modelClass = model.getClass();
        Field field = modelClass.getDeclaredField(foreignKeyName);
        field.setAccessible(true);
        Object val;

        if (field.isAnnotationPresent(OneToMany.class)) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            loadOneToManyRelation(model, field, oneToMany);
            val = field.get(model);
            if (val instanceof Collection<?> collection) {
                return new ArrayList<>((Collection<R>) collection);
            }
        }

        if (field.isAnnotationPresent(ManyToOne.class)) {
            ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
            loadManyToOneRelation(model, field, manyToOne);
            val = field.get(model);
            if (val != null) return List.of((R) val);
        }

        if (field.isAnnotationPresent(OneToOne.class)) {
            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            loadOneToOneRelation(model, field, oneToOne);
            val = field.get(model);
            if (val != null) return List.of((R) val);
        }

        throw new IllegalArgumentException("Field '" + foreignKeyName + "' is missing relationship annotation");
    }

    private <T> void loadOneToManyRelation(T model, Field field, OneToMany oneToMany) throws IllegalAccessException, SQLException {
        Object primaryKeyVal = getPrimaryKeyValue(model);

        if (primaryKeyVal == null) return;

        Class<?> targetModelClass = oneToMany.targetModel();
        String mappedBy = oneToMany.mappedBy();

        String condition = buildCondition(mappedBy);

        List<?> relatedModels = retrieveParser.retrieve(targetModelClass, condition, primaryKeyVal);

        if (Collection.class.isAssignableFrom(field.getType())) {
            Collection<Object> collection = newCollectionInstance(field.getType());
            collection.addAll(relatedModels);
            field.set(model, collection);
        }
    }

    private <T> void loadManyToOneRelation(T model, Field field, ManyToOne manyToOne) throws IllegalAccessException, SQLException {
        ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

        if (foreignKey == null) {
            throw new IllegalArgumentException("Missing @ForeignKey annotation on '" + field.getName() + "' field of '" + model.getClass() + "' class");
        }

        Object foreignKeyVal = getForeignKeyVal(model, foreignKey.column());

        if (foreignKeyVal == null) return;

        Class<?> targetModel = manyToOne.targetModel();
        String referredColumn;

        if (foreignKey.referencedColumnName().isEmpty()) {
            referredColumn = getPrimaryKeyField(targetModel);
        } else {
            referredColumn = foreignKey.referencedColumnName();
        }

        String condition = buildCondition(referredColumn);
        List<?> relatedModels = retrieveParser.retrieve(targetModel, condition, foreignKeyVal);

        if (!relatedModels.isEmpty()) {
            field.set(model, relatedModels.getFirst());
        }
    }

    private <T> void loadOneToOneRelation(T model, Field field, OneToOne oneToOne) throws IllegalAccessException, SQLException {
        // Bidirectional
        if (!oneToOne.mappedBy().isEmpty()) {
            Object primaryKeyVal = getPrimaryKeyValue(model);
            if (primaryKeyVal == null) return;

            Class<?> targetModel = oneToOne.targetModel();
            String condition = buildCondition(oneToOne.mappedBy());
            List<?> relatedModels = retrieveParser.retrieve(targetModel, condition, primaryKeyVal);

            if (!relatedModels.isEmpty()) {
                field.set(model, relatedModels.getFirst());
            }

            return;
        }

        // Undirectional
        ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
        if (foreignKey == null) {
            throw new IllegalArgumentException("Missing @ForeignKey annotation on '" + field.getName() + "' field of '" + model.getClass() + "' class");
        }

        Object foreignKeyVal = getForeignKeyVal(model, foreignKey.column());

        if (foreignKeyVal == null) return;

        Class<?> targetModel = oneToOne.targetModel();
        String referredColumn;

        if (foreignKey.referencedColumnName().isEmpty()) {
            referredColumn = getPrimaryKeyField(targetModel);
        } else {
            referredColumn = foreignKey.referencedColumnName();
        }

        String condition = buildCondition(referredColumn);
        List<?> relatedModels = retrieveParser.retrieve(targetModel, condition, foreignKeyVal);

        if (!relatedModels.isEmpty()) {
            field.set(model, relatedModels.getFirst());
        }
    }

    private Object getPrimaryKeyValue(Object model) throws IllegalAccessException {
        return fieldReflector.getPrimaryKeyValue(model);
    }

    private String getPrimaryKeyField(Class<?> modelClass) {
        return fieldReflector.getPrimaryKeyName(modelClass);
    }

    private Object getForeignKeyVal(Object model, String fieldName) throws IllegalAccessException {
        Class<?> modelClass = model.getClass();

        for (Field field : modelClass.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field.get(model);
            }
        }

        return null;
    }

    private Collection<Object> newCollectionInstance(Class<?> collectionType) {
        if (List.class.isAssignableFrom(collectionType)) {
            return new ArrayList<>();
        } else if (Set.class.isAssignableFrom(collectionType)) {
            return new HashSet<>();
        }

        return new ArrayList<>();
    }

    private String buildCondition(String columnName) {
        if (mongoDBQuery != null) return columnName + " : ?";

        if (sqlDBQuery != null) return columnName + " = ?";

        return "";
    }

    private void executeCascadeOperations(Object model, CascadeType cascadeType) throws IllegalAccessException, SQLException {
        Class<?> modelClass = model.getClass();
        Field[] fields = modelClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(OneToMany.class)) {
                OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                if (shouldCascade(oneToMany.cascade(), cascadeType)) {
                    Object val = field.get(model);
                    if (val instanceof Collection<?> collection) {
                        for (Object item : collection) {
                            insertParser.insert(item);
                        }
                    }
                }

                continue;
            }

            if (field.isAnnotationPresent(ManyToOne.class)) {
                ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                if (shouldCascade(manyToOne.cascade(), cascadeType)) {
                    Object relatedModel = field.get(model);
                    if (relatedModel != null) insertParser.insert(relatedModel);
                }

                continue;
            }

            if (field.isAnnotationPresent(OneToOne.class)) {
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                if (shouldCascade(oneToOne.cascade(), cascadeType)) {
                    Object relatedModel = field.get(model);
                    if (relatedModel != null) insertParser.insert(relatedModel);
                }
            }
        }
    }

    private boolean shouldCascade(CascadeType[] cascadeTypes, CascadeType targetType) {
        for (CascadeType type : cascadeTypes) {
            if (type == CascadeType.ALL || type == targetType) return true;
        }

        return false;
    }
}
