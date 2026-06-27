import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AlertPanel extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public AlertPanel() {
        setTitle("Alert History - Change Log");
        setSize(700, 400);
        setLayout(new BorderLayout());

        // Table ke columns set karna jaisa proposal mein hai
        String[] columns = {"ID", "File Path", "Old Hash", "New Hash", "Change Type", "Date Detected"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Database se data fetch karna
        loadAlertsFromDatabase();
    }

    private void loadAlertsFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM change_log")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String path = rs.getString("file_path");
                String oldHash = rs.getString("old_hash");
                String newHash = rs.getString("new_hash");
                String type = rs.getString("change_type");
                String date = rs.getString("changed_at");

                // Table mein row add karna
                String[] rowData = {id, path, oldHash, newHash, type, date};
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading alerts: " + e.getMessage());
        }
    }
}