import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/*
 * David Notes
 *
 * Main Menu Structure:
 *
 * Welcome to Redbird Connect
 * ------------------------
 * 1. Login
 * 2. Register
 * 3. Exit
 *
 * Please select an option:
 *
 *
 *
 * After Login Menu Structure:
 *
 * Main Menu
 * ------------------------
 * 1. My Profile
 * 2. Class Groups
 * 3. Friends
 * 4. Messages
 * 5. Logout
 *
 * Please select an option:
 *
 *
 *
 * Profile Menu Structure:
 *
 * My Profile
 * ------------------------
 * Current Tags: [IT326] [Junior] [IT Major]
 *
 * 1. View Profile Details
 * 2. Edit Profile
 * 3. Add Tag
 * 4. Remove Tag
 * 5. Enable/Disable Anonymous Mode
 * 6. Back
 *
 * Please select an option:
 *
 *
 *
 * Class Groups Menu Structure:
 *
 * Class Groups
 * ------------------------
 * Your Groups:
 * - IT 326 - Software Engineering
 * - IT 355 - Database Management
 * - IT 328 - Programming Languages
 *
 * 1. View My Groups
 * 2. Join Group
 * 3. Create Group
 * 4. Leave Group
 * 5. Back
 *
 * Please select an option:
 *
 *
 *
 * Inside Group Menu Structure:
 *
 * IT 326 - Software Engineering
 * ------------------------
 * Recent Posts:
 * [User1] How do I implement a singleton pattern?
 * [Anonymous] When is the next homework due?
 * [User3] Looking for project partners
 *
 * 1. View All Posts
 * 2. Create Post
 * 3. My Posts
 * 4. Bookmarked Posts
 * 5. Back
 *
 * Please select an option:
 *
 *
 *
 * Friends Menu Structure:
 *
 * Friends
 * ------------------------
 * Your Friends:
 * - User1 (Online)
 * - User2 (Offline)
 * - User3 (Online)
 *
 * 1. View Friends List
 * 2. Send Friend Request
 * 3. View Pending Requests
 * 4. Blocked Users
 * 5. Remove Friend
 * 6. Back
 *
 * Please select an option:
 *
 *
 */

public class TerminalUI {

    private final Scanner scanner;
    private final StudentHandler studentHandler;
    private final GroupHandler groupHandler;
    private final PostHandler postHandler;
    private final TagHandler tagHandler;
    private final ProfileHandler profileHandler;
    private final StudentController studentController;
    private Student currentUser;
    private boolean isRunning;
    private final DatabaseHandler dbHandler;
    private Group currentGroup;

    public TerminalUI() {
        Database database = DatabaseFactory.getDatabase(
                DatabaseFactory.DatabaseType.MYSQL,
                "StudentDB");
        this.dbHandler = new DatabaseHandler(database);

        // Establish database connection immediately
        try {
            database.connect();
            System.out.println("Successfully connected to database.");
        } catch (RuntimeException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.err.println("Please check your database configuration and try again.");
            System.exit(1); // Exit if we can't connect to the database
        }

        this.scanner = new Scanner(System.in);
        this.studentHandler = StudentHandler.getInstance();
        this.groupHandler = new GroupHandler(dbHandler);
        this.postHandler = new PostHandler();
        this.tagHandler = new TagHandler(dbHandler);
        this.profileHandler = new ProfileHandler(dbHandler);
        this.studentController = new StudentController(studentHandler, groupHandler, dbHandler);
        this.isRunning = true;

        // Add shutdown hook for cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (dbHandler != null) {
                dbHandler.getDatabase().disconnect();
                System.out.println("Database connection closed.");
            }
        }));
    }

    public void start() {
        while (isRunning) {
            if (currentUser == null) {
                showAuthenticationMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthenticationMenu() {
        System.out.println("\nWelcome to Redbird Connect");
        System.out.println("------------------------");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.println("\nPlease select an option:");

        String input = scanner.nextLine();
        if (input.equals("debug") || input.equals("0")) {
            showDebugMenu();
        } else {
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> handleLogin();
                    case 2 -> handleRegistration();
                    case 3 -> exit();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void handleLogin() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            // Use your existing StudentHandler/Controller for authentication
            Student student = studentHandler.authenticateStudent(
                    email,
                    password);
            if (student != null) {
                currentUser = student;
                System.out.println("Login successful!");
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private void handleRegistration() {
        System.out.print("Desired email: ");
        String email = scanner.nextLine();
        System.out.print("Desired password: ");
        String password = scanner.nextLine();
        System.out.print("Enter your name (first and last): ");
        String name = scanner.nextLine();
        System.out.print(
                "Enter your year (Freshman/Sophomore/Junior/Senior): ");
        String year = scanner.nextLine();

        try {
            // Check if email already exists
            Student existingStudent = studentHandler.getStudentByUsername(
                    email);
            if (existingStudent != null) {
                System.out.println(
                        "An account with this email already exists.");
                return;
            }

            // Create new student (using 0 as temporary ID, database will assign real ID)
            Student newStudent = new Student(
                    0,
                    email,
                    name,
                    year,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>());

            // Add student to database with password
            boolean success = dbHandler.addStudent(newStudent, password);

            if (success) {
                System.out.println(
                        "Registration successful! Please login with your credentials.");
            } else {
                System.out.println("Registration failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        System.out.println("\nMain Menu");
        System.out.println("------------------------");
        System.out.println("1. My Profile");
        System.out.println("2. Class Groups");
        System.out.println("3. Friends");
        System.out.println("4. Messages");
        System.out.println("5. Logout");
        System.out.println("\nPlease select an option:");

        // String input = scanner.nextLine();
        // if (input.equals("debug") || input.equals("0")) {
        //     showDebugMenu();
        //     return;
        // }

        int choice = getIntInput(0, 5);
        switch (choice) {
            case 0 -> showDebugMenu();
            case 1 -> showProfileMenu();
            case 2 -> showGroupsMenu();
            case 3 -> showFriendsMenu();
            case 4 -> showMessagesMenu();
            case 5 -> logout();
        }
    }

    private void showProfileMenu() {
        while (true) {
            System.out.println("\nProfile Menu");
            System.out.println("------------------------");
            System.out.println("1. View my profile");
            System.out.println("2. Edit profile");
            System.out.println("3. Add tag");
            System.out.println("4. Remove tag");
            System.out.println("5. Delete account");
            System.out.println("6. Back");
            
            int choice = getIntInput(1, 6);
            
            switch (choice) {
                case 1:
                    viewProfile();
                    break;
                case 2:
                    editProfile();
                    break;
                case 3:
                    createTag();
                    break;
                case 4:
                    removeTag();
                    break;
                case 5:
                    if (deleteAccount()) {
                        return; // Exit to main menu if account deleted
                    }
                    break;
                case 6:
                    return;
            }
        }
    }

    private void showGroupsMenu() {
        boolean inGroupsMenu = true;
        while (inGroupsMenu) {
            System.out.println("\nClass Groups");
            System.out.println("------------------------");
            System.out.println("1. View My Groups");
            System.out.println("2. View All Groups");
            System.out.println("3. Join Group");
            System.out.println("4. Create Group");
            System.out.println("5. Leave Group");
            System.out.println("6. Enter Group");
            System.out.println("7. Back");

            int choice = getIntInput(1, 7);
            switch (choice) {
                case 1 -> viewGroups();
                case 2 -> viewAllGroups();
                case 3 -> joinGroup();
                case 4 -> createGroup();
                case 5 -> leaveGroup();
                case 6 -> enterGroup();
                case 7 -> inGroupsMenu = false;
            }
        }
    }

    private void enterGroup() {
        System.out.println("Enter group name to access: ");
        String groupName = scanner.nextLine();
        Group selectedGroup = groupHandler.findGroupByName(groupName);

        if (selectedGroup == null) {
            System.out.println("Group not found.");
            return;
        }

        if (!selectedGroup.isMember(currentUser)) {
            System.out.println(
                    "You must be a member of the group to access it.");
            return;
        }

        currentGroup = selectedGroup;
        showPostsMenu();
    }

    private void viewGroups() {
        System.out.println("\nYour Groups:");
        System.out.println("------------------------");

        try {
            List<Group> userGroups = currentUser.getGroups();
            if (userGroups.isEmpty()) {
                System.out.println("You are not a member of any groups.");
            } else {
                for (Group group : userGroups) {
                    System.out.println(
                            "- " + group.getName() + ": " + group.getDescription());
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving groups: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void joinGroup() {
        System.out.println("Enter desired group name to join: ");
        String name = scanner.nextLine();

        try {
            Group groupToJoin = groupHandler.findGroupByName(name);
            if (groupToJoin == null) {
                System.out.println("Group not found.");
                return;
            }

            if (currentUser.getGroups().contains(groupToJoin)) {
                System.out.println("Student is already in this group");
            } else {
                currentUser.joinGroup(groupToJoin);
                // Update the database
                groupHandler.addMemberToGroup(groupToJoin.getID(), currentUser);
                System.out.println(
                        "Successfully joined group: " + groupToJoin.getName());
            }
        } catch (Exception e) {
            System.out.println("Error joining group: " + e.getMessage());
        }
    }

    private void createGroup() {
        try {
            // Input validation
            System.out.println("Enter desired group name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Group name cannot be empty.");
                return;
            }

            // Check if group already exists
            if (groupHandler.findGroupByName(name) != null) {
                System.out.println("A group with this name already exists.");
                return;
            }

            System.out.println("Enter desired group description: ");
            String desc = scanner.nextLine().trim();
            if (desc.isEmpty()) {
                System.out.println("Group description cannot be empty.");
                return;
            }

            // Create new group (ID will be set by MySQLHandler)
            Group newGroup = new Group(0, name, desc);
            boolean success = groupHandler.addGroup(newGroup);

            if (success) {
                // Add creator as first member
                success = groupHandler.addMemberToGroup(
                        newGroup.getID(),
                        currentUser);
                if (success) {
                    System.out.println("Group created successfully: " + name);
                    currentUser.joinGroup(newGroup);
                } else {
                    System.out.println(
                            "Group created but failed to add you as a member.");
                }
            } else {
                System.out.println("Failed to create group. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error creating group: " + e.getMessage());
        }
    }

    private void leaveGroup() {
        System.out.println("Enter desired group name to leave: ");
        String name = scanner.nextLine();
        Group groupToLeave = groupHandler.findGroupByName(name);
        if (groupToLeave == null) {
            System.out.println("Group not found.");
            return;
        }

        if (currentUser.getGroups().contains(groupToLeave)) {
            // Update both database and in-memory state
            if (dbHandler.leaveGroup(groupToLeave, currentUser)) {
                currentUser.leaveGroup(groupToLeave);
                System.out.println(
                        "Successfully left group: " + groupToLeave.getName());
            } else {
                System.out.println("Failed to leave group. Please try again.");
            }
        } else {
            System.out.println("You are not a member of this group");
        }
    }

    private void viewAllGroups() {
        System.out.println("\nAll Available Groups:");
        System.out.println("------------------------");

        try {
            List<Group> allGroups = groupHandler.getAllGroups();
            if (allGroups.isEmpty()) {
                System.out.println("No groups exist yet.");
            } else {
                for (Group group : allGroups) {
                    System.out.println("\nGroup: " + group.getName());
                    System.out.println(
                            "Description: " + group.getDescription());
                    System.out.println(
                            "Members: " + group.getActiveMembers().size());
                    System.out.println("------------------------");
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving groups: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Helper method for getting validated integer input
    private int getIntInput(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println(
                        "Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    // Implementation of menu actions using your existing handlers
    private void viewProfileDetails() {
        System.out.println("\nProfile Details");
        System.out.println("------------------------");
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Year: " + currentUser.getYear());
        List<Tag> currentTags = tagHandler.getTagsByStudent(currentUser);
        System.out.println("Tags: " + currentTags);
        // Add more profile details as needed
    }

    private void getFriends() {
        System.out.println("Current friends: ");
        List<Student> friends = dbHandler.getFriends(currentUser.getID());
        if (friends.isEmpty()) {
            System.out.println("No friends yet.");
        } else {
            for (Student friend : friends) {
                System.out.println("- " + friend.getName());
            }
        }
    }

    private void editProfile() {
        System.out.println("\nEdit Profile Options");
        System.out.println("------------------------");
        System.out.println("\n1. Change my email");
        System.out.println("2. Change my name");
        System.out.println("3. Change my year");
        System.out.println("4. Back");
        int choice = getIntInput(1, 4);
        switch (choice) {
            case 1 -> changeEmail();
            case 2 -> changeName();
            case 3 -> changeYear();
            case 4 -> {
            } // Return to main menu
        }
    }

    private void changeEmail() {
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();
        currentUser.setEmail(email);
    }

    private void changeName() {
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        currentUser.setName(name);
    }

    private void changeYear() {
        System.out.println("Enter new year: ");
        String year = scanner.nextLine();
        currentUser.setYear(year);
    }

    private void createTag() {
        System.out.println("\nAdd a Tag");
        System.out.println("------------------------");
        System.out.print("Enter tag name: ");
        String name = scanner.nextLine();
        System.out.print("Enter tag description: ");
        String description = scanner.nextLine();

        Tag newTag = new Tag(name, description);
        if (dbHandler.containsTag(newTag)) {
            if (dbHandler.addTagToStudent(currentUser.getID(), newTag.getID())) {
                currentUser.addTag(newTag);
                System.out.println("Tag added successfully!");
            } else {
                System.out.println("Failed to add tag.");
            }
        } else {
            System.out.println("Tag does not exist in the system.");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void addExistingTag() {
        // Show available tags
        List<Tag> allTags = tagHandler.getAllTags();
        if (allTags.isEmpty()) {
            System.out.println("No tags available. Please create a new tag first.");
            return;
        }

        // Get current student's tags
        List<Tag> currentTags = tagHandler.getTagsByStudent(currentUser);

        System.out.println("\nAvailable Tags:");
        for (Tag tag : allTags) {
            boolean hasTag = currentTags.stream().anyMatch(t -> t.getID() == tag.getID());
            System.out.println("- " + tag.getName() + 
                (tag.getDescription() != null ? " (" + tag.getDescription() + ")" : "") +
                (hasTag ? " [Already Added]" : ""));
        }

        System.out.print("\nEnter tag name to add: ");
        String tagName = scanner.nextLine();
        Tag tagToAdd = tagHandler.getTagByName(tagName);
        
        if (tagToAdd == null) {
            System.out.println("Tag not found.");
            return;
        }

        // Check if student already has this tag
        if (currentTags.stream().anyMatch(t -> t.getID() == tagToAdd.getID())) {
            System.out.println("You already have this tag.");
            return;
        }

        try {
            if (studentHandler.addTagToStudent(currentUser.getID(), tagToAdd)) {
                System.out.println("Tag added successfully!");
            } else {
                System.out.println("Failed to add tag to your profile.");
            }
        } catch (Exception e) {
            System.out.println("Error adding tag: " + e.getMessage());
        }
    }

    private void removeTag() {
        System.out.print("Enter tag name to remove: ");
        String tag = scanner.nextLine();
        // fetch tag with matching name and set to tagRemove
        Tag tagRemove = tagHandler.getTagByName(tag);
        try {
            studentHandler.removeTagFromStudent(currentUser.getID(), tagRemove);
            System.out.println("Tag successfully removed!");
        } catch (Exception e) {
            System.out.println("Error removing tag: " + e.getMessage());
        }
    }

    // Add other necessary methods...

    private void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }

    private void exit() {
        isRunning = false;
        System.out.println("Thank you for using Redbird Connect!");
        scanner.close();
    }

    private void showPostsMenu() {
        boolean inPostsMenu = true;
        while (inPostsMenu) {
            System.out.println("\nPosts");
            System.out.println("------------------------");
            System.out.println("1. View All Posts");
            System.out.println("2. Create Post");
            System.out.println("3. My Posts");
            System.out.println("4. Bookmarked Posts");
            System.out.println("5. Back");

            int choice = getIntInput(1, 5);
            switch (choice) {
                case 1 -> viewAllPosts();
                case 2 -> createPost();
                case 3 -> viewMyPosts();
                case 4 -> viewBookmarkedPosts();
                case 5 -> inPostsMenu = false;
            }
        }
    }

    private void viewAllPosts() {
        if (currentGroup == null) {
            System.out.println("No group selected.");
            return;
        }

        System.out.println("\nAll Posts in " + currentGroup.getName());
        System.out.println("------------------------");

        List<Post> posts = dbHandler.getGroupPosts(currentGroup.getID());

        if (posts == null || posts.isEmpty()) {
            System.out.println("No posts in this group yet.");
        } else {
            for (Post post : posts) {
                Student owner = post.getOwner();
                String authorName;
                if (owner == null) {
                    authorName = "Unknown User";
                } else if (owner.isAnonymous()) {
                    authorName = "Anonymous";
                } else {
                    authorName = owner.getName();
                }
                System.out.println("\nPost by " + authorName + ":");
                System.out.println(post.getContent());
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void createPost() {
        if (currentGroup == null) {
            System.out.println("No group selected.");
            return;
        }

        // Verify group membership
        if (!dbHandler.isStudentInGroup(
                currentUser.getID(),
                currentGroup.getID())) {
            System.out.println(
                    "You must be a member of the group to create posts.");
            return;
        }

        System.out.println("\nCreate New Post");
        System.out.println("------------------------");
        System.out.print("Enter post content:\n");
        String content = scanner.nextLine();

        if (content.trim().isEmpty()) {
            System.out.println("Post content cannot be empty.");
            return;
        }

        // Create new post with the current group context
        Post newPost = new Post(0, content, currentUser, currentGroup); // ID will be set by database

        try {
            // First create in database to get the ID
            if (dbHandler.createPost(newPost)) {
                // Then add to post handler with the assigned ID
                if (postHandler.addPost(newPost)) {
                    System.out.println("Post created successfully!");
                } else {
                    System.out.println("Failed to add post to memory cache.");
                }
            } else {
                System.out.println("Failed to create post in database.");
            }
        } catch (Exception e) {
            System.out.println("Error creating post: " + e.getMessage());
        }
    }

    private void viewMyPosts() {
        if (currentGroup == null) {
            System.out.println("No group selected.");
            return;
        }

        while (true) {
            System.out.println("\nYour Posts in " + currentGroup.getName());
            System.out.println("------------------------");

            List<Post> allPosts = dbHandler.getGroupPosts(currentGroup.getID());
            List<Post> myPosts = allPosts
                    .stream()
                    .filter(post -> post.getOwner().getID() == currentUser.getID())
                    .collect(Collectors.toList());

            if (myPosts.isEmpty()) {
                System.out.println(
                        "You haven't made any posts in this group yet.");
                System.out.println("\nPress Enter to go back...");
                scanner.nextLine();
                return;
            }

            // Display posts with numbers
            for (int i = 0; i < myPosts.size(); i++) {
                System.out.println("\n[" + (i + 1) + "] Post:");
                System.out.println(myPosts.get(i).getContent());
            }

            System.out.println("\nOptions:");
            System.out.println("1. Edit a post");
            System.out.println("2. Delete a post");
            System.out.println("3. Back");

            int choice = getIntInput(1, 3);

            if (choice == 3) {
                return;
            }

            System.out.println(
                    "Enter the number of the post to " +
                            (choice == 1 ? "edit" : "delete") +
                            " (1-" +
                            myPosts.size() +
                            "):");
            int postIndex = getIntInput(1, myPosts.size()) - 1;
            Post selectedPost = myPosts.get(postIndex);

            if (choice == 1) {
                editPost(selectedPost);
            } else {
                deletePost(selectedPost);
            }
        }
    }

    private void editPost(Post post) {
        System.out.println("\nCurrent content:");
        System.out.println(post.getContent());
        System.out.println(
                "\nEnter new content (or press Enter without typing to cancel):");
        String newContent = scanner.nextLine();

        if (newContent.trim().isEmpty()) {
            System.out.println("Edit cancelled.");
            return;
        }

        // First update in database
        if (dbHandler.editPost(post)) {
            // Then update in memory
            post.setContent(newContent);
            postHandler.updatePostContent(post.getID(), newContent);
            System.out.println("Post updated successfully!");
        } else {
            System.out.println("Failed to update post in database.");
        }
    }

    private void deletePost(Post post) {
        System.out.println("Are you sure you want to delete this post? (y/n)");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y")) {
            // First delete from database
            if (dbHandler.deletePost(post.getID())) {
                // Then remove from memory
                postHandler.removePost(post.getID());
                System.out.println("Post deleted successfully!");
            } else {
                System.out.println("Failed to delete post from database.");
            }
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private void viewBookmarkedPosts() {
        List<Post> bookmarked = dbHandler.getBookmarkedPosts(
                currentUser.getID());
        if (bookmarked.isEmpty()) {
            System.out.println("No bookmarked posts.");
            return;
        }

        System.out.println("\nBookmarked Posts:");
        for (Post post : bookmarked) {
            displayPost(post);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void displayPost(Post post) {
        System.out.println("\n------------------------");
        String authorName = post.getOwner().isAnonymous()
                ? "Anonymous"
                : post.getOwner().getName();
        System.out.println("Author: " + authorName);
        System.out.println("Group: " + post.getGroup().getName());
        System.out.println("Content: " + post.getContent());
        System.out.println("------------------------");
    }

    private int generateUniquePostId() {
        // Simple implementation - should be replaced with database auto-increment
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    private void showFriendsMenu() {
        boolean inFriendsMenu = true;
        while (inFriendsMenu) {
            System.out.println("\nFriends");
            System.out.println("------------------------");
            System.out.println("1. View Friends List");
            System.out.println("2. Send Friend Request");
            System.out.println("3. View Pending Requests");
            System.out.println("4. Blocked Users");
            System.out.println("5. Remove Friend");
            System.out.println("6. Back");

            int choice = getIntInput(1, 6);
            switch (choice) {
                case 1 -> getFriends();
                case 2 -> sendFriendRequest();
                case 3 -> viewPendingRequests();
                case 4 -> showBlockedUsersMenu();
                case 5 -> removeFriend();
                case 6 -> inFriendsMenu = false;
            }
        }
    }

    private void sendFriendRequest() {
        System.out.print("Enter the username of the user you want to add: ");
        String username = scanner.nextLine();

        // Find the user by username
        Student targetUser = studentHandler.getStudentByUsername(username);
        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }

        // Check if already friends
        List<Student> friends = dbHandler.getFriends(currentUser.getID());
        if (friends.stream().anyMatch(f -> f.getID() == targetUser.getID())) {
            System.out.println("You are already friends with this user.");
            return;
        }

        // Check if user is blocked
        if (dbHandler.isUserBlocked(currentUser.getID(), targetUser.getID())) {
            System.out.println("You cannot send a friend request to a blocked user.");
            return;
        }

        // Check if there's already a pending request
        if (dbHandler.hasPendingFriendRequest(currentUser.getID(), targetUser.getID())) {
            System.out.println("You already have a pending friend request to this user.");
            return;
        }

        // Send the friend request
        if (dbHandler.sendFriendRequest(currentUser.getID(), targetUser.getID())) {
            System.out.println("Friend request sent successfully!");
        } else {
            System.out.println("Failed to send friend request. Please try again.");
        }
    }

    private void viewPendingRequests() {
        List<Student> requests = dbHandler.getIncomingFriendRequests(currentUser.getID());
        if (requests.isEmpty()) {
            System.out.println("No pending friend requests.");
            return;
        }

        System.out.println("\nPending Friend Requests:");
        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ". " + requests.get(i).getName());
        }

        System.out.println("\n1. Accept a request");
        System.out.println("2. Decline a request");
        System.out.println("3. Back");

        int choice = getIntInput(1, 3);
        if (choice == 3)
            return;

        System.out.println("Enter the number of the request (1-" + requests.size() + "): ");
        int requestNum = getIntInput(1, requests.size());
        Student requester = requests.get(requestNum - 1);

        if (choice == 1) {
            if (dbHandler.acceptFriendRequest(requester.getID(), currentUser.getID())) {
                System.out.println("Friend request accepted!");
            } else {
                System.out.println("Failed to accept friend request.");
            }
        } else {
            if (dbHandler.declineFriendRequest(requester.getID(), currentUser.getID())) {
                System.out.println("Friend request declined.");
            } else {
                System.out.println("Failed to decline friend request.");
            }
        }
    }

    private void showBlockedUsersMenu() {
        boolean inBlockedMenu = true;
        while (inBlockedMenu) {
            System.out.println("\nBlocked Users");
            System.out.println("------------------------");
            
            // Display current blocked users
            List<Student> blockedUsers = dbHandler.getBlockedUsers(currentUser.getID());
            if (blockedUsers.isEmpty()) {
                System.out.println("You haven't blocked any users.");
            } else {
                System.out.println("Currently blocked users:");
                for (Student user : blockedUsers) {
                    System.out.println("- " + user.getName());
                }
            }

            System.out.println("\n1. Block a User");
            System.out.println("2. Unblock a User");
            System.out.println("3. Back");

            int choice = getIntInput(1, 3);
            switch (choice) {
                case 1 -> blockUser();
                case 2 -> unblockUser();
                case 3 -> inBlockedMenu = false;
            }
        }
    }

    private void unblockUser() {
        System.out.print("Enter the username of the user to unblock: ");
        String username = scanner.nextLine();

        Student targetUser = studentHandler.getStudentByUsername(username);
        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }

        if (!dbHandler.isUserBlocked(currentUser.getID(), targetUser.getID())) {
            System.out.println("This user is not blocked.");
            return;
        }

        if (dbHandler.unblockUser(currentUser.getID(), targetUser.getID())) {
            System.out.println("User unblocked successfully.");
        } else {
            System.out.println("Failed to unblock user.");
        }
    }

    private void blockUser() {
        System.out.print("Enter the username of the user to block: ");
        String username = scanner.nextLine();

        Student targetUser = studentHandler.getStudentByUsername(username);
        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }

        if (dbHandler.blockUser(currentUser.getID(), targetUser.getID())) {
            System.out.println("User blocked successfully.");
        } else {
            System.out.println("Failed to block user.");
        }
    }

    private void removeFriend() {
        System.out.print("Enter the username of the friend to remove: ");
        String username = scanner.nextLine();

        Student targetUser = studentHandler.getStudentByUsername(username);
        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }

        List<Student> friends = dbHandler.getFriends(currentUser.getID());
        if (friends.stream().noneMatch(f -> f.getID() == targetUser.getID())) {
            System.out.println("This user is not in your friends list.");
            return;
        }

        if (dbHandler.removeFriend(currentUser.getID(), targetUser.getID())) {
            System.out.println("Friend removed successfully.");
        } else {
            System.out.println("Failed to remove friend.");
        }
    }

    private void showDebugMenu() {
        boolean inDebugMenu = true;
        while (inDebugMenu) {
            System.out.println("\nDebug Menu");
            System.out.println("------------------------");
            System.out.println("1. View All Database Students");
            System.out.println("2. View All Database Groups");
            System.out.println("3. View Database Connection Status");
            System.out.println("4. Test Database Queries");
            System.out.println("5. Reset/Create Database");
            System.out.println("6. Return to Main Menu");

            int choice = getIntInput(1, 6);
            switch (choice) {
                case 1 -> debugViewAllStudents();
                case 2 -> debugViewAllGroups();
                case 3 -> debugCheckConnection();
                case 4 -> debugTestQueries();
                case 5 -> debugResetDatabase();
                case 6 -> inDebugMenu = false;
            }
        }
    }

    private void debugViewAllStudents() {
        System.out.println("\nAll Students in Database:");
        System.out.println("------------------------");
        try {
            List<Student> allStudents = dbHandler.getAllStudents();
            if (allStudents == null || allStudents.isEmpty()) {
                System.out.println("No students found in database.");
            } else {
                for (Student student : allStudents) {
                    System.out.println("ID: " + student.getID());
                    System.out.println("Username/Email: " + student.getEmail());
                    System.out.println("Year: " + student.getYear());
                    System.out.println("------------------------");
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving students: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void debugViewAllGroups() {
        System.out.println("\nAll Groups in Database:");
        System.out.println("------------------------");
        List<Group> allGroups = groupHandler.getAllGroups();
        for (Group group : allGroups) {
            System.out.println("ID: " + group.getID());
            System.out.println("Name: " + group.getName());
            System.out.println("Description: " + group.getDescription());
            System.out.println("------------------------");
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void debugCheckConnection() {
        System.out.println("\nDatabase Connection Status:");
        System.out.println("------------------------");
        System.out.println("Connected: " + dbHandler.getDatabase().isConnected());
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void debugTestQueries() {
        System.out.println("\nTesting Basic Queries...");
        System.out.println("------------------------");
        // Add basic query tests here if needed
        System.out.println("Query testing complete.");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void debugResetDatabase() {
        System.out.println("\nResetting/Creating Database...");
        System.out.println("------------------------");
        if (dbHandler.resetDatabase()) {
            System.out.println("Database reset successful!");
        } else {
            System.out.println("Failed to reset database. Check console for errors.");
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void showMessagesMenu() {
        System.out.println("\nMessages feature coming soon!");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void toggleAnonymousMode() {
        boolean currentMode = currentUser.isAnonymous();
        if (dbHandler.toggleAnonymousMode(currentUser.getID(), !currentMode)) {
            currentUser.setAnonymous(!currentMode);
            System.out.println("Anonymous mode " + (currentUser.isAnonymous() ? "enabled" : "disabled"));
        } else {
            System.out.println("Failed to toggle anonymous mode");
        }
    }

    private boolean deleteAccount() {
        System.out.println("\nWARNING: This action cannot be undone!");
        System.out.print("Enter your password to confirm account deletion: ");
        String password = scanner.nextLine();
        
        try {
            // Verify password before deletion
            if (dbHandler.authenticateStudent(currentUser.getName(), password) != null) {
                if (studentController.removeStudent(currentUser.getID())) {
                    System.out.println("Account deleted successfully.");
                    currentUser = null; // Clear current user
                    return true;
                } else {
                    System.out.println("Failed to delete account.");
                }
            } else {
                System.out.println("Incorrect password. Account deletion cancelled.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting account: " + e.getMessage());
        }
        return false;
    }

    private void viewProfile() {
        System.out.println("\nProfile Information");
        System.out.println("------------------------");
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Year: " + currentUser.getYear());
        System.out.println("Anonymous Mode: " + (currentUser.isAnonymous() ? "Enabled" : "Disabled"));
        
        // Display tags
        System.out.println("\nInterests/Tags:");
        List<Tag> tags = currentUser.getTags();
        if (tags.isEmpty()) {
            System.out.println("No tags added yet.");
        } else {
            for (Tag tag : tags) {
                System.out.println("- " + tag.getName());
            }
        }

        // Display recent activity
        System.out.println("\nRecent Activity:");
        List<Activity> activities = profileHandler.getProfileActivity(currentUser.getID());
        if (activities.isEmpty()) {
            System.out.println("No recent activity.");
        } else {
            for (Activity activity : activities) {
                System.out.println("- " + activity.getDescription() + " (" + activity.getTimestamp() + ")");
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
