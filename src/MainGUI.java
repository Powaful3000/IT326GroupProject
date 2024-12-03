import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.swing.border.Border;

public class MainGUI extends JFrame {
    private final StudentController studentController;
    private final DatabaseHandler dbHandler;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private StudentHandler studentHandler;
    private PostHandler postHandler;
    private TagHandler tagHandler;
    private JPanel postPanel;
    private JPanel groupPanel;
    private JPanel tagPanel;
    private JPanel settingsPanel;
    private DialogManager dialogManager;

    public MainGUI(StudentController studentController, DatabaseHandler dbHandler) {
        this.studentController = studentController;
        this.dbHandler = dbHandler;
        studentHandler = new StudentHandler();
        postHandler = new PostHandler();
        tagHandler = new TagHandler();
        createAndShowGUI();
        dialogManager = new DialogManager(this, dbHandler, studentHandler, postHandler, tagHandler);
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Redbird Connect");
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
        mainPanel.add(createProfileManagementPanel(), "Profile");

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

    private JPanel createPostManagementPanel() {
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
            dialogManager.handleCreatePost(postContent, this::refreshPostLists);
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
            dialogManager.handleEditPostButtonClick(selectedPost, this::refreshPostLists);
        });

        deletePostBtn.addActionListener(e -> {
            Post selectedPost = myPostsList.getSelectedValue();
            dialogManager.handleDeletePost(selectedPost, this::refreshPostLists);
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

        // Bookmarked Posts Section
        JPanel bookmarkedPostsPanel = new JPanel(new BorderLayout());
        bookmarkedPostsPanel.setBorder(BorderFactory.createTitledBorder("Bookmarked Posts"));
        
        DefaultListModel<Post> bookmarkedPostsModel = new DefaultListModel<>();
        JList<Post> bookmarkedPostsList = new JList<>(bookmarkedPostsModel);
        bookmarkedPostsList.setCellRenderer(new PostListCellRenderer());
        JScrollPane bookmarkedPostsScroll = new JScrollPane(bookmarkedPostsList);

        JPanel bookmarkButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeBookmarkBtn = new JButton("Remove Bookmark");
        
        removeBookmarkBtn.addActionListener(e -> {
            Post selectedPost = bookmarkedPostsList.getSelectedValue();
            dialogManager.handleRemoveBookmark(selectedPost, this::refreshPostLists);
        });

        bookmarkButtonPanel.add(removeBookmarkBtn);
        bookmarkedPostsPanel.add(bookmarkedPostsScroll, BorderLayout.CENTER);
        bookmarkedPostsPanel.add(bookmarkButtonPanel, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(bookmarkedPostsPanel, gbc);

        // Add the content panel to the main post panel
        postPanel.add(contentPanel, BorderLayout.CENTER);

        // Navigation buttons at the bottom
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        navPanel.add(backBtn);
        postPanel.add(navPanel, BorderLayout.SOUTH);

        // Initial load of posts
        refreshPostLists();

        return postPanel;
    }

    // Helper class for post list rendering
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

    // Helper method to refresh post lists
    private void refreshPostLists() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            // Find the post management panel
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof JPanel && comp.getName() != null && comp.getName().equals("PostManagement")) {
                    JPanel postPanel = (JPanel) comp;
                    
                    // Find and refresh My Posts list
                    updateMyPostsList(postPanel);
                    
                    // Find and refresh Bookmarked Posts list
                    updateBookmarkedPostsList(postPanel);
                    
                    break;
                }
            }
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

    private List<Component> findComponentsByTitle(Container container, String title) {
        List<Component> components = new ArrayList<>();
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getBorder() instanceof TitledBorder) {
                    TitledBorder border = (TitledBorder) panel.getBorder();
                    if (border.getTitle().equals(title)) {
                        for (Component child : panel.getComponents()) {
                            if (child instanceof JScrollPane) {
                                components.add(((JScrollPane) child).getViewport().getView());
                            }
                        }
                    }
                }
                components.addAll(findComponentsByTitle(panel, title));
            }
        }
        return components;
    }

    private JPanel createGroupManagementPanel() {
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
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterGroups(); }
            public void removeUpdate(DocumentEvent e) { filterGroups(); }
            public void insertUpdate(DocumentEvent e) { filterGroups(); }

            private void filterGroups() {
                String searchText = searchField.getText().toLowerCase();
                // Implement group filtering logic here
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

        DefaultListModel<String> groupListModel = new DefaultListModel<>();
        JList<String> groupList = new JList<>(groupListModel);
        groupList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(groupList);
        scrollPane.setPreferredSize(new Dimension(600, 400));
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

        // Bottom Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton backButton = new JButton("Back to Dashboard");
        JButton createGroupBtn = new JButton("Create Group");
        JButton joinGroupBtn = new JButton("Join Group");
        JButton viewDetailsBtn = new JButton("View Details");

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        
        createGroupBtn.addActionListener(e -> {
            System.out.println("\n====== Create Group Button Clicked ======");
            showCreateGroupDialog();
        });

        joinGroupBtn.addActionListener(e -> showJoinGroupDialog());

        viewDetailsBtn.addActionListener(e -> showGroupDetailsDialog());

        buttonPanel.add(backButton);
        buttonPanel.add(createGroupBtn);
        buttonPanel.add(joinGroupBtn);
        buttonPanel.add(viewDetailsBtn);

        gbc.gridy = 3;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        groupPanel.add(buttonPanel, gbc);

        return groupPanel;
    }

    private JPanel createGroupDetailsPanel(Group group) {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Main content uses GridBagLayout for flexible positioning
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Group Info Section
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Group Information"));
        
        GridBagConstraints infoGbc = new GridBagConstraints();
        infoGbc.insets = new Insets(5, 5, 5, 5);
        infoGbc.fill = GridBagConstraints.HORIZONTAL;

        // Group name
        infoGbc.gridx = 0;
        infoGbc.gridy = 0;
        infoPanel.add(new JLabel("Name:"), infoGbc);
        
        JLabel nameLabel = new JLabel(group.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        infoGbc.gridx = 1;
        infoPanel.add(nameLabel, infoGbc);

        // Group description
        infoGbc.gridx = 0;
        infoGbc.gridy = 1;
        infoPanel.add(new JLabel("Description:"), infoGbc);
        
        JLabel descLabel = new JLabel(group.getDescription());
        infoGbc.gridx = 1;
        infoPanel.add(descLabel, infoGbc);

        // Created date
        infoGbc.gridx = 0;
        infoGbc.gridy = 2;
        infoPanel.add(new JLabel("Created:"), infoGbc);
        
        JLabel createdLabel = new JLabel(group.getCreationDate().toString());
        infoGbc.gridx = 1;
        infoPanel.add(createdLabel, infoGbc);

        // Add info panel to content panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(infoPanel, gbc);

        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Group Statistics"));
        
        GridBagConstraints statsGbc = new GridBagConstraints();
        statsGbc.insets = new Insets(5, 5, 5, 5);
        statsGbc.fill = GridBagConstraints.HORIZONTAL;

        // Member count
        statsGbc.gridx = 0;
        statsGbc.gridy = 0;
        statsPanel.add(new JLabel("Total Members:"), statsGbc);
        
        JLabel memberCountLabel = new JLabel(String.valueOf(group.getMembers().size()));
        statsGbc.gridx = 1;
        statsPanel.add(memberCountLabel, statsGbc);

        // Post count
        statsGbc.gridx = 0;
        statsGbc.gridy = 1;
        statsPanel.add(new JLabel("Total Posts:"), statsGbc);
        
        int postCount = postHandler.getPostsByGroup(group).size();
        JLabel postCountLabel = new JLabel(String.valueOf(postCount));
        statsGbc.gridx = 1;
        statsPanel.add(postCountLabel, statsGbc);

        // Active members (members without end date)
        statsGbc.gridx = 0;
        statsGbc.gridy = 2;
        statsPanel.add(new JLabel("Active Members:"), statsGbc);
        
        int activeMembers = group.getActiveMembers().size();
        JLabel activeMembersLabel = new JLabel(String.valueOf(activeMembers));
        statsGbc.gridx = 1;
        statsPanel.add(activeMembersLabel, statsGbc);

        // Add stats panel to content panel
        gbc.gridy = 1;
        contentPanel.add(statsPanel, gbc);

        // Members Management Section
        JPanel membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBorder(BorderFactory.createTitledBorder("Member Management"));

        // Create the table model for members
        String[] columnNames = {"Name", "Join Date", "End Date", "Status"};
        Object[][] data = new Object[group.getMembers().size()][4];
        int i = 0;
        for (Student member : group.getMembers()) {
            data[i][0] = member.getName();
            data[i][1] = group.getMemberJoinDate(member);
            data[i][2] = group.getMemberEndDate(member);
            data[i][3] = group.getMemberEndDate(member) == null ? "Active" : "Inactive";
            i++;
        }

        JTable memberTable = new JTable(data, columnNames);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(memberTable);
        membersPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Member management buttons
        JPanel memberButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addMemberBtn = new JButton("Add Member");
        JButton removeMemberBtn = new JButton("Remove Member");
        JButton setEndDateBtn = new JButton("Set End Date");

        addMemberBtn.addActionListener(e -> showAddMemberDialog(group));
        removeMemberBtn.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            dialogManager.handleRemoveMember(selectedRow, memberTable, group, this::refreshGroupDetails);
        });

        setEndDateBtn.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            dialogManager.handleSetEndDate(selectedRow, memberTable, group, this::refreshGroupDetails);
        });

        memberButtonPanel.add(addMemberBtn);
        memberButtonPanel.add(removeMemberBtn);
        memberButtonPanel.add(setEndDateBtn);
        membersPanel.add(memberButtonPanel, BorderLayout.SOUTH);

        // Add members panel to content panel
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        contentPanel.add(membersPanel, gbc);

        // Add content panel to details panel
        detailsPanel.add(contentPanel, BorderLayout.CENTER);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back to Groups");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Groups"));
        navPanel.add(backBtn);
        detailsPanel.add(navPanel, BorderLayout.SOUTH);

        return detailsPanel;
    }

    private void showAddMemberDialog(Group group) {
        String username = JOptionPane.showInputDialog(this, "Enter username to add:");
        if (username != null && !username.trim().isEmpty()) {
            Student student = studentHandler.getStudentByUsername(username);
            if (student != null) {
                if (dbHandler.addMemberToGroup(group.getID(), student.getID())) {
                    refreshGroupDetails(group);
                    JOptionPane.showMessageDialog(this, "Member added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add member!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Student not found!");
            }
        }
    }

    private void showSetEndDateDialog(Group group, Student member) {
        // Create a date picker dialog
        JDialog dialog = new JDialog(this, "Set Membership End Date", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Date input fields
        JComboBox<Integer> yearBox = new JComboBox<>(new Integer[] {
            2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030
        });
        JComboBox<Integer> monthBox = new JComboBox<>(new Integer[] {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
        });
        JComboBox<Integer> dayBox = new JComboBox<>(new Integer[] {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31
        });

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        panel.add(yearBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Month:"), gbc);
        gbc.gridx = 1;
        panel.add(monthBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1;
        panel.add(dayBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            dialogManager.handleEndDateOkButton(yearBox, monthBox, dayBox, dialog, group, member, this::refreshGroupDetails);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshGroupDetails(Group group) {
        // Find and remove the old details panel
        String panelName = "GroupDetails-" + group.getName();
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel && panelName.equals(comp.getName())) {
                mainPanel.remove(comp);
                break;
            }
        }
        
        // Create and add the new details panel
        JPanel newDetailsPanel = createGroupDetailsPanel(group);
        newDetailsPanel.setName(panelName);
        mainPanel.add(newDetailsPanel, panelName);
        
        // Show the updated panel
        cardLayout.show(mainPanel, panelName);
    }

    private JPanel createTagManagementPanel() {
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
            dialogManager.handleCreateTag(tagNameField, tagDescArea, this::refreshTagLists);
        });

        // My Tags Section
        JPanel myTagsPanel = new JPanel(new BorderLayout());
        myTagsPanel.setBorder(BorderFactory.createTitledBorder("My Tags"));
        
        DefaultListModel<Tag> myTagsModel = new DefaultListModel<>();
        JList<Tag> myTagsList = new JList<>(myTagsModel);
        myTagsList.setCellRenderer(new TagListCellRenderer());
        JScrollPane myTagsScroll = new JScrollPane(myTagsList);

        JPanel tagButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeTagBtn = new JButton("Remove Tag");
        JButton editTagBtn = new JButton("Edit Tag");
        
        removeTagBtn.addActionListener(e -> {
            Tag selectedTag = myTagsList.getSelectedValue();
            Student currentStudent = studentHandler.getCurrentStudent();
            dialogManager.handleRemoveTag(selectedTag, currentStudent, this::refreshTagLists);
        });

        editTagBtn.addActionListener(e -> {
            Tag selectedTag = myTagsList.getSelectedValue();
            dialogManager.handleEditTagButtonClick(selectedTag, this::refreshTagLists);
        });

        tagButtonPanel.add(removeTagBtn);
        tagButtonPanel.add(editTagBtn);
        myTagsPanel.add(myTagsScroll, BorderLayout.CENTER);
        myTagsPanel.add(tagButtonPanel, BorderLayout.SOUTH);

        // Available Tags Section
        JPanel availableTagsPanel = new JPanel(new BorderLayout());
        availableTagsPanel.setBorder(BorderFactory.createTitledBorder("Available Tags"));
        
        DefaultListModel<Tag> availableTagsModel = new DefaultListModel<>();
        JList<Tag> availableTagsList = new JList<>(availableTagsModel);
        availableTagsList.setCellRenderer(new TagListCellRenderer());
        JScrollPane availableTagsScroll = new JScrollPane(availableTagsList);

        JPanel availableTagButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addTagBtn = new JButton("Add Tag");
        
        addTagBtn.addActionListener(e -> {
            Tag selectedTag = availableTagsList.getSelectedValue();
            Student currentStudent = studentHandler.getCurrentStudent();
            dialogManager.handleAddTag(selectedTag, currentStudent, this::refreshTagLists);
        });

        availableTagButtonPanel.add(addTagBtn);
        availableTagsPanel.add(availableTagsScroll, BorderLayout.CENTER);
        availableTagsPanel.add(availableTagButtonPanel, BorderLayout.SOUTH);

        // Add panels to content panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        contentPanel.add(createTagPanel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 1.0;
        contentPanel.add(myTagsPanel, gbc);

        gbc.gridx = 1;
        contentPanel.add(availableTagsPanel, gbc);

        // Add content panel to main panel
        tagPanel.add(contentPanel, BorderLayout.CENTER);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        navPanel.add(backBtn);
        tagPanel.add(navPanel, BorderLayout.SOUTH);

        // Initial load of tags
        refreshTagLists();

        return tagPanel;
    }

    // Helper class for tag list rendering
    private class TagListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Tag) {
                Tag tag = (Tag) value;
                setText(String.format("<html><b>%s</b><br/><i>%s</i></html>",
                    tag.getName(),
                    tag.getDescription()));
            }
            return this;
        }
    }

    private void showEditTagDialog(Tag tag) {
        JDialog dialog = new JDialog(this, "Edit Tag", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);

        JTextField nameField = new JTextField(tag.getName(), 20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Description field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);

        JTextArea descArea = new JTextArea(tag.getDescription(), 3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descArea);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            dialogManager.handleEditTag(tag, nameField, descArea, dialog, this::refreshTagLists);
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void refreshTagLists() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof JPanel && "TagManagement".equals(comp.getName())) {
                    JPanel tagPanel = (JPanel) comp;
                    
                    // Update My Tags list
                    List<Tag> myTags = tagHandler.getTagsByStudent(currentStudent);
                    for (Component c : findComponentsByTitle(tagPanel, "My Tags")) {
                        if (c instanceof JList) {
                            DefaultListModel<Tag> model = (DefaultListModel<Tag>) ((JList<?>) c).getModel();
                            model.clear();
                            for (Tag tag : myTags) {
                                model.addElement(tag);
                            }
                        }
                    }
                    
                    // Update Available Tags list
                    List<Tag> allTags = tagHandler.getAllTags();
                    allTags.removeAll(myTags); // Remove tags the student already has
                    for (Component c : findComponentsByTitle(tagPanel, "Available Tags")) {
                        if (c instanceof JList) {
                            DefaultListModel<Tag> model = (DefaultListModel<Tag>) ((JList<?>) c).getModel();
                            model.clear();
                            for (Tag tag : allTags) {
                                model.addElement(tag);
                            }
                        }
                    }
                    break;
                }
            }
        }
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

    private JPanel createProfileManagementPanel() {
        JPanel profilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Anonymous Mode Toggle
        JCheckBox anonymousMode = new JCheckBox("Anonymous Mode");
        anonymousMode.addActionListener(e -> {
            dialogManager.handleAnonymousModeToggle(anonymousMode.isSelected());
        });

        // Friend Management Section
        JPanel friendPanel = new JPanel(new GridLayout(0, 1));
        friendPanel.setBorder(BorderFactory.createTitledBorder("Friend Management"));
        
        JButton addFriendBtn = new JButton("Send Friend Request");
        JButton viewRequestsBtn = new JButton("View Friend Requests");
        JButton viewFriendsBtn = new JButton("View Friends");
        JButton blockUserBtn = new JButton("Block User");

        // Add action listeners for each button
        addFriendBtn.addActionListener(e -> dialogManager.showSendFriendRequestDialog());
        viewRequestsBtn.addActionListener(e -> dialogManager.showFriendRequestsDialog());
        viewFriendsBtn.addActionListener(e -> dialogManager.showFriendsListDialog());
        blockUserBtn.addActionListener(e -> dialogManager.showBlockUserDialog());

        friendPanel.add(addFriendBtn);
        friendPanel.add(viewRequestsBtn);
        friendPanel.add(viewFriendsBtn);
        friendPanel.add(blockUserBtn);

        // Add components to main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        profilePanel.add(anonymousMode, gbc);
        
        gbc.gridy = 1;
        profilePanel.add(friendPanel, gbc);

        return profilePanel;
    }

    // Friend request dialog methods
    private void showSendFriendRequestDialog() {
        String username = JOptionPane.showInputDialog(this, "Enter username to send friend request:");
        if (username != null && !username.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                dbHandler.sendFriendRequest(currentStudent.getID(), Integer.parseInt(username));
            }
        }
    }

    private void showFriendRequestsDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Student> requests = dbHandler.getFriendRequests(currentStudent.getID());
            // Show requests in a dialog
            JDialog dialog = new JDialog(this, "Friend Requests", true);
            dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
            
            for (Student requester : requests) {
                JPanel requestPanel = new JPanel();
                requestPanel.add(new JLabel(requester.getName()));
                JButton acceptBtn = new JButton("Accept");
                JButton declineBtn = new JButton("Decline");
                
                acceptBtn.addActionListener(e -> {
                    dbHandler.acceptFriendRequest(requester.getID());
                    dialog.dispose();
                });
                
                declineBtn.addActionListener(e -> {
                    dbHandler.declineFriendRequest(requester.getID());
                    dialog.dispose();
                });
                
                requestPanel.add(acceptBtn);
                requestPanel.add(declineBtn);
                dialog.add(requestPanel);
            }
            
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }

    private void showFriendsListDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Student> friends = dbHandler.getFriends(currentStudent.getID());
            StringBuilder sb = new StringBuilder("Your Friends:\n\n");
            for (Student friend : friends) {
                sb.append(friend.getName()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Friends List", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showBlockUserDialog() {
        String username = JOptionPane.showInputDialog(this, "Enter username to block:");
        if (username != null && !username.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                dbHandler.blockUser(currentStudent.getID(), Integer.parseInt(username));
            }
        }
    }

    // Post Management Methods
    private void showCreatePostDialog() {
        String content = JOptionPane.showInputDialog(this, "Enter post content:");
        if (content != null && !content.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                Post post = new Post(generatePostId(), content, currentStudent, null);
                dbHandler.createPost(post);
            }
        }
    }

    private void showEditPostDialog(Post post) {
        String newContent = JOptionPane.showInputDialog(this, "Edit post:", post.getContent());
        if (newContent != null && !newContent.trim().isEmpty()) {
            post.setContent(newContent);
            dbHandler.editPost(post);
        }
    }

    private void showBookmarkPostDialog() {
        String postId = JOptionPane.showInputDialog(this, "Enter post ID to bookmark:");
        if (postId != null && !postId.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                dbHandler.bookmarkPost(currentStudent.getID(), Integer.parseInt(postId));
            }
        }
    }

    private void showBookmarkedPostsDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Post> bookmarkedPosts = dbHandler.getBookmarkedPosts(currentStudent.getID());
            StringBuilder sb = new StringBuilder("Your Bookmarked Posts:\n\n");
            for (Post post : bookmarkedPosts) {
                sb.append(post.getContent()).append(" (ID: ").append(post.getID()).append(")\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Bookmarked Posts", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Group Management Methods
    private void showCreateGroupDialog() {
        System.out.println("\n====== MainGUI showCreateGroupDialog Debug ======");
        System.out.println("Creating dialog components...");
        
        JTextField nameField = new JTextField(20);
        JTextArea descField = new JTextArea(4, 20);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        panel.add(new JLabel("Group Name:"), gbc);
        gbc.gridy = 1;
        panel.add(nameField, gbc);
        gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridy = 3;
        panel.add(new JScrollPane(descField), gbc);
        
        System.out.println("Showing dialog to user...");
        int option = JOptionPane.showConfirmDialog(this, panel, "Create New Group",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        System.out.println("Dialog result: " + (option == JOptionPane.OK_OPTION ? "OK" : "Cancel"));
        
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            System.out.println("\nValidating user input:");
            System.out.println("- Name: '" + name + "'");
            System.out.println("- Description: '" + desc + "'");
            
            if (name.isEmpty()) {
                System.out.println("Error: Name is empty");
                JOptionPane.showMessageDialog(this, "Group name cannot be empty!");
                return;
            }
            
            System.out.println("\nCreating Group object...");
            Group group = new Group(0, name, desc);
            System.out.println("Group object created:");
            System.out.println("- ID: " + group.getID());
            System.out.println("- Name: " + group.getName());
            System.out.println("- Description: " + group.getDescription());
            System.out.println("- Creation Date: " + group.getCreationDate());
            
            System.out.println("\nCalling studentController.createGroup()...");
            boolean success = studentController.createGroup(group);
            System.out.println("Create group result: " + success);
            
            if (success) {
                System.out.println("Group creation successful, refreshing UI...");
                JOptionPane.showMessageDialog(this, "Group created successfully!");
                
                System.out.println("Fetching updated group list...");
                List<Group> updatedGroups = studentController.getAllGroups();
                System.out.println("Found " + updatedGroups.size() + " groups");
                
                DefaultListModel<String> model = new DefaultListModel<>();
                for (Group g : updatedGroups) {
                    System.out.println("Adding group to list: " + g.getName());
                    model.addElement(g.getName());
                }
                
                System.out.println("Updating UI components...");
                updateGroupList(model);
                System.out.println("UI refresh complete");
                
            } else {
                System.out.println("Group creation failed, showing error message");
                JOptionPane.showMessageDialog(this, 
                    "Failed to create group. This could be because:\n" +
                    "1. The group name is already taken\n" +
                    "2. There was a database error\n" +
                    "Please try again with a different name.",
                    "Group Creation Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("User cancelled group creation");
        }
        System.out.println("Group creation dialog closed");
    }

    private void updateGroupList(DefaultListModel<String> model) {
        System.out.println("\n====== MainGUI updateGroupList Debug ======");
        System.out.println("Searching for Groups panel...");
        
        System.out.println("Main panel component count: " + mainPanel.getComponentCount());
        for (Component comp : mainPanel.getComponents()) {
            System.out.println("Found component: " + comp.getClass().getSimpleName() + 
                             ", Name: " + (comp.getName() != null ? comp.getName() : "null"));
            
            if (comp instanceof JPanel && "Groups".equals(comp.getName())) {
                System.out.println("Found Groups panel");
                JList<String> list = findJList((JPanel)comp);
                if (list != null) {
                    System.out.println("Found JList, updating model...");
                    list.setModel(model);
                    System.out.println("Model updated successfully");
                    return;
                }
                System.out.println("Warning: Could not find JList component in Groups panel");
                return;
            }
        }
        System.out.println("Warning: Could not find Groups panel");
    }

    private JList<String> findJList(Container container) {
        System.out.println("Searching container: " + container.getClass().getSimpleName());
        System.out.println("Component count: " + container.getComponentCount());
        
        // First try to find a JList directly
        for (Component comp : container.getComponents()) {
            System.out.println("Checking component: " + comp.getClass().getSimpleName());
            
            if (comp instanceof JList) {
                System.out.println("Found direct JList");
                return (JList<String>) comp;
            }
            
            // Check JScrollPane
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                System.out.println("ScrollPane view: " + (view != null ? view.getClass().getSimpleName() : "null"));
                if (view instanceof JList) {
                    System.out.println("Found JList in ScrollPane");
                    return (JList<String>) view;
                }
            }
            
            // Check if it's a titled border panel that might contain our list panel
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Border border = panel.getBorder();
                if (border instanceof TitledBorder) {
                    String title = ((TitledBorder) border).getTitle();
                    System.out.println("Found panel with title: " + title);
                    if ("Available Groups".equals(title)) {
                        System.out.println("Found Available Groups panel, searching inside...");
                        JList<String> list = findJList(panel);
                        if (list != null) {
                            return list;
                        }
                    }
                }
            }
            
            // Recursively search other containers
            if (comp instanceof Container) {
                System.out.println("Recursively searching in: " + comp.getClass().getSimpleName());
                JList<String> list = findJList((Container) comp);
                if (list != null) {
                    return list;
                }
            }
        }
        return null;
    }

    private void showJoinGroupDialog() {
        String groupName = JOptionPane.showInputDialog(this, "Enter group name to join:");
        if (groupName != null && !groupName.trim().isEmpty()) {
            Group group = studentController.getGroupByName(groupName);
            if (group != null) {
                Student currentStudent = studentHandler.getCurrentStudent();
                if (currentStudent != null && dbHandler.addMemberToGroup(group.getID(), currentStudent.getID())) {
                    JOptionPane.showMessageDialog(this, "Successfully joined group!");
                    List<Group> updatedGroups = studentController.getAllGroups();
                    DefaultListModel<String> model = new DefaultListModel<>();
                    for (Group g : updatedGroups) {
                        model.addElement(g.getName());
                    }
                    updateGroupList(model);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to join group!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Group not found!");
            }
        }
    }

    private void showGroupDetailsDialog() {
        JList<String> selectedList = findJList(mainPanel);
        if (selectedList != null) {
            String selectedGroupName = selectedList.getSelectedValue();
            if (selectedGroupName != null) {
                Group group = studentController.getGroupByName(selectedGroupName);
                if (group != null) {
                    JPanel detailsPanel = createGroupDetailsPanel(group);
                    detailsPanel.setName("GroupDetails-" + group.getName());
                    mainPanel.add(detailsPanel, "GroupDetails-" + group.getName());
                    cardLayout.show(mainPanel, "GroupDetails-" + group.getName());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a group to view details!");
            }
        }
    }

    // Tag System Methods
    private void showAddTagDialog() {
        String tagId = JOptionPane.showInputDialog(this, "Enter tag ID to add:");
        if (tagId != null && !tagId.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                dbHandler.addTagToStudent(currentStudent.getID(), Integer.parseInt(tagId));
            }
        }
    }

    private void showRemoveTagDialog() {
        String tagId = JOptionPane.showInputDialog(this, "Enter tag ID to remove:");
        if (tagId != null && !tagId.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                dbHandler.removeTagFromStudent(currentStudent.getID(), Integer.parseInt(tagId));
            }
        }
    }

    // User Settings Methods
    private void showToggleAnonymousModeDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            int option = JOptionPane.showConfirmDialog(this, 
                "Would you like to enable anonymous mode?", 
                "Toggle Anonymous Mode", 
                JOptionPane.YES_NO_OPTION);
            dbHandler.toggleAnonymousMode(currentStudent.getID(), option == JOptionPane.YES_OPTION);
        }
    }

    private void showBlockedUsersDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Student> blockedUsers = dbHandler.getBlockedUsers(currentStudent.getID());
            StringBuilder sb = new StringBuilder("Blocked Users:\n\n");
            for (Student user : blockedUsers) {
                sb.append(user.getName()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Blocked Users", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private int generatePostId() {
        return (int) (Math.random() * 9000) + 1000; // Simple ID generation
    }

    // Additional Post Management Methods
    private void showViewGroupPostsDialog() {
        String groupId = JOptionPane.showInputDialog(this, "Enter group ID to view posts:");
        if (groupId != null && !groupId.trim().isEmpty()) {
            Group group = new Group(Integer.parseInt(groupId), "", "");
            List<Post> posts = postHandler.getPostsByGroup(group);
            showPostsList("Posts in Group " + groupId, posts);
        }
    }

    private void showViewStudentPostsDialog() {
        String username = JOptionPane.showInputDialog(this, "Enter username to view posts:");
        if (username != null && !username.trim().isEmpty()) {
            Student student = dbHandler.getStudentByUsername(username);
            if (student != null) {
                List<Post> posts = postHandler.getPostsByStudent(student);
                showPostsList("Posts by " + username, posts);
            }
        }
    }

    private void showRemovePostDialog() {
        String postId = JOptionPane.showInputDialog(this, "Enter post ID to remove:");
        if (postId != null && !postId.trim().isEmpty()) {
            postHandler.removePost(Integer.parseInt(postId));
        }
    }

    private void showPostsList(String title, List<Post> posts) {
        StringBuilder sb = new StringBuilder();
        for (Post post : posts) {
            sb.append("ID: ").append(post.getID())
              .append(", Content: ").append(post.getContent())
              .append(", Owner: ").append(post.getOwner().getName())
              .append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Additional Group Management Methods
    private void showViewGroupMembersDialog() {
        String groupId = JOptionPane.showInputDialog(this, "Enter group ID to view members:");
        if (groupId != null && !groupId.trim().isEmpty()) {
            Group group = new Group(Integer.parseInt(groupId), "", "");
            List<Student> members = group.getMembers();
            showStudentsList("Group Members", members);
        }
    }

    private void showViewAllGroupsDialog() {
        List<Group> groups = dbHandler.getAllGroups();
        StringBuilder sb = new StringBuilder("All Groups:\n\n");
        for (Group group : groups) {
            sb.append("ID: ").append(group.getID())
              .append(", Name: ").append(group.getName())
              .append(", Size: ").append(group.getSize())
              .append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "All Groups", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUpdateMembershipEndDateDialog() {
        JTextField groupIdField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        Object[] message = {
            "Group ID:", groupIdField,
            "End Date:", dateField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Update Membership End Date", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int groupId = Integer.parseInt(groupIdField.getText());
                java.sql.Date endDate = java.sql.Date.valueOf(dateField.getText());
                Student currentStudent = studentHandler.getCurrentStudent();
                if (currentStudent != null) {
                    dbHandler.updateMembershipEndDate(currentStudent.getID(), groupId, endDate);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Additional Tag Management Methods
    private void showCreateTagDialog() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        Object[] message = {
            "Tag Name:", nameField,
            "Description:", descField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Create Tag", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String desc = descField.getText();
            if (!name.trim().isEmpty()) {
                Tag tag = new Tag(0, name, desc);
                tagHandler.addTag(tag);
            }
        }
    }

    private void showViewAllTagsDialog() {
        List<Tag> tags = tagHandler.getAllTags();
        StringBuilder sb = new StringBuilder("All Tags:\n\n");
        for (Tag tag : tags) {
            sb.append("Name: ").append(tag.getName())
              .append(", Description: ").append(tag.getDescription())
              .append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "All Tags", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showViewStudentTagsDialog() {
        String username = JOptionPane.showInputDialog(this, "Enter username to view tags:");
        if (username != null && !username.trim().isEmpty()) {
            Student student = dbHandler.getStudentByUsername(username);
            if (student != null) {
                List<Tag> tags = student.getTags();
                StringBuilder sb = new StringBuilder("Tags for " + username + ":\n\n");
                for (Tag tag : tags) {
                    sb.append("Name: ").append(tag.getName())
                      .append(", Description: ").append(tag.getDescription())
                      .append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Student Tags", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showStudentsList(String title, List<Student> students) {
        StringBuilder sb = new StringBuilder();
        for (Student student : students) {
            sb.append("ID: ").append(student.getID())
              .append(", Name: ").append(student.getName())
              .append(", Year: ").append(student.getYear())
              .append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Update initializeComponents to add new buttons
    private void initializeComponents() {
        mainPanel = new JPanel();
        postPanel = new JPanel();
        groupPanel = new JPanel();
        tagPanel = new JPanel();
        settingsPanel = new JPanel();
        
        // Additional Post Management buttons
        JButton viewGroupPostsBtn = new JButton("View Group Posts");
        JButton viewStudentPostsBtn = new JButton("View Student Posts");
        JButton removePostBtn = new JButton("Remove Post");

        // Additional Group Management buttons
        JButton viewGroupMembersBtn = new JButton("View Group Members");
        JButton viewAllGroupsBtn = new JButton("View All Groups");
        JButton updateMembershipBtn = new JButton("Update Membership End Date");

        // Additional Tag Management buttons
        JButton createTagBtn = new JButton("Create Tag");
        JButton viewAllTagsBtn = new JButton("View All Tags");
        JButton viewStudentTagsBtn = new JButton("View Student Tags");

        // Add action listeners
        viewGroupPostsBtn.addActionListener(e -> showViewGroupPostsDialog());
        viewStudentPostsBtn.addActionListener(e -> showViewStudentPostsDialog());
        removePostBtn.addActionListener(e -> showRemovePostDialog());
        viewGroupMembersBtn.addActionListener(e -> showViewGroupMembersDialog());
        viewAllGroupsBtn.addActionListener(e -> showViewAllGroupsDialog());
        updateMembershipBtn.addActionListener(e -> showUpdateMembershipEndDateDialog());
        createTagBtn.addActionListener(e -> showCreateTagDialog());
        viewAllTagsBtn.addActionListener(e -> showViewAllTagsDialog());
        viewStudentTagsBtn.addActionListener(e -> showViewStudentTagsDialog());

        // Add new buttons to panels
        postPanel.add(viewGroupPostsBtn);
        postPanel.add(viewStudentPostsBtn);
        postPanel.add(removePostBtn);

        groupPanel.add(viewGroupMembersBtn);
        groupPanel.add(viewAllGroupsBtn);
        groupPanel.add(updateMembershipBtn);

        tagPanel.add(createTagBtn);
        tagPanel.add(viewAllTagsBtn);
        tagPanel.add(viewStudentTagsBtn);

        // Add panels to main container
        mainPanel.add(postPanel);
        mainPanel.add(groupPanel);
        mainPanel.add(tagPanel);
        mainPanel.add(settingsPanel);
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
