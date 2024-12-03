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

    private int generateUniqueGroupId() {
        System.out.println("\n====== GroupHandler generateUniqueGroupId Debug ======");
        System.out.println("Current groups in memory: " + groups.size());
        
        // Get max ID from in-memory list
        int maxMemoryId = groups.stream()
                .mapToInt(Group::getID)
                .max()
                .orElse(0);
        System.out.println("Max ID in memory: " + maxMemoryId);
        
        // Get all groups from database to find max ID
        List<Group> dbGroups = dbHandler.getAllGroups();
        int maxDbId = dbGroups.stream()
                .mapToInt(Group::getID)
                .max()
                .orElse(0);
        System.out.println("Max ID in database: " + maxDbId);
        
        // Use the higher of the two values
        int maxId = Math.max(maxMemoryId, maxDbId);
        int newId = maxId + 1;
        
        System.out.println("Using maximum ID: " + maxId);
        System.out.println("Generated new ID: " + newId);
        return newId;
    }

    public boolean addGroup(Group group) {
        System.out.println("\n====== GroupHandler addGroup Debug ======");
        System.out.println("Initial group state:");
        System.out.println("- Original ID: " + group.getID());
        System.out.println("- Name: " + group.getName());
        System.out.println("- Description: " + group.getDescription());
        
        // Check if a group with this name already exists
        boolean nameExists = dbHandler.getAllGroups().stream()
                .anyMatch(g -> g.getName().equals(group.getName()));
        if (nameExists) {
            System.out.println("Error: Group with name '" + group.getName() + "' already exists");
            return false;
        }
        
        int newId = generateUniqueGroupId();
        group.setID(newId);
        System.out.println("\nAfter ID generation:");
        System.out.println("- New ID: " + newId);
        
        System.out.println("\nAttempting to add group to database...");
        boolean success = dbHandler.addGroup(group);
        System.out.println("Database operation result: " + success);
        
        if (success) {
            groups.add(group);
            System.out.println("Group added to in-memory list");
            System.out.println("New group count: " + groups.size());
            return true;
        }
        System.out.println("Failed to add group to database");
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
