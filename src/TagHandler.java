import java.util.ArrayList;
import java.util.List;

public class TagHandler {

    // List to store tags (can be replaced with database integration)
    private List<Tag> tags;
    private DatabaseHandler dbHandler;

    // Constructor
    public TagHandler(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.tags = new ArrayList<>();
        loadTagsFromDatabase();
    }

    private void loadTagsFromDatabase() {
        List<Tag> dbTags = dbHandler.getAllTags();
        if (dbTags != null) {
            tags.clear();
            tags.addAll(dbTags);
        }
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

        // First add to database
        if (dbHandler.addTag(tag)) {
            // If successful in database, add to in-memory list
            tags.add(tag);
            System.out.println("Tag added: " + tag.getName());
            return true;
        }
        return false;
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

        // First update in database
        if (dbHandler.updateTag(tag)) {
            // If successful in database, update in-memory
            existingTag.setName(tag.getName());
            existingTag.setDescription(tag.getDescription());
            System.out.println("Tag updated: " + tag.getName());
            return true;
        }
        return false;
    }

    // Remove a tag
    public boolean removeTag(Tag tag) {
        if (tag == null) {
            System.err.println("Cannot remove null tag.");
            return false;
        }

        // First remove from database
        if (dbHandler.removeTag(tag)) {
            // If successful in database, remove from in-memory list
            boolean removed = tags.remove(tag);
            if (removed) {
                System.out.println("Tag removed: " + tag.getName());
            }
            return removed;
        }
        return false;
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
            // Get student's tags from database
            List<Integer> studentTagIds = dbHandler.getStudentTagIds(student.getID());
            for (Integer tagId : studentTagIds) {
                Tag tag = getTagById(tagId);
                if (tag != null) {
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
