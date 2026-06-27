import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class HashGenerator {
    public static String generateSHA256(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // [cite: 79]
            FileInputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount;

            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fis.close();

            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString(); // 64-character string return karega
        } catch (Exception e) {
            return null;
        }
    }
}