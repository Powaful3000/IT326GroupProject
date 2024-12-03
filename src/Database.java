import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Database {

    // Attribute to store the database name
    protected String dbName;

    // Constructor to initialize the database name
    public Database(String dbName) {
        if (dbName == null || dbName.isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty.");
        }
        this.dbName = dbName;
    }

    // Getter for dbName
    public String getDbName() {
        return dbName;
    }

    // Setter for dbName
    public void setDbName(String dbName) {
        if (dbName == null || dbName.isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty.");
        }
        this.dbName = dbName;
    }

    // Abstract method to connect to the database
    public abstract void connect();

    // Abstract method to disconnect from the database
    public abstract void disconnect();

    // Optional utility method for database name validation
    public void validateDatabaseName() {
        if (dbName == null || dbName.isEmpty()) {
            throw new IllegalStateException("Database name is invalid.");
        }
    }

    // Example of a reusable utility method for error logging
    protected void logError(String message) {
        System.err.println("[Database Error] " + message);
    }

    // Optional method to print database status
    public void printStatus(String status) {
        System.out.println("Database [" + dbName + "] Status: " + status);
    }

    // Abstract method to prepare SQL statements
    public abstract PreparedStatement prepareStatement(String query) throws SQLException;

    // Add abstract methods to match MySQLHandler implementations
    public abstract boolean editPost(Post post);

    public abstract boolean leaveGroup(Group group, Student student);

    public abstract boolean containsTag(Tag tag);

    public abstract boolean createPost(Post post);

    public abstract boolean addTagToStudent(int studentId, int tagId);

    public abstract boolean removeTagFromStudent(int studentId, int tagId);

    public abstract boolean addPostToGroup(int postId, int groupId);

    public abstract boolean updateMembershipEndDate(int studentId, int groupId, java.sql.Date endDate);

    public abstract boolean isStudentInGroup(int studentId, int groupId);

    public abstract boolean joinGroup(int studentId, int groupId);

    public abstract boolean leaveGroup(int studentId, int groupId);

    public abstract Group findGroupByName(String groupName);
}
