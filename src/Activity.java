public class Activity {
    private String description;
    private String timestamp;

    public Activity(String description, String timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }
} 