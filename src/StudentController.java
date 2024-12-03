import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class StudentController {

    private final StudentHandler studentHandler;
    private final GroupHandler groupHandler;

    // Constructor
    public StudentController(StudentHandler studentHandler, GroupHandler groupHandler) {
        if (studentHandler == null) {
            throw new IllegalArgumentException("StudentHandler cannot be null");
        }
        this.studentHandler = studentHandler;
        this.groupHandler = groupHandler;
    }

    // Register a new student
    public boolean registerStudent(String name, String year, int id) {
        // Validate inputs
        if (!Validator.isValidString(name)) {
            System.err.println("Registration failed: Invalid name.");
            return false;
        }
        if (!Validator.isValidYear(year)) {
            System.err.println("Registration failed: Invalid year.");
            return false;
        }
        if (!Validator.isValidId(id)) {
            System.err.println("Registration failed: Invalid ID.");
            return false;
        }

        // Check if the student already exists
        if (studentHandler.getStudentById(id) != null) {
            System.err.println("Registration failed: Student with ID " + id + " already exists.");
            return false;
        }

        // Create and add the student
        Student newStudent = new Student(id, name, year, null, null, null);
        boolean success = studentHandler.addStudent(newStudent);

        if (success) {
            System.out.println("Student registered successfully: " + name);
        } else {
            System.err.println("Registration failed: Unable to add the student.");
        }
        return success;
    }

    // Login a student
    public boolean loginStudent(int id) {
        // Validate input
        if (!Validator.isValidId(id)) {
            System.err.println("Login failed: Invalid ID.");
            return false;
        }

        // Check if the student exists
        Student student = studentHandler.getStudentById(id);
        if (student == null) {
            System.err.println("Login failed: Student with ID " + id + " does not exist.");
            return false;
        }

        // Set the current student
        studentHandler.setCurrentStudent(student);
        System.out.println("Login successful for student: " + student.getName());
        return true;
    }

    // Update a student's information
    public boolean updateStudentInfo(int id, String newName, String newYear) {
        // Validate inputs
        if (!Validator.isValidString(newName)) {
            System.err.println("Update failed: Invalid name.");
            return false;
        }
        if (!Validator.isValidYear(newYear)) {
            System.err.println("Update failed: Invalid year.");
            return false;
        }

        // Update the student using StudentHandler
        boolean success = studentHandler.updateStudent(id, newName, newYear);

        if (success) {
            System.out.println("Student updated successfully: ID " + id);
        } else {
            System.err.println("Update failed: Unable to update the student.");
        }
        return success;
    }

    // Remove a student
    public boolean removeStudent(int id) {
        // Validate input
        if (!Validator.isValidId(id)) {
            System.err.println("Removal failed: Invalid ID.");
            return false;
        }

        // Remove the student using StudentHandler
        boolean success = studentHandler.removeStudent(id);

        if (success) {
            System.out.println("Student removed successfully: ID " + id);
        } else {
            System.err.println("Removal failed: Unable to remove the student.");
        }
        return success;
    }

    // Print all students
    public void printAllStudents() {
        studentHandler.printAllStudents();
    }

    // Add a getter method
    public StudentHandler getStudentHandler() {
        return studentHandler;
    }

    // Or add a method to get students directly
    public List<Student> getAllStudents() {
        return studentHandler.getAllStudents();
    }

    public List<Post> getGroupPosts(Group group) {
        if (group == null) {
            System.err.println("Cannot get posts for null group");
            return new ArrayList<>();
        }

        // Get all posts from the student handler
        List<Post> allPosts = studentHandler.getAllPosts();

        // Filter posts that belong to the specified group
        return allPosts.stream()
                .filter(post -> post.isInGroup(group))
                .collect(Collectors.toList());
    }

    public List<Group> getAllGroups() {
        return groupHandler.getAllGroups();
    }

    public boolean createGroup(Group group) {
        System.out.println("\n====== StudentController createGroup Debug ======");
        System.out.println("Validating input group:");
        if (group == null) {
            System.out.println("Error: Group is null");
            return false;
        }
        
        System.out.println("Group details:");
        System.out.println("- ID: " + group.getID());
        System.out.println("- Name: " + group.getName());
        System.out.println("- Description: " + group.getDescription());
        System.out.println("- Creation Date: " + group.getCreationDate());
        
        System.out.println("\nForwarding to groupHandler.addGroup()...");
        boolean result = groupHandler.addGroup(group);
        System.out.println("GroupHandler result: " + (result ? "success" : "failure"));
        return result;
    }

    public Group getGroupByName(String name) {
        return groupHandler.getAllGroups().stream()
                .filter(group -> group.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean joinGroup(Group group) {
        if (group == null) {
            System.err.println("Cannot join null group");
            return false;
        }

        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) {
            System.err.println("No student currently logged in");
            return false;
        }

        System.out.println("Attempting to join group: " + group.getName());
        System.out.println("Current student: " + currentStudent.getName());

        return groupHandler.addMemberToGroup(group.getID(), currentStudent);
    }

    public Group getGroupByID(int groupID) {
        return groupHandler.getGroupByID(groupID);
    }

    public List<Student> searchStudents(String searchTerm) {
        return studentHandler.getAllStudents().stream()
            .filter(s -> s.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<Student> searchStudentsByTag(Tag tag) {
        return studentHandler.getAllStudents().stream()
            .filter(s -> s.getTags().contains(tag))
            .collect(Collectors.toList());
    }

    public List<Student> getGroupMembers(Group group) {
        if (group == null) {
            System.err.println("Cannot get members for null group");
            return new ArrayList<>();
        }
        return group.getMembers();
    }
}
