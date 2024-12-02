import java.util.List;
import java.sql.PreparedStatement;

public class DatabaseHandler extends Database implements DatabaseOperations {
    private final MySQLHandler sqlHandler;

    public DatabaseHandler(Database database) {
        super(database.getDbName());
        this.sqlHandler = (MySQLHandler) database;
        System.out.println("DatabaseHandler created for database: " + database.getDbName());
    }

    @Override
    public void connect() {
        // Connection is managed by MySQLHandler singleton, no need to connect here
    }

    @Override
    public void disconnect() {
        // Connection is managed by MySQLHandler singleton, no need to disconnect here
    }

    public boolean addStudent(Student student, String password) {
        return sqlHandler.addStudent(student, password);
    }

    public Student getStudentByUsername(String username) {
        return sqlHandler.getStudentByUsername(username);
    }

    public boolean updateStudent(int id, String newName, String newYear) {
        return sqlHandler.updateStudent(id, newName, newYear);
    }

    public boolean removeStudent(int id) {
        return sqlHandler.removeStudent(id);
    }

    public Student authenticateStudent(String username, String password) {
        return sqlHandler.authenticateStudent(username, password);
    }

    public boolean doesUsernameExist(String username) {
        return sqlHandler.doesUsernameExist(username);
    }

    public List<Group> getAllGroups() {
        return sqlHandler.getAllGroups();
    }

    public boolean addGroup(Group group) {
        return sqlHandler.addGroup(group);
    }

    public boolean addMemberToGroup(int groupId, int studentId) {
        return sqlHandler.addMemberToGroup(groupId, studentId);
    }

    @Override
    public PreparedStatement prepareStatement(String query) {
        return sqlHandler.prepareStatement(query);
    }

    @Override
    public boolean editPost(Post post) {
        return sqlHandler.editPost(post);
    }

    @Override
    public boolean leaveGroup(Group group, Student student) {
        return sqlHandler.leaveGroup(group, student);
    }

    @Override
    public boolean containsTag(Tag tag) {
        return sqlHandler.containsTag(tag);
    }

    @Override
    public void createPost(Post post) {
        sqlHandler.createPost(post);
    }

    @Override
    public boolean addTagToStudent(int studentId, int tagId) {
        return sqlHandler.addTagToStudent(studentId, tagId);
    }

    @Override
    public boolean removeTagFromStudent(int studentId, int tagId) {
        return sqlHandler.removeTagFromStudent(studentId, tagId);
    }

    @Override
    public boolean addPostToGroup(int postId, int groupId) {
        return sqlHandler.addPostToGroup(postId, groupId);
    }

    @Override
    public boolean updateMembershipEndDate(int studentId, int groupId, java.sql.Date endDate) {
        return sqlHandler.updateMembershipEndDate(studentId, groupId, endDate);
    }
}