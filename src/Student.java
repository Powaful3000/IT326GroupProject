import java.util.List;

public class Student {

    // Attributes
    private int userID;
    private String userName;
    private String userYear;
    private List<Tag> userTags;
    private List<Group> userGroups;
    private List<Post> userPosts;

    // Constructor
    public Student(int userID, String userName, String userYear, List<Tag> userTags, List<Group> userGroups,
            List<Post> userPosts) {
        this.userID = userID;
        this.userName = userName;
        this.userYear = userYear;
        this.userTags = userTags;
        this.userGroups = userGroups;
        this.userPosts = userPosts;
    }

    // Getters
    public int getID() {
        return userID;
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

    // Setters
    public void setID(int userID) {
        this.userID = userID;
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
}
