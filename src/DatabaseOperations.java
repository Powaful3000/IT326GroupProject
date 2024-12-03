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

    boolean createPost(Post post);

    boolean addTagToStudent(int studentId, int tagId);

    boolean removeTagFromStudent(int studentId, int tagId);

    boolean addPostToGroup(int postId, int groupId);

    boolean updateMembershipEndDate(int studentId, int groupId, java.sql.Date endDate);

    boolean sendFriendRequest(int fromUserId, int toUserId);

    boolean acceptFriendRequest(int requestId);

    boolean declineFriendRequest(int requestId);

    List<Student> getFriendRequests(int userId);

    List<Student> getFriends(int userId);

    boolean blockUser(int blockerId, int blockedId);

    List<Student> getBlockedUsers(int userId);

    boolean bookmarkPost(int userId, int postId);

    List<Post> getBookmarkedPosts(int userId);

    boolean toggleAnonymousMode(int userId, boolean isAnonymous);

    boolean removeFriend(int userId, int friendId);

    boolean unblockUser(int blockerId, int blockedId);

    boolean removeMemberFromGroup(int groupId, int studentId);

    boolean deleteStudent(int studentId);
    
    boolean updateStudentPassword(int studentId, String newPassword);

    List<Post> getGroupPosts(int groupId);
}