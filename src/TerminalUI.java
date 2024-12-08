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
 * 4. Block User
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
        this.tagHandler = new TagHandler();
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

        int choice = getIntInput(1, 3);
        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleRegistration();
            case 3 -> exit();
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

        int choice = getIntInput(1, 5);
        switch (choice) {
            case 1 -> showProfileMenu();
            case 2 -> showGroupsMenu();
            case 3 -> showFriendsMenu();
            // case 4 -> showMessagesMenu();
            case 5 -> logout();
        }
    }

    private void showProfileMenu() {
        System.out.println("\nMy Profile");
        System.out.println("------------------------");
        System.out.println("Current Tags: " + currentUser.getTags());
        System.out.println("\n1. View Profile Details");
        System.out.println("2. View Friends List");
        System.out.println("3. Edit Profile");
        System.out.println("4. Add Tag");
        System.out.println("5. Remove Tag");
        System.out.println("6. Enable/Disable Anonymous Mode");
        System.out.println("7. Back");

        int choice = getIntInput(1, 6);
        switch (choice) {
            case 1 -> viewProfileDetails();
            case 2 -> getFriends();
            case 3 -> editProfile();
            case 4 -> addTag();
            case 5 -> removeTag();
            // case 5 -> toggleAnonymousMode();
            case 7 -> {
            } // Return to main menu
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
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Tags: " + currentUser.getTags());
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
        System.out.println("5. Back");
        int choice = getIntInput(1, 4);
        switch (choice) {
            case 1 -> changeEmail();
            case 2 -> changeName();
            case 3 -> changeYear();
            case 5 -> {
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

    private void addTag() {
        System.out.print("Enter new tag name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new tag description: ");
        String desc = scanner.nextLine();
        Tag tagAdd = new Tag(0, name, desc); // placeholder 0
        try {
            studentHandler.addTagToStudent(currentUser.getID(), tagAdd);
            System.out.println("Tag added successfully!");
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

        if (posts.isEmpty()) {
            System.out.println("No posts in this group yet.");
        } else {
            for (Post post : posts) {
                System.out.println(
                        "\nPost by " + post.getOwner().getName() + ":");
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
            System.out.println("4. Block User");
            System.out.println("5. Remove Friend");
            System.out.println("6. Back");

            int choice = getIntInput(1, 6);
            switch (choice) {
                case 1 -> getFriends();
                case 2 -> sendFriendRequest();
                case 3 -> viewPendingRequests();
                case 4 -> blockUser();
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
}
