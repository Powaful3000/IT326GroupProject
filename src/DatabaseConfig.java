import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties props = new Properties();
    
    static {
        try {
            props.load(new FileInputStream("src/database.properties"));
        } catch (IOException e) {
            System.err.println("Could not load database properties: " + e.getMessage());
        }
    }
    
    // Database configuration
    public static final String DB_HOST = props.getProperty("DB_HOST", "localhost");
    public static final int DB_PORT = Integer.parseInt(props.getProperty("DB_PORT", "3306"));
    public static final String DB_NAME = props.getProperty("DB_NAME", "StudentDB");
    public static final String DB_USER = props.getProperty("DB_USER", "MySQLUser");
    public static final String DB_PASSWORD = props.getProperty("DB_PASSWORD", "MySQLPassword");
    
    // Connection URL builder
    public static String getConnectionUrl() {
        return String.format("jdbc:mysql://%s:%d/%s", DB_HOST, DB_PORT, DB_NAME);
    }
}