public class Tag {

    // Attributes
    private int id;
    private String name;
    private String description;

    // Constructor with ID
    public Tag(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Constructor without ID (for new tags)
    public Tag(String name, String description) {
        this.id = 0; // Will be set after database insertion
        this.name = name;
        this.description = description;
    }

    // Getters
    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setID(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Method to compare two tags
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Same object reference
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Not the same class
        }
        Tag tag = (Tag) obj;
        return id == tag.id; // Tags are equal if their IDs are the same
    }

    @Override
    public int hashCode() {
        return id; // Generate hash based on tag ID
    }

    // Method to print tag details
    public void printDetails() {
        System.out.println("Tag ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
    }

    @Override
    public String toString() {
        return name + (description != null && !description.isEmpty() ? " - " + description : "");
    }
}
