import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.*;

public class RedbirdConnectTest {

    private final DatabaseHandler dbHandler;

    @Before
    public void init() {
        Tag t1 = new Tag(0, "name", "description");
        Tag t2;
        Group g1 = new Group(0, "name", "description");
        Group g2;

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
        if (dbHandler.addStudent(testStudent, "password")) {
            return true;
        } else {
            return false;
        }
    }
}
