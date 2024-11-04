package com.cryptography.ui.panels;

import javax.swing.*;
import java.awt.*;
import com.cryptography.ui.UIUtils;
import com.cryptography.crypto.CryptoOperations;
import static com.cryptography.ui.UIConstants.*;
import javax.crypto.SecretKey;
import java.util.Base64;
import javax.swing.border.*;
import javax.swing.JOptionPane;
import com.cryptography.utils.ClipboardUtils;

public class DecryptionPanel extends JPanel {
    private JTextArea decryptInputArea;
    private JTextArea decryptOutputArea;
    private JTextArea rsaPrivateKeyArea;
    private JTextField aesKeyField;
    private JComboBox<String> decryptMethodBox;
    private JComboBox<String> decryptKeySizeBox;

    public DecryptionPanel() {
        setLayout(new BorderLayout(5, 5));
        initializeComponents();
    }

    private void initializeComponents() {
        add(createControlPanel(), BorderLayout.WEST);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridLayout(5, 1, 3, 3));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.setPreferredSize(new Dimension(250, -1));
        controlPanel.setBackground(BACKGROUND_COLOR);

        decryptMethodBox = new JComboBox<>(new String[]{"AES (Symmetric)", "RSA (Asymmetric)"});
        decryptKeySizeBox = new JComboBox<>();
        
        JPanel methodPanel = createMethodPanel();
        JPanel keySizePanel = createKeySizePanel();
        JPanel keyInputPanel = createKeyInputPanel();
        JPanel buttonsPanel = createButtonsPanel();

        controlPanel.add(methodPanel);
        controlPanel.add(keySizePanel);
        controlPanel.add(keyInputPanel);
        controlPanel.add(buttonsPanel);

        return controlPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        contentPanel.setBackground(BACKGROUND_COLOR);

        decryptInputArea = new JTextArea(15, 50);
        decryptOutputArea = new JTextArea(15, 50);
        decryptOutputArea.setEditable(false);

        contentPanel.add(CopyButtonsPanel.createTextAreaWithCopyButton(decryptInputArea, "Encrypted Text", aesKeyField));
        contentPanel.add(CopyButtonsPanel.createTextAreaWithCopyButton(decryptOutputArea, "Decryption Result", aesKeyField));

        return contentPanel;
    }

    private JPanel createMethodPanel() {
        JPanel methodPanel = new JPanel(new BorderLayout(5, 0));
        methodPanel.setBackground(BACKGROUND_COLOR);
        methodPanel.add(new JLabel("Method:"), BorderLayout.WEST);
        decryptMethodBox.setBackground(INPUT_BG_COLOR);
        decryptMethodBox.setForeground(TEXT_COLOR);
        methodPanel.add(decryptMethodBox, BorderLayout.CENTER);
        
        decryptMethodBox.addActionListener(e -> updateKeySizes());
        
        return methodPanel;
    }

    private JPanel createKeySizePanel() {
        JPanel keySizePanel = new JPanel(new BorderLayout(5, 0));
        keySizePanel.setBackground(BACKGROUND_COLOR);
        keySizePanel.add(new JLabel("Key Size:"), BorderLayout.WEST);
        decryptKeySizeBox.setBackground(INPUT_BG_COLOR);
        decryptKeySizeBox.setForeground(TEXT_COLOR);
        keySizePanel.add(decryptKeySizeBox, BorderLayout.CENTER);
        updateKeySizes();
        return keySizePanel;
    }

    private JPanel createKeyInputPanel() {
        JPanel keyPanel = new JPanel(new CardLayout());
        keyPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel aesPanel = createAESKeyPanel();
        JPanel rsaPanel = createRSAKeyPanel();
        
        keyPanel.add(aesPanel, "AES");
        keyPanel.add(rsaPanel, "RSA");
        
        decryptMethodBox.addActionListener(e -> {
            CardLayout cl = (CardLayout) keyPanel.getLayout();
            cl.show(keyPanel, decryptMethodBox.getSelectedItem().toString().startsWith("AES") ? "AES" : "RSA");
        });
        
        return keyPanel;
    }

    private JPanel createAESKeyPanel() {
        JPanel aesPanel = new JPanel(new BorderLayout(5, 0));
        aesPanel.setBackground(BACKGROUND_COLOR);
        aesPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JPanel aesKeyInputPanel = new JPanel(new BorderLayout(10, 0));
        aesKeyInputPanel.setBackground(INPUT_BG_COLOR);
        aesKeyInputPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        
        JLabel aesKeyLabel = new JLabel("AES Key:");
        aesKeyLabel.setFont(REGULAR_FONT);
        aesKeyLabel.setForeground(TEXT_COLOR);
        
        aesKeyField = new JTextField();
        aesKeyField.setFont(REGULAR_FONT);
        aesKeyField.setBackground(INPUT_BG_COLOR);
        aesKeyField.setForeground(TEXT_COLOR);
        aesKeyField.setCaretColor(TEXT_COLOR);
        aesKeyField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        aesKeyInputPanel.add(aesKeyLabel, BorderLayout.WEST);
        aesKeyInputPanel.add(aesKeyField, BorderLayout.CENTER);
        aesPanel.add(aesKeyInputPanel, BorderLayout.CENTER);
        
        return aesPanel;
    }

    private JPanel createRSAKeyPanel() {
        JPanel rsaPanel = new JPanel(new BorderLayout(5, 0));
        rsaPanel.setBackground(BACKGROUND_COLOR);
        
        rsaPrivateKeyArea = new JTextArea(3, 30);
        rsaPrivateKeyArea.setLineWrap(true);
        rsaPrivateKeyArea.setWrapStyleWord(true);
        rsaPrivateKeyArea.setBackground(INPUT_BG_COLOR);
        rsaPrivateKeyArea.setForeground(TEXT_COLOR);
        rsaPrivateKeyArea.setCaretColor(TEXT_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(rsaPrivateKeyArea);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(INPUT_BG_COLOR);
        scrollPane.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1),
                "RSA Private Key",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT.deriveFont(Font.PLAIN, 14),
                TEXT_COLOR
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        rsaPanel.add(scrollPane, BorderLayout.CENTER);
        return rsaPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        JButton decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(e -> executeOperation());
        
        JButton pasteButton = new JButton("Paste Key");
        pasteButton.addActionListener(e -> pasteKeyFromClipboard());
        
        buttonsPanel.add(UIUtils.createStyledButton(decryptButton));
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(UIUtils.createStyledButton(pasteButton));
        
        return buttonsPanel;
    }

    private void updateKeySizes() {
        decryptKeySizeBox.removeAllItems();
        if (decryptMethodBox.getSelectedItem().toString().startsWith("AES")) {
            decryptKeySizeBox.addItem("128");
            decryptKeySizeBox.addItem("192");
            decryptKeySizeBox.addItem("256");
        } else {
            decryptKeySizeBox.addItem("1024");
            decryptKeySizeBox.addItem("2048");
            decryptKeySizeBox.addItem("3072");
            decryptKeySizeBox.addItem("4096");
        }
    }

    private void pasteKeyFromClipboard() {
        try {
            String clipboardText = ClipboardUtils.getFromClipboard();
            if (decryptMethodBox.getSelectedItem().toString().startsWith("AES")) {
                aesKeyField.setText(clipboardText);
            } else {
                rsaPrivateKeyArea.setText(clipboardText);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error pasting key: " + ex.getMessage());
        }
    }

    private void executeOperation() {
        try {
            String method = decryptMethodBox.getSelectedItem().toString();
            String input = decryptInputArea.getText();

            if (method.startsWith("AES")) {
                String key = aesKeyField.getText();
                if (key.isEmpty()) {
                    throw new IllegalStateException("Please enter the AES key.");
                }
                SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(
                    Base64.getDecoder().decode(key), "AES"
                );
                String plaintext = CryptoOperations.decryptAES(input, secretKey);
                decryptOutputArea.setText(plaintext);
            } else {
                String privateKeyStr = rsaPrivateKeyArea.getText();
                if (privateKeyStr.isEmpty()) {
                    throw new IllegalStateException("Please enter the RSA private key.");
                }
                
                privateKeyStr = privateKeyStr
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
                
                byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
                java.security.spec.PKCS8EncodedKeySpec keySpec = 
                    new java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes);
                java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
                java.security.PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
                
                String plaintext = CryptoOperations.decryptRSA(input, privateKey);
                decryptOutputArea.setText(plaintext);
            }
        } catch (Exception e) {
            decryptOutputArea.setText("Error: " + e.getMessage() + 
                "\nMake sure you have entered the correct key and encrypted text.");
        }
    }

    public void setInputText(String text) {
        decryptInputArea.setText(text);
    }

    public void setAESKey(String key) {
        aesKeyField.setText(key);
    }
} 