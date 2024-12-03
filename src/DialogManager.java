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

    public DialogManager(JFrame parentFrame, DatabaseHandler dbHandler, StudentHandler studentHandler, PostHandler postHandler, TagHandler tagHandler) {
        this.parentFrame = parentFrame;
        this.dbHandler = dbHandler;
        this.studentHandler = studentHandler;
        this.postHandler = postHandler;
        this.tagHandler = tagHandler;
    }

    /**
     * Shows dialog for sending a friend request
     */
    public void showSendFriendRequestDialog() {
        String username = JOptionPane.showInputDialog(parentFrame, 
            "Enter username to send friend request:",
            "Send Friend Request",
            JOptionPane.QUESTION_MESSAGE);
            
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
                
                if (dbHandler.sendFriendRequest(currentStudent.getID(), targetStudent.getID())) {
                    JOptionPane.showMessageDialog(parentFrame,
                        "Friend request sent successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                        "Failed to send friend request. The request may already exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
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
        if (currentStudent == null) return;

        List<Student> friends = dbHandler.getFriends(currentStudent.getID());
        
        JDialog dialog = new JDialog(parentFrame, "Friends List", true);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Student friend : friends) {
            listModel.addElement(friend.getName());
        }
        
        JList<String> friendsList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(friendsList);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeFriendBtn = new JButton("Remove Friend");
        JButton closeBtn = new JButton("Close");
        
        removeFriendBtn.addActionListener(e -> {
            String selectedName = friendsList.getSelectedValue();
            if (selectedName != null) {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to remove " + selectedName + " from your friends?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    Student friendToRemove = friends.stream()
                        .filter(f -> f.getName().equals(selectedName))
                        .findFirst()
                        .orElse(null);
                        
                    if (friendToRemove != null && 
                        dbHandler.removeFriend(currentStudent.getID(), friendToRemove.getID())) {
                        listModel.removeElement(selectedName);
                        JOptionPane.showMessageDialog(dialog,
                            "Friend removed successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(removeFriendBtn);
        buttonPanel.add(closeBtn);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    /**
     * Shows dialog for blocking a user
     */
    public void showBlockUserDialog() {
        String username = JOptionPane.showInputDialog(parentFrame,
            "Enter username to block:",
            "Block User",
            JOptionPane.QUESTION_MESSAGE);
            
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
                        "Failed to block user. They may already be blocked.",
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

            updateDashboardInfo(authenticatedStudent);
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

    public void showEditPostDialog(Post post, Runnable onSuccess) {
        String newContent = JOptionPane.showInputDialog(parentFrame, 
            "Edit post:", 
            post.getContent());
            
        if (newContent != null && !newContent.trim().isEmpty()) {
            post.setContent(newContent);
            dbHandler.editPost(post);
            onSuccess.run();
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

    public void handleRemoveMember(int selectedRow, JTable memberTable, Group group, Consumer<Group> onSuccess) {
        if (selectedRow >= 0) {
            String memberName = (String) memberTable.getValueAt(selectedRow, 0);
            Student member = studentHandler.getStudentByName(memberName);
            if (member != null) {
                int confirm = JOptionPane.showConfirmDialog(parentFrame,
                    "Are you sure you want to remove " + memberName + " from the group?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (dbHandler.leaveGroup(group, member)) {
                        onSuccess.accept(group);
                        JOptionPane.showMessageDialog(parentFrame, "Member removed successfully!");
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, "Failed to remove member!");
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Please select a member to remove!");
        }
    }

    public void handleSetEndDate(int selectedRow, JTable memberTable, Group group, Consumer<Group> onSuccess) {
        if (selectedRow >= 0) {
            String memberName = (String) memberTable.getValueAt(selectedRow, 0);
            Student member = studentHandler.getStudentByName(memberName);
            if (member != null) {
                showSetEndDateDialog(group, member, onSuccess);
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Please select a member to set end date!");
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
                                    Group group, Student member, Consumer<Group> onSuccess) {
        int year = (Integer) yearBox.getSelectedItem();
        int month = (Integer) monthBox.getSelectedItem();
        int day = (Integer) dayBox.getSelectedItem();
        
        java.sql.Date endDate = java.sql.Date.valueOf(
            String.format("%d-%02d-%02d", year, month, day));
        
        if (dbHandler.updateMembershipEndDate(member.getID(), group.getID(), endDate)) {
            onSuccess.accept(group);
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
}