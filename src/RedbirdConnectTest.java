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
        t1 = new Tag(0, "IT", "information technology");
        t2 = new Tag(1,"OffCampus","off-campus housing");
        g1 = new Group(0, "IT326", "project class");
        g2 = new Group(1, "IT355", "security"); 

        tags = Arrays.asList(t1,t2);
        groups = Arrays.asList(g1,g2);

        List<Post> xavierPosts = new ArrayList<>();

        Student testStudent = new Student(
            0,
            "xzamora@ilstu.edu",
            "Xavier Zamora",
            "Senior",
            tags,
            groups,
            xavierPosts
        );
    }

    @Test
    public void testingAccountExistsWhenRegistering() {
        assertFalse(studentController.registerStudent("Xavier Zamora", "Senior", 0));
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
