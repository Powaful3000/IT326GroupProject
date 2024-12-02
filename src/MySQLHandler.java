import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MySQLHandler extends Database implements DatabaseOperations {

    // Connection object for managing database connection
    private Connection connection;
    private boolean isConnected = false;

    // Constructor
    public MySQLHandler(String dbName) {
        super(dbName);
        System.out.println("MySQLHandler instance created for database: " + dbName);
    }

    // Method to establish a connection to the MySQL database
    @Override
    public synchronized void connect() {
        System.out.println("Attempting to connect to MySQL database: " + dbName);
        if (!isConnected) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true";
                String username = "MySQLUser";
                String password = "MySQLPassword";
                connection = DriverManager.getConnection(url, username, password);
                isConnected = true;
                System.out.println("Connected to MySQL database: " + dbName);
            } catch (ClassNotFoundException | SQLException e) {
                logError("Failed to connect to database: " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Already connected to MySQL database: " + dbName);
        }
    }

    // Method to disconnect from the MySQL database
    @Override
    public synchronized void disconnect() {
        if (isConnected) {
            try {
                connection.close();
                isConnected = false;
                System.out.println("Disconnected from MySQL database: " + dbName);
            } catch (SQLException e) {
                logError("Failed to disconnect from database: " + e.getMessage());
            }
        } else {
            System.out.println("Already disconnected from MySQL database: " + dbName);
        }
    }

    private void ensureConnected() {
        if (!isConnected) {
            connect();
        }
    }

    // Method to add a student to the database
    @Override
    public boolean addStudent(Student student, String password) {
        String query = "INSERT INTO students (userID, userName, password, userYear) VALUES (?, ?, ?, ?)";
        try {
            ensureConnected();
            PreparedStatement stmt = prepareStatement(query);
            
            System.out.println("\n====== MySQL Add Student Debug ======");
            System.out.println("Input Values:");
            System.out.println("- userID: " + student.getID());
            System.out.println("- userName: " + student.getName());
            System.out.println("- Original password length: " + password.length());
            System.out.println("- userYear: " + student.getYear());
            
            String hashedPw = hashPassword(password);
            System.out.println("\nPassword Hashing:");
            System.out.println("- Original password (first 3 chars): " + password.substring(0, Math.min(3, password.length())) + "...");
            System.out.println("- Hashed password length: " + hashedPw.length());
            System.out.println("- Hashed password: " + hashedPw);
            
            stmt.setInt(1, student.getID());
            stmt.setString(2, student.getName());
            stmt.setString(3, hashedPw);
            stmt.setString(4, student.getYear());
            
            int result = stmt.executeUpdate();
            System.out.println("\nDatabase Operation:");
            System.out.println("- SQL Query: " + query);
            System.out.println("- Execution result: " + result);
            
            // Verify stored data
            verifyStoredStudent(student.getID());
            
            System.out.println("====== End MySQL Add Student Debug ======\n");
            return result > 0;
        } catch (SQLException e) {
            System.out.println("\nSQL Error occurred:");
            System.out.println("- Error message: " + e.getMessage());
            System.out.println("- SQL State: " + e.getSQLState());
            System.out.println("- Error Code: " + e.getErrorCode());
            logError("Failed to add student: " + e.getMessage());
            return false;
        } finally {
            // disconnect();
        }
    }

    private void verifyStoredStudent(int studentId) {
        try {
            String query = "SELECT * FROM students WHERE userID = ?";
            PreparedStatement stmt = prepareStatement(query);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("\nVerifying Stored Data:");
            if (rs.next()) {
                System.out.println("Found student in database:");
                System.out.println("- userID: " + rs.getInt("userID"));
                System.out.println("- userName: " + rs.getString("userName"));
                System.out.println("- stored password hash length: " + rs.getString("password").length());
                System.out.println("- stored password hash: " + rs.getString("password"));
                System.out.println("- userYear: " + rs.getString("userYear"));
            } else {
                System.out.println("WARNING: Student not found in database after insertion!");
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error verifying stored student: " + e.getMessage());
        }
    }

    // Method to remove a student from the database
    public void removeStudent(Student student) {
        String query = "DELETE FROM students WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, student.getID());
            stmt.executeUpdate();
            System.out.println("Student removed: " + student.getName());
        } catch (SQLException e) {
            logError("Failed to remove student: " + e.getMessage());
        }
    }

    // Method to remove a group from the database
    public void removeGroup(Group group) {
        String query = "DELETE FROM student_groups WHERE groupID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, group.getID());
            stmt.executeUpdate();
            System.out.println("Group removed: " + group.getName());
        } catch (SQLException e) {
            logError("Failed to remove group: " + e.getMessage());
        }
    }

    // Method to add a post to the database
    public void addPost(Post post) {
        String query = "INSERT INTO posts (postID, postContent, postOwner) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, post.getID());
            stmt.setString(2, post.getContent());
            stmt.setInt(3, post.getOwner().getID());
            stmt.executeUpdate();
            System.out.println("Post added: " + post.getContent());
        } catch (SQLException e) {
            logError("Failed to add post: " + e.getMessage());
        }
    }

    // Method to remove a post from the database
    public void removePost(Post post) {
        String query = "DELETE FROM posts WHERE postID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, post.getID());
            stmt.executeUpdate();
            System.out.println("Post removed: " + post.getContent());
        } catch (SQLException e) {
            logError("Failed to remove post: " + e.getMessage());
        }
    }

    // Method to check if a student exists in the database
    public boolean doesStudentExist(Student student) {
        String query = "SELECT * FROM students WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, student.getID());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logError("Failed to check if student exists: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Student authenticateStudent(String username, String password) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            ensureConnected();
            String query = "SELECT * FROM students WHERE userName = ? AND password = ?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            
            System.out.println("Debug - Auth attempt for user: " + username);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Student student = new Student(
                    rs.getInt("userID"),
                    rs.getString("userName"),
                    rs.getString("userYear"),
                    null, null, null
                );
                System.out.println("Debug - Authentication successful for: " + username);
                return student;
            }
            System.out.println("Debug - Authentication failed for: " + username);
            return null;
            
        } catch (SQLException e) {
            logError("Failed to authenticate student: " + e.getMessage());
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                logError("Failed to close resources: " + e.getMessage());
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            // System.out.println("MySQLHandler hashPassword: " + password + " hash: " + hashedBytes.toString());


            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            // System.out.println("MySQLHandler hashPassword return: " + sb.toString());

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    private boolean verifyPassword(String password, String storedHash) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(storedHash);
    }

    @Override
    public PreparedStatement prepareStatement(String query) {
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            logError("Failed to prepare statement: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT * FROM student_groups";
        try {
            ensureConnected();
            PreparedStatement stmt = prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Group group = new Group(
                    rs.getInt("groupID"),
                    rs.getString("groupName"),
                    rs.getInt("groupSize"),
                    rs.getString("groupDescription"),
                    getGroupMembers(rs.getInt("groupID"))
                );
                groups.add(group);
            }
        } catch (SQLException e) {
            logError("Failed to get groups: " + e.getMessage());
        } finally {
            // disconnect();
        }
        return groups;
    }

    private List<Student> getGroupMembers(int groupId) {
        List<Student> members = new ArrayList<>();
        String query = "SELECT s.* FROM students s " +
                      "JOIN group_memberships gm ON s.userID = gm.studentID " +
                      "WHERE gm.groupID = ?";
        try {
            PreparedStatement stmt = prepareStatement(query);
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("userID"),
                    rs.getString("userName"),
                    rs.getString("userYear"),
                    null, null, null  // Tags, Groups, Posts can be loaded separately if needed
                );
                members.add(student);
            }
        } catch (SQLException e) {
            logError("Failed to get group members: " + e.getMessage());
        }
        return members;
    }

    @Override
    public boolean addGroup(Group group) {
        String query = "INSERT INTO student_groups (groupID, groupName, groupSize, groupDescription) VALUES (?, ?, ?, ?)";
        try {
            ensureConnected();
            PreparedStatement stmt = prepareStatement(query);
            stmt.setInt(1, group.getID());
            stmt.setString(2, group.getName());
            stmt.setInt(3, group.getSize());
            stmt.setString(4, group.getDescription());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logError("Failed to add group: " + e.getMessage());
            return false;
        } finally {
            // disconnect();
        }
    }

    @Override
    public boolean addMemberToGroup(int groupId, int studentId) {
        String query = "INSERT INTO group_memberships (groupID, studentID, joinDate) VALUES (?, ?, ?)";
        try {
            ensureConnected();
            PreparedStatement stmt = prepareStatement(query);
            stmt.setInt(1, groupId);
            stmt.setInt(2, studentId);
            stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                // Update group size
                updateGroupSize(groupId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logError("Failed to add member to group: " + e.getMessage());
            return false;
        } finally {
            // disconnect();
        }
    }

    private void updateGroupSize(int groupId) {
        String query = "UPDATE student_groups SET groupSize = " +
                      "(SELECT COUNT(*) FROM group_memberships WHERE groupID = ?) " +
                      "WHERE groupID = ?";
        try {
            PreparedStatement stmt = prepareStatement(query);
            stmt.setInt(1, groupId);
            stmt.setInt(2, groupId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logError("Failed to update group size: " + e.getMessage());
        }
    }

    @Override
    public Student getStudentByUsername(String username) {
        String query = "SELECT * FROM students WHERE userName = ?";
        try {
            ensureConnected();
            PreparedStatement stmt = prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student(
                    rs.getInt("userID"),
                    rs.getString("userName"),
                    rs.getString("userYear"),
                    null, null, null // Tags, Groups, Posts loaded separately if needed
                );
                return student;
            }
        } catch (SQLException e) {
            logError("Failed to get student: " + e.getMessage());
        } finally {
            // disconnect();
        }
        return null;
    }

    @Override
    public boolean removeStudent(int id) {
        String query = "DELETE FROM students WHERE userID = ?";
        try {
            ensureConnected();
            PreparedStatement stmt = prepareStatement(query);
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logError("Failed to remove student: " + e.getMessage());
            return false;
        } finally {
            // disconnect();
        }
    }

    @Override
    public boolean updateStudent(int id, String newName, String newYear) {
        String query = "UPDATE students SET userName = ?, userYear = ? WHERE userID = ?";
        try {
            ensureConnected();
            PreparedStatement stmt = prepareStatement(query);
            stmt.setString(1, newName);
            stmt.setString(2, newYear);
            stmt.setInt(3, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logError("Failed to update student: " + e.getMessage());
            return false;
        } finally {
            // disconnect();
        }
    }

    @Override
    public boolean doesUsernameExist(String username) {
        String query = "SELECT COUNT(*) FROM students WHERE userName = ?";
        try {
            ensureConnected();
            PreparedStatement stmt = prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logError("Failed to check username: " + e.getMessage());
        } finally {
            // disconnect();
        }
        return false;
    }
}
