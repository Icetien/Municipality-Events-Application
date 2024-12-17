/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EventManager;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/**
 *
 * @author DWIGHT
 */
public class PBKDF2UTILITY {

public static String hashPassword(String password) {
    try {
        int iterations = 65536;
        int keyLength = 128;
        byte[] salt = generateSalt();

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Combine salt and hash for storage
        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
    } catch (Exception e) {
        throw new RuntimeException("Error hashing password: " + e.getMessage());
    }
}

public static byte[] generateSalt() {
    try {
        // Generate a 16-byte (128-bit) salt
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Error generating salt: " + e.getMessage());
    }
}

public static boolean validatePassword(String password, String storedHash) throws Exception {
    String hashedPassword = hashPassword(password); // Hash the input password
    return hashedPassword.equals(storedHash); // Compare with stored hash
}

}
