import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RedbirdConnectTest {

    public DatabaseHandler dbHandler;
    public StudentController studentController;
    public StudentHandler studentHandler;
    public GroupHandler groupHandler;
    public MySQLHandler sqlHandler;
    public Tag t1, t2;
    public Group g1, g2;
    public List<Tag> tags;
    public List<Group> groups;

    @Before
    public void init() {
        Database database = DatabaseFactory.getDatabase(
                DatabaseFactory.DatabaseType.MYSQL,
                "StudentDB");
        this.dbHandler = new DatabaseHandler(database);

        try {
            database.connect();
            System.out.println("Successfully connected to database.");
        } catch (RuntimeException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.err.println("Please check your database configuration and try again.");
            System.exit(1); // Exit if we can't connect to the database
        }

        this.studentHandler = StudentHandler.getInstance();
        this.groupHandler = new GroupHandler(dbHandler);
        this.studentController = new StudentController(studentHandler, groupHandler, sqlHandler);
        t1 = new Tag(1, "IT", "information technology");
        t2 = new Tag(2,"OffCampus","off-campus housing");
        g1 = new Group(1, "IT326", "project class");
        g2 = new Group(2, "IT355", "security"); 

        tags = Arrays.asList(t1,t2);
        groups = Arrays.asList(g1,g2);

        List<Post> xavierPosts = new ArrayList<>();

        Student xavier = new Student(
            1,
            "xzamora@ilstu.edu",
            "Xavier Zamora",
            "Senior",
            tags,
            groups,
            xavierPosts
        );

        studentController.registerStudent("Xavier Zamora", "Senior", 1);
    }

    // passes on existing account attempting to be registered
    @Test
    public void testingAccountExistsWhenRegistering() {
        assertFalse(studentController.registerStudent("Xavier Zamora", "Senior", 1));
    }

    // passes on new account successfully registering
    @Test
    public void testingAccountDoesNotExistWhenRegistering() {
        this.studentHandler = StudentHandler.getInstance();
        this.groupHandler = new GroupHandler(dbHandler);
        this.studentController = new StudentController(studentHandler, groupHandler, sqlHandler);
        t1 = new Tag(1, "IT", "information technology");
        g1 = new Group(1, "IT328", "computation theory");

        tags = Arrays.asList(t1);
        groups = Arrays.asList(g1);

        List<Post> posts = new ArrayList<>();

        Student testStudent = new Student(
            2,
            "bob@ilstu.edu",
            "Bob bob",
            "Junior",
            tags,
            groups,
            posts
        );

        assertTrue(studentController.registerStudent("Bob bob", "Junior", 2));
    }

    // passes on successful login attempt
    @Test
    public void loginSuccess() {
        assertTrue(studentController.loginStudent(1));
    }

    // pass on failed login attempt
    @Test
    public void loginFail() {
        assertFalse(studentController.loginStudent(10));
    }

    @Test
    public void leaveGroupSuccess() {
        List<Post> xavierPosts = new ArrayList<>();

        g1 = new Group(1, "IT326", "project class");
        g2 = new Group(5, "IT348", "machine learning");

        groups = Arrays.asList(g1);

        Student jon = new Student(
            3,
            "Jon@ilstu.edu",
            "Jon Jon",
            "Freshman",
            tags,
            groups,
            xavierPosts
        );

        dbHandler.joinGroup(3, 5);
        assertTrue(dbHandler.leaveGroup(g2, jon));
    }

    @Test
    public void leaveGroupFail() {
        List<Post> xavierPosts = new ArrayList<>();

        g1 = new Group(11, "COM223", "project group");

        groups = Arrays.asList(g1);

        Student bri = new Student(
            4,
            "bri@ilstu.edu",
            "bri",
            "Sophomore",
            tags,
            groups,
            xavierPosts
        );

        assertFalse(dbHandler.leaveGroup(g1, bri));
    }

    // Cleanup for gc
    @After
    public void cleanUp() {
        dbHandler = null;
        studentController = null;
        t1 = null;
        t2 = null;
        g1 = null;
        g2 = null;
        tags = null;
        groups = null;

    }
}
