import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class DialogManager {
    private final JFrame parentFrame;
    private final DatabaseHandler dbHandler;
    private final StudentHandler studentHandler;
    private final PostHandler postHandler;
    private final TagHandler tagHandler;
    private final StudentController studentController;
    private final JPanel mainPanel;
    private final JPanel groupPanel;
    private Runnable refreshCallback;
    private PanelFactory panelFactory;

    public DialogManager(JFrame parentFrame, DatabaseHandler dbHandler, StudentHandler studentHandler, PostHandler postHandler, TagHandler tagHandler, StudentController studentController, PanelFactory panelFactory) {
        this.parentFrame = parentFrame;
        this.dbHandler = dbHandler;
        this.studentHandler = studentHandler;
        this.postHandler = postHandler;
        this.tagHandler = tagHandler;
        this.studentController = studentController;
        this.mainPanel = ((MainGUI)parentFrame).getMainPanel();
        this.groupPanel = findPanel(mainPanel, "Groups");
        this.refreshCallback = () -> {};
        this.panelFactory = panelFactory;
    }

    private JPanel findPanel(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel && name.equals(comp.getName())) {
                return (JPanel) comp;
            }
        }
        return null;
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    /**
     * Shows dialog for sending a friend request
     */
    public void showSendFriendRequestDialog() {
        String username = JOptionPane.showInputDialog(parentFrame, "Enter username to send friend request:");
        if (username != null && !username.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                dbHandler.sendFriendRequest(currentStudent.getID(), Integer.parseInt(username));
            }
        }
    }

    /**
     * Shows dialog displaying pending friend requests
     */
    public void showFriendRequestsDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) return;

        List<Student> requests = dbHandler.getFriendRequests(currentStudent.getID());
        if (requests.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                "No pending friend requests",
                "Friend Requests",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parentFrame, "Friend Requests", true);
        dialog.setLayout(new BorderLayout());

        JPanel requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        
        for (Student requester : requests) {
            JPanel requestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            requestPanel.setBorder(BorderFactory.createEtchedBorder());
            
            JLabel nameLabel = new JLabel(requester.getName());
            JButton acceptBtn = new JButton("Accept");
            JButton declineBtn = new JButton("Decline");
            
            acceptBtn.addActionListener(e -> {
                if (dbHandler.acceptFriendRequest(requester.getID())) {
                    requestPanel.setVisible(false);
                    dialog.pack();
                    JOptionPane.showMessageDialog(dialog,
                        "Friend request accepted!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
            
            declineBtn.addActionListener(e -> {
                if (dbHandler.declineFriendRequest(requester.getID())) {
                    requestPanel.setVisible(false);
                    dialog.pack();
                    JOptionPane.showMessageDialog(dialog,
                        "Friend request declined!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
            
            requestPanel.add(nameLabel);
            requestPanel.add(acceptBtn);
            requestPanel.add(declineBtn);
            requestsPanel.add(requestPanel);
        }

        JScrollPane scrollPane = new JScrollPane(requestsPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    /**
     * Shows dialog displaying friend list
     */
    public void showFriendsListDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Student> friends = dbHandler.getFriends(currentStudent.getID());
            StringBuilder sb = new StringBuilder("Your Friends:\n\n");
            for (Student friend : friends) {
                sb.append(friend.getName()).append("\n");
            }
            JOptionPane.showMessageDialog(parentFrame, sb.toString(), "Friends List", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Shows dialog for blocking a user
     */
    public void showBlockUserDialog() {
        String username = JOptionPane.showInputDialog(parentFrame, "Enter username to block:");
        if (username != null && !username.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                Student targetStudent = dbHandler.getStudentByUsername(username);
                if (targetStudent == null) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "User not found!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (dbHandler.blockUser(currentStudent.getID(), targetStudent.getID())) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "User blocked successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Failed to block user!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Shows dialog displaying blocked users
     */
    public void showBlockedUsersDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) return;

        List<Student> blockedUsers = dbHandler.getBlockedUsers(currentStudent.getID());
        
        JDialog dialog = new JDialog(parentFrame, "Blocked Users", true);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Student blocked : blockedUsers) {
            listModel.addElement(blocked.getName());
        }
        
        JList<String> blockedList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(blockedList);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton unblockBtn = new JButton("Unblock");
        JButton closeBtn = new JButton("Close");
        
        unblockBtn.addActionListener(e -> {
            String selectedName = blockedList.getSelectedValue();
            if (selectedName != null) {
                Student userToUnblock = blockedUsers.stream()
                    .filter(u -> u.getName().equals(selectedName))
                    .findFirst()
                    .orElse(null);
                    
                if (userToUnblock != null && 
                    dbHandler.unblockUser(currentStudent.getID(), userToUnblock.getID())) {
                    listModel.removeElement(selectedName);
                    JOptionPane.showMessageDialog(dialog,
                        "User unblocked successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(unblockBtn);
        buttonPanel.add(closeBtn);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    public boolean handleLogin(String username, String password, JPanel mainPanel) {
        if (!Validator.isValidString(username) || !Validator.isValidString(password)) {
            JOptionPane.showMessageDialog(parentFrame, "Please enter both username and password");
            return false;
        }

        System.out.println("Attempting authentication for user: " + username);

        Student authenticatedStudent = dbHandler.authenticateStudent(username, password);
        if (authenticatedStudent != null) {
            studentHandler.setCurrentStudent(authenticatedStudent);
            studentHandler.addStudent(authenticatedStudent);

            if (parentFrame instanceof MainGUI) {
                MainGUI mainGUI = (MainGUI) parentFrame;
                panelFactory.updateDashboardInfo(authenticatedStudent);
                panelFactory.refreshGroupLists(groupPanel);
            }
            
            JOptionPane.showMessageDialog(parentFrame, "Login successful!");
            System.out.println("Showing dashboard panel");
            
            // Get the CardLayout from mainPanel and show dashboard
            CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
            cardLayout.show(mainPanel, "Dashboard");
            return true;
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Invalid username or password");
            return false;
        }
    }

    private void updateDashboardInfo(Student student) {
        // Find the dashboard panel in the main panel
        for (Component comp : parentFrame.getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                JPanel mainPanel = (JPanel) comp;
                for (Component dashComp : mainPanel.getComponents()) {
                    if (dashComp instanceof JPanel && "Dashboard".equals(dashComp.getName())) {
                        JPanel dashboardPanel = (JPanel) dashComp;
                        
                        // Update the student information labels
                        for (Component infoComp : dashboardPanel.getComponents()) {
                            if (infoComp instanceof JPanel && 
                                infoComp.getParent().getLayout() instanceof GridBagLayout) {
                                JPanel infoPanel = (JPanel) infoComp;
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
        }
    }

    public void handleLoginAttempt(JTextField usernameField, JPasswordField passwordField, JPanel mainPanel) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (handleLogin(username, password, mainPanel)) {
            // Clear login fields
            usernameField.setText("");
            passwordField.setText("");
        }
    }

    public void handleCreateAccount(JTextField usernameField, JPasswordField passwordField, 
                                  JComboBox<String> yearComboBox, JPanel mainPanel) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String year = (String) yearComboBox.getSelectedItem();

        if (!Validator.isValidString(username) || !Validator.isValidPassword(password)) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Invalid username or password. Password must be at least 8 characters long and include at least one letter and one number.");
            return;
        }

        if (dbHandler.doesUsernameExist(username)) {
            JOptionPane.showMessageDialog(parentFrame, "Username already exists. Please choose a different username.");
            return;
        }

        int id = (int) (Math.random() * 9000) + 1000;
        Student newStudent = new Student(id, username, year, null, null, null);

        if (dbHandler.addStudent(newStudent, password)) {
            JOptionPane.showMessageDialog(parentFrame, "Account created successfully!");
            CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
            cardLayout.show(mainPanel, "Login");
            
            // Clear input fields
            usernameField.setText("");
            passwordField.setText("");
            yearComboBox.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Failed to create account. Please try again.");
        }
    }

    public void handleCreatePost(JTextArea postContent, Runnable onSuccess) {
        String content = postContent.getText().trim();
        if (!content.isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                Post post = new Post(0, content, currentStudent, null); // ID will be set by database
                dbHandler.createPost(post);
                postContent.setText("");
                onSuccess.run(); // Call refreshPostLists
                JOptionPane.showMessageDialog(parentFrame, "Post created successfully!");
            } else {
                JOptionPane.showMessageDialog(parentFrame, "You must be logged in to create a post!");
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Post content cannot be empty!");
        }
    }

    private void showEditPostDialog(Post post, Runnable onSuccess) {
        String newContent = JOptionPane.showInputDialog(parentFrame, 
            "Edit post:", 
            post.getContent());
        if (newContent != null && !newContent.trim().isEmpty()) {
            post.setContent(newContent);
            if (dbHandler.editPost(post)) {
                onSuccess.run();
                JOptionPane.showMessageDialog(parentFrame, 
                    "Post updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void handleEditPostButtonClick(Post selectedPost, Runnable onSuccess) {
        if (selectedPost != null) {
            showEditPostDialog(selectedPost, onSuccess);
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Please select a post to edit!");
        }
    }

    public void handleDeletePost(Post selectedPost, Runnable onSuccess) {
        if (selectedPost != null) {
            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to delete this post?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                postHandler.deletePost(selectedPost);
                onSuccess.run();
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Please select a post to delete!");
        }
    }

    public void handleRemoveBookmark(Post selectedPost, Runnable onSuccess) {
        if (selectedPost != null) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                dbHandler.bookmarkPost(currentStudent.getID(), selectedPost.getID()); // Toggle bookmark
                onSuccess.run();
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Please select a post to remove bookmark!");
        }
    }

    public void handleRemoveMember(int selectedRow, JTable memberTable, Group group, Consumer<Group> refreshCallback) {
        if (selectedRow >= 0) {  // Check if a row is selected
            String memberName = (String) memberTable.getValueAt(selectedRow, 0);
            Student memberToRemove = dbHandler.getStudentByUsername(memberName);
            
            if (memberToRemove != null) {
                if (dbHandler.removeMemberFromGroup(group.getID(), memberToRemove.getID())) {
                    // Update the group object with new member data
                    Group updatedGroup = studentController.getGroupByID(group.getID());
                    
                    // Call the refresh callback with updated group data
                    refreshCallback.accept(updatedGroup);
                    
                    JOptionPane.showMessageDialog(parentFrame,
                        "Member removed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                        "Failed to remove member from group.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                "Please select a member to remove.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleSetEndDate(int selectedRow, JTable memberTable, Group group, Consumer<Group> onSuccess) {
        Student member = null;
        
        if (selectedRow >= 0) {
            // Get member from selected row
            String memberName = (String) memberTable.getValueAt(selectedRow, 0);
            member = dbHandler.getStudentByUsername(memberName);
        } else {
            // If no row selected, prompt for username
            String username = JOptionPane.showInputDialog(parentFrame, 
                "Enter username of member to set end date:",
                "Set End Date",
                JOptionPane.QUESTION_MESSAGE);
                
            if (username != null && !username.trim().isEmpty()) {
                member = dbHandler.getStudentByUsername(username);
            }
        }
        
        if (member != null) {
            if (group.isMember(member)) {
                showSetEndDateDialog(group, member, onSuccess);
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                    "This student is not a member of the group.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                "Student not found!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSetEndDateDialog(Group group, Student member, Consumer<Group> onSuccess) {
        // Create a date picker dialog
        JDialog dialog = new JDialog(parentFrame, "Set Membership End Date", true);
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
            handleEndDateOkButton(yearBox, monthBox, dayBox, dialog, group, member, onSuccess);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    public void handleEndDateOkButton(JComboBox<Integer> yearBox, JComboBox<Integer> monthBox, 
                                    JComboBox<Integer> dayBox, JDialog dialog, 
                                    Group group, Student member, Consumer<Group> refreshCallback) {
        int year = (Integer) yearBox.getSelectedItem();
        int month = (Integer) monthBox.getSelectedItem();
        int day = (Integer) dayBox.getSelectedItem();
        
        java.sql.Date endDate = java.sql.Date.valueOf(
            String.format("%d-%02d-%02d", year, month, day));
        
        if (dbHandler.updateMembershipEndDate(member.getID(), group.getID(), endDate)) {
            // Get fresh group data after update
            Group updatedGroup = studentController.getGroupByID(group.getID());
            
            // Call the refresh callback with updated group data
            refreshCallback.accept(updatedGroup);
            
            JOptionPane.showMessageDialog(dialog, "End date set successfully!");
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(dialog, "Failed to set end date!");
        }
    }

    public void handleCreateTag(JTextField nameField, JTextArea descField, Runnable onSuccess) {
        String name = nameField.getText();
        String desc = descField.getText();
        if (!name.trim().isEmpty()) {
            Tag tag = new Tag(0, name, desc);
            if (!dbHandler.containsTag(tag)) {
                tagHandler.addTag(tag);
                nameField.setText("");
                descField.setText("");
                onSuccess.run();
            }
        }
    }

    public void handleRemoveTag(Tag selectedTag, Student currentStudent, Runnable onSuccess) {
        if (selectedTag != null) {
            if (dbHandler.removeTagFromStudent(currentStudent.getID(), selectedTag.getID())) {
                onSuccess.run();
                JOptionPane.showMessageDialog(parentFrame, "Tag removed successfully!");
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Failed to remove tag!");
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Please select a tag to remove!");
        }
    }

    public void handleEditTagButtonClick(Tag selectedTag, Runnable onSuccess) {
        if (selectedTag != null) {
            showEditTagDialog(selectedTag, onSuccess);
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Please select a tag to edit!");
        }
    }

    private void showEditTagDialog(Tag tag, Runnable onSuccess) {
        String newName = JOptionPane.showInputDialog(parentFrame, 
            "Edit tag name:", 
            tag.getName());
            
        if (newName != null && !newName.trim().isEmpty()) {
            tag.setName(newName);
            tagHandler.updateTag(tag);
            onSuccess.run();
        }
    }

    public void handleAddTag(Tag selectedTag, Student currentStudent, Runnable onSuccess) {
        if (selectedTag != null) {
            if (currentStudent != null) {
                if (dbHandler.addTagToStudent(currentStudent.getID(), selectedTag.getID())) {
                    onSuccess.run();
                    JOptionPane.showMessageDialog(parentFrame, "Tag added successfully!");
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Failed to add tag!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Please select a tag to add!");
        }
    }

    /**
     * Handles the editing of a tag
     * @param tag The tag to edit
     * @param nameField The text field containing the new name
     * @param descArea The text area containing the new description
     * @param dialog The dialog window to close after successful edit
     * @param refreshCallback Callback to refresh the tag lists after successful edit
     */
    public void handleEditTag(Tag tag, JTextField nameField, JTextArea descArea, JDialog dialog, Runnable refreshCallback) {
        String newName = nameField.getText().trim();
        String newDesc = descArea.getText().trim();
        
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Tag name cannot be empty!");
            return;
        }

        tag.setName(newName);
        tag.setDescription(newDesc);
        tagHandler.updateTag(tag);
        refreshCallback.run();
        dialog.dispose();
    }

    /**
     * Handles the logout process
     * @param mainPanel The main panel containing the card layout
     */
    public void handleLogout(JPanel mainPanel) {
        int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
            cardLayout.show(mainPanel, "Welcome");
            studentHandler.setCurrentStudent(null);
        }
    }

    /**
     * Handles toggling of anonymous mode
     * @param isSelected Whether anonymous mode should be enabled
     */
    public void handleAnonymousModeToggle(boolean isSelected) {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            currentStudent.toggleAnonymousMode();
            dbHandler.toggleAnonymousMode(currentStudent.getID(), isSelected);
        }
    }

    public void showAddTagDialog() {
        String tagName = JOptionPane.showInputDialog(parentFrame, "Enter tag name:");
        if (tagName != null && !tagName.trim().isEmpty()) {
            String description = JOptionPane.showInputDialog(parentFrame, "Enter tag description:");
            if (description != null) {
                Tag newTag = new Tag(0, tagName.trim(), description.trim());
                tagHandler.addTag(newTag);
                refreshCallback.run();
            }
        }
    }

    public void showEditTagDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) return;
        
        List<Tag> tags = tagHandler.getTagsByStudent(currentStudent);
        Tag selectedTag = (Tag) JOptionPane.showInputDialog(
            parentFrame,
            "Select tag to edit:",
            "Edit Tag",
            JOptionPane.QUESTION_MESSAGE,
            null,
            tags.toArray(),
            null
        );
        
        if (selectedTag != null) {
            showEditTagDialog(selectedTag, refreshCallback);
        }
    }

    public void showDeleteTagDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) return;
        
        List<Tag> tags = tagHandler.getTagsByStudent(currentStudent);
        Tag selectedTag = (Tag) JOptionPane.showInputDialog(
            parentFrame,
            "Select tag to delete:",
            "Delete Tag",
            JOptionPane.QUESTION_MESSAGE,
            null,
            tags.toArray(),
            null
        );
        
        if (selectedTag != null) {
            tagHandler.removeTag(selectedTag);
            refreshCallback.run();
        }
    }

    public void showViewTagsDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) return;
        
        List<Tag> tags = tagHandler.getTagsByStudent(currentStudent);
        StringBuilder sb = new StringBuilder("Your Tags:\n\n");
        for (Tag tag : tags) {
            sb.append(tag.getName()).append(" - ").append(tag.getDescription()).append("\n");
        }
        JOptionPane.showMessageDialog(parentFrame, sb.toString(), "My Tags", JOptionPane.INFORMATION_MESSAGE);
    }

    public void handleEditAccount(Student student, String username, String password, String year, JDialog dialog) {
        if (!Validator.isValidString(username)) {
            JOptionPane.showMessageDialog(dialog, "Username cannot be empty!");
            return;
        }

        student.setName(username);
        student.setYear(year);
        
        if (!password.isEmpty()) {
            dbHandler.updateStudentPassword(student.getID(), password);
        }
        
        if (dbHandler.updateStudent(student)) {
            JOptionPane.showMessageDialog(dialog, "Account updated successfully!");
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(dialog, "Failed to update account!");
        }
    }

    public void handleProfileManagement() {
        // Move from MainGUI:
        // - showEditAccountDialog()
        // - showDeleteAccountDialog()
        // - showToggleAnonymousModeDialog()
        // - showBlockedUsersDialog()
    }

    public void handlePostManagement() {
        // Move from MainGUI:
        // - showCreatePostDialog()
        // - showEditPostDialog()
        // - showBookmarkPostDialog()
        // - showBookmarkedPostsDialog()
        // - refreshPostLists()
    }

    public void handleTagManagement() {
        // Move from MainGUI:
        // - showAddTagDialog()
        // - showEditTagDialog()
        // - showRemoveTagDialog()
        // - showViewTagsDialog()
        // - refreshTagLists()
    }

    // public List<Component> findComponentsByTitle(Container container, String title) {
    //     // Move the entire findComponentsByTitle method here
    // }

    public void showEditAccountDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) return;

        JDialog dialog = new JDialog(parentFrame, "Edit Account", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        JTextField usernameField = new JTextField(currentStudent.getName(), 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        // Password field
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);

        // Year selection
        String[] years = {"Freshman", "Sophomore", "Junior", "Senior"};
        JComboBox<String> yearComboBox = new JComboBox<>(years);
        yearComboBox.setSelectedItem(currentStudent.getYear());
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        dialog.add(yearComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            handleEditAccount(currentStudent, usernameField.getText(), 
                new String(passwordField.getPassword()), 
                (String)yearComboBox.getSelectedItem(), dialog);
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    public void showDeleteAccountDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) return;

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Are you sure you want to delete your account?\nThis action cannot be undone.",
            "Confirm Account Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dbHandler.deleteStudent(currentStudent.getID())) {
                JOptionPane.showMessageDialog(parentFrame, "Account deleted successfully");
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "Welcome");
                studentHandler.setCurrentStudent(null);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Failed to delete account");
            }
        }
    }

    public void showToggleAnonymousModeDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent == null) return;

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Do you want to toggle anonymous mode?",
            "Toggle Anonymous Mode",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean newMode = !currentStudent.isAnonymous();
            if (dbHandler.toggleAnonymousMode(currentStudent.getID(), newMode)) {
                currentStudent.toggleAnonymousMode();
                JOptionPane.showMessageDialog(parentFrame, 
                    "Anonymous mode " + (newMode ? "enabled" : "disabled"));
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Failed to toggle anonymous mode");
            }
        }
    }

    public void showCreatePostDialog() {
        String content = JOptionPane.showInputDialog(parentFrame, "Enter post content:");
        if (content != null && !content.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                Post post = new Post(generatePostId(), content, currentStudent, null);
                if (dbHandler.createPost(post)) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Post created successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Failed to create post!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private int generatePostId() {
        return (int) (Math.random() * 9000) + 1000; // Simple ID generation
    }

    public void showBookmarkPostDialog() {
        String postId = JOptionPane.showInputDialog(parentFrame, "Enter post ID to bookmark:");
        if (postId != null && !postId.trim().isEmpty()) {
            Student currentStudent = studentHandler.getCurrentStudent();
            if (currentStudent != null) {
                if (dbHandler.bookmarkPost(currentStudent.getID(), Integer.parseInt(postId))) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Post bookmarked successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Failed to bookmark post!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void showBookmarkedPostsDialog() {
        Student currentStudent = studentHandler.getCurrentStudent();
        if (currentStudent != null) {
            List<Post> bookmarkedPosts = dbHandler.getBookmarkedPosts(currentStudent.getID());
            
            JDialog dialog = new JDialog(parentFrame, "Bookmarked Posts", true);
            dialog.setLayout(new BorderLayout());
            
            DefaultListModel<Post> listModel = new DefaultListModel<>();
            for (Post post : bookmarkedPosts) {
                listModel.addElement(post);
            }
            
            JList<Post> postList = new JList<>(listModel);
            postList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, 
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    Post post = (Post) value;
                    setText(String.format("<html><b>%s</b><br/>%s</html>",
                        post.getOwner().getName(),
                        post.getContent()));
                    return this;
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(postList);
            dialog.add(scrollPane, BorderLayout.CENTER);
            
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            dialog.add(closeButton, BorderLayout.SOUTH);
            
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(parentFrame);
            dialog.setVisible(true);
        }
    }

    public void showViewGroupPostsDialog() {
        String groupId = JOptionPane.showInputDialog(parentFrame, "Enter group ID to view posts:");
        if (groupId != null && !groupId.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(groupId);
                List<Post> groupPosts = dbHandler.getGroupPosts(id);
                
                if (groupPosts.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "No posts found for this group", 
                        "Group Posts", 
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                JDialog dialog = new JDialog(parentFrame, "Group Posts", true);
                dialog.setLayout(new BorderLayout());
                
                DefaultListModel<Post> listModel = new DefaultListModel<>();
                for (Post post : groupPosts) {
                    listModel.addElement(post);
                }
                
                JList<Post> postList = new JList<>(listModel);
                postList.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        Post post = (Post) value;
                        setText(String.format("<html><b>%s</b><br/>%s</html>",
                            post.getOwner().getName(),
                            post.getContent()));
                        return this;
                    }
                });
                
                JScrollPane scrollPane = new JScrollPane(postList);
                dialog.add(scrollPane, BorderLayout.CENTER);
                
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> dialog.dispose());
                dialog.add(closeButton, BorderLayout.SOUTH);
                
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Invalid group ID", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showViewStudentPostsDialog() {
        String username = JOptionPane.showInputDialog(parentFrame, "Enter username to view posts:");
        if (username != null && !username.trim().isEmpty()) {
            Student student = dbHandler.getStudentByUsername(username);
            if (student != null) {
                List<Post> posts = postHandler.getPostsByStudent(student);
                
                if (posts.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "No posts found for this user", 
                        "User Posts", 
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                JDialog dialog = new JDialog(parentFrame, "Posts by " + username, true);
                dialog.setLayout(new BorderLayout());
                
                DefaultListModel<Post> listModel = new DefaultListModel<>();
                for (Post post : posts) {
                    listModel.addElement(post);
                }
                
                JList<Post> postList = new JList<>(listModel);
                postList.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        Post post = (Post) value;
                        setText(String.format("<html><b>%s</b><br/>%s</html>",
                            post.getOwner().getName(),
                            post.getContent()));
                        return this;
                    }
                });
                
                JScrollPane scrollPane = new JScrollPane(postList);
                dialog.add(scrollPane, BorderLayout.CENTER);
                
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> dialog.dispose());
                dialog.add(closeButton, BorderLayout.SOUTH);
                
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                    "User not found", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showRemovePostDialog() {
        String postId = JOptionPane.showInputDialog(parentFrame, "Enter post ID to remove:");
        if (postId != null && !postId.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(postId);
                if (postHandler.removePost(id)) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Post removed successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Failed to remove post!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Invalid post ID", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showPostsList(String title, List<Post> posts) {
        if (posts.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No posts found", 
                title, 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setLayout(new BorderLayout());
        
        DefaultListModel<Post> listModel = new DefaultListModel<>();
        for (Post post : posts) {
            listModel.addElement(post);
        }
        
        JList<Post> postList = new JList<>(listModel);
        postList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Post post = (Post) value;
                setText(String.format("<html><b>ID: %d</b><br/>%s<br/><i>by %s</i></html>",
                    post.getID(),
                    post.getContent(),
                    post.getOwner().getName()));
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(postList);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    public void showViewGroupMembersDialog() {
        String groupId = JOptionPane.showInputDialog(parentFrame, "Enter group ID to view members:");
        if (groupId != null && !groupId.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(groupId);
                Group group = studentController.getGroupByID(id);
                if (group != null) {
                    List<Student> members = studentController.getGroupMembers(group);
                    
                    if (members.isEmpty()) {
                        JOptionPane.showMessageDialog(parentFrame, 
                            "No members found in this group", 
                            "Group Members", 
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    
                    JDialog dialog = new JDialog(parentFrame, "Group Members", true);
                    dialog.setLayout(new BorderLayout());
                    
                    DefaultListModel<String> listModel = new DefaultListModel<>();
                    for (Student member : members) {
                        listModel.addElement(member.getName());
                    }
                    
                    JList<String> memberList = new JList<>(listModel);
                    JScrollPane scrollPane = new JScrollPane(memberList);
                    dialog.add(scrollPane, BorderLayout.CENTER);
                    
                    JButton closeButton = new JButton("Close");
                    closeButton.addActionListener(e -> dialog.dispose());
                    dialog.add(closeButton, BorderLayout.SOUTH);
                    
                    dialog.setSize(300, 400);
                    dialog.setLocationRelativeTo(parentFrame);
                    dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Group not found", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Invalid group ID", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showViewAllGroupsDialog() {
        List<Group> groups = studentController.getAllGroups();
        
        if (groups.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No groups found", 
                "All Groups", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(parentFrame, "All Groups", true);
        dialog.setLayout(new BorderLayout());
        
        DefaultListModel<Group> listModel = new DefaultListModel<>();
        for (Group group : groups) {
            listModel.addElement(group);
        }
        
        JList<Group> groupList = new JList<>(listModel);
        groupList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Group group = (Group) value;
                setText(String.format("<html><b>%s</b> (ID: %d)<br/>Size: %d members</html>",
                    group.getName(),
                    group.getID(),
                    group.getSize()));
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(groupList);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    public void showUpdateMembershipEndDateDialog() {
        JTextField groupIdField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        Object[] message = {
            "Group ID:", groupIdField,
            "End Date:", dateField
        };
        
        int option = JOptionPane.showConfirmDialog(parentFrame, message, 
            "Update Membership End Date", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int groupId = Integer.parseInt(groupIdField.getText());
                java.sql.Date endDate = java.sql.Date.valueOf(dateField.getText());
                Student currentStudent = studentHandler.getCurrentStudent();
                if (currentStudent != null) {
                    if (dbHandler.updateMembershipEndDate(currentStudent.getID(), groupId, endDate)) {
                        JOptionPane.showMessageDialog(parentFrame, 
                            "Membership end date updated successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, 
                            "Failed to update membership end date!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Invalid group ID", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Invalid date format. Use YYYY-MM-DD", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showCreateTagDialog() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        Object[] message = {
            "Tag Name:", nameField,
            "Description:", descField
        };
        
        int option = JOptionPane.showConfirmDialog(parentFrame, message, 
            "Create Tag", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame,
                    "Tag name cannot be empty!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Tag tag = new Tag(0, name, desc);
            if (tagHandler.addTag(tag)) {
                JOptionPane.showMessageDialog(parentFrame,
                    "Tag created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshCallback.run();
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                    "Failed to create tag!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showViewAllTagsDialog() {
        List<Tag> tags = tagHandler.getAllTags();
        
        if (tags.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No tags found", 
                "All Tags", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(parentFrame, "All Tags", true);
        dialog.setLayout(new BorderLayout());
        
        DefaultListModel<Tag> listModel = new DefaultListModel<>();
        for (Tag tag : tags) {
            listModel.addElement(tag);
        }
        
        JList<Tag> tagList = new JList<>(listModel);
        tagList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Tag tag = (Tag) value;
                setText(String.format("<html><b>%s</b><br/><i>%s</i></html>",
                    tag.getName(),
                    tag.getDescription()));
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tagList);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    public void showViewStudentTagsDialog() {
        String username = JOptionPane.showInputDialog(parentFrame, "Enter username to view tags:");
        if (username != null && !username.trim().isEmpty()) {
            Student student = dbHandler.getStudentByUsername(username);
            if (student != null) {
                List<Tag> tags = student.getTags();
                
                if (tags.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "No tags found for this user", 
                        "Student Tags", 
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                JDialog dialog = new JDialog(parentFrame, "Tags for " + username, true);
                dialog.setLayout(new BorderLayout());
                
                DefaultListModel<Tag> listModel = new DefaultListModel<>();
                for (Tag tag : tags) {
                    listModel.addElement(tag);
                }
                
                JList<Tag> tagList = new JList<>(listModel);
                tagList.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, 
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        Tag tag = (Tag) value;
                        setText(String.format("<html><b>%s</b><br/><i>%s</i></html>",
                            tag.getName(),
                            tag.getDescription()));
                        return this;
                    }
                });
                
                JScrollPane scrollPane = new JScrollPane(tagList);
                dialog.add(scrollPane, BorderLayout.CENTER);
                
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> dialog.dispose());
                dialog.add(closeButton, BorderLayout.SOUTH);
                
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                    "User not found", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStudentsList(String title, List<Student> students) {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No students found", 
                title, 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setLayout(new BorderLayout());
        
        DefaultListModel<Student> listModel = new DefaultListModel<>();
        for (Student student : students) {
            listModel.addElement(student);
        }
        
        JList<Student> studentList = new JList<>(listModel);
        studentList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Student student = (Student) value;
                setText(String.format("<html><b>%s</b><br/>ID: %d, Year: %s</html>",
                    student.getName(),
                    student.getID(),
                    student.getYear()));
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentList);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    public void setPanelFactory(PanelFactory panelFactory) {
        this.panelFactory = panelFactory;
    }
}