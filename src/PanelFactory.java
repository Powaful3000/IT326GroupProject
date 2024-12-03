import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PanelFactory {
    private final StudentController studentController;
    private final DatabaseHandler dbHandler;
    private final StudentHandler studentHandler;
    private final DialogManager dialogManager;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final PostHandler postHandler;
    private final TagHandler tagHandler;

    public PanelFactory(StudentController studentController, DatabaseHandler dbHandler, 
                       StudentHandler studentHandler, DialogManager dialogManager,
                       CardLayout cardLayout, JPanel mainPanel, PostHandler postHandler,
                       TagHandler tagHandler) {
        this.studentController = studentController;
        this.dbHandler = dbHandler;
        this.studentHandler = studentHandler;
        this.dialogManager = dialogManager;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.postHandler = postHandler;
        this.tagHandler = tagHandler;
    }

    public void initializePanels() {
        // Create all panels first
        JPanel welcomePanel = createWelcomePanel();
        JPanel loginPanel = createLoginPanel();
        JPanel accountPanel = createAccountPanel();
        JPanel dashboardPanel = createStudentDashboardPanel();
        JPanel groupManagementPanel = createGroupManagementPanel();
        JPanel postManagementPanel = createPostManagementPanel();
        JPanel tagManagementPanel = createTagManagementPanel();
        JPanel searchPanel = createSearchPanel();
        JPanel profilePanel = createProfileManagementPanel();

        // Set names for panels that need them
        groupManagementPanel.setName("Groups");
        dashboardPanel.setName("Dashboard");
        tagManagementPanel.setName("Tags");
        
        // Add panels to main panel
        mainPanel.add(welcomePanel, "Welcome");
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(accountPanel, "Create Account");
        mainPanel.add(dashboardPanel, "Dashboard");
        mainPanel.add(groupManagementPanel, "Groups");
        mainPanel.add(postManagementPanel, "Posts");
        mainPanel.add(tagManagementPanel, "Tags");
        mainPanel.add(searchPanel, "Search");
        mainPanel.add(profilePanel, "Profile");
        
        // Set up refresh callbacks after all panels are added
        dialogManager.setRefreshCallback(() -> refreshTagLists(findPanel(mainPanel, "Tags")));
        
        // Initial refresh of group lists
        refreshGroupLists(groupManagementPanel);
    }

    public JPanel findPanel(Container container, String name) {
        if (container == null) return null;
        
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel && name.equals(comp.getName())) {
                return (JPanel) comp;
            }
            if (comp instanceof Container) {
                JPanel found = findPanel((Container) comp, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public JPanel createWelcomePanel() {
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

    public JPanel createLoginPanel() {
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

        // Password FieldcreateWelcomePanel
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
            dialogManager.handleLoginAttempt(usernameField, passwordField, mainPanel);
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

    public JPanel createAccountPanel() {
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
            dialogManager.handleCreateAccount(usernameField, passwordField, yearComboBox, mainPanel);
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

    public JPanel createStudentDashboardPanel() {
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
        infoPanel.setName("StudentInfoPanel");

        // Create labels with specific names for identification
        JLabel idLabel = new JLabel("N/A");
        idLabel.setName("studentId");
        JLabel nameLabel = new JLabel("N/A");
        nameLabel.setName("studentName");
        JLabel yearLabel = new JLabel("N/A");
        yearLabel.setName("studentYear");

        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(idLabel);
        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(nameLabel);
        infoPanel.add(new JLabel("Year:"));
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
            JOptionPane.showMessageDialog(null, "Edit Profile functionality coming soon!");
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dialogManager.handleLogout(mainPanel);
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

    public JPanel createPostManagementPanel() {
        JPanel postPanel = new JPanel(new BorderLayout());
        postPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Post Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        postPanel.add(titleLabel, BorderLayout.NORTH);

        // Main content panel with GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Create Post Section
        JPanel createPostPanel = new JPanel(new BorderLayout());
        createPostPanel.setBorder(BorderFactory.createTitledBorder("Create New Post"));
        
        JTextArea postContent = new JTextArea(4, 30);
        postContent.setLineWrap(true);
        postContent.setWrapStyleWord(true);
        JScrollPane postScrollPane = new JScrollPane(postContent);
        
        JButton createPostBtn = new JButton("Create Post");
        createPostBtn.addActionListener(e -> {
            dialogManager.handleCreatePost(postContent, () -> refreshPostLists(postPanel));
        });

        createPostPanel.add(postScrollPane, BorderLayout.CENTER);
        createPostPanel.add(createPostBtn, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(createPostPanel, gbc);

        // My Posts Section
        JPanel myPostsPanel = new JPanel(new BorderLayout());
        myPostsPanel.setBorder(BorderFactory.createTitledBorder("My Posts"));
        
        DefaultListModel<Post> myPostsModel = new DefaultListModel<>();
        JList<Post> myPostsList = new JList<>(myPostsModel);
        myPostsList.setCellRenderer(new PostListCellRenderer());
        JScrollPane myPostsScroll = new JScrollPane(myPostsList);

        JPanel myPostsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editPostBtn = new JButton("Edit");
        JButton deletePostBtn = new JButton("Delete");
        
        editPostBtn.addActionListener(e -> {
            Post selectedPost = myPostsList.getSelectedValue();
            dialogManager.handleEditPostButtonClick(selectedPost, () -> refreshPostLists(postPanel));
        });

        deletePostBtn.addActionListener(e -> {
            Post selectedPost = myPostsList.getSelectedValue();
            dialogManager.handleDeletePost(selectedPost, () -> refreshPostLists(postPanel));
        });

        myPostsButtonPanel.add(editPostBtn);
        myPostsButtonPanel.add(deletePostBtn);
        myPostsPanel.add(myPostsScroll, BorderLayout.CENTER);
        myPostsPanel.add(myPostsButtonPanel, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        contentPanel.add(myPostsPanel, gbc);

        // Navigation buttons at the bottom
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        navPanel.add(backBtn);
        postPanel.add(navPanel, BorderLayout.SOUTH);

        // Add the content panel to the main post panel
        postPanel.add(contentPanel, BorderLayout.CENTER);

        // Initial load of posts
        refreshPostLists(postPanel);

        return postPanel;
    }

    private void refreshPostLists(JPanel postPanel) {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            updateMyPostsList(postPanel);
            updateBookmarkedPostsList(postPanel);
        }
    }

    private void updateMyPostsList(JPanel postPanel) {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Post> myPosts = postHandler.getPostsByStudent(currentStudent);
            for (Component comp : findComponentsByTitle(postPanel, "My Posts")) {
                if (comp instanceof JList) {
                    DefaultListModel<Post> model = (DefaultListModel<Post>) ((JList<?>) comp).getModel();
                    model.clear();
                    for (Post post : myPosts) {
                        model.addElement(post);
                    }
                }
            }
        }
    }

    private void updateBookmarkedPostsList(JPanel postPanel) {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Post> bookmarkedPosts = dbHandler.getBookmarkedPosts(currentStudent.getID());
            for (Component comp : findComponentsByTitle(postPanel, "Bookmarked Posts")) {
                if (comp instanceof JList) {
                    DefaultListModel<Post> model = (DefaultListModel<Post>) ((JList<?>) comp).getModel();
                    model.clear();
                    for (Post post : bookmarkedPosts) {
                        model.addElement(post);
                    }
                }
            }
        }
    }

    // Inner class for post list rendering
    private class PostListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Post) {
                Post post = (Post) value;
                setText(String.format("<html><b>%s</b><br/>%s</html>",
                    post.getOwner().getName(),
                    post.getContent()));
            }
            return this;
        }
    }

    private List<Component> findComponentsByTitle(Container container, String title) {
        List<Component> components = new ArrayList<>();
        System.out.println("\n====== Finding Components Debug ======");
        System.out.println("Searching for title: " + title);
        System.out.println("Container type: " + container.getClass().getSimpleName());
        
        for (Component comp : container.getComponents()) {
            System.out.println("Checking component: " + comp.getClass().getSimpleName());
            
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Border border = panel.getBorder();
                if (border instanceof TitledBorder) {
                    String borderTitle = ((TitledBorder) border).getTitle();
                    System.out.println("Found panel with title: " + borderTitle);
                    if (title.equals(borderTitle)) {
                        System.out.println("Found matching panel!");
                        // Look for JList inside the panel
                        for (Component child : panel.getComponents()) {
                            System.out.println("Checking child: " + child.getClass().getSimpleName());
                            if (child instanceof JScrollPane) {
                                Component view = ((JScrollPane) child).getViewport().getView();
                                if (view instanceof JList) {
                                    System.out.println("Found JList in ScrollPane");
                                    components.add(view);
                                }
                            }
                        }
                    }
                }
                
                // Recursively search in nested panels
                if (panel.getComponentCount() > 0) {
                    System.out.println("Searching nested panel...");
                    components.addAll(findComponentsByTitle(panel, title));
                }
            } else if (comp instanceof JSplitPane) {
                System.out.println("Searching JSplitPane components...");
                JSplitPane splitPane = (JSplitPane) comp;
                components.addAll(findComponentsByTitle((Container)splitPane.getTopComponent(), title));
                components.addAll(findComponentsByTitle((Container)splitPane.getBottomComponent(), title));
            }
        }
        
        System.out.println("Found " + components.size() + " matching components");
        return components;
    }

    public JPanel createTagManagementPanel() {
        JPanel tagPanel = new JPanel(new BorderLayout());
        tagPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tagPanel.setName("TagManagement");

        // Title
        JLabel titleLabel = new JLabel("Tag Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tagPanel.add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Create Tag Section
        JPanel createTagPanel = new JPanel(new GridBagLayout());
        createTagPanel.setBorder(BorderFactory.createTitledBorder("Create New Tag"));
        
        GridBagConstraints createGbc = new GridBagConstraints();
        createGbc.insets = new Insets(5, 5, 5, 5);
        createGbc.fill = GridBagConstraints.HORIZONTAL;

        // Tag name field
        createGbc.gridx = 0;
        createGbc.gridy = 0;
        createTagPanel.add(new JLabel("Tag Name:"), createGbc);

        JTextField tagNameField = new JTextField(20);
        createGbc.gridx = 1;
        createTagPanel.add(tagNameField, createGbc);

        // Tag description field
        createGbc.gridx = 0;
        createGbc.gridy = 1;
        createTagPanel.add(new JLabel("Description:"), createGbc);

        JTextArea tagDescArea = new JTextArea(3, 20);
        tagDescArea.setLineWrap(true);
        tagDescArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(tagDescArea);
        createGbc.gridx = 1;
        createTagPanel.add(descScrollPane, createGbc);

        // Create button
        JButton createTagBtn = new JButton("Create Tag");
        createGbc.gridx = 1;
        createGbc.gridy = 2;
        createTagPanel.add(createTagBtn, createGbc);

        createTagBtn.addActionListener(e -> {
            dialogManager.handleCreateTag(tagNameField, tagDescArea, () -> refreshTagLists(tagPanel));
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(createTagPanel, gbc);

        // Add the content panel to the main tag panel
        tagPanel.add(contentPanel, BorderLayout.CENTER);

        // Navigation buttons at the bottom
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        navPanel.add(backBtn);
        tagPanel.add(navPanel, BorderLayout.SOUTH);

        return tagPanel;
    }

    public void refreshTagLists(JPanel tagPanel) {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Tag> myTags = tagHandler.getTagsByStudent(currentStudent);
            for (Component comp : findComponentsByTitle(tagPanel, "My Tags")) {
                if (comp instanceof JList) {
                    DefaultListModel<Tag> model = (DefaultListModel<Tag>) ((JList<?>) comp).getModel();
                    model.clear();
                    for (Tag tag : myTags) {
                        model.addElement(tag);
                    }
                }
            }
        }
    }

    public JPanel createProfileManagementPanel() {
        JPanel profilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Profile Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        profilePanel.add(titleLabel, gbc);

        // Account Management Section
        JPanel accountPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        accountPanel.setBorder(BorderFactory.createTitledBorder("Account Management"));

        JButton editAccountBtn = new JButton("Edit Account Information");
        JButton deleteAccountBtn = new JButton("Delete Account");
        JButton anonymousModeBtn = new JButton("Toggle Anonymous Mode");

        editAccountBtn.addActionListener(e -> dialogManager.showEditAccountDialog());
        deleteAccountBtn.addActionListener(e -> dialogManager.showDeleteAccountDialog());
        anonymousModeBtn.addActionListener(e -> dialogManager.showToggleAnonymousModeDialog());

        accountPanel.add(editAccountBtn);
        accountPanel.add(deleteAccountBtn);
        accountPanel.add(anonymousModeBtn);

        gbc.gridy = 1;
        profilePanel.add(accountPanel, gbc);

        // Social Management Section
        JPanel socialPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        socialPanel.setBorder(BorderFactory.createTitledBorder("Social Management"));

        JButton friendRequestsBtn = new JButton("View Friend Requests");
        JButton friendListBtn = new JButton("View Friends List");
        JButton blockedUsersBtn = new JButton("View Blocked Users");

        friendRequestsBtn.addActionListener(e -> dialogManager.showFriendRequestsDialog());
        friendListBtn.addActionListener(e -> dialogManager.showFriendsListDialog());
        blockedUsersBtn.addActionListener(e -> dialogManager.showBlockedUsersDialog());

        socialPanel.add(friendRequestsBtn);
        socialPanel.add(friendListBtn);
        socialPanel.add(blockedUsersBtn);

        gbc.gridy = 2;
        profilePanel.add(socialPanel, gbc);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        navPanel.add(backBtn);

        gbc.gridy = 3;
        profilePanel.add(navPanel, gbc);

        return profilePanel;
    }

    public JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Search");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        searchPanel.add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Search by username section
        JPanel userSearchPanel = new JPanel(new BorderLayout());
        userSearchPanel.setBorder(BorderFactory.createTitledBorder("Search Users"));
        
        JTextField userSearchField = new JTextField(20);
        JButton userSearchBtn = new JButton("Search");
        
        JPanel userSearchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userSearchInputPanel.add(new JLabel("Username: "));
        userSearchInputPanel.add(userSearchField);
        userSearchInputPanel.add(userSearchBtn);
        
        DefaultListModel<Student> userResultsModel = new DefaultListModel<>();
        JList<Student> userResultsList = new JList<>(userResultsModel);
        JScrollPane userScrollPane = new JScrollPane(userResultsList);

        userSearchBtn.addActionListener(e -> {
            String searchTerm = userSearchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                List<Student> results = studentController.searchStudents(searchTerm);
                userResultsModel.clear();
                for (Student student : results) {
                    userResultsModel.addElement(student);
                }
            }
        });

        userSearchPanel.add(userSearchInputPanel, BorderLayout.NORTH);
        userSearchPanel.add(userScrollPane, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        contentPanel.add(userSearchPanel, gbc);

        // Search by tag section
        JPanel tagSearchPanel = new JPanel(new BorderLayout());
        tagSearchPanel.setBorder(BorderFactory.createTitledBorder("Search by Tags"));
        
        JComboBox<Tag> tagComboBox = new JComboBox<>();
        List<Tag> allTags = tagHandler.getAllTags();
        for (Tag tag : allTags) {
            tagComboBox.addItem(tag);
        }
        
        JButton tagSearchBtn = new JButton("Search");
        
        JPanel tagSearchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tagSearchInputPanel.add(new JLabel("Tag: "));
        tagSearchInputPanel.add(tagComboBox);
        tagSearchInputPanel.add(tagSearchBtn);
        
        DefaultListModel<Student> tagResultsModel = new DefaultListModel<>();
        JList<Student> tagResultsList = new JList<>(tagResultsModel);
        JScrollPane tagScrollPane = new JScrollPane(tagResultsList);

        tagSearchBtn.addActionListener(e -> {
            Tag selectedTag = (Tag) tagComboBox.getSelectedItem();
            if (selectedTag != null) {
                List<Student> results = studentController.searchStudentsByTag(selectedTag);
                tagResultsModel.clear();
                for (Student student : results) {
                    tagResultsModel.addElement(student);
                }
            }
        });

        tagSearchPanel.add(tagSearchInputPanel, BorderLayout.NORTH);
        tagSearchPanel.add(tagScrollPane, BorderLayout.CENTER);

        gbc.gridy = 1;
        contentPanel.add(tagSearchPanel, gbc);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        navPanel.add(backBtn);

        searchPanel.add(contentPanel, BorderLayout.CENTER);
        searchPanel.add(navPanel, BorderLayout.SOUTH);

        return searchPanel;
    }

    public JPanel createGroupManagementPanel() {
        JPanel groupPanel = new JPanel(new GridBagLayout());
        groupPanel.setName("Groups");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Group Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        groupPanel.add(titleLabel, gbc);

        // Search/Filter Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

        JTextField searchField = new JTextField(20);
        DefaultListModel<String> groupListModel = new DefaultListModel<>();

        // Initial population of the list
        List<Group> groups = studentController.getAllGroups();
        for (Group group : groups) {
            groupListModel.addElement(group.getName());
        }

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterGroups(); }
            public void removeUpdate(DocumentEvent e) { filterGroups(); }
            public void insertUpdate(DocumentEvent e) { filterGroups(); }

            private void filterGroups() {
                String searchText = searchField.getText().toLowerCase().trim();
                groupListModel.clear();
                
                List<Group> allGroups = studentController.getAllGroups();
                for (Group group : allGroups) {
                    if (group.getName().toLowerCase().contains(searchText) || 
                        group.getDescription().toLowerCase().contains(searchText)) {
                        groupListModel.addElement(group.getName());
                    }
                }
            }
        });

        // Create the JList with the model
        JList<String> groupList = new JList<>(groupListModel);

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        groupPanel.add(searchPanel, gbc);

        // Create a split panel layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.7); // Give 70% to top panel

        // Available Groups Panel
        JPanel availableGroupsPanel = new JPanel(new BorderLayout());
        availableGroupsPanel.setBorder(BorderFactory.createTitledBorder("Available Groups"));
        groupList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane availableScrollPane = new JScrollPane(groupList);
        availableGroupsPanel.add(availableScrollPane, BorderLayout.CENTER);

        // My Groups Panel
        JPanel myGroupsPanel = new JPanel(new BorderLayout());
        myGroupsPanel.setBorder(BorderFactory.createTitledBorder("My Groups"));
        DefaultListModel<String> myGroupsModel = new DefaultListModel<>();
        JList<String> myGroupsList = new JList<>(myGroupsModel);
        myGroupsList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane myGroupsScrollPane = new JScrollPane(myGroupsList);
        myGroupsPanel.add(myGroupsScrollPane, BorderLayout.CENTER);

        // Populate My Groups
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Group> allGroups = studentController.getAllGroups();
            for (Group group : allGroups) {
                if (group.isMember(currentStudent)) {
                    myGroupsModel.addElement(group.getName());
                }
            }
        }

        // Add panels to split pane
        splitPane.setTopComponent(availableGroupsPanel);
        splitPane.setBottomComponent(myGroupsPanel);

        // Update the layout
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        groupPanel.add(splitPane, gbc);

        return groupPanel;
    }

    public JPanel createGroupDetailsPanel(Group group) {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel(group.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        detailsPanel.add(titleLabel, BorderLayout.NORTH);

        // Details content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Description
        JTextArea descArea = new JTextArea(group.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        contentPanel.add(descScroll, gbc);

        // Member list
        String[] columnNames = {"Name", "Join Date", "Status"};
        Object[][] data = new Object[group.getMembers().size()][3];
        int i = 0;
        for (Student member : group.getMembers()) {
            data[i][0] = member.getName();
            data[i][1] = "N/A"; // You can add actual join date if available
            data[i][2] = group.isMemberActive(member) ? "Active" : "Inactive";
            i++;
        }
        JTable memberTable = new JTable(data, columnNames);
        JScrollPane tableScroll = new JScrollPane(memberTable);
        
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        contentPanel.add(tableScroll, gbc);

        detailsPanel.add(contentPanel, BorderLayout.CENTER);

        // Navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back to Groups");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Groups"));
        navPanel.add(backBtn);
        detailsPanel.add(navPanel, BorderLayout.SOUTH);

        return detailsPanel;
    }

    public void refreshGroupLists(JPanel groupPanel) {
        try {
            if (groupPanel == null) {
                groupPanel = findPanel(mainPanel, "Groups");
                if (groupPanel == null) {
                    System.err.println("Error: Could not find Groups panel");
                    return;
                }
            }

            List<Group> updatedGroups = studentController.getAllGroups();
            
            // Find the JSplitPane
            Component splitPaneComponent = null;
            for (Component comp : groupPanel.getComponents()) {
                if (comp instanceof JSplitPane) {
                    splitPaneComponent = comp;
                    break;
                }
                if (comp instanceof JPanel) {
                    // Search one level deeper if needed
                    for (Component innerComp : ((JPanel) comp).getComponents()) {
                        if (innerComp instanceof JSplitPane) {
                            splitPaneComponent = innerComp;
                            break;
                        }
                    }
                }
            }

            if (splitPaneComponent instanceof JSplitPane) {
                JSplitPane splitPane = (JSplitPane) splitPaneComponent;
                Component topComponent = splitPane.getTopComponent();
                
                if (topComponent instanceof JPanel) {
                    JPanel availableGroupsPanel = (JPanel) topComponent;
                    for (Component comp : availableGroupsPanel.getComponents()) {
                        if (comp instanceof JScrollPane) {
                            JScrollPane scrollPane = (JScrollPane) comp;
                            Component view = scrollPane.getViewport().getView();
                            if (view instanceof JList) {
                                @SuppressWarnings("unchecked")
                                JList<String> groupList = (JList<String>) view;
                                DefaultListModel<String> model = new DefaultListModel<>();
                                for (Group g : updatedGroups) {
                                    model.addElement(g.getName());
                                }
                                groupList.setModel(model);
                                return;  // Successfully updated
                            }
                        }
                    }
                }
            }
            System.err.println("Warning: Could not find group list to update");
        } catch (Exception e) {
            System.err.println("Error refreshing group lists: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateGroupList(DefaultListModel<String> model, JPanel groupPanel) {
        for (Component comp : findComponentsByTitle(groupPanel, "Available Groups")) {
            if (comp instanceof JList<?>) {
                @SuppressWarnings("unchecked")
                JList<String> list = (JList<String>) comp;
                list.setModel(model);
                break;
            }
        }
    }

    public void updateDashboardInfo(Student student) {
        if (student == null) {
            System.err.println("Cannot update dashboard: student is null");
            return;
        }

        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel && "Dashboard".equals(comp.getName())) {
                JPanel dashboardPanel = (JPanel) comp;
                
                for (Component dashComp : dashboardPanel.getComponents()) {
                    if (dashComp instanceof JPanel && 
                        ((JPanel) dashComp).getBorder() instanceof TitledBorder && 
                        "Student Information".equals(((TitledBorder) ((JPanel) dashComp).getBorder()).getTitle())) {
                        
                        updateStudentInfoLabels((JPanel)dashComp, student);
                        break;
                    }
                }
                
                dashboardPanel.revalidate();
                dashboardPanel.repaint();
                break;
            }
        }
    }

    private void updateStudentInfoLabels(JPanel infoPanel, Student student) {
        for (Component c : infoPanel.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if ("studentId".equals(label.getName())) {
                    label.setText(String.valueOf(student.getID()));
                } else if ("studentName".equals(label.getName())) {
                    label.setText(student.getName());
                } else if ("studentYear".equals(label.getName())) {
                    label.setText(student.getYear());
                }
            }
        }
    }
} 