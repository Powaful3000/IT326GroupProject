import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MySQLHandler extends Database implements DatabaseOperations {

    // Connection object for managing database connection
    private Connection connection;
    private boolean isConnected = false;

    // Database table constants
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_GROUPS = "student_groups";
    private static final String TABLE_MEMBERSHIPS = "group_memberships";
    private static final String TABLE_POSTS = "posts";
    private static final String TABLE_TAGS = "tags";
    private static final String TABLE_STUDENT_TAGS = "student_tags";
    private static final String TABLE_POST_GROUPS = "post_groups";

    // SQL Query Constants
    private static final String SQL_SELECT_STUDENT_BY_ID = "SELECT * FROM " + TABLE_STUDENTS + " WHERE userID = ?";
    private static final String SQL_SELECT_STUDENT_BY_USERNAME = "SELECT * FROM " + TABLE_STUDENTS
            + " WHERE userName = ?";
    private static final String SQL_INSERT_STUDENT = "INSERT INTO " + TABLE_STUDENTS
            + " (userID, userName, password, userYear) VALUES (?, ?, ?, ?)";
    private static final String SQL_DELETE_STUDENT = "DELETE FROM " + TABLE_STUDENTS + " WHERE userID = ?";
    private static final String SQL_INSERT_POST = "INSERT INTO " + TABLE_POSTS
            + " (postID, postContent, postOwner) VALUES (?, ?, ?)";
    private static final String SQL_DELETE_POST = "DELETE FROM " + TABLE_POSTS + " WHERE postID = ?";
    private static final String SQL_INSERT_GROUP = "INSERT INTO student_groups (groupID, groupName, groupDescription, groupSize, creationDate) VALUES (?, ?, ?, DEFAULT, DEFAULT)";
    private static final String SQL_SELECT_GROUP_MEMBERS = "SELECT s.* FROM " + TABLE_STUDENTS + " s JOIN "
            + TABLE_MEMBERSHIPS +
            " gm ON s.userID = gm.studentID WHERE gm.groupID = ?";
    private static final String SQL_DELETE_GROUP = "DELETE FROM " + TABLE_GROUPS + " WHERE groupID = ?";
    private static final String SQL_SELECT_ALL_GROUPS = "SELECT * FROM " + TABLE_GROUPS;
    private static final String SQL_INSERT_MEMBER = "INSERT INTO " + TABLE_MEMBERSHIPS
            + " (groupID, studentID, joinDate) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_GROUP_SIZE = "UPDATE " + TABLE_GROUPS
            + " SET groupSize = (SELECT COUNT(*) FROM " + TABLE_MEMBERSHIPS + " WHERE groupID = ?) WHERE groupID = ?";
    private static final String SQL_UPDATE_STUDENT = "UPDATE " + TABLE_STUDENTS
            + " SET userName = ?, userYear = ? WHERE userID = ?";
    private static final String SQL_AUTHENTICATE_STUDENT = "SELECT * FROM " + TABLE_STUDENTS
            + " WHERE userName = ? AND password = ?";
    private static final String SQL_CHECK_USERNAME_EXISTS = "SELECT COUNT(*) FROM " + TABLE_STUDENTS
            + " WHERE userName = ?";
    private static final String SQL_SELECT_GROUP_MEMBERS_DETAILED = "SELECT s.* FROM " + TABLE_STUDENTS + " s " +
            "JOIN " + TABLE_MEMBERSHIPS + " gm ON s.userID = gm.studentID " +
            "WHERE gm.groupID = ?";
    private static final String SQL_INSERT_TAG = "INSERT INTO " + TABLE_TAGS + " (name, description) VALUES (?, ?)";
    private static final String SQL_ADD_TAG_TO_STUDENT = "INSERT INTO " + TABLE_STUDENT_TAGS
            + " (studentID, tagID) VALUES (?, ?)";
    private static final String SQL_REMOVE_TAG_FROM_STUDENT = "DELETE FROM " + TABLE_STUDENT_TAGS
            + " WHERE studentID = ? AND tagID = ?";
    private static final String SQL_GET_STUDENT_TAGS = "SELECT t.* FROM " + TABLE_TAGS + " t JOIN " + TABLE_STUDENT_TAGS
            +
            " st ON t.tagID = st.tagID WHERE st.studentID = ?";
    private static final String SQL_ADD_POST_TO_GROUP = "INSERT INTO " + TABLE_POST_GROUPS
            + " (postID, groupID) VALUES (?, ?)";
    private static final String SQL_UPDATE_MEMBERSHIP_END_DATE = "UPDATE " + TABLE_MEMBERSHIPS
            + " SET endDate = ? WHERE studentID = ? AND groupID = ?";
    private static final String SQL_UPDATE_POST = "UPDATE " + TABLE_POSTS + " SET postContent = ? WHERE postID = ?";
    private static final String SQL_LEAVE_GROUP = "UPDATE " + TABLE_MEMBERSHIPS + " SET endDate = CURRENT_TIMESTAMP " +
            "WHERE studentID = ? AND groupID = ? AND (endDate IS NULL OR endDate > CURRENT_TIMESTAMP)";
    private static final String SQL_CHECK_TAG_EXISTS = "SELECT COUNT(*) FROM " + TABLE_TAGS
            + " WHERE name = ? AND description = ?";

    // New SQL constants for friend system
    private static final String SQL_SEND_FRIEND_REQUEST = 
        "INSERT INTO friend_requests (fromUserID, toUserID) VALUES (?, ?)";
    private static final String SQL_ACCEPT_FRIEND_REQUEST = 
        "UPDATE friend_requests SET status = 'ACCEPTED' WHERE requestID = ?";
    private static final String SQL_DECLINE_FRIEND_REQUEST = 
        "UPDATE friend_requests SET status = 'DECLINED' WHERE requestID = ?";
    private static final String SQL_GET_FRIEND_REQUESTS = 
        "SELECT s.* FROM students s JOIN friend_requests fr ON s.userID = fr.fromUserID " +
        "WHERE fr.toUserID = ? AND fr.status = 'PENDING'";
    private static final String SQL_GET_FRIENDS = 
        "SELECT s.* FROM students s JOIN friends f ON (s.userID = f.userID2 OR s.userID = f.userID1) " +
        "WHERE (f.userID1 = ? OR f.userID2 = ?) AND s.userID != ?";
    
    // SQL constants for blocking system
    private static final String SQL_BLOCK_USER = 
        "INSERT INTO blocked_users (blockerID, blockedID) VALUES (?, ?)";
    private static final String SQL_GET_BLOCKED_USERS = 
        "SELECT s.* FROM students s JOIN blocked_users b ON s.userID = b.blockedID WHERE b.blockerID = ?";
    
    // SQL constants for bookmarks and anonymous mode
    private static final String SQL_BOOKMARK_POST = 
        "INSERT INTO bookmarked_posts (userID, postID) VALUES (?, ?)";
    private static final String SQL_GET_BOOKMARKED_POSTS = 
        "SELECT p.* FROM posts p JOIN bookmarked_posts bp ON p.postID = bp.postID WHERE bp.userID = ?";
    private static final String SQL_TOGGLE_ANONYMOUS = 
        "UPDATE students SET isAnonymous = ? WHERE userID = ?";
    private static final String SQL_GET_STUDENT_BY_ID = 
        "SELECT * FROM students WHERE userID = ?";

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
        return executeUpdate(
                SQL_INSERT_STUDENT,
                stmt -> {
                    System.out.println("\n====== MySQL Add Student Debug ======");
                    System.out.println("Input Values:");
                    System.out.println("- userID: " + student.getID());
                    System.out.println("- userName: " + student.getName());
                    System.out.println("- Original password length: " + password.length());
                    System.out.println("- userYear: " + student.getYear());

                    String hashedPw = hashPassword(password);
                    System.out.println("\nPassword Hashing:");
                    System.out.println("- Original password (first 3 chars): "
                            + password.substring(0, Math.min(3, password.length())) + "...");
                    System.out.println("- Hashed password length: " + hashedPw.length());
                    System.out.println("- Hashed password: " + hashedPw);

                    stmt.setInt(1, student.getID());
                    stmt.setString(2, student.getName());
                    stmt.setString(3, hashedPw);
                    stmt.setString(4, student.getYear());

                    System.out.println("\nDatabase Operation:");
                    System.out.println("- SQL Query: INSERT INTO students...");
                });
    }

    // Method to remove a student from the database
    public void removeStudent(Student student) {
        executeUpdate(
                SQL_DELETE_STUDENT,
                stmt -> stmt.setInt(1, student.getID()));
    }

    // Method to remove a group from the database
    public void removeGroup(Group group) {
        executeUpdate(
                SQL_DELETE_GROUP,
                stmt -> stmt.setInt(1, group.getID()));
    }

    // Method to add a post to the database
    public void createPost(Post post) {
        executeUpdate(
                SQL_INSERT_POST,
                stmt -> {
                    stmt.setInt(1, post.getID());
                    stmt.setString(2, post.getContent());
                    stmt.setInt(3, post.getOwner().getID());
                    System.out.println("Post created: " + post.getContent());
                });
    }

    // Method to remove a post from the database
    public void removePost(Post post) {
        executeUpdate(
                SQL_DELETE_POST,
                stmt -> {
                    stmt.setInt(1, post.getID());
                    System.out.println("Post removed: " + post.getContent());
                });
    }

    // Method to check if a student exists in the database
    public boolean doesStudentExist(Student student) {
        return executeQuery(
                SQL_SELECT_STUDENT_BY_ID,
                stmt -> stmt.setInt(1, student.getID()),
                rs -> rs.next() && rs.getInt(1) > 0);
    }

    @Override
    public Student authenticateStudent(String username, String password) {
        return executeQuery(
                SQL_AUTHENTICATE_STUDENT,
                stmt -> {
                    stmt.setString(1, username);
                    stmt.setString(2, hashPassword(password));
                    System.out.println("Debug - Auth attempt for user: " + username);
                },
                rs -> {
                    if (rs.next()) {
                        Student student = new Student(
                                rs.getInt("userID"),
                                rs.getString("userName"),
                                rs.getString("userYear"),
                                null, null, null);
                        System.out.println("Debug - Authentication successful for: " + username);
                        return student;
                    }
                    System.out.println("Debug - Authentication failed for: " + username);
                    return null;
                });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            // System.out.println("MySQLHandler hashPassword: " + password + " hash: " +
            // hashedBytes.toString());

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

    @Override
    public PreparedStatement prepareStatement(String query) {
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            logError("Failed to prepare statement: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Query example (multiple results)
    @Override
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        executeQuery(
                SQL_SELECT_ALL_GROUPS,
                stmt -> {
                }, // No parameters needed
                rs -> {
                    while (rs.next()) {
                        Group group = new Group(
                            rs.getInt("groupID"),
                            rs.getString("groupName"),
                            rs.getString("groupDescription")
                        );
                        // Set additional properties
                        group.setCreationDate(rs.getTimestamp("creationDate"));
                        // Load members and their dates
                        List<Student> members = getGroupMembers(rs.getInt("groupID"));
                        for (Student member : members) {
                            group.addMember(member);
                            // Load join and end dates from the database
                            Date joinDate = getMemberJoinDate(group.getID(), member.getID());
                            Date endDate = getMemberEndDate(group.getID(), member.getID());
                            if (joinDate != null) {
                                group.setMemberJoinDate(member, joinDate);
                            }
                            if (endDate != null) {
                                group.setMemberEndDate(member, endDate);
                            }
                        }
                        groups.add(group);
                    }
                    return groups;
                });
        return groups;
    }

    private List<Student> getGroupMembers(int groupId) {
        return executeQuery(
                SQL_SELECT_GROUP_MEMBERS_DETAILED,
                stmt -> stmt.setInt(1, groupId),
                rs -> {
                    List<Student> members = new ArrayList<>();
                    while (rs.next()) {
                        members.add(new Student(
                                rs.getInt("userID"),
                                rs.getString("userName"),
                                rs.getString("userYear"),
                                null, null, null));
                    }
                    return members;
                });
    }

    @Override
    public boolean addGroup(Group group) {
        System.out.println("\n====== MySQL Add Group Debug ======");
        System.out.println("Input Values:");
        System.out.println("- groupID: " + group.getID());
        System.out.println("- groupName: " + group.getName());
        System.out.println("- groupDescription: " + group.getDescription());
        System.out.println("\nSQL Statement:");
        System.out.println(SQL_INSERT_GROUP);
        
        try {
            return executeUpdate(
                SQL_INSERT_GROUP,
                stmt -> {
                    System.out.println("\nSetting Parameters:");
                    System.out.println("1. Setting groupID = " + group.getID());
                    stmt.setInt(1, group.getID());
                    System.out.println("2. Setting groupName = " + group.getName());
                    stmt.setString(2, group.getName());
                    System.out.println("3. Setting groupDescription = " + group.getDescription());
                    stmt.setString(3, group.getDescription());
                    System.out.println("All parameters set successfully");
                });
        } catch (Exception e) {
            System.err.println("\nUnexpected error in addGroup:");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addMemberToGroup(int groupId, int studentId) {
        return executeUpdate(
                SQL_INSERT_MEMBER,
                stmt -> {
                    stmt.setInt(1, groupId);
                    stmt.setInt(2, studentId);
                    stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                });
    }

    private void updateGroupSize(int groupId) {
        executeUpdate(
                SQL_UPDATE_GROUP_SIZE,
                stmt -> {
                    stmt.setInt(1, groupId);
                    stmt.setInt(2, groupId);
                });
    }

    // Query example (single result)
    @Override
    public Student getStudentByUsername(String username) {
        return executeQuery(
                SQL_SELECT_STUDENT_BY_USERNAME,
                stmt -> stmt.setString(1, username),
                rs -> rs.next() ? new Student(
                        rs.getInt("userID"),
                        rs.getString("userName"),
                        rs.getString("userYear"),
                        null, null, null) : null);
    }

    @Override
    public boolean removeStudent(int id) {
        return executeUpdate(SQL_DELETE_STUDENT, stmt -> stmt.setInt(1, id));
    }

    // Update example
    @Override
    public boolean updateStudent(int id, String newName, String newYear) {
        return executeUpdate(
                SQL_UPDATE_STUDENT,
                stmt -> {
                    stmt.setString(1, newName);
                    stmt.setString(2, newYear);
                    stmt.setInt(3, id);
                });
    }

    @Override
    public boolean doesUsernameExist(String username) {
        return executeQuery(
                SQL_CHECK_USERNAME_EXISTS,
                stmt -> stmt.setString(1, username),
                rs -> rs.next() && rs.getInt(1) > 0);
    }

    private void closeResources(ResultSet rs, PreparedStatement stmt) {
        try {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
            logError("Failed to close resources: " + e.getMessage());
        }
    }

    private void closeStatement(PreparedStatement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
            logError("Failed to close prepared statement: " + e.getMessage());
        }
    }

    // New methods for tag handling
    public boolean addTag(Tag tag) {
        return executeUpdate(
                SQL_INSERT_TAG,
                stmt -> {
                    stmt.setString(1, tag.getName());
                    stmt.setString(2, tag.getDescription());
                });
    }

    public boolean addTagToStudent(int studentId, int tagId) {
        return executeUpdate(
                SQL_ADD_TAG_TO_STUDENT,
                stmt -> {
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, tagId);
                });
    }

    public boolean removeTagFromStudent(int studentId, int tagId) {
        return executeUpdate(
                SQL_REMOVE_TAG_FROM_STUDENT,
                stmt -> {
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, tagId);
                });
    }

    // New method for post-group relationship
    public boolean addPostToGroup(int postId, int groupId) {
        return executeUpdate(
                SQL_ADD_POST_TO_GROUP,
                stmt -> {
                    stmt.setInt(1, postId);
                    stmt.setInt(2, groupId);
                });
    }

    // New method for updating membership end date
    public boolean updateMembershipEndDate(int studentId, int groupId, java.sql.Date endDate) {
        return executeUpdate(
                SQL_UPDATE_MEMBERSHIP_END_DATE,
                stmt -> {
                    stmt.setDate(1, endDate);
                    stmt.setInt(2, studentId);
                    stmt.setInt(3, groupId);
                });
    }

    public boolean editPost(Post post) {
        return executeUpdate(
                SQL_UPDATE_POST,
                stmt -> {
                    stmt.setString(1, post.getContent());
                    stmt.setInt(2, post.getID());
                    System.out.println("Updating post ID: " + post.getID() + " with new content: " + post.getContent());
                });
    }

    public boolean leaveGroup(Group group, Student student) {
        boolean success = executeUpdate(
                SQL_LEAVE_GROUP,
                stmt -> {
                    stmt.setInt(1, student.getID());
                    stmt.setInt(2, group.getID());
                    System.out.println("Student " + student.getName() + " leaving group: " + group.getName());
                });
        if (success) {
            // Update the group size after a member leaves
            updateGroupSize(group.getID());
        }
        return success;
    }

    public boolean containsTag(Tag tag) {
        return executeQuery(
                SQL_CHECK_TAG_EXISTS,
                stmt -> {
                    stmt.setString(1, tag.getName());
                    stmt.setString(2, tag.getDescription());
                },
                rs -> rs.next() && rs.getInt(1) > 0);
    }

    private void verifyStoredStudent(int studentId) {
        executeQuery(
                SQL_SELECT_STUDENT_BY_ID,
                stmt -> stmt.setInt(1, studentId),
                rs -> {
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
                    return null;
                });
    }

    private boolean verifyPassword(String password, String storedHash) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(storedHash);
    }

    // Template for queries that return a result
    private <T> T executeQuery(String query, PreparedStatementConsumer preparer, ResultSetHandler<T> handler) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            ensureConnected();
            stmt = prepareStatement(query);
            preparer.accept(stmt);
            rs = stmt.executeQuery();
            return handler.handle(rs);
        } catch (SQLException e) {
            logError("Failed to execute query: " + e.getMessage());
            return null;
        } finally {
            closeResources(rs, stmt);
        }
    }

    // Template for updates/deletes/inserts
    private boolean executeUpdate(String query, PreparedStatementConsumer preparer) {
        PreparedStatement stmt = null;
        try {
            System.out.println("\n====== MySQL Execute Update Debug ======");
            System.out.println("Query: " + query);
            ensureConnected();
            stmt = prepareStatement(query);
            System.out.println("Setting parameters...");
            preparer.accept(stmt);
            System.out.println("Parameters set, executing update...");
            int result = stmt.executeUpdate();
            System.out.println("Update complete, rows affected: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("\n====== MySQL Error Details ======");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nStack Trace:");
            e.printStackTrace();
            System.err.println("\nQuery that caused the error:");
            System.err.println(query);
            logError("Failed to execute update: " + e.getMessage());
            return false;
        } finally {
            closeStatement(stmt);
        }
    }

    // Functional interfaces
    @FunctionalInterface
    private interface PreparedStatementConsumer {
        void accept(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }

    // Add new methods for friend system
    public boolean sendFriendRequest(int fromUserId, int toUserId) {
        return executeUpdate(SQL_SEND_FRIEND_REQUEST,
            stmt -> {
                stmt.setInt(1, fromUserId);
                stmt.setInt(2, toUserId);
                System.out.println("Sending friend request from user " + fromUserId + " to user " + toUserId);
            });
    }

    public boolean acceptFriendRequest(int requestId) {
        return executeUpdate(SQL_ACCEPT_FRIEND_REQUEST,
            stmt -> {
                stmt.setInt(1, requestId);
                System.out.println("Accepting friend request ID: " + requestId);
            });
    }

    public boolean declineFriendRequest(int requestId) {
        return executeUpdate(SQL_DECLINE_FRIEND_REQUEST,
            stmt -> {
                stmt.setInt(1, requestId);
                System.out.println("Declining friend request ID: " + requestId);
            });
    }

    public List<Student> getFriendRequests(int userId) {
        return executeQuery(SQL_GET_FRIEND_REQUESTS,
            stmt -> stmt.setInt(1, userId),
            this::mapResultSetToStudentList);
    }

    public List<Student> getFriends(int userId) {
        return executeQuery(SQL_GET_FRIENDS,
            stmt -> {
                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                stmt.setInt(3, userId);
            },
            this::mapResultSetToStudentList);
    }

    public boolean blockUser(int blockerId, int blockedId) {
        return executeUpdate(SQL_BLOCK_USER,
            stmt -> {
                stmt.setInt(1, blockerId);
                stmt.setInt(2, blockedId);
                System.out.println("User " + blockerId + " blocking user " + blockedId);
            });
    }

    public List<Student> getBlockedUsers(int userId) {
        return executeQuery(SQL_GET_BLOCKED_USERS,
            stmt -> stmt.setInt(1, userId),
            this::mapResultSetToStudentList);
    }

    public boolean bookmarkPost(int userId, int postId) {
        return executeUpdate(SQL_BOOKMARK_POST,
            stmt -> {
                stmt.setInt(1, userId);
                stmt.setInt(2, postId);
                System.out.println("User " + userId + " bookmarking post " + postId);
            });
    }

    public List<Post> getBookmarkedPosts(int userId) {
        return executeQuery(SQL_GET_BOOKMARKED_POSTS,
            stmt -> stmt.setInt(1, userId),
            this::mapResultSetToPostList);
    }

    public boolean toggleAnonymousMode(int userId, boolean isAnonymous) {
        return executeUpdate(SQL_TOGGLE_ANONYMOUS,
            stmt -> {
                stmt.setBoolean(1, isAnonymous);
                stmt.setInt(2, userId);
                System.out.println("Toggling anonymous mode for user " + userId + " to " + isAnonymous);
            });
    }

    // Helper method for mapping ResultSet to List<Student>
    private List<Student> mapResultSetToStudentList(ResultSet rs) throws SQLException {
        List<Student> students = new ArrayList<>();
        while (rs.next()) {
            students.add(new Student(
                rs.getInt("userID"),
                rs.getString("userName"),
                rs.getString("userYear"),
                new ArrayList<>(),  // Tags will be loaded separately if needed
                new ArrayList<>(),  // Groups will be loaded separately if needed
                new ArrayList<>()   // Posts will be loaded separately if needed
            ));
        }
        return students;
    }

    // Helper method for mapping ResultSet to List<Post>
    private List<Post> mapResultSetToPostList(ResultSet rs) throws SQLException {
        List<Post> posts = new ArrayList<>();
        while (rs.next()) {
            posts.add(new Post(
                rs.getInt("postID"),
                rs.getString("postContent"),
                getStudentById(rs.getInt("postOwner")),
                null  // Group will be loaded separately if needed
            ));
        }
        return posts;
    }

    public Student getStudentById(int userId) {
        return executeQuery(SQL_GET_STUDENT_BY_ID,
            stmt -> stmt.setInt(1, userId),
            rs -> {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("userID"),
                        rs.getString("userName"),
                        rs.getString("userYear"),
                        new ArrayList<>(),  // Tags will be loaded separately if needed
                        new ArrayList<>(),  // Groups will be loaded separately if needed
                        new ArrayList<>()   // Posts will be loaded separately if needed
                    );
                }
                return null;
            });
    }

    // Add helper methods for member dates
    private Date getMemberJoinDate(int groupId, int studentId) {
        return executeQuery(
            "SELECT joinDate FROM group_memberships WHERE groupID = ? AND studentID = ?",
            stmt -> {
                stmt.setInt(1, groupId);
                stmt.setInt(2, studentId);
            },
            rs -> rs.next() ? rs.getTimestamp("joinDate") : null
        );
    }

    private Date getMemberEndDate(int groupId, int studentId) {
        return executeQuery(
            "SELECT endDate FROM group_memberships WHERE groupID = ? AND studentID = ?",
            stmt -> {
                stmt.setInt(1, groupId);
                stmt.setInt(2, studentId);
            },
            rs -> rs.next() ? rs.getTimestamp("endDate") : null
        );
    }

    public boolean removeFriend(int userId, int friendId) {
        String query = "DELETE FROM friends WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        try (PreparedStatement stmt = prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing friend: " + e.getMessage());
            return false;
        }
    }

    public boolean unblockUser(int blockerId, int blockedId) {
        String query = "DELETE FROM blocked_users WHERE blocker_id = ? AND blocked_id = ?";
        try (PreparedStatement stmt = prepareStatement(query)) {
            stmt.setInt(1, blockerId);
            stmt.setInt(2, blockedId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error unblocking user: " + e.getMessage());
            return false;
        }
    }

}
