import javax.swing.SwingUtilities;

public class Application {
    public static void main(String[] args) {
        System.out.println("Starting application...");

        // Use DatabaseFactory to get the singleton instance
        Database database = DatabaseFactory.getDatabase(DatabaseFactory.DatabaseType.MYSQL, "StudentDB");
        DatabaseHandler dbHandler = new DatabaseHandler(database);
        dbHandler.connect();

        // Initialize handlers
        StudentHandler studentHandler = StudentHandler.getInstance();
        GroupHandler groupHandler = new GroupHandler(dbHandler);

        // Initialize controller
        final StudentController studentController = new StudentController(studentHandler, groupHandler,
                (MySQLHandler) database);

        // Start GUI
        SwingUtilities.invokeLater(() -> new MainGUI(studentController, dbHandler));

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("shutdown disconnect");
            dbHandler.disconnect();
        }));
    }
}