import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// Abstract class to handle generic database operations
public abstract class DatabaseHandler<T> {

    // Protected connection attribute for subclasses
    protected Connection connection;

    // Constructor to set up the database connection
    public DatabaseHandler(Connection connection) {
        this.connection = connection;
    }

    // Abstract method to add an entity to the database
    public abstract void add(T entity);

    // Abstract method to retrieve an entity by ID
    public abstract T getByID(int id);

    // Abstract method to update an entity in the database
    public abstract void update(T entity);

    // Abstract method to delete an entity by ID
    public abstract void delete(int id);

    // Abstract method to list all entities in the database
    public abstract List<T> listAll();

    // Close the database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to close the database connection.");
        }
    }
}
