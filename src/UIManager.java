import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UIManager {
    private final DialogManager dialogManager;
    private final PanelFactory panelFactory;
    private final PanelManager panelManager;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private JList<Group> groupList;
    private DefaultListModel<Group> groupListModel;
    private final JFrame parentFrame;
    private final GroupHandler groupHandler;
    private final StudentController studentController;

    public UIManager(JFrame parentFrame, DatabaseHandler dbHandler,
            StudentHandler studentHandler, PostHandler postHandler,
            TagHandler tagHandler, StudentController studentController,
            GroupHandler groupHandler) {
        this.parentFrame = parentFrame;
        this.studentController = studentController;
        this.groupHandler = groupHandler;
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        this.panelManager = new PanelManager(cardLayout, mainPanel);

        this.groupListModel = new DefaultListModel<>();
        this.groupList = new JList<>(groupListModel);

        this.dialogManager = new DialogManager(parentFrame, dbHandler, studentHandler,
                postHandler, tagHandler, studentController,
                null, this);
        this.panelFactory = new PanelFactory(studentController, dbHandler, studentHandler,
                dialogManager, cardLayout, mainPanel,
                postHandler, tagHandler);

        setupGroupManagement();
        dialogManager.setPanelFactory(panelFactory);
        panelFactory.initializePanels();
        initializePanels();
        showPanel("Welcome");
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void showPanel(String panelName) {
        if (panelManager.hasPanel(panelName)) {
            panelManager.showPanel(panelName);
            refreshPanel(panelName);
        } else {
            System.err.println("Panel not found: " + panelName);
        }
    }

    public void refreshPanel(String panelName) {
        JPanel panel = panelManager.getPanel(panelName);
        if (panel != null) {
            panel.revalidate();
            panel.repaint();
        }
    }

    public void refreshPanels() {
        // Refresh specific panels that need updating
        refreshPanel("Dashboard");
        refreshPanel("Groups");
        refreshPanel("Tags");
        refreshPanel("Posts");
        refreshPanel("Search");
    }

    public void updatePanelState(String panelName, boolean enabled) {
        JPanel panel = panelManager.getPanel(panelName);
        if (panel != null) {
            panel.setEnabled(enabled);
            for (Component comp : panel.getComponents()) {
                comp.setEnabled(enabled);
            }
            refreshPanel(panelName);
        }
    }

    public void updateAllPanelsState(boolean enabled) {
        for (String panelName : new String[] { "Dashboard", "Groups", "Tags", "Posts", "Search", "Profile" }) {
            updatePanelState(panelName, enabled);
        }
    }

    public void initializePanels() {
        // Initialize all panels in a disabled state except Welcome/Login/Create Account
        updatePanelState("Welcome", true);
        updatePanelState("Login", true);
        updatePanelState("Create Account", true);

        // Disable authenticated panels initially
        String[] authenticatedPanels = {
                "Dashboard", "Groups", "Tags", "Posts", "Search", "Profile"
        };
        for (String panelName : authenticatedPanels) {
            updatePanelState(panelName, false);
        }
    }

    public void enableAuthenticatedPanels(boolean enabled) {
        String[] authenticatedPanels = {
                "Dashboard", "Groups", "Tags", "Posts", "Search", "Profile"
        };
        for (String panelName : authenticatedPanels) {
            updatePanelState(panelName, enabled);
        }
    }

    public PanelManager getPanelManager() {
        return panelManager;
    }

    private void setupGroupManagement() {
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        updateGroupList();
    }

    public void updateGroupList() {
        groupListModel.clear();
        List<Group> groups = groupHandler.getAllGroups();

        // Sort groups by size (in case database sorting isn't working)
        groups.sort((g1, g2) -> Integer.compare(g2.getSize(), g1.getSize()));

        for (Group group : groups) {
            groupListModel.addElement(group);
        }
    }

    private void handleJoinGroup() {
        Group selectedGroup = groupList.getSelectedValue();
        if (selectedGroup != null) {
            if (studentController.joinGroup(selectedGroup.getID())) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Successfully joined group!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                        "Failed to join group",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            String groupName = JOptionPane.showInputDialog(parentFrame,
                    "Enter group name to join:",
                    "Join Group",
                    JOptionPane.QUESTION_MESSAGE);

            if (groupName != null && !groupName.trim().isEmpty()) {
                Group group = groupHandler.findGroupByName(groupName);
                if (group != null) {
                    if (studentController.joinGroup(group.getID())) {
                        JOptionPane.showMessageDialog(parentFrame,
                                "Successfully joined group!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parentFrame,
                                "Failed to join group",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Group not found!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        updateGroupList();
    }

    private void handleLeaveGroup() {
        Group selectedGroup = groupList.getSelectedValue();
        if (selectedGroup != null) {
            if (studentController.leaveGroup(selectedGroup.getID())) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Successfully left group!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                        "Failed to leave group",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            String groupName = JOptionPane.showInputDialog(parentFrame,
                    "Enter group name to leave:",
                    "Leave Group",
                    JOptionPane.QUESTION_MESSAGE);

            if (groupName != null && !groupName.trim().isEmpty()) {
                Group group = groupHandler.findGroupByName(groupName);
                if (group != null) {
                    if (studentController.leaveGroup(group.getID())) {
                        JOptionPane.showMessageDialog(parentFrame,
                                "Successfully left group!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parentFrame,
                                "Failed to leave group",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Group not found!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        updateGroupList();
    }

    public void refreshGroupList() {
        updateGroupList();
    }

    public JList<Group> getGroupList() {
        return groupList;
    }
}