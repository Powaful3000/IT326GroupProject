import java.util.ArrayList;
import java.util.List;

public class PostHandler {

    // List to store posts (can be replaced with database integration)
    private List<Post> posts;

    // Constructor
    public PostHandler() {
        this.posts = new ArrayList<>();
    }

    // Add a new post
    public boolean addPost(Post post) {
        if (post == null) {
            System.err.println("Cannot add null post.");
            return false;
        }
        if (getPostById(post.getID()) != null) {
            System.err.println("Post with ID " + post.getID() + " already exists.");
            return false;
        }
        posts.add(post);
        System.out.println("Post added by " + post.getOwner().getName() + ": " + post.getContent());
        return true;
    }

    // Remove a post by its ID
    public boolean removePost(int postId) {
        Post post = getPostById(postId);
        if (post == null) {
            System.err.println("Post with ID " + postId + " not found.");
            return false;
        }
        posts.remove(post);
        System.out.println("Post removed: " + post.getContent());
        return true;
    }

    // Update a post's content
    public boolean updatePostContent(int postId, String newContent) {
        Post post = getPostById(postId);
        if (post == null) {
            System.err.println("Post with ID " + postId + " not found.");
            return false;
        }
        post.setContent(newContent);
        System.out.println("Post updated: " + post.getContent());
        return true;
    }

    // Retrieve a post by its ID
    public Post getPostById(int postId) {
        for (Post post : posts) {
            if (post.getID() == postId) {
                return post;
            }
        }
        return null; // Not found
    }

    // Retrieve all posts for a specific student
    public List<Post> getPostsByStudent(Student student) {
        List<Post> studentPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.isOwnedBy(student)) {
                studentPosts.add(post);
            }
        }
        return studentPosts;
    }

    // Retrieve all posts in a specific group
    public List<Post> getPostsByGroup(Group group) {
        List<Post> groupPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.isInGroup(group)) {
                groupPosts.add(post);
            }
        }
        return groupPosts;
    }

    // Print all posts
    public void printAllPosts() {
        if (posts.isEmpty()) {
            System.out.println("No posts found.");
        } else {
            for (Post post : posts) {
                System.out.println("Post ID: " + post.getID() + ", Content: " + post.getContent() +
                        ", Owner: " + post.getOwner().getName() + ", Group: " + post.getGroup().getName());
            }
        }
    }
}
