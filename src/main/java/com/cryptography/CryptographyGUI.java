package src.main.java.com.cryptography;

import javax.swing.*;
import java.awt.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.ByteArrayOutputStream;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;

public class CryptographyGUI extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> methodBox;
    private JComboBox<String> keySizeBox;
    private JTextField keyField;
    private JPanel keyPanel;
    private JButton generateKeysButton;
    private KeyPair currentKeyPair;
    private JTextArea keyDisplayArea;
    private JTabbedPane tabbedPane;
    private JPanel encryptionPanel;
    private JPanel decryptionPanel;
    private JTextArea decryptInputArea;
    private JTextArea decryptOutputArea;
    private JTextArea rsaPrivateKeyArea;
    private JTextField aesKeyField;

    public CryptographyGUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("control", BACKGROUND_COLOR);
            UIManager.put("text", TEXT_COLOR);
            UIManager.put("nimbusBase", ACCENT_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Cryptography Application - Ajman University");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        JPanel teamPanel = new JPanel();
        teamPanel.setBackground(ACCENT_COLOR);
        teamPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel teamLabel = new JLabel("Team Members: Ayman Sahyoun | Aws Silawi | Muhannad Basyouni");
        teamLabel.setForeground(Color.WHITE);
        teamLabel.setFont(TITLE_FONT.deriveFont(Font.PLAIN, 16));
        teamPanel.add(teamLabel);
        add(teamPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TITLE_FONT);
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        encryptionPanel = createEncryptionPanel();
        decryptionPanel = createDecryptionPanel();
        
        tabbedPane.addTab("Encryption", createStyledIcon("🔒"), encryptionPanel);
        tabbedPane.addTab("Decryption", createStyledIcon("🔓"), decryptionPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
        
        setPreferredSize(new Dimension(1200, 800));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(ACCENT_COLOR);
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    private Icon createStyledIcon(String unicode) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2.setColor(ACCENT_COLOR);
                g2.drawString(unicode, x, y + 16);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 20;
            }

            @Override
            public int getIconHeight() {
                return 20;
            }
        };
    }

    private JPanel createStyledButton(JButton button) {
        button.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 14));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(ACCENT_COLOR.darker(), 1),
            new EmptyBorder(8, 20, 8, 20)
        ));
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(button);
        return panel;
    }

    private void styleTextArea(JTextArea textArea, String title) {
        textArea.setFont(REGULAR_FONT.deriveFont(Font.PLAIN, 14));
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                new LineBorder(ACCENT_COLOR, 1),
                title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                TITLE_FONT.deriveFont(Font.PLAIN, 14),
                ACCENT_COLOR
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
    }

    private JPanel createEncryptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Control panel (left side)
        JPanel controlPanel = new JPanel(new GridLayout(5, 1, 3, 3));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.setPreferredSize(new Dimension(250, -1));
        
        // Method selection panel
        JPanel methodPanel = new JPanel(new BorderLayout(5, 0));
        methodPanel.add(new JLabel("Method:"), BorderLayout.WEST);
        methodBox = new JComboBox<>(new String[]{"AES (Symmetric)", "RSA (Asymmetric)"});
        methodPanel.add(methodBox, BorderLayout.CENTER);
        
        // Key size panel
        JPanel keySizePanel = new JPanel(new BorderLayout(5, 0));
        keySizePanel.add(new JLabel("Key Size:"), BorderLayout.WEST);
        keySizeBox = new JComboBox<>();
        keySizePanel.add(keySizeBox, BorderLayout.CENTER);
        updateKeySizes();
        
        // Key management panel with card layout
        keyPanel = new JPanel(new CardLayout());
        
        // AES panel
        JPanel aesPanel = new JPanel(new BorderLayout(5, 0));
        aesPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Create styled AES key input panel
        JPanel aesKeyInputPanel = new JPanel(new BorderLayout(10, 0));
        aesKeyInputPanel.setBackground(Color.WHITE);
        aesKeyInputPanel.setBorder(new CompoundBorder(
            new LineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        
        // Style the label
        JLabel aesKeyLabel = new JLabel("AES Key:");
        aesKeyLabel.setFont(REGULAR_FONT);
        aesKeyLabel.setForeground(TEXT_COLOR);
        
        // Style the text field
        keyField = new JTextField();
        keyField.setFont(REGULAR_FONT);
        keyField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        aesKeyInputPanel.add(aesKeyLabel, BorderLayout.WEST);
        aesKeyInputPanel.add(keyField, BorderLayout.CENTER);
        aesPanel.add(aesKeyInputPanel, BorderLayout.CENTER);
        
        // Add a button to generate a random AES key
        JButton generateAESKeyButton = new JButton("Generate Random Key");
        generateAESKeyButton.addActionListener(e -> generateRandomAESKey());
        aesPanel.add(generateAESKeyButton, BorderLayout.SOUTH);
        
        // RSA panel
        JPanel rsaPanel = new JPanel(new BorderLayout(5, 0));
        generateKeysButton = new JButton("Generate Keys");
        generateKeysButton.addActionListener(e -> generateAndDisplayKeys());
        rsaPanel.add(generateKeysButton, BorderLayout.CENTER);
        
        keyPanel.add(aesPanel, "AES");
        keyPanel.add(rsaPanel, "RSA");
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        // Create encrypt button
        JButton encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(e -> executeOperation("Encrypt"));
        
        // Add buttons to panel
        buttonsPanel.add(createStyledButton(encryptButton));
        
        // Add all panels to control panel
        controlPanel.add(methodPanel);
        controlPanel.add(keySizePanel);
        controlPanel.add(keyPanel);
        controlPanel.add(buttonsPanel);
        
        // Content panel (right side)
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 5, 5)) {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                return new Dimension(size.width, size.height);
            }
        };
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        // Create input panel with equal height
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputArea = new JTextArea(10, 50);  // Adjusted height
        styleTextArea(inputArea, "Input Text");
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        
        outputArea = new JTextArea(10, 50);  // Adjusted height
        styleTextArea(outputArea, "Encryption Result");
        
        outputArea.setEditable(false);
        
        // Add text areas with copy buttons
        contentPanel.add(createTextAreaWithCopyButton(inputArea, "Input Text"));
        contentPanel.add(createTextAreaWithCopyButton(outputArea, "Encryption Result"));
        
        // Create a panel for key display
        JPanel keyDisplayPanel = new JPanel(new BorderLayout());
        keyDisplayArea = new JTextArea(8, 50);
        styleTextArea(keyDisplayArea, "Generated Keys");
        keyDisplayArea.setEditable(false);
        keyDisplayPanel.add(createTextAreaWithCopyButton(keyDisplayArea, "Generated Keys"), BorderLayout.CENTER);
        
        contentPanel.add(keyDisplayPanel);
        
        // Method change listener
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
        
        panel.add(controlPanel, BorderLayout.WEST);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void generateRandomAESKey() {
        try {
            int keySize = Integer.parseInt(keySizeBox.getSelectedItem().toString());
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keySize);
            SecretKey secretKey = keyGen.generateKey();
            keyField.setText(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating AES key: " + e.getMessage());
        }
    }

    private JPanel createDecryptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Create left control panel
        JPanel controlPanel = new JPanel(new GridLayout(5, 1, 3, 3));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.setPreferredSize(new Dimension(250, -1));
        
        // Method selection panel
        JPanel methodPanel = new JPanel(new BorderLayout(5, 0));
        methodPanel.add(new JLabel("Method:"), BorderLayout.WEST);
        JComboBox<String> decryptMethodBox = new JComboBox<>(new String[]{"AES (Symmetric)", "RSA (Asymmetric)"});
        methodPanel.add(decryptMethodBox, BorderLayout.CENTER);
        
        // Key size panel
        JPanel keySizePanel = new JPanel(new BorderLayout(5, 0));
        keySizePanel.add(new JLabel("Key Size:"), BorderLayout.WEST);
        JComboBox<String> decryptKeySizeBox = new JComboBox<>();
        keySizePanel.add(decryptKeySizeBox, BorderLayout.CENTER);
        
        // Key input panel with card layout
        JPanel decryptKeyPanel = new JPanel(new CardLayout());
        
        // AES key panel
        JPanel aesKeyPanel = new JPanel(new BorderLayout(5, 0));
        aesKeyPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Create styled AES key input panel
        JPanel aesKeyInputPanel = new JPanel(new BorderLayout(10, 0));
        aesKeyInputPanel.setBackground(Color.WHITE);
        aesKeyInputPanel.setBorder(new CompoundBorder(
            new LineBorder(ACCENT_COLOR, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        
        // Style the label
        JLabel aesKeyLabel = new JLabel("AES Key:");
        aesKeyLabel.setFont(REGULAR_FONT);
        aesKeyLabel.setForeground(TEXT_COLOR);
        
        // Style the text field
        aesKeyField = new JTextField();
        aesKeyField.setFont(REGULAR_FONT);
        aesKeyField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        aesKeyInputPanel.add(aesKeyLabel, BorderLayout.WEST);
        aesKeyInputPanel.add(aesKeyField, BorderLayout.CENTER);
        aesKeyPanel.add(aesKeyInputPanel, BorderLayout.CENTER);
        
        // RSA key panel
        JPanel rsaPanel = new JPanel(new BorderLayout(5, 0));
        rsaPrivateKeyArea = new JTextArea(3, 30);
        rsaPrivateKeyArea.setLineWrap(true);
        rsaPrivateKeyArea.setWrapStyleWord(true);
        styleTextArea(rsaPrivateKeyArea, "RSA Private Key");
        rsaPanel.add(new JScrollPane(rsaPrivateKeyArea), BorderLayout.CENTER);
        
        // Add key panels to card layout
        decryptKeyPanel.add(aesKeyPanel, "AES");
        decryptKeyPanel.add(rsaPanel, "RSA");
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        // Create decrypt button
        JButton decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(e -> executeOperation("Decrypt"));
        
        // Create paste button
        JButton pasteButton = new JButton("Paste Key");
        pasteButton.addActionListener(e -> {
            try {
                String clipboardText = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
                if (decryptMethodBox.getSelectedItem().toString().startsWith("AES")) {
                    aesKeyField.setText(clipboardText);
                } else {
                    rsaPrivateKeyArea.setText(clipboardText);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error pasting key: " + ex.getMessage());
            }
        });
        
        // Add buttons to panel
        buttonsPanel.add(createStyledButton(decryptButton));
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(createStyledButton(pasteButton));
        
        // Add all panels to control panel
        controlPanel.add(methodPanel);
        controlPanel.add(keySizePanel);
        controlPanel.add(decryptKeyPanel);
        controlPanel.add(buttonsPanel);
        
        // Add control panel to main panel
        panel.add(controlPanel, BorderLayout.WEST);
        
        // Add method change listener
        decryptMethodBox.addActionListener(e -> {
            decryptKeySizeBox.removeAllItems();
            if (decryptMethodBox.getSelectedItem().toString().startsWith("AES")) {
                decryptKeySizeBox.addItem("128");
                decryptKeySizeBox.addItem("192");
                decryptKeySizeBox.addItem("256");
                CardLayout cl = (CardLayout) decryptKeyPanel.getLayout();
                cl.show(decryptKeyPanel, "AES");
            } else {
                decryptKeySizeBox.addItem("1024");
                decryptKeySizeBox.addItem("2048");
                decryptKeySizeBox.addItem("3072");
                decryptKeySizeBox.addItem("4096");
                CardLayout cl = (CardLayout) decryptKeyPanel.getLayout();
                cl.show(decryptKeyPanel, "RSA");
            }
        });
        
        // Set initial selection
        decryptMethodBox.setSelectedIndex(0);
        
        // Create and add the text areas panel
        JPanel textAreasPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textAreasPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        decryptInputArea = new JTextArea(15, 50);
        decryptOutputArea = new JTextArea(15, 50);
        decryptOutputArea.setEditable(false);
        
        textAreasPanel.add(createTextAreaWithCopyButton(decryptInputArea, "Encrypted Text"));
        textAreasPanel.add(createTextAreaWithCopyButton(decryptOutputArea, "Decryption Result"));
        
        panel.add(textAreasPanel, BorderLayout.CENTER);
        
        return panel;
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

    private void executeOperation(String operation) {
        try {
            if (operation.equals("Encrypt")) {
                String method = methodBox.getSelectedItem().toString();
                int keySize = Integer.parseInt(keySizeBox.getSelectedItem().toString());
                String input = inputArea.getText();
                String key = keyField.getText();

                if (method.startsWith("AES")) {
                    handleAESOperation("Encrypt", keySize, input, key);
                } else {
                    if (currentKeyPair == null) {
                        throw new IllegalStateException("Please generate RSA keys first.");
                    }
                    handleRSAOperation("Encrypt", keySize, input, null);
                }
            } else {
                String method = methodBox.getSelectedItem().toString();
                int keySize = Integer.parseInt(keySizeBox.getSelectedItem().toString());
                String input = decryptInputArea.getText();

                if (method.startsWith("AES")) {
                    String key = aesKeyField.getText();
                    handleAESOperation("Decrypt", keySize, input, key);
                } else {
                    if (currentKeyPair == null) {
                        String privateKeyStr = rsaPrivateKeyArea.getText();
                        if (privateKeyStr.isEmpty()) {
                            throw new IllegalStateException("Please enter the RSA private key for decryption.");
                        }
                        
                        privateKeyStr = privateKeyStr
                            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                            .replace("-----END RSA PRIVATE KEY-----", "")
                            .replaceAll("\\s+", "");
                        
                        try {
                            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
                            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
                            
                            currentKeyPair = new KeyPair(null, privateKey);
                        } catch (Exception e) {
                            throw new IllegalStateException("Invalid RSA private key format: " + e.getMessage());
                        }
                    }
                    handleRSAOperation("Decrypt", keySize, input, null);
                }
            }
        } catch (Exception e) {
            String errorMessage = "Error: " + e.getMessage() + 
                                "\nMake sure you have entered the correct key and " +
                                (operation.equals("Encrypt") ? "input text." : "encrypted text.");
            if (operation.equals("Encrypt")) {
                outputArea.setText(errorMessage);
            } else {
                decryptOutputArea.setText(errorMessage);
            }
            e.printStackTrace();
        }
    }

    private void handleAESOperation(String operation, int keySize, String input, String keyString) throws Exception {
        if (keySize != 128 && keySize != 192 && keySize != 256) {
            throw new IllegalArgumentException("Invalid key size. Must be 128, 192, or 256 bits.");
        }
        
        // Convert the Base64 key string to bytes
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        
        if (operation.equals("Encrypt")) {
            String ciphertext = encryptAES(input, key);
            outputArea.setText(ciphertext);
        } else {
            try {
                String plaintext = decryptAES(input, key);
                decryptOutputArea.setText(plaintext);
            } catch (Exception e) {
                throw new Exception("Decryption failed. Please ensure you're using the correct key.");
            }
        }
    }

    private void handleRSAOperation(String operation, int keySize, String input, String key) throws Exception {
        if (keySize < 1024 || keySize > 4096 || keySize % 1024 != 0) {
            throw new IllegalArgumentException("Invalid key size. Must be 1024, 2048, 3072, or 4096 bits.");
        }
        
        if (currentKeyPair == null) {
            throw new IllegalStateException("Please generate keys first using the 'Generate Keys' button.");
        }
        
        if (operation.equals("Encrypt")) {
            String ciphertext = encryptRSA(input, currentKeyPair.getPublic());
            outputArea.setText(ciphertext);
            outputArea.revalidate();
            outputArea.repaint();
        } else {
            String plaintext = decryptRSA(input, currentKeyPair.getPrivate());
            decryptOutputArea.setText(plaintext);
            decryptOutputArea.revalidate();
            decryptOutputArea.repaint();
        }
    }

    private String encryptAES(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes("UTF-8"));
        
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);
        
        return Base64.getEncoder().encodeToString(combined);
    }

    private String decryptAES(String ciphertext, SecretKey key) throws Exception {
        byte[] combined = Base64.getDecoder().decode(ciphertext);
        
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        byte[] encrypted = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        byte[] decryptedBytes = cipher.doFinal(encrypted);
        return new String(decryptedBytes, "UTF-8");
    }

    private String encryptRSA(String plaintext, PublicKey publicKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            // Cast to RSAPublicKey to access getModulus()
            RSAPublicKey rsaKey = (RSAPublicKey) publicKey;
            byte[] inputBytes = plaintext.getBytes("UTF-8");
            int maxLength = (rsaKey.getModulus().bitLength() / 8) - 11; // PKCS1 padding requires 11 bytes
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int offset = 0;
            
            while (offset < inputBytes.length) {
                int chunkSize = Math.min(maxLength, inputBytes.length - offset);
                byte[] chunk = new byte[chunkSize];
                System.arraycopy(inputBytes, offset, chunk, 0, chunkSize);
                
                byte[] encryptedChunk = cipher.doFinal(chunk);
                outputStream.write(encryptedChunk);
                
                offset += chunkSize;
            }
            
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new Exception("Encryption failed: " + e.getMessage());
        }
    }

    private String decryptRSA(String ciphertext, PrivateKey privateKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            // Cast to RSAPrivateKey to access getModulus()
            RSAPrivateKey rsaKey = (RSAPrivateKey) privateKey;
            byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
            int keySize = rsaKey.getModulus().bitLength() / 8;
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int offset = 0;
            
            while (offset < encryptedBytes.length) {
                int chunkSize = Math.min(keySize, encryptedBytes.length - offset);
                byte[] chunk = new byte[chunkSize];
                System.arraycopy(encryptedBytes, offset, chunk, 0, chunkSize);
                
                byte[] decryptedChunk = cipher.doFinal(chunk);
                outputStream.write(decryptedChunk);
                
                offset += chunkSize;
            }
            
            return new String(outputStream.toByteArray(), "UTF-8");
        } catch (Exception e) {
            throw new Exception("Decryption failed: " + e.getMessage());
        }
    }

    private void generateAndDisplayKeys() {
        try {
            int keySize = Integer.parseInt(keySizeBox.getSelectedItem().toString());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(keySize);
            currentKeyPair = keyGen.generateKeyPair();
            
            StringBuilder result = new StringBuilder();
            
            // Store public key
            result.append("-----BEGIN RSA PUBLIC KEY-----\n");
            result.append(Base64.getEncoder().encodeToString(currentKeyPair.getPublic().getEncoded())).append("\n");
            result.append("-----END RSA PUBLIC KEY-----\n\n");
            
            // Store private key
            result.append("-----BEGIN RSA PRIVATE KEY-----\n");
            result.append(Base64.getEncoder().encodeToString(currentKeyPair.getPrivate().getEncoded())).append("\n");
            result.append("-----END RSA PRIVATE KEY-----");
            
            keyDisplayArea.setText(result.toString());
            outputArea.setText("RSA keys have been generated successfully. You can now encrypt/decrypt messages.");
        } catch (Exception e) {
            outputArea.setText("Error generating keys: " + e.getMessage());
        }
    }

    private JPanel createTextAreaWithCopyButton(JTextArea textArea, String title) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        styleTextArea(textArea, title);
        
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
        panel.add(createCopyButtonsPanel(textArea, buttons), BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createCopyButtonsPanel(JTextArea textArea, String... buttonLabels) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        for (String label : buttonLabels) {
            JButton copyButton = new JButton(label);
            copyButton.setFont(REGULAR_FONT);
            copyButton.setBackground(ACCENT_COLOR);
            copyButton.setForeground(Color.WHITE);
            copyButton.addActionListener(e -> {
                String textToCopy = "";
                String content = textArea.getText();
                
                switch (label) {
                    case "Copy Encrypted Text":
                        textToCopy = content;
                        pasteToDecryption(textToCopy);
                        break;
                        
                    case "Copy Decrypted Text":
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
                        
                    case "Copy All":
                        textToCopy = content;
                        break;
                }
                
                if (!textToCopy.isEmpty()) {
                    StringSelection selection = new StringSelection(textToCopy);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                    
                    String message = "Text copied to clipboard";
                    if (label.equals("Copy Encrypted Text")) {
                        message += " and pasted to decryption tab";
                    }
                    
                    JOptionPane.showMessageDialog(
                        this,
                        message,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            });
            buttonPanel.add(createStyledButton(copyButton));
        }
        
        return buttonPanel;
    }

    private void pasteToDecryption(String text) {
        // Switch to decryption tab
        tabbedPane.setSelectedIndex(1);
        
        // Set the encrypted text
        decryptInputArea.setText(text);
        
        // Copy the key from encryption to decryption
        aesKeyField.setText(keyField.getText());
        
        // Give focus to the decryption input area
        decryptInputArea.requestFocus();
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CryptographyGUI().setVisible(true);
        });
    }
} 