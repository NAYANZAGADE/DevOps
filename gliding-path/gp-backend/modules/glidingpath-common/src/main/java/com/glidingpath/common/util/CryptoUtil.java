package com.glidingpath.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for AES encryption and decryption.
 */
public class CryptoUtil {

    private static final String AES = "AES";

    /**
     * Generates a SecretKeySpec for AES encryption from the given string key.
     * Key length must be 16, 24, or 32 bytes.
     *
     * @param key The encryption key
     * @return SecretKeySpec object
     */
    private static SecretKeySpec getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid AES key length. Must be 16, 24, or 32 bytes. Found: " + keyBytes.length);
        }

        return new SecretKeySpec(keyBytes, AES);
    }

    /**
     * Encrypts a plain text string using AES algorithm and returns Base64-encoded ciphertext.
     *
     * @param plainText The original string to encrypt
     * @param key       The secret key used for encryption
     * @return Encrypted and Base64-encoded string
     * @throws Exception if encryption fails
     */
    public static String encrypt(String plainText, String key) throws Exception {
        SecretKeySpec secretKey = getKey(key); // Validate and generate key
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // ECB mode with padding
        cipher.init(Cipher.ENCRYPT_MODE, secretKey); // Initialize cipher in ENCRYPT mode
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)); // Encrypt data
        return Base64.getEncoder().encodeToString(encryptedBytes); // Return Base64 string
    }

    /**
     * Decrypts a Base64-encoded string using AES algorithm and returns the original plain text.
     *
     * @param encryptedText The Base64-encoded ciphertext
     * @param key           The secret key used for decryption
     * @return Decrypted original string
     * @throws Exception if decryption fails
     */
    public static String decrypt(String encryptedText, String key) throws Exception {
        SecretKeySpec secretKey = getKey(key); // Validate and generate key
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // ECB mode with padding
        cipher.init(Cipher.DECRYPT_MODE, secretKey); // Initialize cipher in DECRYPT mode
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText); // Decode Base64
        return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8); // Return decrypted text
    }
}
