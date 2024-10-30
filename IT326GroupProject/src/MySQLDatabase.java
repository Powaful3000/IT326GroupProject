import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// MySQLDatabase class extending DatabaseHandler to manage entities in MySQL
public class MySQLDatabase<T> extends DatabaseHandler<T> {

    // Constructor to establish a connection and pass it to the superclass
    public MySQLDatabase(String url, String user, String password) {
        super(null); // Call the superclass constructor with a null connection initially
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            super.connection = this.connection; // Set the superclass's connection
            System.out.println("Connected to MySQL database successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to MySQL database.");
        }
    }

    // Add an entity to the database
    @Override
    public void add(T entity) {
        if (entity instanceof Student) {
            addStudent((Student) entity);
        } else if (entity instanceof Group) {
            addGroup((Group) entity);
        } else if (entity instanceof Post) {
            addPost((Post) entity);
        } else {
            System.out.println("Unsupported entity type.");
        }
    }

    // Retrieve an entity by ID
    @Override
    public T getByID(int id) {
        // Implement based on the specific type
        System.out.println("Generic retrieval not implemented.");
        return null;
    }

    // Update an entity in the database
    @Override
    public void update(T entity) {
        if (entity instanceof Student) {
            updateStudent((Student) entity);
        } else if (entity instanceof Group) {
            updateGroup((Group) entity);
        } else if (entity instanceof Post) {
            updatePost((Post) entity);
        } else {
            System.out.println("Unsupported entity type.");
        }
    }

    // Delete an entity by ID
    @Override
    public void delete(int id) {
        // Implement deletion logic specific to each entity type
        System.out.println("Generic deletion not implemented.");
    }

    // List all entities in the database
    @Override
    public List<T> listAll() {
        // Implement listing logic specific to each entity type
        System.out.println("Generic listing not implemented.");
        return new ArrayList<>();
    }

    // Method to add a Student to the MySQL database
    private void addStudent(Student student) {
        String sql = "INSERT INTO students (userID, userName, userYear) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, student.getID());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getYear());
            stmt.executeUpdate();
            System.out.println("Student added to the database: " + student);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a Student in the MySQL database
    private void updateStudent(Student student) {
        String sql = "UPDATE students SET userName = ?, userYear = ? WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getYear());
            stmt.setInt(3, student.getID());
            stmt.executeUpdate();
            System.out.println("Student updated in the database: " + student);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to add a Group to the MySQL database
    private void addGroup(Group group) {
        String sql = "INSERT INTO groups (groupID, groupName, groupDescription) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, group.getID());
            stmt.setString(2, group.getName());
            stmt.setString(3, group.getDescription());
            stmt.executeUpdate();
            System.out.println("Group added to the database: " + group);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a Group in the MySQL database
    private void updateGroup(Group group) {
        String sql = "UPDATE groups SET groupName = ?, groupDescription = ? WHERE groupID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, group.getName());
            stmt.setString(2, group.getDescription());
            stmt.setInt(3, group.getID());
            stmt.executeUpdate();
            System.out.println("Group updated in the database: " + group);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to add a Post to the MySQL database
    private void addPost(Post post) {
        String sql = "INSERT INTO posts (postID, postContent, postOwner) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, post.getID());
            stmt.setString(2, post.getContent());
            stmt.setInt(3, post.getOwner());
            stmt.executeUpdate();
            System.out.println("Post added to the database: " + post);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update a Post in the MySQL database
    private void updatePost(Post post) {
        String sql = "UPDATE posts SET postContent = ? WHERE postID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, post.getContent());
            stmt.setInt(2, post.getID());
            stmt.executeUpdate();
            System.out.println("Post updated in the database: " + post);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
