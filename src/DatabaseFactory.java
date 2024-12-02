public class DatabaseFactory {

    // Enum to define supported database types
    public enum DatabaseType {
        MYSQL,
        POSTGRESQL // Extendable to other database types in the future
    }

    // Singleton instance of MySQLHandler
    private static MySQLHandler mysqlHandlerInstance;

    // Method to create a database instance
    public static Database getDatabase(DatabaseType type, String dbName) {
        if (!Validator.isValidString(dbName)) {
            throw new IllegalArgumentException("Invalid database name.");
        }

        switch (type) {
            case MYSQL:
                if (mysqlHandlerInstance == null) {
                    mysqlHandlerInstance = new MySQLHandler(dbName);
                }
                return mysqlHandlerInstance;
            // Extend here for other database types
            case POSTGRESQL:
                throw new UnsupportedOperationException("PostgreSQLHandler is not implemented yet.");
            default:
                throw new IllegalArgumentException("Unsupported database type.");
        }
    }
}
