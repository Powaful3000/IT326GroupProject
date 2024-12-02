import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainGUI {
    private final StudentController studentController;
    private final DatabaseHandler dbHandler;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainGUI(StudentController studentController, DatabaseHandler dbHandler) {
        this.studentController = studentController;
        this.dbHandler = dbHandler;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("RedBird Connect");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Center on screen

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add different panels
        mainPanel.add(createWelcomePanel(), "Welcome");
        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createAccountPanel(), "Create Account");
        mainPanel.add(createStudentDashboardPanel(), "Dashboard");
        mainPanel.add(createGroupManagementPanel(), "Groups");
        mainPanel.add(createPostManagementPanel(), "Posts");
        mainPanel.add(createTagManagementPanel(), "Tags");
        mainPanel.add(createSearchPanel(), "Search");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to RedBird Connect");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        welcomePanel.add(welcomeLabel, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        welcomePanel.add(loginButton, gbc);

        // Create Account button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(e -> cardLayout.show(mainPanel, "Create Account"));
        gbc.gridx = 1;
        welcomePanel.add(createAccountButton, gbc);

        return welcomePanel;
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // Username Field
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridy = 2;
        gbc.gridx = 0;
        loginPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (!Validator.isValidString(username) || !Validator.isValidString(password)) {
                JOptionPane.showMessageDialog(null, "Please enter both username and password");
                return;
            }

            System.out.println("Attempting authentication for user: " + username);

            Student authenticatedStudent = dbHandler.authenticateStudent(username, password);
            if (authenticatedStudent != null) {
                StudentHandler studentHandler = studentController.getStudentHandler();
                studentHandler.setCurrentStudent(authenticatedStudent);
                studentHandler.addStudent(authenticatedStudent);

                updateDashboardInfo(authenticatedStudent);
                JOptionPane.showMessageDialog(null, "Login successful!");
                System.out.println("Showing dashboard panel");
                cardLayout.show(mainPanel, "Dashboard");
                // Clear login fields
                usernameField.setText("");
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password");
            }
        });
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
        gbc.gridy = 4;
        loginPanel.add(backButton, gbc);

        return loginPanel;
    }

    private JPanel createAccountPanel() {
        JPanel createAccountPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        createAccountPanel.add(titleLabel, gbc);

        // Username Field
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        createAccountPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        createAccountPanel.add(usernameField, gbc);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridy = 2;
        gbc.gridx = 0;
        createAccountPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        createAccountPanel.add(passwordField, gbc);

        // Year Field
        JLabel yearLabel = new JLabel("Year:");
        String[] years = { "Freshman", "Sophomore", "Junior", "Senior" };
        JComboBox<String> yearComboBox = new JComboBox<>(years);
        gbc.gridy = 3;
        gbc.gridx = 0;
        createAccountPanel.add(yearLabel, gbc);
        gbc.gridx = 1;
        createAccountPanel.add(yearComboBox, gbc);

        // Create Account Button
        JButton createButton = new JButton("Create Account");
        createButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String year = (String) yearComboBox.getSelectedItem();

            if (!Validator.isValidString(username) || !Validator.isValidPassword(password)) {
                JOptionPane.showMessageDialog(null,
                        "Invalid username or password. Password must be at least 8 characters long and include at least one letter and one number.");
                return;
            }

            if (dbHandler.doesUsernameExist(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.");
                return;
            }

            int id = (int) (Math.random() * 9000) + 1000;
            Student newStudent = new Student(id, username, year, null, null, null);

            if (dbHandler.addStudent(newStudent, password)) {
                JOptionPane.showMessageDialog(null, "Account created successfully!");
                cardLayout.show(mainPanel, "Login");
                usernameField.setText("");
                passwordField.setText("");
                yearComboBox.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create account. Please try again.");
            }
        });
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        createAccountPanel.add(createButton, gbc);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
        gbc.gridy = 5;
        createAccountPanel.add(backButton, gbc);

        return createAccountPanel;
    }

    private JPanel createPostManagementPanel() {
        JPanel postPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create new post interface
        // View/edit existing posts
        // Post feed for groups

        return postPanel;
    }

    private JPanel createGroupManagementPanel() {
        JPanel groupPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Group Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        groupPanel.add(titleLabel, gbc);

        // Create the group list model first
        DefaultListModel<String> groupListModel = new DefaultListModel<>();
        JList<String> groupList = new JList<>(groupListModel);

        // Search/Filter Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

        JTextField searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                filterGroups();
            }

            public void removeUpdate(DocumentEvent e) {
                filterGroups();
            }

            public void insertUpdate(DocumentEvent e) {
                filterGroups();
            }

            private void filterGroups() {
                String searchText = searchField.getText().toLowerCase();
                groupListModel.clear();
                studentController.getAllGroups().stream()
                        .filter(group -> group.getName().toLowerCase().contains(searchText))
                        .forEach(group -> groupListModel.addElement(group.getName()));
            }
        });

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        groupPanel.add(searchPanel, gbc);

        // Group List Panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Available Groups"));

        JScrollPane scrollPane = new JScrollPane(groupList);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // Populate group list
        List<Group> groups = studentController.getAllGroups();
        for (Group group : groups) {
            groupListModel.addElement(group.getName());
        }

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        groupPanel.add(listPanel, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Create Group Button
        JButton createGroupBtn = new JButton("Create Group");
        createGroupBtn.addActionListener(e -> {
            String groupName = JOptionPane.showInputDialog(groupPanel, "Enter group name:");
            if (groupName != null && !groupName.trim().isEmpty()) {
                String description = JOptionPane.showInputDialog(groupPanel, "Enter group description:");
                if (description != null) {
                    Group newGroup = new Group(groupName, description);
                    if (studentController.createGroup(newGroup)) {
                        groupListModel.addElement(groupName);
                        JOptionPane.showMessageDialog(groupPanel, "Group created successfully!");
                    } else {
                        JOptionPane.showMessageDialog(groupPanel, "Failed to create group. Name might be taken.");
                    }
                }
            }
        });

        // Join Group Button
        JButton joinGroupBtn = new JButton("Join Group");
        joinGroupBtn.addActionListener(e -> {
            Student currentStudent = studentController.getStudentHandler().getCurrentStudent();
            if (currentStudent == null) {
                JOptionPane.showMessageDialog(groupPanel, "Please log in first.");
                return;
            }

            String selectedGroup = groupList.getSelectedValue();
            if (selectedGroup != null) {
                Group group = studentController.getGroupByName(selectedGroup);
                if (group != null) {
                    if (studentController.joinGroup(group)) {
                        JOptionPane.showMessageDialog(groupPanel, "Successfully joined group!");
                    } else {
                        JOptionPane.showMessageDialog(groupPanel,
                                "Failed to join group. You might already be a member.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(groupPanel, "Please select a group to join.");
            }
        });

        // View Details Button
        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.addActionListener(e -> {
            String selectedGroup = groupList.getSelectedValue();
            if (selectedGroup != null) {
                Group group = studentController.getGroupByName(selectedGroup);
                if (group != null) {
                    // Create and show group details panel
                    JPanel detailsPanel = createGroupDetailsPanel(group);
                    mainPanel.add(detailsPanel, "GroupDetails-" + group.getName());
                    cardLayout.show(mainPanel, "GroupDetails-" + group.getName());
                }
            } else {
                JOptionPane.showMessageDialog(groupPanel, "Please select a group to view.");
            }
        });

        // Back Button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));

        buttonPanel.add(createGroupBtn);
        buttonPanel.add(joinGroupBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(backButton);

        gbc.gridy = 3;
        gbc.weighty = 0.0; // Reset weight for buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        groupPanel.add(buttonPanel, gbc);

        return groupPanel;
    }

    private JPanel createGroupDetailsPanel(Group group) {
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Group Info Section
        JLabel titleLabel = new JLabel("Group: " + group.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        detailsPanel.add(titleLabel, gbc);

        JLabel descLabel = new JLabel("Description: " + group.getDescription());
        gbc.gridy = 1;
        detailsPanel.add(descLabel, gbc);

        // Members List Section
        JLabel membersLabel = new JLabel("Members:");
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        detailsPanel.add(membersLabel, gbc);

        DefaultListModel<String> memberListModel = new DefaultListModel<>();
        for (Student member : group.getMembers()) {
            memberListModel.addElement(member.getName());
        }
        JList<String> memberList = new JList<>(memberListModel);
        JScrollPane memberScroll = new JScrollPane(memberList);
        memberScroll.setPreferredSize(new Dimension(350, 200));
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        detailsPanel.add(memberScroll, gbc);

        // Posts Section
        JLabel postsLabel = new JLabel("Group Posts:");
        gbc.gridy = 4;
        gbc.weighty = 0.0;
        detailsPanel.add(postsLabel, gbc);

        DefaultListModel<String> postListModel = new DefaultListModel<>();
        List<Post> groupPosts = studentController.getGroupPosts(group);
        for (Post post : groupPosts) {
            postListModel.addElement(post.getContent());
        }
        JList<String> postList = new JList<>(postListModel);
        JScrollPane postScroll = new JScrollPane(postList);
        postScroll.setPreferredSize(new Dimension(350, 200));
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        detailsPanel.add(postScroll, gbc);

        // Admin Controls Section
        JPanel adminPanel = new JPanel(new FlowLayout());
        JButton addMemberBtn = new JButton("Add Member");
        JButton removeMemberBtn = new JButton("Remove Member");
        JButton deleteGroupBtn = new JButton("Delete Group");

        addMemberBtn.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Enter username to add:");
            if (username != null && !username.trim().isEmpty()) {
                // Add member logic
            }
        });

        removeMemberBtn.addActionListener(e -> {
            String username = JOptionPane.showInputDialog("Enter username to remove:");
            if (username != null && !username.trim().isEmpty()) {
                // Remove member logic
            }
        });

        deleteGroupBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this group?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Delete group logic
            }
        });

        adminPanel.add(addMemberBtn);
        adminPanel.add(removeMemberBtn);
        adminPanel.add(deleteGroupBtn);

        gbc.gridy = 6;
        gbc.weighty = 0.0;
        detailsPanel.add(adminPanel, gbc);

        // Back Button
        JButton backButton = new JButton("Back to Groups");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Groups"));
        gbc.gridy = 7;
        detailsPanel.add(backButton, gbc);

        return detailsPanel;
    }

    private JPanel createTagManagementPanel() {
        JPanel tagPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add/remove tags interface
        // View all available tags
        // Tag search/filter functionality

        return tagPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();

        JButton profileBtn = new JButton("Profile");
        JButton groupsBtn = new JButton("Groups");
        JButton postsBtn = new JButton("Posts");
        JButton tagsBtn = new JButton("Tags");
        JButton logoutBtn = new JButton("Logout");

        // Add action listeners to switch between panels
        profileBtn.addActionListener(e -> cardLayout.show(mainPanel, "Profile"));
        groupsBtn.addActionListener(e -> cardLayout.show(mainPanel, "Groups"));
        // ... add other listeners

        return navPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();

        // Search by tags
        // Search for groups
        // Search for other students
        // Filter options

        return searchPanel;
    }

    private JPanel createStudentDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setName("Dashboard");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Student Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dashboardPanel.add(titleLabel, gbc);

        // Student Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        infoPanel.add(new JLabel("ID:"));
        JLabel idLabel = new JLabel("N/A");
        infoPanel.add(idLabel);

        infoPanel.add(new JLabel("Name:"));
        JLabel nameLabel = new JLabel("N/A");
        infoPanel.add(nameLabel);

        infoPanel.add(new JLabel("Year:"));
        JLabel yearLabel = new JLabel("N/A");
        infoPanel.add(yearLabel);

        gbc.gridy = 1;
        dashboardPanel.add(infoPanel, gbc);

        // Navigation Buttons Panel
        JPanel navPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        navPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));

        JButton groupsButton = new JButton("My Groups");
        groupsButton.addActionListener(e -> cardLayout.show(mainPanel, "Groups"));

        JButton postsButton = new JButton("My Posts");
        postsButton.addActionListener(e -> cardLayout.show(mainPanel, "Posts"));

        JButton tagsButton = new JButton("Manage Tags");
        tagsButton.addActionListener(e -> cardLayout.show(mainPanel, "Tags"));

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> cardLayout.show(mainPanel, "Search"));

        JButton profileButton = new JButton("Edit Profile");
        profileButton.addActionListener(e -> {
            // TODO: Implement edit profile functionality
            JOptionPane.showMessageDialog(null, "Edit Profile functionality coming soon!");
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                cardLayout.show(mainPanel, "Welcome");
            }
        });

        navPanel.add(groupsButton);
        navPanel.add(postsButton);
        navPanel.add(tagsButton);
        navPanel.add(searchButton);
        navPanel.add(profileButton);
        navPanel.add(logoutButton);

        gbc.gridy = 2;
        dashboardPanel.add(navPanel, gbc);

        return dashboardPanel;
    }

    private void updateDashboardInfo(Student student) {
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && "Dashboard".equals(comp.getName())) {
                JPanel dashboardPanel = (JPanel) comp;
                for (Component dashComp : dashboardPanel.getComponents()) {
                    if (dashComp instanceof JPanel &&
                            dashComp.getParent().getLayout() instanceof GridBagLayout) {
                        JPanel infoPanel = (JPanel) dashComp;
                        Component[] labels = infoPanel.getComponents();
                        for (int i = 0; i < labels.length; i++) {
                            if (labels[i] instanceof JLabel) {
                                JLabel label = (JLabel) labels[i];
                                switch (i) {
                                    case 1: // ID label
                                        label.setText(String.valueOf(student.getID()));
                                        break;
                                    case 3: // Name label
                                        label.setText(student.getName());
                                        break;
                                    case 5: // Year label
                                        label.setText(student.getYear());
                                        break;
                                }
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting application...");

        // Use DatabaseFactory to get the singleton instance
        Database database = DatabaseFactory.getDatabase(DatabaseFactory.DatabaseType.MYSQL, "StudentDB");
        DatabaseHandler dbHandler = new DatabaseHandler(database);
        dbHandler.connect();

        // Initialize handlers
        StudentHandler studentHandler = new StudentHandler();
        GroupHandler groupHandler = new GroupHandler(dbHandler);

        // Initialize controller
        final StudentController studentController = new StudentController(studentHandler, groupHandler);

        // Start GUI
        SwingUtilities.invokeLater(() -> new MainGUI(studentController, dbHandler));

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("shutdown disconnect");
                dbHandler.disconnect();
            }
        }));
    }
}
