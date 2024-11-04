package com.cryptography;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatDarkLaf;
import com.cryptography.ui.UIUtils;
import com.cryptography.ui.panels.EncryptionPanel;
import com.cryptography.ui.panels.DecryptionPanel;
import static com.cryptography.ui.UIConstants.*;
import javax.imageio.ImageIO;

public class CryptoApplication extends JFrame {
    private JTabbedPane tabbedPane;
    private EncryptionPanel encryptionPanel;
    private DecryptionPanel decryptionPanel;

    public CryptoApplication() {
        setupUI();
        initializeComponents();
        setupFrame();
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIUtils.setupUIDefaults();
            loadApplicationIcon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadApplicationIcon() {
        try {
            Image icon = ImageIO.read(getClass().getResourceAsStream("/images/app_icon.png"));
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        setTitle("Cryptography Application (INT303) - Ajman University");
        Container contentPane = getContentPane();
        contentPane.setBackground(BACKGROUND_COLOR);
        ((JPanel)contentPane).setBorder(null);
        setLayout(new BorderLayout(0, 0));

        add(createTeamPanel(), BorderLayout.NORTH);
        add(createTabbedPane(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createTeamPanel() {
        JPanel teamPanel = new JPanel();
        teamPanel.setBackground(ACCENT_COLOR);
        teamPanel.setBorder(null);
        JLabel teamLabel = new JLabel("Team Members: Ayman Sahyoun | Muhannad Al Basyouni");
        teamLabel.setForeground(TEXT_COLOR);
        teamLabel.setFont(TITLE_FONT.deriveFont(Font.PLAIN, 16));
        teamPanel.add(teamLabel);
        return teamPanel;
    }

    private JTabbedPane createTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TITLE_FONT);
        tabbedPane.setBorder(null);
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setOpaque(true);

        encryptionPanel = new EncryptionPanel();
        decryptionPanel = new DecryptionPanel();

        tabbedPane.addTab("Encryption", encryptionPanel);
        tabbedPane.addTab("Decryption", decryptionPanel);

        return tabbedPane;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(ACCENT_COLOR);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setForeground(TEXT_COLOR);
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 800));
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CryptoApplication().setVisible(true);
        });
    }
} 