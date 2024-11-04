package com.cryptography.ui.panels;

import javax.swing.*;
import java.awt.*;
import com.cryptography.ui.UIUtils;
import com.cryptography.crypto.CryptoOperations;
import static com.cryptography.ui.UIConstants.*;
import java.security.KeyPair;
import javax.crypto.SecretKey;
import java.util.Base64;
import javax.swing.border.*;

public class EncryptionPanel extends JPanel {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> methodBox;
    private JComboBox<String> keySizeBox;
    private JTextField keyField;
    private JPanel keyPanel;
    private JButton generateKeysButton;
    private KeyPair currentKeyPair;
    private JTextArea keyDisplayArea;

    public EncryptionPanel() {
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

        methodBox = new JComboBox<>(new String[]{"AES (Symmetric)", "RSA (Asymmetric)"});
        keySizeBox = new JComboBox<>();
        keyPanel = new JPanel(new CardLayout());
        
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
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        contentPanel.setBackground(BACKGROUND_COLOR);

        inputArea = new JTextArea(10, 50);
        outputArea = new JTextArea(10, 50);
        keyDisplayArea = new JTextArea(8, 50);

        outputArea.setEditable(false);
        keyDisplayArea.setEditable(false);

        contentPanel.add(CopyButtonsPanel.createTextAreaWithCopyButton(inputArea, "Input Text", keyField));
        contentPanel.add(CopyButtonsPanel.createTextAreaWithCopyButton(outputArea, "Encryption Result", keyField));
        contentPanel.add(CopyButtonsPanel.createTextAreaWithCopyButton(keyDisplayArea, "Generated Keys", keyField));

        return contentPanel;
    }

    private JPanel createMethodPanel() {
        JPanel methodPanel = new JPanel(new BorderLayout(5, 0));
        methodPanel.setBackground(BACKGROUND_COLOR);
        methodPanel.add(new JLabel("Method:"), BorderLayout.WEST);
        methodBox.setBackground(INPUT_BG_COLOR);
        methodBox.setForeground(TEXT_COLOR);
        methodPanel.add(methodBox, BorderLayout.CENTER);
        
        methodBox.addActionListener(e -> {
            updateKeySizes();
            CardLayout cl = (CardLayout) keyPanel.getLayout();
            if (methodBox.getSelectedItem().toString().startsWith("AES")) {
                cl.show(keyPanel, "AES");
            } else {
                cl.show(keyPanel, "RSA");
                currentKeyPair = null;
            }
        });
        
        return methodPanel;
    }

    private JPanel createKeySizePanel() {
        JPanel keySizePanel = new JPanel(new BorderLayout(5, 0));
        keySizePanel.setBackground(BACKGROUND_COLOR);
        keySizePanel.add(new JLabel("Key Size:"), BorderLayout.WEST);
        keySizeBox.setBackground(INPUT_BG_COLOR);
        keySizeBox.setForeground(TEXT_COLOR);
        keySizePanel.add(keySizeBox, BorderLayout.CENTER);
        updateKeySizes();
        return keySizePanel;
    }

    private JPanel createKeyInputPanel() {
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
        
        keyField = new JTextField();
        keyField.setFont(REGULAR_FONT);
        keyField.setBackground(INPUT_BG_COLOR);
        keyField.setForeground(TEXT_COLOR);
        keyField.setCaretColor(TEXT_COLOR);
        keyField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        aesKeyInputPanel.add(aesKeyLabel, BorderLayout.WEST);
        aesKeyInputPanel.add(keyField, BorderLayout.CENTER);
        aesPanel.add(aesKeyInputPanel, BorderLayout.CENTER);
        
        JButton generateAESKeyButton = new JButton("Generate Random Key");
        generateAESKeyButton.addActionListener(e -> generateRandomAESKey());
        aesPanel.add(generateAESKeyButton, BorderLayout.SOUTH);
        
        JPanel rsaPanel = new JPanel(new BorderLayout(5, 0));
        generateKeysButton = new JButton("Generate Keys");
        generateKeysButton.addActionListener(e -> generateAndDisplayKeys());
        rsaPanel.add(generateKeysButton, BorderLayout.CENTER);
        
        keyPanel.add(aesPanel, "AES");
        keyPanel.add(rsaPanel, "RSA");
        
        return keyPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        JButton encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(e -> executeOperation());
        
        buttonsPanel.add(UIUtils.createStyledButton(encryptButton));
        
        return buttonsPanel;
    }

    private void updateKeySizes() {
        keySizeBox.removeAllItems();
        if (methodBox.getSelectedItem().toString().startsWith("AES")) {
            keySizeBox.addItem("128");
            keySizeBox.addItem("192");
            keySizeBox.addItem("256");
        } else {
            keySizeBox.addItem("1024");
            keySizeBox.addItem("2048");
            keySizeBox.addItem("3072");
            keySizeBox.addItem("4096");
        }
    }

    private void generateRandomAESKey() {
        try {
            SecretKey key = CryptoOperations.generateAESKey(
                Integer.parseInt(keySizeBox.getSelectedItem().toString())
            );
            keyField.setText(Base64.getEncoder().encodeToString(key.getEncoded()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating AES key: " + e.getMessage());
        }
    }

    private void generateAndDisplayKeys() {
        try {
            currentKeyPair = CryptoOperations.generateRSAKeyPair(
                Integer.parseInt(keySizeBox.getSelectedItem().toString())
            );
            
            StringBuilder result = new StringBuilder();
            result.append("-----BEGIN RSA PUBLIC KEY-----\n")
                  .append(Base64.getEncoder().encodeToString(currentKeyPair.getPublic().getEncoded()))
                  .append("\n-----END RSA PUBLIC KEY-----\n\n")
                  .append("-----BEGIN RSA PRIVATE KEY-----\n")
                  .append(Base64.getEncoder().encodeToString(currentKeyPair.getPrivate().getEncoded()))
                  .append("\n-----END RSA PRIVATE KEY-----");
            
            keyDisplayArea.setText(result.toString());
            outputArea.setText("RSA keys have been generated successfully.");
        } catch (Exception e) {
            outputArea.setText("Error generating keys: " + e.getMessage());
        }
    }

    private void executeOperation() {
        try {
            String method = methodBox.getSelectedItem().toString();
            String input = inputArea.getText();

            if (method.startsWith("AES")) {
                String key = keyField.getText();
                if (key.isEmpty()) {
                    throw new IllegalStateException("Please enter or generate an AES key.");
                }
                SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(
                    Base64.getDecoder().decode(key), "AES"
                );
                String ciphertext = CryptoOperations.encryptAES(input, secretKey);
                outputArea.setText(ciphertext);
            } else {
                if (currentKeyPair == null) {
                    throw new IllegalStateException("Please generate RSA keys first.");
                }
                String ciphertext = CryptoOperations.encryptRSA(input, currentKeyPair.getPublic());
                outputArea.setText(ciphertext);
            }
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage() + "\nMake sure you have entered the correct key and input text.");
        }
    }

    public String getEncryptedText() {
        return outputArea.getText();
    }

    public String getAESKey() {
        return keyField.getText();
    }
} 