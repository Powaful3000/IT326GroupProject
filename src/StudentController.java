import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class StudentController {

    private StudentHandler studentHandler;
    private GroupHandler groupHandler;
    private DatabaseHandler dbHandler;

    // Constructor
    public StudentController(StudentHandler studentHandler, GroupHandler groupHandler, DatabaseHandler dbHandler) {
        this.studentHandler = studentHandler;
        this.groupHandler = groupHandler;
        this.dbHandler = dbHandler;
    }

    // Register a new student
    public boolean registerStudent(String name, String year, int id) {
        if (studentHandler.getStudentById(id) != null) {
            return false;
        }
        Student newStudent = new Student(id, null, name, year, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        return studentHandler.addStudent(newStudent);
    }

    // Login a student
    public boolean loginStudent(int id) {
        return studentHandler.getStudentById(id) != null;
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
        return studentHandler.removeStudent(id);
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
        System.out.println("DEBUG: joinGroup called for group: " +
                (group != null ? group.getName() : "null"));

        if (group == null) {
            System.err.println("Cannot join null group");
            return false;
        }

        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) {
            System.err.println("No student currently logged in");
            return false;
        }

        // Check if student is already a member
        if (dbHandler.isStudentInGroup(currentStudent.getID(), group.getID())) {
            System.err.println("Student is already a member of this group");
            return false;
        }

        System.out.println("DEBUG: Attempting to join group: " + group.getName());
        System.out.println("DEBUG: Current student: " + currentStudent.getName());

        boolean success = groupHandler.addMemberToGroup(group.getID(), currentStudent);
        System.out.println("DEBUG: Join group result: " + success);
        return success;
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

    public boolean joinGroup(int groupId) {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) {
            System.err.println("No student currently logged in");
            return false;
        }

        // First try to join in database
        if (dbHandler.joinGroup(currentStudent.getID(), groupId)) {
            // If successful, get the group and add it to student's groups
            Group group = dbHandler.getGroupByID(groupId);
            if (group != null) {
                currentStudent.joinGroup(group);
                System.out.println("Successfully joined group: " + group.getName());
                return true;
            }
        }
        return false;
    }

    public boolean leaveGroup(int groupId) {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) {
            System.err.println("No student currently logged in");
            return false;
        }

        // First try to leave in database
        if (dbHandler.leaveGroup(currentStudent.getID(), groupId)) {
            // If successful, find and remove the group from student's groups
            Group groupToLeave = currentStudent.getGroups().stream()
                    .filter(g -> g.getID() == groupId)
                    .findFirst()
                    .orElse(null);

            if (groupToLeave != null) {
                currentStudent.leaveGroup(groupToLeave);
                System.out.println("Successfully left group: " + groupToLeave.getName());
                return true;
            }
        }
        return false;
    }

    public boolean addTagToStudent(Tag tag) {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) {
            System.err.println("No student currently logged in");
            return false;
        }

        // First try to add tag to database
        if (dbHandler.addTagToStudent(currentStudent.getID(), tag.getID())) {
            // If successful in database, update in-memory
            currentStudent.addTag(tag);
            System.out.println("Successfully added tag: " + tag.getName());
            return true;
        }
        return false;
    }

    public boolean removeTagFromStudent(Tag tag) {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) {
            System.err.println("No student currently logged in");
            return false;
        }

        // First try to remove tag from database
        if (dbHandler.removeTagFromStudent(currentStudent.getID(), tag.getID())) {
            // If successful in database, update in-memory
            currentStudent.removeTag(tag);
            System.out.println("Successfully removed tag: " + tag.getName());
            return true;
        }
        return false;
    }
}
