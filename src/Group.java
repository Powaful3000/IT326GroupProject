import java.util.List;
import java.util.ArrayList;

public class Group {

    // Attributes
    private int groupID;
    private String groupName;
    private int groupSize;
    private String groupDescription;
    private List<Student> groupMembers;

    // Constructor
    public Group(int groupID, String groupName, int groupSize, String groupDescription, List<Student> groupMembers) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupSize = groupSize;
        this.groupDescription = groupDescription;
        this.groupMembers = groupMembers;
    }

    // Add this constructor overload
    public Group(String groupName, String groupDescription) {
        this.groupID = (int) (Math.random() * 9000) + 1000; // Simple ID generation
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupSize = 0;
        this.groupMembers = new ArrayList<>();
    }

    // Getters
    public int getID() {
        return groupID;
    }

    public String getName() {
        return groupName;
    }

    public int getSize() {
        return groupSize;
    }

    public String getDescription() {
        return groupDescription;
    }

    public List<Student> getMembers() {
        return groupMembers;
    }

    // Setters
    public void setID(int groupID) {
        this.groupID = groupID;
    }

    public void setName(String groupName) {
        this.groupName = groupName;
    }

    public void setSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public void setDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public void setMembers(List<Student> groupMembers) {
        this.groupMembers = groupMembers;
    }

    // Methods

    // Add a student to the group
    public boolean addMember(Student student) {
        if (groupMembers.contains(student)) {
            return false; // Student is already a member
        }
        groupMembers.add(student);
        groupSize++;
        return true;
    }

    // Remove a student from the group
    public boolean removeMember(Student student) {
        if (groupMembers.remove(student)) {
            groupSize--;
            return true; // Student successfully removed
        }
        return false; // Student was not a member
    }

    // Check if a student is a member of the group
    public boolean isMember(Student student) {
        return groupMembers.contains(student);
    }

    // Print group details
    public void printDetails() {
        System.out.println("Group ID: " + groupID);
        System.out.println("Group Name: " + groupName);
        System.out.println("Group Size: " + groupSize);
        System.out.println("Group Description: " + groupDescription);
        System.out.println("Members:");
        for (Student student : groupMembers) {
            System.out.println(" - " + student.getName());
        }
    }
}
