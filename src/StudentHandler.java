import java.util.List;
import java.util.ArrayList;

public class StudentHandler {

    // List to store students (can be replaced with database integration)
    private List<Student> students;

    private Student currentStudent;

    // Constructor
    public StudentHandler() {
        this.students = new ArrayList<>();
    }

    // Add a student
    public boolean addStudent(Student student) {
        if (student == null) {
            System.err.println("Cannot add null student.");
            return false;
        }
        if (getStudentById(student.getID()) != null) {
            System.err.println("Student with ID " + student.getID() + " already exists.");
            return false;
        }
        students.add(student);
        System.out.println("Student added: " + student.getName());
        return true;
    }

    // Remove a student
    public boolean removeStudent(int studentID) {
        Student student = getStudentById(studentID);
        if (student == null) {
            System.err.println("Student with ID " + studentID + " not found.");
            return false;
        }
        students.remove(student);
        System.out.println("Student removed: " + student.getName());
        return true;
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
        student.addTag(tag);
        System.out.println("Tag added to student " + student.getName() + ": " + tag.getName());
        return true;
    }

    // Remove a tag from a student
    public boolean removeTagFromStudent(int studentID, Tag tag) {
        Student student = getStudentById(studentID);
        if (student == null) {
            System.err.println("Student with ID " + studentID + " not found.");
            return false;
        }
        student.removeTag(tag);
        System.out.println("Tag removed from student " + student.getName() + ": " + tag.getName());
        return true;
    }

    // Print all students
    public void printAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            for (Student student : students) {
                System.out.println("ID: " + student.getID() + ", Name: " + student.getName() + ", Year: " + student.getYear());
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
        return currentStudent;
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
    }
}
