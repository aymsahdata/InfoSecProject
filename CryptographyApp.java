import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.*;
import java.util.Base64;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;

public class CryptographyApp {
    private static final Scanner scanner = new Scanner(System.in);
    
                // Display team members
    private static void displayTeamMembers() {
        System.out.println("\n=== Team Members ===");
        System.out.println("1. Ayman Sahyoun");
        System.out.println("2. Aws Silawi");
        System.out.println("3. Muhannad Basyouni");
        System.out.println("==================\n");
    }

    public static void main(String[] args) {
        displayTeamMembers();
        
        while (true) {
            try {
                System.out.println("\n=== Cryptography Application ===");
                System.out.println("1. Encrypt Data");
                System.out.println("2. Decrypt Data");
                System.out.println("3. Exit");
                System.out.print("Choose an option (1-3): ");
                
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                if (choice == 3) break;
                
                if (choice != 1 && choice != 2) {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }
                
                System.out.println("\nSelect encryption method:");
                System.out.println("1. Symmetric (AES)");
                System.out.println("2. Asymmetric (RSA)");
                System.out.print("Choose method (1-2): ");
                
                int method = scanner.nextInt();
                scanner.nextLine();
                
                if (method == 1) {
                    handleSymmetricEncryption(choice == 1);
                } else if (method == 2) {
                    handleAsymmetricEncryption(choice == 1);
                } else {
                    System.out.println("Invalid method selected.");
                }
                
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    private static void handleSymmetricEncryption(boolean isEncrypt) throws Exception {
                                // Get key size from user
        System.out.println("\nAvailable AES key sizes: 128, 192, 256 bits");
        System.out.print("Enter desired key size in bits: ");
        int keySize = scanner.nextInt();
        scanner.nextLine();
        
                                // Validate key size
        if (keySize != 128 && keySize != 192 && keySize != 256) {
            throw new IllegalArgumentException("Invalid key size. Must be 128, 192, or 256 bits.");
        }
        
                                // Calculate required character length (bits / 8 for bytes)
        int charLength = keySize / 8;
        System.out.printf("Enter the symmetric key (%d characters for %d-bit encryption): ", charLength, keySize);
        String keyString = scanner.nextLine();
        
        if (keyString.length() != charLength) {
            throw new IllegalArgumentException(
                String.format("Key must be exactly %d characters for %d-bit encryption", charLength, keySize));
        }
        
                                // Create secret key
        SecretKey key = new SecretKeySpec(keyString.getBytes(), "AES");
        
        if (isEncrypt) {
            System.out.print("Enter plaintext to encrypt: ");
            String plaintext = scanner.nextLine();
            String ciphertext = encryptAES(plaintext, key);
            System.out.println("\nEncryption Details:");
            System.out.println("Key Size: " + keySize + " bits");
            System.out.println("Key: " + keyString);
            System.out.println("Encrypted text: " + ciphertext);
        } else {
            System.out.print("Enter ciphertext to decrypt: ");
            String ciphertext = scanner.nextLine();
            String plaintext = decryptAES(ciphertext, key);
            System.out.println("\nDecryption Details:");
            System.out.println("Key Size: " + keySize + " bits");
            System.out.println("Key: " + keyString);
            System.out.println("Decrypted text: " + plaintext);
        }
    }
    
    private static void handleAsymmetricEncryption(boolean isEncrypt) throws Exception {
        System.out.println("\nAvailable RSA key sizes: 1024, 2048, 3072, 4096 bits");
        System.out.print("Enter desired key size in bits: ");
        int keySize = scanner.nextInt();
        scanner.nextLine();
        
        if (keySize < 1024 || keySize > 4096 || keySize % 1024 != 0) {
            throw new IllegalArgumentException("Invalid key size. Must be 1024, 2048, 3072, or 4096 bits.");
        }
        
        if (isEncrypt) {
            // For encryption, generate new key pair
            System.out.println("\nGenerating " + keySize + "-bit RSA key pair...");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(keySize);
            KeyPair pair = keyGen.generateKeyPair();
            
            System.out.print("Enter plaintext to encrypt: ");
            String plaintext = scanner.nextLine();
            String ciphertext = encryptRSA(plaintext, pair.getPublic());
            
            System.out.println("\nEncryption Details:");
            System.out.println("Key Size: " + keySize + " bits");
            System.out.println("Encrypted text: " + ciphertext);
            
            // Print keys for later use
            System.out.println("\n-----BEGIN RSA PUBLIC KEY-----");
            System.out.println(Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
            System.out.println("-----END RSA PUBLIC KEY-----");
            
            System.out.println("\n-----BEGIN RSA PRIVATE KEY-----");
            System.out.println(Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));
            System.out.println("-----END RSA PRIVATE KEY-----");
        } else {
            // For decryption, request the private key
            System.out.println("\nPaste the private key (without BEGIN/END markers):");
            String privateKeyStr = scanner.nextLine();
            
            // Convert the Base64 private key string back to a PrivateKey object
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            System.out.print("Enter ciphertext to decrypt: ");
            String ciphertext = scanner.nextLine();
            
            String plaintext = decryptRSA(ciphertext, privateKey);
            System.out.println("\nDecryption Details:");
            System.out.println("Key Size: " + keySize + " bits");
            System.out.println("Decrypted text: " + plaintext);
        }
    }
    
    private static String encryptAES(String plaintext, SecretKey key) throws Exception {
        // Create cipher with proper transformation
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // Generate IV
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // Initialize cipher
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        // Encrypt
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        
        // Combine IV and encrypted part
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);
        
        return Base64.getEncoder().encodeToString(combined);
    }
    
    private static String decryptAES(String ciphertext, SecretKey key) throws Exception {
        // Decode from Base64
        byte[] combined = Base64.getDecoder().decode(ciphertext);
        
        // Extract IV
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // Extract encrypted part
        byte[] encrypted = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);
        
        // Create cipher with proper transformation
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        // Decrypt
        byte[] decryptedBytes = cipher.doFinal(encrypted);
        return new String(decryptedBytes);
    }
    
    private static String encryptRSA(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    private static String decryptRSA(String ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes);
    }
}
