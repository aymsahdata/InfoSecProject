package com.cryptography.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import static com.cryptography.ui.UIConstants.*;

public class UIUtils {
    public static JPanel createStyledButton(JButton button) {
        button.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 14));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 20, 8, 20)
        ));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(button);
        return panel;
    }

    public static void styleTextArea(JTextArea textArea, String title) {
        textArea.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 14));
        textArea.setBackground(INPUT_BG_COLOR);
        textArea.setForeground(TEXT_COLOR);
        textArea.setCaretColor(TEXT_COLOR);
        textArea.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT.deriveFont(Font.PLAIN, 14),
                TEXT_COLOR
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
    }

    public static void setupUIDefaults() {
        UIManager.put("control", BACKGROUND_COLOR);
        UIManager.put("text", TEXT_COLOR);
        UIManager.put("nimbusBase", ACCENT_COLOR);
        UIManager.put("TextArea.background", INPUT_BG_COLOR);
        UIManager.put("TextField.background", INPUT_BG_COLOR);
        UIManager.put("ComboBox.background", INPUT_BG_COLOR);
        UIManager.put("TabbedPane.background", BACKGROUND_COLOR);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("ComboBox.foreground", TEXT_COLOR);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("TabbedPane.foreground", TEXT_COLOR);
        UIManager.put("TabbedPane.selected", ACCENT_COLOR);
        UIManager.put("TabbedPane.background", BACKGROUND_COLOR);
        UIManager.put("TabbedPane.selectedBackground", ACCENT_COLOR);
        UIManager.put("TabbedPane.unselectedBackground", BACKGROUND_COLOR);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND_COLOR);
        UIManager.put("TabbedPane.focusColor", ACCENT_COLOR);
        UIManager.put("TabbedPane.hoverColor", BUTTON_HOVER_COLOR);
        UIManager.put("TabbedPane.underlineColor", ACCENT_COLOR);
        UIManager.put("TabbedPane.inactiveUnderlineColor", BACKGROUND_COLOR);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("RootPane.background", BACKGROUND_COLOR);
        UIManager.put("ContentPane.background", BACKGROUND_COLOR);
    }
} 