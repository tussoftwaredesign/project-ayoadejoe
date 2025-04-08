package client_package;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Utils {
	
	// Helper method to encrypt a string using AES with the shared secret
   public static byte[] encryptString(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext.getBytes("UTF-8"));
    }
    
    // Helper method to decrypt a byte array into a string using AES with the shared secret
    public static String decryptString(byte[] ciphertext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(ciphertext);
        return new String(decrypted, "UTF-8");
    }
}
