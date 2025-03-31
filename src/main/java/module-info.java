module dbConnect {
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires com.google.gson;
    requires java.sql;
    exports dbConnect;
    exports dbConnect.mapper;
    exports dbConnect.models.autogen;
    exports dbConnect.models.constrain;
    exports dbConnect.models.enums;
    exports dbConnect.models.notnull;
}