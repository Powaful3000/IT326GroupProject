import java.util.ArrayList;
import java.util.List;

public class GroupHandler {

    private final DatabaseHandler dbHandler;
    private List<Group> groups;

    public GroupHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.groups = new ArrayList<>();
    }

    public List<Group> getAllGroups() {
        return dbHandler.getAllGroups();
    }

    public Group findGroupByName(String groupName) {
        return dbHandler.findGroupByName(groupName);
    }

    public boolean addGroup(Group group) {
        if (dbHandler.addGroup(group)) {
            groups.add(group);
            return true;
        }
        return false;
    }

    public boolean addMemberToGroup(int groupId, Student student) {
        if (student == null) {
            System.err.println("Cannot add null student to group");
            return false;
        }
        return dbHandler.addMemberToGroup(groupId, student.getID());
    }

    private Group getGroupById(int groupId) {
        return groups.stream()
                .filter(group -> group.getID() == groupId)
                .findFirst()
                .orElse(null);
    }

    public Group getGroupByID(int groupID) {
        return getAllGroups().stream()
                .filter(group -> group.getID() == groupID)
                .findFirst()
                .orElse(null);
    }
}
