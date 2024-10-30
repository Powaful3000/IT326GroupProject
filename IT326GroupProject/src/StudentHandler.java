import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// StudentHandler class extending DatabaseHandler to manage Student entities
public class StudentHandler extends DatabaseHandler<Student> {

    // Constructor to pass the connection to the superclass
    public StudentHandler(Connection connection) {
        super(connection);
    }

    // Add a Student to the database
    @Override
    public void add(Student student) {
        String sql = "INSERT INTO students (userID, userName, userYear) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, student.getID());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getYear());
            stmt.executeUpdate();
            System.out.println("Student added to the database: " + student);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add Student to the database.");
        }
    }

    // Retrieve a Student by ID from the database
    @Override
    public Student getByID(int id) {
        String sql = "SELECT * FROM students WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Student(
                    rs.getInt("userID"),
                    rs.getString("userName"),
                    rs.getString("userYear")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve Student from the database.");
        }
        return null;
    }

    // Update a Student in the database
    @Override
    public void update(Student student) {
        String sql = "UPDATE students SET userName = ?, userYear = ? WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getYear());
            stmt.setInt(3, student.getID());
            stmt.executeUpdate();
            System.out.println("Student updated in the database: " + student);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update Student in the database.");
        }
    }

    // Delete a Student by ID from the database
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM students WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student deleted from the database. ID: " + id);
            } else {
                System.out.println("No Student found with ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete Student from the database.");
        }
    }

    // List all Students in the database
    @Override
    public List<Student> listAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("userID"),
                    rs.getString("userName"),
                    rs.getString("userYear")
                );
                students.add(student);
            }
            System.out.println("Retrieved all Students from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to list all Students from the database.");
        }
        return students;
    }

    // Add a tag to a Student
    public void addTag(int studentID, String tag) {
        String sql = "INSERT INTO student_tags (userID, tagName) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentID);
            stmt.setString(2, tag);
            stmt.executeUpdate();
            System.out.println("Tag added to Student ID: " + studentID);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add tag to Student.");
        }
    }

    // Edit a tag for a Student
    public void editTag(int studentID, String oldTag, String newTag) {
        String sql = "UPDATE student_tags SET tagName = ? WHERE userID = ? AND tagName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newTag);
            stmt.setInt(2, studentID);
            stmt.setString(3, oldTag);
            stmt.executeUpdate();
            System.out.println("Tag updated for Student ID: " + studentID);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update tag for Student.");
        }
    }

    // Delete a tag for a Student
    public void deleteTag(int studentID, String tag) {
        String sql = "DELETE FROM student_tags WHERE userID = ? AND tagName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentID);
            stmt.setString(2, tag);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Tag deleted for Student ID: " + studentID);
            } else {
                System.out.println("No tag found for Student ID: " + studentID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete tag for Student.");
        }
    }
}
