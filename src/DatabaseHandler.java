import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseHandler extends Database implements DatabaseOperations {
    private final MySQLHandler sqlHandler;

    public DatabaseHandler(Database database) {
        super(database.getDbName());
        this.sqlHandler = (MySQLHandler) database;
        System.out.println("DatabaseHandler created for database: " + database.getDbName());
    }

    public Database getDatabase() {
        return sqlHandler;
    }

    @Override
    public void connect() {
        // Connection is managed by MySQLHandler singleton, no need to connect here
    }

    @Override
    public void disconnect() {
        // Connection is managed by MySQLHandler singleton, no need to disconnect here
    }

    @Override
    public boolean addStudent(Student student, String password) {
        return sqlHandler.addStudent(student, password);
    }

    @Override
    public Student getStudentByUsername(String username) {
        return sqlHandler.getStudentByUsername(username);
    }

    public boolean updateStudent(Student student) {
        return sqlHandler.updateStudent(student.getID(), student.getName(), student.getYear());
    }

    @Override
    public boolean updateStudent(int id, String newName, String newYear) {
        return sqlHandler.updateStudent(id, newName, newYear);
    }

    @Override
    public boolean removeStudent(int id) {
        return sqlHandler.removeStudent(id);
    }

    @Override
    public Student authenticateStudent(String username, String password) {
        return sqlHandler.authenticateStudent(username, password);
    }

    @Override
    public boolean doesUsernameExist(String username) {
        return sqlHandler.doesUsernameExist(username);
    }

    @Override
    public List<Group> getAllGroups() {
        return sqlHandler.getAllGroups();
    }

    @Override
    public boolean addGroup(Group group) {
        System.out.println("\n====== DatabaseHandler Debug ======");
        System.out.println("Forwarding addGroup call to MySQLHandler");
        System.out.println("Group details:");
        System.out.println("- ID: " + group.getID());
        System.out.println("- Name: " + group.getName());
        System.out.println("- Description: " + group.getDescription());
        return sqlHandler.addGroup(group);
    }

    @Override
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
    public boolean createPost(Post post) {
        return sqlHandler.createPost(post);
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

    @Override
    public boolean sendFriendRequest(int fromUserId, int toUserId) {
        return sqlHandler.sendFriendRequest(fromUserId, toUserId);
    }

    @Override
    public boolean acceptFriendRequest(int requestId) {
        return sqlHandler.acceptFriendRequest(requestId);
    }

    @Override
    public boolean declineFriendRequest(int requestId) {
        return sqlHandler.declineFriendRequest(requestId);
    }

    @Override
    public boolean blockUser(int blockerId, int blockedId) {
        return sqlHandler.blockUser(blockerId, blockedId);
    }

    @Override
    public boolean bookmarkPost(int userId, int postId) {
        return sqlHandler.bookmarkPost(userId, postId);
    }

    @Override
    public boolean toggleAnonymousMode(int userId, boolean isAnonymous) {
        return sqlHandler.toggleAnonymousMode(userId, isAnonymous);
    }

    @Override
    public List<Student> getFriendRequests(int userId) {
        return sqlHandler.getFriendRequests(userId);
    }

    @Override
    public List<Student> getFriends(int userId) {
        return sqlHandler.getFriends(userId);
    }

    @Override
    public List<Student> getBlockedUsers(int userId) {
        return sqlHandler.getBlockedUsers(userId);
    }

    @Override
    public List<Post> getBookmarkedPosts(int userId) {
        return sqlHandler.getBookmarkedPosts(userId);
    }

    @Override
    public boolean removeFriend(int userId, int friendId) {
        return sqlHandler.removeFriend(userId, friendId);
    }

    @Override
    public boolean unblockUser(int blockerId, int blockedId) {
        return sqlHandler.unblockUser(blockerId, blockedId);
    }

    @Override
    public boolean removeMemberFromGroup(int groupId, int studentId) {
        return sqlHandler.removeMemberFromGroup(groupId, studentId);
    }

    @Override
    public boolean deleteStudent(int studentId) {
        return sqlHandler.deleteStudent(studentId);
    }

    @Override
    public boolean updateStudentPassword(int studentId, String newPassword) {
        return sqlHandler.updateStudentPassword(studentId, newPassword);
    }

    @Override
    public List<Post> getGroupPosts(int groupId) {
        return sqlHandler.getGroupPosts(groupId);
    }

    public Group findGroupByName(String groupName) {
        return sqlHandler.findGroupByName(groupName);
    }

    @Override
    public boolean isStudentInGroup(int studentId, int groupId) {
        return sqlHandler.isStudentInGroup(studentId, groupId);
    }

    @Override
    public boolean joinGroup(int studentId, int groupId) {
        return sqlHandler.joinGroup(studentId, groupId);
    }

    @Override
    public boolean leaveGroup(int studentId, int groupId) {
        return sqlHandler.leaveGroup(studentId, groupId);
    }

    @Override
    public Group getGroupByID(int groupId) {
        return sqlHandler.getGroupByID(groupId);
    }

    @Override
    public boolean deletePost(int postId) {
        return sqlHandler.deletePost(postId);
    }

    @Override
    public boolean hasPendingFriendRequest(int senderId, int receiverId) {
        return sqlHandler.hasPendingFriendRequest(senderId, receiverId);
    }

    @Override
    public List<Student> getIncomingFriendRequests(int studentId) {
        return sqlHandler.getIncomingFriendRequests(studentId);
    }

    @Override
    public boolean acceptFriendRequest(int requesterId, int accepterId) {
        return sqlHandler.acceptFriendRequest(requesterId, accepterId);
    }

    @Override
    public boolean declineFriendRequest(int requesterId, int declinerId) {
        return sqlHandler.declineFriendRequest(requesterId, declinerId);
    }

    @Override
    public boolean isUserBlocked(int blockerId, int blockedId) {
        return sqlHandler.isUserBlocked(blockerId, blockedId);
    }

    @Override
    public boolean resetDatabase() {
        return sqlHandler.resetDatabase();
    }

    @Override
    public boolean executeUpdate(String sql) {
        return sqlHandler.executeUpdate(sql);
    }

    public List<Student> getAllStudents() {
        return sqlHandler.getAllStudents();
    }
}