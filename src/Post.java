public class Post {

    // Attributes
    private int postID;
    private String postContent;
    private Student postOwner;
    private Group postGroup;

    // Constructor
    public Post(int postID, String postContent, Student postOwner, Group postGroup) {
        this.postID = postID;
        this.postContent = postContent;
        this.postOwner = postOwner;
        this.postGroup = postGroup;
    }

    // Getters
    public int getID() {
        return postID;
    }

    public String getContent() {
        return postContent;
    }

    public Student getOwner() {
        return postOwner;
    }

    public Group getGroup() {
        return postGroup;
    }

    // Setters
    public void setID(int postID) {
        this.postID = postID;
    }

    public void setContent(String postContent) {
        this.postContent = postContent;
    }

    public void setOwner(Student postOwner) {
        this.postOwner = postOwner;
    }

    public void setGroup(Group postGroup) {
        this.postGroup = postGroup;
    }

    // Method to check if the post belongs to a specific group
    public boolean isInGroup(Group group) {
        return this.postGroup.equals(group);
    }

    // Method to check if the post belongs to a specific student
    public boolean isOwnedBy(Student student) {
        return this.postOwner.equals(student);
    }

    // Method to print post details
    public void printDetails() {
        System.out.println("Post ID: " + postID);
        System.out.println("Content: " + postContent);
        System.out.println("Owner: " + postOwner.getName());
        System.out.println("Group: " + postGroup.getName());
    }
}
