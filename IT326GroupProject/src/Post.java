public class Post {

    // Attributes
    private int postID;
    private String postContent;
    private int postOwner; // userID of the owner

    // Constructor
    public Post(int postID, String postContent, int postOwner) {
        this.postID = postID;
        this.postContent = postContent;
        this.postOwner = postOwner;
    }

    // Getter methods
    public int getID() {
        return postID;
    }

    public String getContent() {
        return postContent;
    }

    public int getOwner() {
        return postOwner;
    }

    // Setter method to edit post content
    public void setContent(String newContent) {
        this.postContent = newContent;
    }

    // String representation of the Post class
    @Override
    public String toString() {
        return "Post ID: " + postID + ", Content: \"" + postContent + "\", Owner ID: " + postOwner;
    }
}
