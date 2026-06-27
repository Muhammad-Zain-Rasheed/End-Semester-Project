import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class DatabaseManager {
    public static void saveFileRecord(String fileName, String filePath, String hash, long fileSize) {
        String query = "INSERT INTO file_records (file_name, file_path, hash, file_size, last_scanned, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, fileName);
            pstmt.setString(2, filePath);
            pstmt.setString(3, hash);
            pstmt.setLong(4, fileSize);
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(6, "SAFE"); // Baseline scan mein sab SAFE hota hai [cite: 91]

            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error saving record: " + e.getMessage());
        }
    }
}