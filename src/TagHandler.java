import java.util.ArrayList;
import java.util.List;

public class TagHandler {

    // List to store tags (can be replaced with database integration)
    private List<Tag> tags;

    // Constructor
    public TagHandler() {
        this.tags = new ArrayList<>();
    }

    // Add a new tag
    public boolean addTag(Tag tag) {
        if (tag == null) {
            System.err.println("Cannot add null tag.");
            return false;
        }
        if (getTagByName(tag.getName()) != null) {
            System.err.println("Tag with name '" + tag.getName() + "' already exists.");
            return false;
        }
        tags.add(tag);
        System.out.println("Tag added: " + tag.getName());
        return true;
    }

    // Remove a tag by name
    public boolean removeTag(String tagName) {
        Tag tag = getTagByName(tagName);
        if (tag == null) {
            System.err.println("Tag with name '" + tagName + "' not found.");
            return false;
        }
        tags.remove(tag);
        System.out.println("Tag removed: " + tag.getName());
        return true;
    }

    // Retrieve a tag by name
    public Tag getTagByName(String tagName) {
        for (Tag tag : tags) {
            if (tag.getName().equalsIgnoreCase(tagName)) {
                return tag;
            }
        }
        return null; // Not found
    }

    // Retrieve all tags
    public List<Tag> getAllTags() {
        return tags;
    }

    // Associate a tag with a student
    public boolean addTagToStudent(Tag tag, Student student) {
        if (tag == null || student == null) {
            System.err.println("Tag or student cannot be null.");
            return false;
        }
        if (getTagByName(tag.getName()) == null) {
            System.err.println("Tag '" + tag.getName() + "' does not exist.");
            return false;
        }
        student.addTag(tag);
        System.out.println("Tag '" + tag.getName() + "' added to student: " + student.getName());
        return true;
    }

    // Remove a tag from a student
    public boolean removeTagFromStudent(Tag tag, Student student) {
        if (tag == null || student == null) {
            System.err.println("Tag or student cannot be null.");
            return false;
        }
        if (!student.getTags().contains(tag)) {
            System.err.println("Tag '" + tag.getName() + "' is not associated with student: " + student.getName());
            return false;
        }
        student.removeTag(tag);
        System.out.println("Tag '" + tag.getName() + "' removed from student: " + student.getName());
        return true;
    }

    // Print all tags
    public void printAllTags() {
        if (tags.isEmpty()) {
            System.out.println("No tags found.");
        } else {
            System.out.println("Tags:");
            for (Tag tag : tags) {
                System.out.println("- " + tag.getName() + ": " + tag.getDescription());
            }
        }
    }
}
