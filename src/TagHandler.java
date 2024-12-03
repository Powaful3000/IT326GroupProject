import java.util.ArrayList;
import java.util.List;

public class TagHandler {

    // List to store tags (can be replaced with database integration)
    private List<Tag> tags;
    private DatabaseHandler dbHandler;

    // Constructor
    public TagHandler() {
        this.tags = new ArrayList<>();
    }

    public void setDatabaseHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    // Add a new tag
    public boolean addTag(Tag tag) {
        if (tag == null) {
            System.err.println("Cannot add null tag.");
            return false;
        }
        if (getTagByName(tag.getName()) != null) {
            System.err.println("Tag with name " + tag.getName() + " already exists.");
            return false;
        }
        tags.add(tag);
        System.out.println("Tag added: " + tag.getName());
        return true;
    }

    // Update a tag
    public boolean updateTag(Tag tag) {
        if (tag == null) {
            System.err.println("Cannot update null tag.");
            return false;
        }
        Tag existingTag = getTagById(tag.getID());
        if (existingTag == null) {
            System.err.println("Tag with ID " + tag.getID() + " not found.");
            return false;
        }
        existingTag.setName(tag.getName());
        existingTag.setDescription(tag.getDescription());
        System.out.println("Tag updated: " + tag.getName());
        return true;
    }

    // Remove a tag
    public boolean removeTag(Tag tag) {
        if (tag == null) {
            System.err.println("Cannot remove null tag.");
            return false;
        }
        boolean removed = tags.remove(tag);
        if (removed) {
            System.out.println("Tag removed: " + tag.getName());
        } else {
            System.err.println("Failed to remove tag: Tag not found");
        }
        return removed;
    }

    // Retrieve a tag by ID
    public Tag getTagById(int tagId) {
        for (Tag tag : tags) {
            if (tag.getID() == tagId) {
                return tag;
            }
        }
        return null;
    }

    // Retrieve a tag by name
    public Tag getTagByName(String name) {
        for (Tag tag : tags) {
            if (tag.getName().equalsIgnoreCase(name)) {
                return tag;
            }
        }
        return null;
    }

    // Retrieve all tags
    public List<Tag> getAllTags() {
        return new ArrayList<>(tags);
    }

    // Retrieve tags for a student
    public List<Tag> getTagsByStudent(Student student) {
        List<Tag> studentTags = new ArrayList<>();
        if (student != null && dbHandler != null) {
            // Get tags from database based on student ID
            // This would typically involve a database query
            for (Tag tag : tags) {
                // Check if student has this tag in the database
                if (dbHandler.containsTag(tag)) {
                    studentTags.add(tag);
                }
            }
        }
        return studentTags;
    }

    // Print all tags
    public void printAllTags() {
        if (tags.isEmpty()) {
            System.out.println("No tags found.");
        } else {
            for (Tag tag : tags) {
                System.out.println("Tag ID: " + tag.getID() + ", Name: " + tag.getName() +
                        ", Description: " + tag.getDescription());
            }
        }
    }
}
