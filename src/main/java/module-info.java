module dbConnect {
    requires java.sql;
    requires org.mongodb.bson;
    exports dbConnect;
    exports dbConnect.models.autogen;
    exports dbConnect.models.enums;
    exports dbConnect.execution;
    exports dbConnect.models.constrain;
    exports dbConnect.models.notnull;
    exports dbConnect.query;
    exports dbConnect.mapper;
}