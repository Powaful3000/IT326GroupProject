import java.util.List;
import java.util.ArrayList;

public class StudentHandler {

    // List to store students (can be replaced with database integration)
    private List<Student> students;

    private Student currentStudent;

    private static StudentHandler instance; // Add singleton pattern
    private DatabaseHandler dbHandler;
    private TagHandler tagHandler;

    public static StudentHandler getInstance() {
        if (instance == null) {
            instance = new StudentHandler();
        }
        return instance;
    }

    // Constructor
    private StudentHandler() {
        this.students = new ArrayList<>();
        Database database = DatabaseFactory.getDatabase(DatabaseFactory.DatabaseType.MYSQL, "StudentDB");
        this.dbHandler = new DatabaseHandler(database);
        this.tagHandler = new TagHandler(dbHandler);
    }

    // Add a student
    public boolean addStudent(Student student) {
        if (student == null) {
            System.err.println("Cannot add null student.");
            return false;
        }

        Student existingStudent = getStudentById(student.getID());
        if (existingStudent != null) {
            // Update existing student instead of adding duplicate
            existingStudent.setName(student.getName());
            existingStudent.setYear(student.getYear());
            System.out.println("Updated existing student: " + student.getName());
            return true;
        }

        students.add(student);
        System.out.println("Student added: " + student.getName());
        return true;
    }

    // Remove a student
    public boolean removeStudent(int id) {
        // Validate input
        if (!Validator.isValidId(id)) {
            System.err.println("Removal failed: Invalid ID.");
            return false;
        }

        Student studentToRemove = getStudentById(id);
        if (studentToRemove == null) {
            System.err.println("Removal failed: Student not found.");
            return false;
        }

        // Remove all tags from student
        List<Tag> tags = tagHandler.getTagsByStudent(studentToRemove);
        for (Tag tag : tags) {
            dbHandler.removeTagFromStudent(id, tag.getID());
        }

        // Leave all groups
        List<Group> groups = studentToRemove.getGroups();
        for (Group group : groups) {
            dbHandler.leaveGroup(id, group.getID());
        }

        // Delete all posts by this student
        List<Post> posts = dbHandler.getStudentPosts(id);
        for (Post post : posts) {
            dbHandler.removePost(post.getID());
        }

        // Remove all friend relationships
        dbHandler.removeAllFriendships(id);

        // Finally remove the student
        boolean success = dbHandler.removeStudent(id);

        if (success) {
            System.out.println("Student removed successfully: ID " + id);
        } else {
            System.err.println("Removal failed: Unable to remove the student.");
        }
        return success;
    }

    // Update a student's information
    public boolean updateStudent(int studentID, String newName, String newYear) {
        Student student = getStudentById(studentID);
        if (student == null) {
            System.err.println("Student with ID " + studentID + " not found.");
            return false;
        }
        student.setName(newName);
        student.setYear(newYear);
        System.out.println("Student updated: " + student.getName());
        return true;
    }

    // Retrieve a student by ID
    public Student getStudentById(int studentID) {
        for (Student student : students) {
            if (student.getID() == studentID) {
                return student;
            }
        }
        return null; // Not found
    }

    // Retrieve all students
    public List<Student> getAllStudents() {
        return students;
    }

    // Add a tag to a student
    public boolean addTagToStudent(int studentID, Tag tag) {
        Student student = getStudentById(studentID);
        if (student == null) {
            System.err.println("Student with ID " + studentID + " not found.");
            return false;
        }
        
        // First try to add to database
        if (dbHandler.addTagToStudent(studentID, tag.getID())) {
            // If successful in database, update memory
            student.addTag(tag);
            System.out.println("Tag added to student " + student.getName() + ": " + tag.getName());
            return true;
        }
        return false;
    }

    // Remove a tag from a student
    public boolean removeTagFromStudent(int studentID, Tag tag) {
        Student student = getStudentById(studentID);
        if (student == null) {
            System.err.println("Student with ID " + studentID + " not found.");
            return false;
        }
        
        // First try to remove from database
        if (dbHandler.removeTagFromStudent(studentID, tag.getID())) {
            // If successful in database, update memory
            student.removeTag(tag);
            System.out.println("Tag removed from student " + student.getName() + ": " + tag.getName());
            return true;
        }
        return false;
    }

    // Print all students
    public void printAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            for (Student student : students) {
                System.out.println(
                        "ID: " + student.getID() + ", Name: " + student.getName() + ", Year: " + student.getYear());
            }
        }
    }

    public List<Post> getAllPosts() {
        List<Post> allPosts = new ArrayList<>();
        // Collect posts from all students
        for (Student student : students) {
            if (student.getPosts() != null) {
                allPosts.addAll(student.getPosts());
            }
        }
        return allPosts;
    }

    public Student getCurrentStudent() {
        System.out.println("DEBUG: getCurrentStudent called. Current student: " +
                (currentStudent != null ? currentStudent.getName() + "(ID:" + currentStudent.getID() + ")" : "null"));
        return currentStudent;
    }

    public void setCurrentStudent(Student student) {
        System.out.println("DEBUG: setCurrentStudent called with student: " +
                (student != null ? student.getName() + "(ID:" + student.getID() + ")" : "null"));
        this.currentStudent = student;
        if (student != null && !students.contains(student)) {
            students.add(student);
            System.out.println("DEBUG: Added student to list: " + student.getName());
        }
        System.out.println("DEBUG: Current student set to: " +
                (student != null ? student.getName() : "null"));
    }

    public Student getStudentByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        for (Student student : students) {
            if (student.getName().equals(name)) {
                return student;
            }
        }
        return null;
    }

    public Student getStudentByUsername(String username) {
        if (username == null) {
            return null;
        }

        try {
            // Get database instance
            Database database = DatabaseFactory.getDatabase(DatabaseFactory.DatabaseType.MYSQL, "StudentDB");
            DatabaseHandler dbHandler = new DatabaseHandler(database);

            // Look up student in database
            Student student = dbHandler.getStudentByUsername(username);

            // Update local cache if found
            if (student != null && !students.contains(student)) {
                students.add(student);
            }

            return student;
        } catch (Exception e) {
            System.err.println("Error looking up student by username: " + e.getMessage());
            return null;
        }
    }

    public Student authenticateStudent(String email, String password) {
        try {
            // Get database instance through DatabaseHandler
            Database database = DatabaseFactory.getDatabase(DatabaseFactory.DatabaseType.MYSQL, "StudentDB");
            DatabaseHandler dbHandler = new DatabaseHandler(database);

            // Attempt authentication through database
            Student authenticatedStudent = dbHandler.authenticateStudent(email, password);

            if (authenticatedStudent != null) {
                // Update local cache if authentication successful
                setCurrentStudent(authenticatedStudent);
            }

            return authenticatedStudent;
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        }
    }
}
