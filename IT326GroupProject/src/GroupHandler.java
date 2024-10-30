import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// GroupHandler class extending DatabaseHandler to manage Group entities
public class GroupHandler extends DatabaseHandler<Group> {

    // Constructor to pass the connection to the superclass
    public GroupHandler(Connection connection) {
        super(connection);
    }

    // Add a Group to the database
    @Override
    public void add(Group group) {
        String sql = "INSERT INTO groups (groupID, groupName, groupDescription) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, group.getID());
            stmt.setString(2, group.getName());
            stmt.setString(3, group.getDescription());
            stmt.executeUpdate();
            System.out.println("Group added to the database: " + group);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add Group to the database.");
        }
    }

    // Retrieve a Group by ID from the database
    @Override
    public Group getByID(int id) {
        String sql = "SELECT * FROM groups WHERE groupID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Group(
                    rs.getInt("groupID"),
                    rs.getString("groupName"),
                    rs.getString("groupDescription")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve Group from the database.");
        }
        return null;
    }

    // Update a Group in the database
    @Override
    public void update(Group group) {
        String sql = "UPDATE groups SET groupName = ?, groupDescription = ? WHERE groupID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, group.getName());
            stmt.setString(2, group.getDescription());
            stmt.setInt(3, group.getID());
            stmt.executeUpdate();
            System.out.println("Group updated in the database: " + group);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update Group in the database.");
        }
    }

    // Delete a Group by ID from the database
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM groups WHERE groupID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Group deleted from the database. ID: " + id);
            } else {
                System.out.println("No Group found with ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete Group from the database.");
        }
    }

    // List all Groups in the database
    @Override
    public List<Group> listAll() {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT * FROM groups";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Group group = new Group(
                    rs.getInt("groupID"),
                    rs.getString("groupName"),
                    rs.getString("groupDescription")
                );
                groups.add(group);
            }
            System.out.println("Retrieved all Groups from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to list all Groups from the database.");
        }
        return groups;
    }

    // Create a new Group
    public Group createGroup(String groupName, String groupDescription) {
        Group newGroup = new Group(0, groupName, groupDescription); // Assuming groupID is auto-generated
        add(newGroup);
        return newGroup;
    }

    // Join a Group by ID
    public void joinGroup(int groupID) {
        // Implementation for joining a group (e.g., adding to membership table)
        System.out.println("User joined the Group with ID: " + groupID);
    }

    // Leave a Group by ID
    public void leaveGroup(int groupID) {
        // Implementation for leaving a group (e.g., removing from membership table)
        System.out.println("User left the Group with ID: " + groupID);
    }
}
