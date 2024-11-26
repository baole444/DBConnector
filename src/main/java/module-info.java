module dbConnect {
    requires java.sql;
    exports dbConnect;
    exports dbConnect.models;
    exports dbConnect.models.autogen;
    exports dbConnect.models.enums;
    exports dbConnect.execution;
}