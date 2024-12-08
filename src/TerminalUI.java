import java.util.Scanner;
import java.util.List;


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

    public TerminalUI() {
        Database database = DatabaseFactory.getDatabase(DatabaseFactory.DatabaseType.MYSQL, "StudentDB");
        this.dbHandler = new DatabaseHandler(database);
        this.scanner = new Scanner(System.in);
        this.studentHandler = StudentHandler.getInstance();
        this.groupHandler = new GroupHandler(dbHandler);
        this.postHandler = new PostHandler();
        this.tagHandler = new TagHandler();
        this.isRunning = true;
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
            Student student = studentHandler.authenticateStudent(email, password);
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

    private void handleRegistration()
    {
    	System.out.print("Desired email: ");
        String email = scanner.nextLine();
        System.out.print("Desired password: ");
        String password = scanner.nextLine();
        
        // check if email and password already exist
        // if not, create account
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
            //case 3 -> showFriendsMenu();
            //case 4 -> showMessagesMenu();
            case 5 -> logout();
        }
    }

    private void showProfileMenu() {
        System.out.println("\nMy Profile");
        System.out.println("------------------------");
        System.out.println("Current Tags: " + currentUser.getTags());
        System.out.println("\n1. View Profile Details");
        System.out.println("2. Edit Profile");
        System.out.println("3. Add Tag");
        System.out.println("4. Remove Tag");
        System.out.println("5. Enable/Disable Anonymous Mode");
        System.out.println("6. Back");

        int choice = getIntInput(1, 6);
        switch (choice) {
            case 1 -> viewProfileDetails();
            case 2 -> editProfile();
            case 3 -> addTag();
            case 4 -> removeTag();
            //case 5 -> toggleAnonymousMode();
            case 6 -> {} // Return to main menu
        }
    }

    private void showGroupsMenu() {
        System.out.println("\nClass Groups");
        System.out.println("------------------------");
        // Display user's groups using your existing GroupHandler
        System.out.println("\n1. View My Groups");
        System.out.println("2. Join Group");
        System.out.println("3. Create Group");
        System.out.println("4. Leave Group");
        System.out.println("5. Back");

        int choice = getIntInput(1, 5);
        switch (choice) {
            case 1 -> viewGroups();
            case 2 -> joinGroup();
            case 3 -> createGroup();
            case 4 -> leaveGroup();
            case 5 -> {} // Return to main menu
        }
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
                    System.out.println("- " + group.getName() + ": " + group.getDescription());
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
                System.out.println("Successfully joined group: " + groupToJoin.getName());
            }
        } catch (Exception e) {
            System.out.println("Error joining group: " + e.getMessage());
        }
    }
    
    private void createGroup() {
    	System.out.println("Enter desired group name: ");
    	String name = scanner.nextLine();
    	System.out.println("Enter desired group description: ");
    	String desc = scanner.nextLine();
    	Group newGroup = new Group(0, name, desc); // placeholder 0 until logic figured out
    	groupHandler.addGroup(newGroup);
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
            currentUser.leaveGroup(groupToLeave);
        } else {
            System.out.println("Student is not in this group");
        }
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
                System.out.println("Please enter a number between " + min + " and " + max);
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
            case 4 -> {} // Return to main menu
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
}