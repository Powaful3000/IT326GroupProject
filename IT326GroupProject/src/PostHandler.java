import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// PostHandler class extending DatabaseHandler to manage Post entities
public class PostHandler extends DatabaseHandler<Post> {

    // Constructor to pass the connection to the superclass
    public PostHandler(Connection connection) {
        super(connection);
    }

    // Add a Post to the database
    @Override
    public void add(Post post) {
        String sql = "INSERT INTO posts (postID, postContent, postOwner) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, post.getID());
            stmt.setString(2, post.getContent());
            stmt.setInt(3, post.getOwner());
            stmt.executeUpdate();
            System.out.println("Post added to the database: " + post);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add Post to the database.");
        }
    }

    // Retrieve a Post by ID from the database
    @Override
    public Post getByID(int id) {
        String sql = "SELECT * FROM posts WHERE postID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Post(
                    rs.getInt("postID"),
                    rs.getString("postContent"),
                    rs.getInt("postOwner")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve Post from the database.");
        }
        return null;
    }

    // Update a Post in the database
    @Override
    public void update(Post post) {
        String sql = "UPDATE posts SET postContent = ?, postOwner = ? WHERE postID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, post.getContent());
            stmt.setInt(2, post.getOwner());
            stmt.setInt(3, post.getID());
            stmt.executeUpdate();
            System.out.println("Post updated in the database: " + post);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update Post in the database.");
        }
    }

    // Delete a Post by ID from the database
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM posts WHERE postID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Post deleted from the database. ID: " + id);
            } else {
                System.out.println("No Post found with ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete Post from the database.");
        }
    }

    // List all Posts in the database
    @Override
    public List<Post> listAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Post post = new Post(
                    rs.getInt("postID"),
                    rs.getString("postContent"),
                    rs.getInt("postOwner")
                );
                posts.add(post);
            }
            System.out.println("Retrieved all Posts from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to list all Posts from the database.");
        }
        return posts;
    }

    // Create a new Post
    public Post createPost(int groupID, String content, int ownerID) {
        Post newPost = new Post(0, content, ownerID); // Assuming postID is auto-generated
        add(newPost);
        return newPost;
    }

    // Edit a Post by ID
    public void editPost(int postID, String newContent) {
        String sql = "UPDATE posts SET postContent = ? WHERE postID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newContent);
            stmt.setInt(2, postID);
            stmt.executeUpdate();
            System.out.println("Post updated in the database. ID: " + postID);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to edit Post in the database.");
        }
    }

    // Delete a Post by ID
    public void deletePost(int postID) {
        delete(postID);
    }
}
