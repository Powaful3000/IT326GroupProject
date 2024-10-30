import java.util.ArrayList;
import java.util.List;

public class Group {

    // Attributes
    private int groupID;
    private String groupName;
    private int groupSize;
    private String groupDescription;
    private List<Integer> members; // Stores userIDs of the members

    // Constructor
    public Group(int groupID, String groupName, String groupDescription) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupSize = 0; // Initial size is 0
        this.groupDescription = groupDescription;
        this.members = new ArrayList<>();
    }

    // Getter methods
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

    // Method to add a member to the group
    public void addMember(int userID) {
        if (!members.contains(userID)) {
            members.add(userID);
            groupSize++;
            System.out.println("User ID " + userID + " added to Group ID " + groupID);
        } else {
            System.out.println("User ID " + userID + " is already a member of Group ID " + groupID);
        }
    }

    // Method to remove a member from the group
    public void removeMember(int userID) {
        if (members.contains(userID)) {
            members.remove((Integer) userID);
            groupSize--;
            System.out.println("User ID " + userID + " removed from Group ID " + groupID);
        } else {
            System.out.println("User ID " + userID + " is not a member of Group ID " + groupID);
        }
    }

    // Method to list all members in the group
    public List<Integer> listMembers() {
        return new ArrayList<>(members); // Return a copy of the members list
    }

    // String representation of the Group class
    @Override
    public String toString() {
        return "Group ID: " + groupID + ", Name: " + groupName + ", Size: " + groupSize + ", Description: " + groupDescription;
    }
}
