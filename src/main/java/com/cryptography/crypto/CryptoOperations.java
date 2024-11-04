package com.cryptography.crypto;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;

public class CryptoOperations {
    public static String encryptAES(String plaintext, SecretKey key) throws Exception {
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

    public static String decryptAES(String ciphertext, SecretKey key) throws Exception {
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

    public static String encryptRSA(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        RSAPublicKey rsaKey = (RSAPublicKey) publicKey;
        byte[] inputBytes = plaintext.getBytes("UTF-8");
        int maxLength = (rsaKey.getModulus().bitLength() / 8) - 11;
        
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
    }

    public static String decryptRSA(String ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
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
    }

    public static KeyPair generateRSAKeyPair(int keySize) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize);
        return keyGen.generateKeyPair();
    }

    public static SecretKey generateAESKey(int keySize) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize);
        return keyGen.generateKey();
    }
} 