module dbConnect {
    requires java.sql;
    exports dbConnect;
    exports dbConnect.models.autogen;
    exports dbConnect.models.enums;
    exports dbConnect.execution;
    exports dbConnect.models.constrain;
    exports dbConnect.models.notnull;
}