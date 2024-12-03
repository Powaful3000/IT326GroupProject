import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

public class PanelManager {
    private final Map<String, JPanel> panels;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public PanelManager(CardLayout cardLayout, JPanel mainPanel) {
        this.panels = new HashMap<>();
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
    }

    public void addPanel(String name, JPanel panel) {
        panels.put(name, panel);
        mainPanel.add(panel, name);
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    public boolean hasPanel(String name) {
        return panels.containsKey(name);
    }

    public JPanel getPanel(String name) {
        return panels.get(name);
    }

    public void updatePanel(String name, JPanel updatedPanel) {
        if (panels.containsKey(name)) {
            // Remove old panel
            mainPanel.remove(panels.get(name));
            // Add updated panel
            panels.put(name, updatedPanel);
            mainPanel.add(updatedPanel, name);
            // Refresh display
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    public void setPanelVisibility(String name, boolean visible) {
        JPanel panel = panels.get(name);
        if (panel != null) {
            panel.setVisible(visible);
            if (visible) {
                cardLayout.show(mainPanel, name);
            }
        }
    }

    public void hideAllPanelsExcept(String visiblePanelName) {
        panels.forEach((name, panel) -> {
            panel.setVisible(name.equals(visiblePanelName));
        });
        if (panels.containsKey(visiblePanelName)) {
            cardLayout.show(mainPanel, visiblePanelName);
        }
    }
}