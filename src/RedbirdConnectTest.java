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
    public Tag t1, t2;
    public Group g1, g2;
    public List<Tag> tags;
    public List<Group> groups;

    @Before
    public void init() {
        t1 = new Tag(0, "name", "description");
        t2 = new Tag(1,"name2","desc2");
        g1 = new Group(0, "name", "description");
        g2 = new Group(1, "name2", "description2"); 

        tags = Arrays.asList(t1,t2);
        groups = Arrays.asList(g1,g2);


        List<Tag> xavierTags = Arrays.asList(
            new Tag("IT"),
            new Tag("OffCampus")
        );
        List<Group> xavierGroups = Arrays.asList(
            new Group("IT326"),
            new Group("IT355")
        );
        List<Post> xavierPosts = new ArrayList<>();

        Student testStudent = new Student(
            0,
            "xzamora@ilstu.edu",
            "Xavier Zamora",
            "Senior",
            xavierTags,
            xavierGroups,
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
