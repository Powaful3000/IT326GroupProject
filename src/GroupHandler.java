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

    public boolean addGroup(Group group) {
        if (dbHandler.addGroup(group)) {
            groups.add(group);
            return true;
        }
        return false;
    }

    public boolean addMemberToGroup(int groupId, Student student) {
        if (dbHandler.addMemberToGroup(groupId, student.getID())) {
            Group group = getGroupById(groupId);
            if (group != null) {
                group.addMember(student);
                return true;
            }
        }
        return false;
    }

    private Group getGroupById(int groupId) {
        return groups.stream()
                .filter(group -> group.getID() == groupId)
                .findFirst()
                .orElse(null);
    }
}
