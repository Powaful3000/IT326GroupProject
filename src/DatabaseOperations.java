import java.util.List;

public interface DatabaseOperations {
    boolean addStudent(Student student, String password);

    Student getStudentByUsername(String username);

    boolean updateStudent(int id, String newName, String newYear);

    boolean removeStudent(int id);

    Student authenticateStudent(String username, String password);

    boolean doesUsernameExist(String username);

    List<Group> getAllGroups();

    boolean addGroup(Group group);

    boolean addMemberToGroup(int groupId, int studentId);

    boolean editPost(Post post);

    boolean leaveGroup(Group group, Student student);

    boolean containsTag(Tag tag);

    void createPost(Post post);

    boolean addTagToStudent(int studentId, int tagId);

    boolean removeTagFromStudent(int studentId, int tagId);

    boolean addPostToGroup(int postId, int groupId);

    boolean updateMembershipEndDate(int studentId, int groupId, java.sql.Date endDate);
}