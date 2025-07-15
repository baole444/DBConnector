package dbConnect.execution;

import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.FetchMethod;
import dbConnect.models.relationship.ManyToOne;
import dbConnect.models.relationship.OneToMany;
import dbConnect.models.relationship.OneToOne;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;

import java.lang.reflect.Field;
import java.sql.SQLException;

public class RelationParser {
    private final SqlDBQuery sqlDBQuery;
    private final MongoDBQuery mongoDBQuery;
    private final RetrieveParser retrieveParser;

    public RelationParser(SqlDBQuery sqlDBQuery) {
        this.sqlDBQuery = sqlDBQuery;
        this.mongoDBQuery = null;
        this.retrieveParser = new RetrieveParser(sqlDBQuery);
    }

    public RelationParser(MongoDBQuery mongoDBQuery) {
        this.mongoDBQuery = mongoDBQuery;
        this.sqlDBQuery = null;
        this.retrieveParser = new RetrieveParser(mongoDBQuery);
    }

    public <T> void loadRelationShips(T model, FetchMethod fetchMethod) throws IllegalAccessException,SQLException {
        if (model == null) return;

        Class<?> modelClass = model.getClass();
        Field[] fields = modelClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

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

    private <T> void loadOneToManyRelation(T model, Field field, OneToMany oneToMany) {
    }

    private <T> void loadManyToOneRelation(T model, Field field, ManyToOne manyToOne) {

    }

    private <T> void loadOneToOneRelation(T model, Field field, OneToOne oneToOne) {

    }
}
