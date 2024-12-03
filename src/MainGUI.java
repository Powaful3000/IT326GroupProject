import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    private final StudentController studentController;
    private final DatabaseHandler dbHandler;
    private final UIManager uiManager;

    public MainGUI(StudentController studentController, DatabaseHandler dbHandler) {
        this.studentController = studentController;
        this.dbHandler = dbHandler;
        this.uiManager = new UIManager(this, dbHandler, StudentHandler.getInstance(),
                new PostHandler(), new TagHandler(), studentController,
                new GroupHandler(dbHandler));
        setupFrame();
    }

    private void setupFrame() {
        setTitle("Redbird Connect");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        add(uiManager.getMainPanel());
        setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("Starting application...");
        Database database = DatabaseFactory.getDatabase(DatabaseFactory.DatabaseType.MYSQL, "StudentDB");
        DatabaseHandler dbHandler = new DatabaseHandler(database);
        dbHandler.connect();

        StudentHandler studentHandler = StudentHandler.getInstance();
        GroupHandler groupHandler = new GroupHandler(dbHandler);
        final StudentController studentController = new StudentController(studentHandler, groupHandler,
                (MySQLHandler) dbHandler.getDatabase());

        SwingUtilities.invokeLater(() -> new MainGUI(studentController, dbHandler));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("shutdown disconnect");
            dbHandler.disconnect();
        }));
    }
}
