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
} 