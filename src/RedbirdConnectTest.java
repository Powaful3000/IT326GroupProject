import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedbirdConnectTest {

    private final DatabaseHandler dbHandler;

    @Before
    public void init() {
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
    public void testingAccountExistsWhenRegistering() {}
}
