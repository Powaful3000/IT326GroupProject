import java.util.ArrayList;
import java.util.List;


public class Student {
	// Attributes
    private int userID;
    private String userName;
    private String userYear;
    private List<String> userTags;

    // Constructor
    public Student(int userID, String userName, String userYear) {
        this.userID = userID;
        this.userName = userName;
        this.userYear = userYear;
        this.userTags = new ArrayList<>();
    }

    // Getter methods
    public int getID() {
        return userID;
    }

    public String getName() {
        return userName;
    }

    public String getYear() {
        return userYear;
    }

    public List<String> getTags() {
        return userTags;
    }

    // Add a tag to the student's tags list
    public void addTag(String tag) {
        if (!userTags.contains(tag)) {
            userTags.add(tag);
        }
    }

    // Remove a tag from the student's tags list
    public void removeTag(String tag) {
        userTags.remove(tag);
    }

    // String representation of the Student class
    @Override
    public String toString() {
        return "Student ID: " + userID + ", Name: " + userName + ", Year: " + userYear + ", Tags: " + userTags;
    }
	
}
