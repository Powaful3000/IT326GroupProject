public class Membership {

    // Attributes
    private int userID;
    private int groupID;
    private String joinDate;

    // Constructor
    public Membership(int userID, int groupID, String joinDate) {
        this.userID = userID;
        this.groupID = groupID;
        this.joinDate = joinDate;
    }

    // Getter methods
    public int getUserID() {
        return userID;
    }

    public int getGroupID() {
        return groupID;
    }

    public String getJoinDate() {
        return joinDate;
    }

    // String representation of the Membership class
    @Override
    public String toString() {
        return "Membership - User ID: " + userID + ", Group ID: " + groupID + ", Join Date: " + joinDate;
    }
}
