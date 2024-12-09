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

    // this passes when false but shouldnt 
    @Test
    public void loginSuccess() {
        assertFalse(studentController.loginStudent(1));
    }

    // pass on failed login attempt
    @Test
    public void loginFail() {

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
