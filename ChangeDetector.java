import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class ChangeDetector {

    public static void runIntegrityCheck(File folder, javax.swing.JTextArea logArea) {
        logArea.append("Starting Integrity Check...\n");
        File[] currentFiles = folder.listFiles();

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Phase 1: MODIFIED aur NEW files check karna
            if (currentFiles != null) {
                for (File file : currentFiles) {
                    if (file.isFile()) {
                        String currentHash = HashGenerator.generateSHA256(file);
                        String oldHash = getOldHashFromDB(conn, file.getAbsolutePath());

                        if (oldHash == null) {
                            // Agar database mein hash nahi mila, matlab file nayi hai
                            logArea.append("ALERT: NEW FILE -> " + file.getName() + "\n");
                            logChange(conn, file.getAbsolutePath(), "NULL", currentHash, "NEW FILE");
                        } else if (!oldHash.equals(currentHash)) {
                            // Agar hashes match nahi karte, matlab file modify hui hai
                            logArea.append("ALERT: MODIFIED -> " + file.getName() + "\n");
                            logChange(conn, file.getAbsolutePath(), oldHash, currentHash, "MODIFIED");
                        } else {
                            logArea.append("SAFE: " + file.getName() + "\n");
                        }
                    }
                }
            }

            // Phase 2: DELETED files check karna (Jo DB mein hain par disk par nahi)
            String checkDeletedQuery = "SELECT file_path, hash FROM file_records";
            PreparedStatement stmt = conn.prepareStatement(checkDeletedQuery);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String dbFilePath = rs.getString("file_path");
                String dbHash = rs.getString("hash");
                File checkFile = new File(dbFilePath);

                if (!checkFile.exists() && checkFile.getParentFile().equals(folder)) {
                    logArea.append("ALERT: DELETED -> " + checkFile.getName() + "\n");
                    logChange(conn, dbFilePath, dbHash, "NULL", "DELETED");
                }
            }

        } catch (Exception e) {
            logArea.append("Error during check: " + e.getMessage() + "\n");
        }
        logArea.append("Integrity Check Complete!\n");
    }

    // Database se purana hash nikalne ka helper method
    private static String getOldHashFromDB(Connection conn, String filePath) throws Exception {
        String query = "SELECT hash FROM file_records WHERE file_path = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, filePath);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getString("hash");
        }
        return null;
    }

    // Alerts ko change_log table mein save karne ka helper method
    private static void logChange(Connection conn, String filePath, String oldHash, String newHash, String changeType) throws Exception {
        String query = "INSERT INTO change_log (file_path, old_hash, new_hash, change_type, changed_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, filePath);
        pstmt.setString(2, oldHash);
        pstmt.setString(3, newHash);
        pstmt.setString(4, changeType);
        pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Timestamp save kar raha hai
        pstmt.executeUpdate();
    }
}