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
    private static final String SQL_SELECT_STUDENT_BY_USERNAME = "SELECT * FROM " + TABLE_STUDENTS + " WHERE userName = ?";
    private static final String SQL_INSERT_STUDENT = "INSERT INTO students (userName, password, userYear) VALUES (?, ?, ?)";
    private static final String SQL_DELETE_STUDENT = "DELETE FROM " + TABLE_STUDENTS + " WHERE userID = ?";
    private static final String SQL_INSERT_POST = "INSERT INTO " + TABLE_POSTS + " (postID, postContent, postOwner) VALUES (?, ?, ?)";
    private static final String SQL_DELETE_POST = "DELETE FROM " + TABLE_POSTS + " WHERE postID = ?";
    private static final String SQL_INSERT_GROUP = "INSERT INTO student_groups (groupID, groupName, groupDescription, groupSize, creationDate) VALUES (?, ?, ?, DEFAULT, DEFAULT)";
    private static final String SQL_SELECT_GROUP_MEMBERS = "SELECT s.* FROM " + TABLE_STUDENTS + " s JOIN "
            + TABLE_MEMBERSHIPS +
            " gm ON s.userID = gm.studentID WHERE gm.groupID = ?";
    private static final String SQL_DELETE_GROUP = "DELETE FROM " + TABLE_GROUPS + " WHERE groupID = ?";
    private static final String SQL_SELECT_ALL_GROUPS = "SELECT * FROM " + TABLE_GROUPS + " ORDER BY groupSize DESC";
    private static final String SQL_INSERT_MEMBER = "INSERT INTO " + TABLE_MEMBERSHIPS
            + " (groupID, studentID, joinDate) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_GROUP_SIZE = "UPDATE " + TABLE_GROUPS
            + " SET groupSize = (SELECT COUNT(*) FROM " + TABLE_MEMBERSHIPS + " WHERE groupID = ?) WHERE groupID = ?";
    private static final String SQL_UPDATE_STUDENT = "UPDATE " + TABLE_STUDENTS
            + " SET userName = ?, userYear = ? WHERE userID = ?";
    private static final String SQL_AUTHENTICATE_STUDENT = "SELECT * FROM students WHERE userName = ? AND password = ?";
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
    private static final String SQL_ADD_POST_TO_GROUP = "INSERT INTO " + TABLE_POST_GROUPS + " (postID, groupID) VALUES (?, ?)";
    private static final String SQL_UPDATE_MEMBERSHIP_END_DATE = "UPDATE " + TABLE_MEMBERSHIPS
            + " SET endDate = ? WHERE studentID = ? AND groupID = ?";
    private static final String SQL_UPDATE_POST = "UPDATE " + TABLE_POSTS + " SET postContent = ? WHERE postID = ?";
    private static final String SQL_UPDATE_GROUP_END_DATE = "UPDATE " + TABLE_MEMBERSHIPS +
            " SET endDate = CURRENT_TIMESTAMP " +
            "WHERE studentID = ? AND groupID = ? AND (endDate IS NULL OR endDate > CURRENT_TIMESTAMP)";
    private static final String SQL_LEAVE_GROUP = "DELETE FROM " + TABLE_MEMBERSHIPS + 
        " WHERE studentID = ? AND groupID = ?";
    private static final String SQL_CHECK_TAG_EXISTS = "SELECT COUNT(*) FROM " + TABLE_TAGS
            + " WHERE name = ? AND description = ?";

    // New SQL constants for friend system
    private static final String SQL_SEND_FRIEND_REQUEST = "INSERT INTO friend_requests (fromUserID, toUserID) VALUES (?, ?)";
    private static final String SQL_ACCEPT_FRIEND_REQUEST = "UPDATE friend_requests SET status = 'ACCEPTED' WHERE requestID = ?";
    private static final String SQL_DECLINE_FRIEND_REQUEST = "UPDATE friend_requests SET status = 'DECLINED' WHERE requestID = ?";
    private static final String SQL_GET_FRIEND_REQUESTS = "SELECT s.* FROM students s JOIN friend_requests fr ON s.userID = fr.fromUserID "
            +
            "WHERE fr.toUserID = ? AND fr.status = 'PENDING'";
    private static final String SQL_GET_FRIENDS = "SELECT s.* FROM students s JOIN friends f ON (s.userID = f.userID2 OR s.userID = f.userID1) "
            +
            "WHERE (f.userID1 = ? OR f.userID2 = ?) AND s.userID != ?";

    // SQL constants for blocking system
    private static final String SQL_BLOCK_USER = "INSERT INTO blocked_users (blockerID, blockedID) VALUES (?, ?)";
    private static final String SQL_GET_BLOCKED_USERS = "SELECT s.* FROM students s JOIN blocked_users b ON s.userID = b.blockedID WHERE b.blockerID = ?";

    // SQL constants for bookmarks and anonymous mode
    private static final String SQL_BOOKMARK_POST = "INSERT INTO bookmarked_posts (userID, postID) VALUES (?, ?)";
    private static final String SQL_GET_BOOKMARKED_POSTS = "SELECT p.* FROM posts p JOIN bookmarked_posts bp ON p.postID = bp.postID WHERE bp.userID = ?";
    private static final String SQL_TOGGLE_ANONYMOUS = "UPDATE students SET isAnonymous = ? WHERE userID = ?";
    private static final String SQL_GET_STUDENT_BY_ID = "SELECT * FROM students WHERE userID = ?";

    // Add these with the other SQL constants at the top of the class
    private static final String SQL_REMOVE_FRIEND = "DELETE FROM friends WHERE (userID1 = ? AND userID2 = ?) OR (userID1 = ? AND userID2 = ?)";

    private static final String SQL_UNBLOCK_USER = "DELETE FROM blocked_users WHERE blockerID = ? AND blockedID = ?";

    private static final String SQL_REMOVE_GROUP_MEMBER = "DELETE FROM group_memberships WHERE groupID = ? AND studentID = ?";

    private static final String SQL_GET_MEMBER_JOIN_DATE = "SELECT joinDate FROM group_memberships WHERE groupID = ? AND studentID = ?";

    private static final String SQL_GET_MEMBER_END_DATE = "SELECT endDate FROM group_memberships WHERE groupID = ? AND studentID = ?";

    // Add this SQL constant at the top with other constants
    private static final String SQL_UPDATE_STUDENT_PASSWORD = "UPDATE students SET password = ? WHERE student_id = ?";

    // Add this with the other SQL constants at the top of the class
    private static final String SQL_ADD_GROUP_MEMBER = "INSERT INTO " + TABLE_MEMBERSHIPS
            + " (groupID, studentID, joinDate) VALUES (?, ?, ?)";

    private static final String SQL_CHECK_GROUP_MEMBERSHIP = "SELECT COUNT(*) FROM " + TABLE_MEMBERSHIPS + 
        " WHERE studentID = ? AND groupID = ? AND (endDate IS NULL OR endDate > CURRENT_TIMESTAMP)";

    private static final String SQL_JOIN_GROUP = "INSERT INTO " + TABLE_MEMBERSHIPS
            + " (studentID, groupID, joinDate) VALUES (?, ?, CURRENT_TIMESTAMP)";

    private static final String SQL_GET_GROUP_POSTS = "SELECT p.* FROM " + TABLE_POSTS + " p JOIN " + TABLE_POST_GROUPS + 
        " pg ON p.postID = pg.postID WHERE pg.groupID = ? ORDER BY p.postDate DESC";

    // Add with other SQL constants at the top
    private static final String SQL_FIND_GROUP_BY_NAME = "SELECT * FROM " + TABLE_GROUPS + " WHERE groupName = ?";

    // Add with other SQL constants
    private static final String SQL_CHECK_GROUP_EXISTS = "SELECT COUNT(*) FROM " + TABLE_GROUPS
            + " WHERE groupName = ?";

    // Add this constant with other SQL constants
    private static final String SQL_GET_MAX_GROUP_ID = "SELECT MAX(groupID) FROM " + TABLE_GROUPS;

    private static final String SQL_GET_MAX_POST_ID = "SELECT MAX(postID) FROM " + TABLE_POSTS;

    private static final String SQL_GET_STUDENT_GROUPS = "SELECT g.* FROM " + TABLE_GROUPS + " g JOIN " + 
        TABLE_MEMBERSHIPS + " m ON g.groupID = m.groupID WHERE m.studentID = ? AND (m.endDate IS NULL OR m.endDate > CURRENT_TIMESTAMP)";

    // Add these SQL constants with the others at the top
    private static final String SQL_CHECK_PENDING_REQUEST = "SELECT * FROM friend_requests WHERE fromUserID = ? AND toUserID = ? AND status = 'PENDING'";
    private static final String SQL_GET_INCOMING_REQUESTS = "SELECT s.* FROM students s JOIN friend_requests fr ON s.userID = fr.fromUserID WHERE fr.toUserID = ? AND fr.status = 'PENDING'";
    private static final String SQL_ACCEPT_REQUEST = "UPDATE friend_requests SET status = 'ACCEPTED' WHERE fromUserID = ? AND toUserID = ? AND status = 'PENDING'";
    private static final String SQL_DECLINE_REQUEST = "UPDATE friend_requests SET status = 'DECLINED' WHERE fromUserID = ? AND toUserID = ? AND status = 'PENDING'";
    private static final String SQL_CHECK_BLOCKED = "SELECT * FROM blocked_users WHERE blockerID = ? AND blockedID = ?";

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
                connection = DriverManager.getConnection(
                        DatabaseConfig.getConnectionUrl(),
                        DatabaseConfig.DB_USER,
                        DatabaseConfig.DB_PASSWORD);
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
                    System.out.println("- email: " + student.getEmail());
                    System.out.println("- Original password length: " + password.length());
                    System.out.println("- userYear: " + student.getYear());

                    stmt.setString(1, student.getEmail());
                    stmt.setString(2, hashPassword(password));
                    stmt.setString(3, student.getYear());
                    System.out.println("Parameters set, executing update...");
                }
        );
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
    @Override
    public boolean createPost(Post post) {
        try {
            // Generate and set a new unique ID
            int newId = generateUniquePostId();
            post.setID(newId);

            // First insert the post
            String query = "INSERT INTO posts (postID, postContent, postOwner) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, post.getID());
                stmt.setString(2, post.getContent());
                stmt.setInt(3, post.getOwner().getID());
                stmt.executeUpdate();

                // If post has a group, associate it with the group
                if (post.getGroup() != null) {
                    return addPostToGroup(post.getID(), post.getGroup().getID());
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating post: " + e.getMessage());
            return false;
        }
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
        System.out.println("\n====== MySQL Authentication Debug ======");
        System.out.println("Attempting authentication:");
        System.out.println("- Username: " + username);
        System.out.println("- Password hash: " + hashPassword(password));

        return executeQuery(
                SQL_AUTHENTICATE_STUDENT,
                stmt -> {
                    stmt.setString(1, username);
                    stmt.setString(2, hashPassword(password));
                },
                rs -> {
                    if (rs.next()) {
                        // Create student object
                        Student student = new Student(
                                rs.getInt("userID"),
                                username,
                                rs.getString("userName"),
                                rs.getString("userYear"),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>());

                        // Load student's groups
                        List<Group> groups = executeQuery(
                            SQL_GET_STUDENT_GROUPS,
                            groupStmt -> groupStmt.setInt(1, student.getID()),
                            groupRs -> {
                                List<Group> studentGroups = new ArrayList<>();
                                while (groupRs.next()) {
                                    Group group = new Group(
                                        groupRs.getInt("groupID"),
                                        groupRs.getString("groupName"),
                                        groupRs.getString("groupDescription")
                                    );
                                    studentGroups.add(group);
                                }
                                return studentGroups;
                            }
                        );
                        student.setGroups(groups);
                        System.out.println("Authentication successful! Loaded " + groups.size() + " groups.");
                        return student;
                    }
                    System.out.println("No matching record found");
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
                                rs.getString("groupDescription"));
                        // Set additional properties
                        group.setCreationDate(rs.getTimestamp("creationDate"));

                        // Load members for this group
                        executeQuery(
                                SQL_SELECT_GROUP_MEMBERS_DETAILED,
                                memberStmt -> memberStmt.setInt(1, group.getID()),
                                memberRs -> {
                                    while (memberRs.next()) {
                                        Student member = new Student(
                                                memberRs.getInt("userID"),
                                                null,
                                                memberRs.getString("userName"),
                                                memberRs.getString("userYear"),
                                                new ArrayList<>(),
                                                new ArrayList<>(),
                                                new ArrayList<>());
                                        group.addMember(member);

                                        // Load join and end dates
                                        Date joinDate = getMemberJoinDate(group.getID(), member.getID());
                                        Date endDate = getMemberEndDate(group.getID(), member.getID());
                                        if (joinDate != null) {
                                            group.setMemberJoinDate(member, joinDate);
                                        }
                                        if (endDate != null) {
                                            group.setMemberEndDate(member, endDate);
                                        }
                                    }
                                    return null;
                                });
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
                                null,
                                rs.getString("userName"),
                                rs.getString("userYear"),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()));
                    }
                    return members;
                });
    }

    private int generateUniqueGroupId() {
        return executeQuery(SQL_GET_MAX_GROUP_ID,
                stmt -> {
                },
                rs -> rs.next() ? rs.getInt(1) + 1 : 1);
    }

    @Override
    public boolean addGroup(Group group) {
        if (doesGroupExist(group.getName())) {
            System.out.println("Group with name '" + group.getName() + "' already exists");
            return false;
        }

        // Generate and set a new unique ID
        int newId = generateUniqueGroupId();
        group.setID(newId);

        return executeUpdate(SQL_INSERT_GROUP,
                stmt -> {
                    stmt.setInt(1, group.getID());
                    stmt.setString(2, group.getName());
                    stmt.setString(3, group.getDescription());
                    System.out.println("Adding new group: " + group.getName());
                });
    }

    private boolean doesGroupExist(String groupName) {
        return executeQuery(SQL_CHECK_GROUP_EXISTS,
                stmt -> stmt.setString(1, groupName),
                rs -> rs.next() && rs.getInt(1) > 0);
    }

    @Override
    public boolean addMemberToGroup(int groupId, int studentId) {
        // First check if the student is already a member with an active membership
        if (isStudentInGroup(studentId, groupId)) {
            System.out.println("Student " + studentId + " is already a member of group " + groupId);
            return false;
        }

        return executeUpdate(
                SQL_ADD_GROUP_MEMBER,
                stmt -> {
                    stmt.setInt(1, groupId);
                    stmt.setInt(2, studentId);
                    stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                    System.out.println("Adding member " + studentId + " to group " + groupId);
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
        System.out.println("\n====== MySQL Get Student Debug ======");
        System.out.println("Looking up student with username: " + username);
        System.out.println("Using query: " + SQL_SELECT_STUDENT_BY_USERNAME);
        
        return executeQuery(
            SQL_SELECT_STUDENT_BY_USERNAME,
            stmt -> {
                stmt.setString(1, username);
                System.out.println("Parameters set, executing query...");
            },
            rs -> {
                if (rs.next()) {
                    System.out.println("Found student:");
                    System.out.println("- userID: " + rs.getInt("userID"));
                    System.out.println("- userName: " + rs.getString("userName"));
                    System.out.println("- userYear: " + rs.getString("userYear"));
                    return new Student(
                        rs.getInt("userID"),
                        rs.getString("userName"),
                        rs.getString("userName"),
                        rs.getString("userYear"),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>());
                }
                System.out.println("No student found with username: " + username);
                return null;
            });
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

    @Override
    public boolean leaveGroup(int studentId, int groupId) {
        boolean success = executeUpdate(
            SQL_LEAVE_GROUP,
            stmt -> {
                stmt.setInt(1, studentId);
                stmt.setInt(2, groupId);
                System.out.println("Removing student " + studentId + " from group " + groupId);
            }
        );
        if (success) {
            // Update the group size after a member leaves
            updateGroupSize(groupId);
        }
        return success;
    }

    @Override
    public boolean leaveGroup(Group group, Student student) {
        return leaveGroup(student.getID(), group.getID());
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
                    null,
                    rs.getString("userName"),
                    rs.getString("userYear"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()));
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
                    null // Group will be loaded separately if needed
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
                                null,
                                rs.getString("userName"),
                                rs.getString("userYear"),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>());
                    }
                    return null;
                });
    }

    // Add helper methods for member dates
    private Date getMemberJoinDate(int groupId, int studentId) {
        return executeQuery(
                SQL_GET_MEMBER_JOIN_DATE,
                stmt -> {
                    stmt.setInt(1, groupId);
                    stmt.setInt(2, studentId);
                },
                rs -> rs.next() ? rs.getTimestamp("joinDate") : null);
    }

    private Date getMemberEndDate(int groupId, int studentId) {
        return executeQuery(
                SQL_GET_MEMBER_END_DATE,
                stmt -> {
                    stmt.setInt(1, groupId);
                    stmt.setInt(2, studentId);
                },
                rs -> rs.next() ? rs.getTimestamp("endDate") : null);
    }

    public boolean removeFriend(int userId, int friendId) {
        return executeUpdate(
                SQL_REMOVE_FRIEND,
                stmt -> {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, friendId);
                    stmt.setInt(3, friendId);
                    stmt.setInt(4, userId);
                    System.out.println("Removing friendship between users " + userId + " and " + friendId);
                });
    }

    public boolean unblockUser(int blockerId, int blockedId) {
        return executeUpdate(
                SQL_UNBLOCK_USER,
                stmt -> {
                    stmt.setInt(1, blockerId);
                    stmt.setInt(2, blockedId);
                    System.out.println("Unblocking user " + blockedId + " for blocker " + blockerId);
                });
    }

    public boolean removeMemberFromGroup(int groupId, int studentId) {
        return executeUpdate(
                SQL_REMOVE_GROUP_MEMBER,
                stmt -> {
                    stmt.setInt(1, groupId);
                    stmt.setInt(2, studentId);
                    System.out.println("Removing member " + studentId + " from group " + groupId);
                });
    }

    public boolean deleteStudent(int studentId) {
        return executeUpdate(
                SQL_DELETE_STUDENT,
                stmt -> {
                    stmt.setInt(1, studentId);
                    System.out.println("Deleting student with ID: " + studentId);
                });
    }

    // Add this method to the class
    public boolean updateStudentPassword(int studentId, String newPassword) {
        return executeUpdate(
                SQL_UPDATE_STUDENT_PASSWORD,
                stmt -> {
                    stmt.setString(1, hashPassword(newPassword));
                    stmt.setInt(2, studentId);
                    System.out.println("Updating password for student ID: " + studentId);
                });
    }

    @Override
    public List<Post> getGroupPosts(int groupId) {
        return executeQuery(
            SQL_GET_GROUP_POSTS,
            stmt -> stmt.setInt(1, groupId),
            rs -> {
                List<Post> posts = new ArrayList<>();
                while (rs.next()) {
                    Student owner = getStudentById(rs.getInt("postOwner"));
                    Post post = new Post(
                        rs.getInt("postID"),
                        rs.getString("postContent"),
                        owner,
                        getGroupByID(groupId)
                    );
                    posts.add(post);
                }
                return posts;
            }
        );
    }

    @Override
    public Group getGroupByID(int groupId) {
        return executeQuery(
            "SELECT * FROM " + TABLE_GROUPS + " WHERE groupID = ?",
            stmt -> stmt.setInt(1, groupId),
            rs -> rs.next() ? new Group(
                rs.getInt("groupID"),
                rs.getString("groupName"),
                rs.getString("groupDescription")
            ) : null
        );
    }

    @Override
    public boolean isStudentInGroup(int studentId, int groupId) {
        return executeQuery(
            SQL_CHECK_GROUP_MEMBERSHIP,
            stmt -> {
                stmt.setInt(1, studentId);
                stmt.setInt(2, groupId);
            },
            rs -> rs.next() && rs.getInt(1) > 0
        );
    }

    @Override
    public boolean joinGroup(int studentId, int groupId) {
        return executeUpdate(SQL_JOIN_GROUP,
                stmt -> {
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, groupId);
                    System.out.println("Adding student " + studentId + " to group " + groupId);
                });
    }

    // @Override
    // public boolean leaveGroup(int studentId, int groupId) {
    //     return executeUpdate(SQL_LEAVE_GROUP,
    //             stmt -> {
    //                 stmt.setInt(1, studentId);
    //                 stmt.setInt(2, groupId);
    //                 System.out.println("Removing student " + studentId + " from group " + groupId);
    //             });
    // }

    @Override
    public Group findGroupByName(String groupName) {
        return executeQuery(SQL_FIND_GROUP_BY_NAME,
                stmt -> stmt.setString(1, groupName),
                rs -> {
                    if (!rs.next()) return null;
                    Group group = new Group(
                            rs.getInt("groupID"),
                            rs.getString("groupName"),
                            rs.getString("groupDescription"));
                    
                    // Load members for this group
                    executeQuery(
                            SQL_SELECT_GROUP_MEMBERS_DETAILED,
                            memberStmt -> memberStmt.setInt(1, group.getID()),
                            memberRs -> {
                                while (memberRs.next()) {
                                    Student member = new Student(
                                            memberRs.getInt("userID"),
                                            null,
                                            memberRs.getString("userName"),
                                            memberRs.getString("userYear"),
                                            new ArrayList<>(),
                                            new ArrayList<>(),
                                            new ArrayList<>());
                                    group.addMember(member);

                                    // Load join and end dates
                                    Date joinDate = getMemberJoinDate(group.getID(), member.getID());
                                    Date endDate = getMemberEndDate(group.getID(), member.getID());
                                    if (joinDate != null) {
                                        group.setMemberJoinDate(member, joinDate);
                                    }
                                    if (endDate != null) {
                                        group.setMemberEndDate(member, endDate);
                                    }
                                }
                                return group;
                            });
                    return group;
                });
    }

    private int generateUniquePostId() {
        return executeQuery(SQL_GET_MAX_POST_ID,
                stmt -> {},
                rs -> rs.next() ? rs.getInt(1) + 1 : 1);
    }

    @Override
    public boolean deletePost(int postId) {
        return executeUpdate(
            SQL_DELETE_POST,
            stmt -> {
                stmt.setInt(1, postId);
                System.out.println("Deleting post with ID: " + postId);
            }
        );
    }

    @Override
    public boolean hasPendingFriendRequest(int senderId, int receiverId) {
        return executeQuery(
            SQL_CHECK_PENDING_REQUEST,
            stmt -> {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            },
            ResultSet::next
        );
    }

    @Override
    public boolean acceptFriendRequest(int requesterId, int accepterId) {
        return executeUpdate(
            SQL_ACCEPT_REQUEST,
            stmt -> {
            stmt.setInt(1, requesterId);
            stmt.setInt(2, accepterId);
        }
        );
    }

    @Override
    public boolean declineFriendRequest(int requesterId, int declinerId) {
        return executeUpdate(
            SQL_DECLINE_REQUEST,
            stmt -> {
            stmt.setInt(1, requesterId);
            stmt.setInt(2, declinerId);
        }
        );
    }

    @Override
    public boolean isUserBlocked(int blockerId, int blockedId) {
        return executeQuery(
            SQL_CHECK_BLOCKED,
            stmt -> {
            stmt.setInt(1, blockerId);
            stmt.setInt(2, blockedId);
            },
            ResultSet::next
        );
    }

    @Override
    public List<Student> getIncomingFriendRequests(int studentId) {
        return executeQuery(
            SQL_GET_INCOMING_REQUESTS,
            stmt -> stmt.setInt(1, studentId),
            this::mapResultSetToStudentList
        );
    }

}
