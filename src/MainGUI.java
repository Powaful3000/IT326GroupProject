import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {
    private final StudentController studentController;
    private final DatabaseHandler dbHandler;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private StudentHandler studentHandler;
    private PostHandler postHandler;
    private TagHandler tagHandler;
    private DialogManager dialogManager;
    private PanelFactory panelFactory;

    public MainGUI(StudentController studentController, DatabaseHandler dbHandler) {
        this.studentController = studentController;
        this.dbHandler = dbHandler;
        initializeComponents();
        setupFrame();
    }

    private void initializeComponents() {
        studentHandler = new StudentHandler();
        postHandler = new PostHandler();
        tagHandler = new TagHandler();
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        dialogManager = new DialogManager(this, dbHandler, studentHandler, postHandler, tagHandler, studentController, null);
        panelFactory = new PanelFactory(studentController, dbHandler, studentHandler, dialogManager, cardLayout, mainPanel, postHandler, tagHandler);
        
        dialogManager.setPanelFactory(panelFactory);
        panelFactory.initializePanels();
    }

    private void setupFrame() {
        setTitle("Redbird Connect");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("Starting application...");
        Database database = DatabaseFactory.getDatabase(DatabaseFactory.DatabaseType.MYSQL, "StudentDB");
        DatabaseHandler dbHandler = new DatabaseHandler(database);
        dbHandler.connect();

        StudentHandler studentHandler = new StudentHandler();
        GroupHandler groupHandler = new GroupHandler(dbHandler);
        final StudentController studentController = new StudentController(studentHandler, groupHandler);

        SwingUtilities.invokeLater(() -> new MainGUI(studentController, dbHandler));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("shutdown disconnect");
            dbHandler.disconnect();
        }));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
