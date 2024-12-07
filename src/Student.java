import java.util.List;
import java.util.ArrayList;

public class Student {

    // Attributes
    private int userID;
    private String userEmail;
    private String userName;
    private String userYear;
    private List<Tag> userTags;
    private List<Group> userGroups;
    private List<Post> userPosts;
    private List<Student> friends;
    private List<Student> blockedUsers;
    private List<Student> friendRequests;
    private List<Post> bookmarkedPosts;
    private boolean isAnonymous;
    private String username;

    // Constructor
    public Student(int userID, String email, String userName, String userYear, List<Tag> userTags, List<Group> userGroups,
            List<Post> userPosts) {
        this.userID = userID;
        this.userEmail = email;
        this.userName = userName;
        this.userYear = userYear;
        this.userTags = userTags != null ? userTags : new ArrayList<>();
        this.userGroups = userGroups != null ? userGroups : new ArrayList<>();
        this.userPosts = userPosts != null ? userPosts : new ArrayList<>();
        this.friends = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
        this.friendRequests = new ArrayList<>();
        this.bookmarkedPosts = new ArrayList<>();
        this.isAnonymous = false;
    }

    // Getters
    public int getID() {
        return userID;
    }

    public String getEmail() {
    	return userEmail;
    }
    
    public String getName() {
        return userName;
    }

    public String getYear() {
        return userYear;
    }

    public List<Tag> getTags() {
        return userTags;
    }

    public List<Group> getGroups() {
        return userGroups;
    }

    public List<Post> getPosts() {
        return userPosts;
    }

    public String getUsername() {
        return username;
    }

    // Setters
    public void setID(int userID) {
        this.userID = userID;
    }

    public void setEmail(String email) {
    	this.userEmail = email;
    }
    
    public void setName(String userName) {
        this.userName = userName;
    }

    public void setYear(String userYear) {
        this.userYear = userYear;
    }

    public void setTags(List<Tag> userTags) {
        this.userTags = userTags;
    }

    public void setGroups(List<Group> userGroups) {
        this.userGroups = userGroups;
    }

    public void setPosts(List<Post> userPosts) {
        this.userPosts = userPosts;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Method to add a tag
    public void addTag(Tag tag) {
        if (!userTags.contains(tag)) {
            userTags.add(tag);
        }
    }

    // Method to remove a tag
    public void removeTag(Tag tag) {
        userTags.remove(tag);
    }

    // Method to join a group
    public void joinGroup(Group group) {
        if (!userGroups.contains(group)) {
            userGroups.add(group);
        }
    }

    // Method to leave a group
    public void leaveGroup(Group group) {
        userGroups.remove(group);
    }

    // Method to add a post
    public void addPost(Post post) {
        if (!userPosts.contains(post)) {
            userPosts.add(post);
        }
    }

    // Method to remove a post
    public void removePost(Post post) {
        userPosts.remove(post);
    }

    // Add new methods for friend management
    public void sendFriendRequest(Student other) {
        if (!friends.contains(other) && !blockedUsers.contains(other)) {
            other.receiveFriendRequest(this);
        }
    }

    public void receiveFriendRequest(Student from) {
        if (!friendRequests.contains(from)) {
            friendRequests.add(from);
        }
    }

    public void acceptFriendRequest(Student from) {
        if (friendRequests.contains(from)) {
            friendRequests.remove(from);
            friends.add(from);
            from.friends.add(this);
        }
    }

    public void declineFriendRequest(Student from) {
        friendRequests.remove(from);
    }

    public void removeFriend(Student friend) {
        friends.remove(friend);
        friend.friends.remove(this);
    }

    public void blockUser(Student user) {
        if (!blockedUsers.contains(user)) {
            blockedUsers.add(user);
            friends.remove(user);
            user.friends.remove(this);
        }
    }

    public void bookmarkPost(Post post) {
        if (!bookmarkedPosts.contains(post)) {
            bookmarkedPosts.add(post);
        }
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void toggleAnonymousMode() {
        this.isAnonymous = !this.isAnonymous;
    }
}
