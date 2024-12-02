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
}