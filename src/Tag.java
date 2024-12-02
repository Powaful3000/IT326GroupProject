public class Tag {

    // Attributes
    private String name;
    private String description;

    // Constructor
    public Tag(String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty.");
        }
        this.name = name;
        this.description = description;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty.");
        }
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
        return name.equals(tag.name); // Tags are equal if their names are the same
    }

    @Override
    public int hashCode() {
        return name.hashCode(); // Generate hash based on tag name
    }

    // Method to print tag details
    public void printDetails() {
        System.out.println("Tag Name: " + name);
        System.out.println("Description: " + description);
    }
}
