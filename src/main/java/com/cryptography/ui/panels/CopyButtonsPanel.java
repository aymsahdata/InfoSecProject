package com.cryptography.ui.panels;

import javax.swing.*;
import java.awt.*;
import static com.cryptography.ui.UIConstants.*;
import com.cryptography.utils.ClipboardUtils;
import com.cryptography.ui.UIUtils;

public class CopyButtonsPanel {
    public static JPanel createTextAreaWithCopyButton(JTextArea textArea, String title, JTextField keyField) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        UIUtils.styleTextArea(textArea, title);
        
        String[] buttons;
        if (title.equals("Encryption Result")) {
            buttons = new String[]{"Copy Encrypted Text", "Copy AES Key"};
        } else if (title.equals("Generated Keys")) {
            buttons = new String[]{"Copy Public Key", "Copy Private Key"};
        } else if (title.equals("Decryption Result")) {
            buttons = new String[]{"Copy Decrypted Text"};
        } else {
            buttons = new String[]{"Copy All"};
        }
        
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(createCopyButtonsPanel(textArea, keyField, buttons), BorderLayout.SOUTH);
        
        return panel;
    }

    private static JPanel createCopyButtonsPanel(JTextArea textArea, JTextField keyField, String... buttonLabels) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        for (String label : buttonLabels) {
            JButton copyButton = new JButton(label);
            copyButton.setFont(REGULAR_FONT);
            copyButton.setBackground(ACCENT_COLOR);
            copyButton.setForeground(TEXT_COLOR);
            copyButton.addActionListener(e -> {
                String textToCopy = "";
                String content = textArea.getText();
                
                switch (label) {
                    case "Copy Encrypted Text":
                    case "Copy Decrypted Text":
                    case "Copy All":
                        textToCopy = content;
                        break;
                    case "Copy Public Key":
                        int publicKeyStart = content.indexOf("-----BEGIN RSA PUBLIC KEY-----");
                        int publicKeyEnd = content.indexOf("-----END RSA PUBLIC KEY-----");
                        if (publicKeyStart != -1 && publicKeyEnd != -1) {
                            textToCopy = content.substring(
                                publicKeyStart + "-----BEGIN RSA PUBLIC KEY-----\n".length(),
                                publicKeyEnd
                            ).trim();
                        }
                        break;
                    case "Copy Private Key":
                        int privateKeyStart = content.indexOf("-----BEGIN RSA PRIVATE KEY-----");
                        int privateKeyEnd = content.indexOf("-----END RSA PRIVATE KEY-----");
                        if (privateKeyStart != -1 && privateKeyEnd != -1) {
                            textToCopy = content.substring(
                                privateKeyStart + "-----BEGIN RSA PRIVATE KEY-----\n".length(),
                                privateKeyEnd
                            ).trim();
                        }
                        break;
                    case "Copy AES Key":
                        textToCopy = keyField.getText();
                        break;
                }
                
                if (!textToCopy.isEmpty()) {
                    ClipboardUtils.copyToClipboard(textToCopy, "Text copied to clipboard", textArea);
                }
            });
            buttonPanel.add(UIUtils.createStyledButton(copyButton));
        }
        
        return buttonPanel;
    }
} 